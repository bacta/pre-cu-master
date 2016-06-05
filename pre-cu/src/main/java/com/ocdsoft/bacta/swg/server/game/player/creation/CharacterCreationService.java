package com.ocdsoft.bacta.swg.server.game.player.creation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.utils.StringUtil;
import com.ocdsoft.bacta.soe.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.util.SOECRC32;
import com.ocdsoft.bacta.swg.server.game.GameServerState;
import com.ocdsoft.bacta.swg.server.game.biography.BiographyService;
import com.ocdsoft.bacta.swg.server.game.chat.GameChatService;
import com.ocdsoft.bacta.swg.server.game.message.chat.ChatDestroyAvatar;
import com.ocdsoft.bacta.swg.server.game.message.creation.ClientCreateCharacter;
import com.ocdsoft.bacta.swg.server.game.message.creation.ClientCreateCharacterFailed;
import com.ocdsoft.bacta.swg.server.game.message.creation.ClientCreateCharacterSuccess;
import com.ocdsoft.bacta.swg.server.game.name.NameService;
import com.ocdsoft.bacta.swg.server.game.object.ServerObject;
import com.ocdsoft.bacta.swg.server.game.object.intangible.player.PlayerObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.TangibleObject;
import com.ocdsoft.bacta.swg.server.game.object.tangible.creature.CreatureObject;
import com.ocdsoft.bacta.swg.server.game.object.template.server.ServerCreatureObjectTemplate;
import com.ocdsoft.bacta.swg.server.game.object.template.shared.SharedObjectTemplate;
import com.ocdsoft.bacta.swg.server.game.service.data.ObjectTemplateService;
import com.ocdsoft.bacta.swg.server.game.service.object.ServerObjectService;
import com.ocdsoft.bacta.swg.server.game.util.Gender;
import com.ocdsoft.bacta.swg.server.game.util.Race;
import com.ocdsoft.bacta.swg.server.login.object.CharacterInfo;
import com.ocdsoft.bacta.swg.shared.collision.CollisionProperty;
import com.ocdsoft.bacta.swg.shared.foundation.ConstCharCrcLowerString;
import com.ocdsoft.bacta.swg.shared.foundation.CrcString;
import com.ocdsoft.bacta.swg.shared.math.Transform;
import com.ocdsoft.bacta.swg.shared.math.Vector;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by kyle on 5/9/2016.
 */
@Singleton
public final class CharacterCreationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterCreationService.class);

    public static final String CONFIG_SECTION = "Bacta/GameServer/CharacterCreation";
    public static final CrcString APPEARANCE_SLOT_NAME = new ConstCharCrcLowerString("appearance_inventory");

    private final ServerObjectService serverObjectService;
    private final ProfessionDefaultsService professionDefaultsService;
    private final ObjectTemplateService templateService;
    private final NameService nameService;
    private final StartingLocations startingLocations;
    private final NewbieTutorialService newbieTutorialService;
    private final BiographyService biographyService;
    private final GameChatService chatService;
    private final GameServerState gameServerState;
    private final AllowBaldService allowBaldService;
    private final HairStylesService hairStylesService;

    private final Cache<String, Integer> pendingCreations;
    private final String defaultProfession;
    private final Set<String> disabledProfessions;

    @Inject
    public CharacterCreationService(final ServerObjectService serverObjectService,
                                    final ProfessionDefaultsService professionDefaultsService,
                                    final NameService nameService,
                                    final ObjectTemplateService templateService,
                                    final StartingLocations startingLocations,
                                    final NewbieTutorialService newbieTutorialService,
                                    final AllowBaldService allowBaldService,
                                    final HairStylesService hairStylesService,
                                    final GameChatService chatService,
                                    final BiographyService biographyService,
                                    final GameServerState gameServerState,
                                    final BactaConfiguration bactaConfiguration) {

        this.serverObjectService = serverObjectService;
        this.professionDefaultsService = professionDefaultsService;
        this.templateService = templateService;
        this.startingLocations = startingLocations;
        this.newbieTutorialService = newbieTutorialService;
        this.chatService = chatService;
        this.allowBaldService = allowBaldService;
        this.biographyService = biographyService;
        this.gameServerState = gameServerState;
        this.nameService = nameService;
        this.hairStylesService = hairStylesService;

        this.disabledProfessions = new HashSet<>(bactaConfiguration.getStringCollection(
                CONFIG_SECTION, "disabledProfession"));

        this.defaultProfession = bactaConfiguration.getStringWithDefault(
                CONFIG_SECTION, "defaultProfession", "crafting_artisan");

        this.pendingCreations = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    //TODO: Remove the return type when account service is removed.
    public CharacterInfo createCharacter(final SoeUdpConnection connection, final ClientCreateCharacter createMessage) {
        final int bactaId = connection.getBactaId();
        final String username = connection.getAccountUsername();

        final String serverTemplateName = createMessage.getTemplateName();

        final String speciesGender = Files.getNameWithoutExtension(serverTemplateName);
        final Gender gender = Gender.fromSpeciesGender(speciesGender);
        final Race race = Race.fromSpeciesGender(speciesGender);

        final String firstName = StringUtil.getFirstWord(createMessage.getCharacterName()).toLowerCase();

        String result = nameService.validateName(NameService.PLAYER, bactaId, createMessage.getCharacterName(), race, gender);

        if (result.equals(NameService.NAME_DECLINED_DEVELOPER) && firstName.equalsIgnoreCase(username)) {
            result = NameService.NAME_APPROVED;
        }

        if (!result.equals(NameService.NAME_APPROVED)) {
            ClientCreateCharacterFailed failed = new ClientCreateCharacterFailed(createMessage.getCharacterName(), result);
            connection.sendMessage(failed);
            return null;
        }

        final ServerCreatureObjectTemplate objectTemplate = templateService.getObjectTemplate(createMessage.getTemplateName());

        if (objectTemplate == null) {

            LOGGER.error("Account <{}> attempted to create a player with invalid player template <{}>.",
                    username,
                    createMessage.getTemplateName());

            connection.sendMessage(new ClientCreateCharacterFailed(createMessage.getCharacterName(), NameService.NAME_DECLINED_NO_TEMPLATE));
            return null;
        }

        if (pendingCreations.getIfPresent(firstName) != null) {
            ClientCreateCharacterFailed failed = new ClientCreateCharacterFailed(createMessage.getCharacterName(), NameService.NAME_DECLINED_IN_USE);
            connection.sendMessage(failed);
            return null;
        }

        pendingCreations.put(firstName, bactaId);

        final CreatureObject newCharacterObject = serverObjectService.createObject(objectTemplate.getResourceName());

        if (newCharacterObject == null) {

            LOGGER.error("Account <{}> attempted to create a player but the object service returned null <{}>.",
                    username,
                    createMessage.getTemplateName());

            connection.sendMessage(new ClientCreateCharacterFailed(createMessage.getCharacterName(), NameService.NAME_DECLINED_CANT_CREATE_AVATAR));
            return null;
        }

        newCharacterObject.setObjectName(createMessage.getCharacterName());
        newCharacterObject.setOwnerId(newCharacterObject.getNetworkId());
        newCharacterObject.setPlayerControlled(true);

        StartingLocations.StartingLocationInfo startingLocationInfo = startingLocations.getStartingLocationInfo(createMessage.getStartingLocation());

        final Transform transform = new Transform();
        if (createMessage.isUseNewbieTutorial()) {
            newbieTutorialService.setupCharacterForTutorial(newCharacterObject);
            final Vector newbieTutorialLocation = newbieTutorialService.getTutorialLocation();
            transform.setPositionInParentSpace(newbieTutorialLocation);
        } else {
            newbieTutorialService.setupCharacterToSkipTutorial(newCharacterObject);

            //TODO: Position should be a random point within the radius of these coordinates.
            final Vector position = new Vector(
                    startingLocationInfo.getX(),
                    startingLocationInfo.getY(),
                    startingLocationInfo.getZ());

            transform.setPositionInParentSpace(position);
            transform.yaw(startingLocationInfo.getHeading());
        }

        newCharacterObject.setTransformObjectToParent(transform);

        final CollisionProperty collision = newCharacterObject.getCollisionProperty();
        if (collision != null) {
            collision.setPlayerControlled(true);
        }

        float scaleFactor = createMessage.getScaleFactor();
        final SharedObjectTemplate tmpl = newCharacterObject.getSharedTemplate();

        if (tmpl != null) {
            final float scaleMax = tmpl.getScaleMax();
            final float scaleMin = tmpl.getScaleMin();

            scaleFactor = Math.min(scaleFactor, scaleMax);
            scaleFactor = Math.max(scaleFactor, scaleMin);
        }

        newCharacterObject.setScaleFactor(scaleFactor);
        newCharacterObject.setScale(Vector.XYZ111.multiply(scaleFactor));

        final TangibleObject tangibleObject = TangibleObject.asTangibleObject(newCharacterObject);

        if (tangibleObject != null)
            tangibleObject.setAppearanceData(createMessage.getAppearanceData());

        final String hairStyleTemplate;

        //Validate the hair style. If it is invalid, then use the default hair template.
        if (!isValidHairSelection(createMessage.getHairTemplateName(), speciesGender)) {
            hairStyleTemplate = hairStylesService.getDefaultHairStyle(speciesGender);

            LOGGER.error("{} used an invalid hair style {} for species/gender {}.",
                    username, createMessage.getHairTemplateName(), speciesGender);
        } else {
            hairStyleTemplate = createMessage.getHairTemplateName();
        }

        // hair equip hack - lives on
        if (!hairStyleTemplate.isEmpty()) {
            final ServerObject hair = serverObjectService.createObject(createMessage.getHairTemplateName(), newCharacterObject);
            assert hair != null : String.format("Could not create hair %s\n", createMessage.getHairTemplateName());

            final TangibleObject tangibleHair = TangibleObject.asTangibleObject(hair);

            assert tangibleHair != null : "Hair is not tangible, wtf.  Can't customize it.  (among other things, probably)...";

            tangibleHair.setAppearanceData(createMessage.getHairAppearanceData());
        }

        if (!createMessage.getProfession().isEmpty()) {
            setupPlayer(newCharacterObject, createMessage.getProfession(), createMessage.isJedi());
        }

        if (!createMessage.getBiography().isEmpty()) {
            biographyService.setBiography(newCharacterObject.getNetworkId(), createMessage.getBiography());
        }

        final PlayerObject play = serverObjectService.createObject("object/player/player.iff", newCharacterObject);

        assert play != null : String.format("%d unable to create player object for new character %s", bactaId, newCharacterObject.getNetworkId());

        play.setStationId(bactaId);
        play.setBornDate((int) DateTime.now().getMillis());
        play.setSkillTemplate(createMessage.getSkillTemplate(), true);
        play.setWorkingSkill(createMessage.getWorkingSkill(), true);

        //TODO: Appearance inventory
        // Setup initial A-Tab inventory.
//        SlottedContainer container = newCharacterObject.getSlottedContainerProperty();
//        if(container != null) {
//            int slot = slotIdManager.findSlotId(SlotNames.appearance);
//            //Container::ContainerErrorCode tmp;
//            if(slot != SlotId.INVALID) {
//                Container itemId = container.getObjectInSlot(slot, tmp);
//                Object appearanceInventory = itemId.getObject();
//
//                if(appearanceInventory == null)  {
//                    appearanceInventory = objectService.createObject(s_appearanceTemplate, newCharacterObject);
//                    assert appearanceInventory != null : "Could not create an appearance inventory for the player who lost theirs";
//                }
//            }
//        }


        newCharacterObject.setSceneIdOnThisAndContents(startingLocationInfo.getPlanet());

        // Persist object (Done in Object Manager)

        //Post character setup.
        final CharacterInfo info = new CharacterInfo(
                newCharacterObject.getAssignedObjectName(),
                SOECRC32.hashCode(newCharacterObject.getObjectTemplateName()),
                newCharacterObject.getNetworkId(),
                gameServerState.getClusterId(),
                CharacterInfo.Type.NORMAL,
                false
        );

        nameService.addPlayerName(firstName);

        connection.sendMessage(new ClientCreateCharacterSuccess(newCharacterObject.getNetworkId()));

        pendingCreations.invalidate(firstName);

        final ChatDestroyAvatar chatDestroyAvatar = new ChatDestroyAvatar(firstName);
        chatService.sendToChatServer(chatDestroyAvatar);

        return info;
    }

    public void setupPlayer(final CreatureObject newCharacterObject, String profession, final boolean jedi) {
        if (this.disabledProfessions.contains(profession))
            profession = this.defaultProfession;

        final String sharedTemplateName = newCharacterObject.getSharedTemplate().getResourceName();
        final ProfessionInfo professionInfo = professionDefaultsService.getDefaults(profession);

        createStartingEquipment(newCharacterObject, sharedTemplateName, professionInfo);

        //Apply Profession Mods
        //Apply Racial Mods
        //Validate scale factor based on template
        //Create ghost
        //Create bank
        //Create datapad
        //Create missionBag
        //Create inventory
        //Create appearanceInventory
    }

    /**
     * Validates that the selected hair style belongs to the selected species gender. If no hair style is selected, then
     * checks to make sure that the character may be bald. SOE didn't perform this check, but we learned at SWGEmu
     * that it could easily be hacked to give hair to species that shouldn't have them.
     *
     * @param hairStyleTemplate The template of the hair.
     * @param speciesGender     The species gender string. i.e. human_male
     * @return
     */
    private boolean isValidHairSelection(final String hairStyleTemplate, final String speciesGender) {
        if (hairStyleTemplate.isEmpty())
            return allowBaldService.isAllowedBald(speciesGender);

        return hairStylesService.isValidForPlayerTemplate(speciesGender, hairStyleTemplate);
    }

    private boolean validateScale(final String sharedObjectTemplate, final CreatureObject creatureObject) {
        return false;
    }

    private void applyProfessionMods(final CreatureObject creatureObject) {

    }

    private void applyRacialMods(final CreatureObject creatureObject) {

    }

    /**
     * Creates all the starting equipment based on the player's profession selection. This doesn't include inventory
     * items. It's mainly just the clothes that the player sees on the creation screen. The inventory items will be
     * created post tutorial.
     *
     * @param creatureObject     The creature who will own the items.
     * @param sharedTemplateName The template of the creature.
     * @param professionInfo     The profession of the creature.
     */
    private void createStartingEquipment(final CreatureObject creatureObject, final String sharedTemplateName, final ProfessionInfo professionInfo) {
        if (professionInfo != null) {
            final List<EquipmentInfo> equipmentList = professionInfo.getEquipmentForTemplate(sharedTemplateName);

            //TODO: Use arrangementIndex.
            for (final EquipmentInfo equipmentInfo : equipmentList) {
                serverObjectService.createObject(
                        equipmentInfo.getServerTemplateName(),
                        creatureObject);
            }
        }
    }
}
