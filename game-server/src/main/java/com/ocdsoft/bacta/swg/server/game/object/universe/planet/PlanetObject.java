package com.ocdsoft.bacta.swg.server.game.object.universe.planet;

import com.google.inject.Inject;
import com.ocdsoft.bacta.swg.server.game.message.MapLocation;
import com.ocdsoft.bacta.swg.server.game.message.planet.CollectionServerFirstSet;
import com.ocdsoft.bacta.swg.server.game.object.template.server.ServerObjectTemplate;
import com.ocdsoft.bacta.swg.server.game.object.universe.UniverseObject;
import com.ocdsoft.bacta.swg.server.game.planet.MapLocationType;
import com.ocdsoft.bacta.swg.shared.container.SlotIdManager;
import com.ocdsoft.bacta.swg.shared.template.ObjectTemplateList;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by crush on 5/8/2016.
 * <p>
 * A pool of resource found on a planet.
 */
public class PlanetObject extends UniverseObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanetObject.class);

    @Getter
    private final String planetName;
//
//    //Travel Locations
//    //private List<TravelPoint> travelPointList;
//
//    //Weather
//    private int weatherIndex;
//    private float windVelocityX;
//    private float windVelocityY;
//    private float windVelocityZ;
//
    //Planetary map support
//    private final TLongObjectMap<MapLocation> mapLocationMapStatic; //static stuff like buildings and cities
//    private final TLongObjectMap<MapLocation> mapLocationMapDynamic; //dynamic stuff like camps
//    private final TLongObjectMap<MapLocation> mapLocationMapPersist; //semi-permanent stuff like player structures, vendors, etc.

    private final List<MapLocation> mapLocationListStatic;
    private final List<MapLocation> mapLocationListDynamic;
    private final List<MapLocation> mapLocationListPersist;

    private final AtomicInteger mapLocationListVersionStatic;
    private final AtomicInteger mapLocationListVersionDynamic;
    private final AtomicInteger mapLocationListVersionPersist;


    private final CollectionServerFirstSet collectionServerFirst;
    private final AtomicInteger collectionServerFirstUpdateNumber;



    @Inject
    public PlanetObject(final String planetName,
                        final ObjectTemplateList objectTemplateList,
                        final SlotIdManager slotIdManager,
                        final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);

        this.planetName = planetName;

        this.collectionServerFirst = new CollectionServerFirstSet();
        this.collectionServerFirstUpdateNumber = new AtomicInteger(0);

        this.mapLocationListStatic = new ArrayList<>(1000);
        this.mapLocationListDynamic = new ArrayList<>(1000);
        this.mapLocationListPersist = new ArrayList<>(1000);

        this.mapLocationListVersionStatic = new AtomicInteger(0);
        this.mapLocationListVersionDynamic = new AtomicInteger(0);
        this.mapLocationListVersionPersist = new AtomicInteger(0);
    }

    public boolean addMapLocation(final MapLocation location, final int mapLocationType, final boolean enforceLocationCountLimits) {
        LOGGER.error("Not implemented");
        return false;
    }

    public boolean removeMapLocation(final long networkId) {
        LOGGER.error("Not implemented");
        return false;
    }

    public boolean removeMapLocation(final long networkId, final int mapLocationType) {
        LOGGER.error("Not implemented");
        return false;
    }

    public List<MapLocation> getMapLocations(final MapLocationType mapLocationType) {
        switch (mapLocationType) {
            case STATIC:
                return Collections.unmodifiableList(this.mapLocationListStatic);
            case DYNAMIC:
                return Collections.unmodifiableList(this.mapLocationListDynamic);
            case PERSIST:
                return Collections.unmodifiableList(this.mapLocationListPersist);
            default:
                LOGGER.error("Unknown map location type");
                return null;
        }
    }

    public int getMapLocationVersionForType(final MapLocationType mapLocationType) {
        switch (mapLocationType) {
            case STATIC:
                return mapLocationListVersionStatic.get();
            case DYNAMIC:
                return mapLocationListVersionDynamic.get();
            case PERSIST:
                return mapLocationListVersionPersist.get();
            default:
                LOGGER.error("Unknown map location type");
                return 0;
        }
    }

}
