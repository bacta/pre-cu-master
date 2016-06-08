package com.ocdsoft.bacta.swg.server.game.name.generator;

import com.google.inject.Singleton;
import com.ocdsoft.bacta.swg.server.game.util.Gender;
import com.ocdsoft.bacta.swg.server.game.util.Race;

/**
 * Created by Kyle on 8/17/2014.
 */
@Singleton
public class PlayerNameGenerator extends CreatureNameGenerator {


    @Override
    public String validateName(final String name, final Race race, final Gender gender) {

        String firstName = name.indexOf(" ") != -1 ? name.substring(0, name.indexOf(" ")) : name;

        // TODO: Name Checks
//        if (allFirstNames.contains(firstName.toLowerCase())) {
//            return NameService.NAME_DECLINED_IN_USE;
//        }

        return super.validateName(name, race, gender);
    }

    public void addPlayerName(String firstName) {
        // TODO: Name Checks
//        if (!allFirstNames.add(firstName)) {
//            throw new RuntimeException("Created a character with a duplicate first name");
//        }
    }
}
