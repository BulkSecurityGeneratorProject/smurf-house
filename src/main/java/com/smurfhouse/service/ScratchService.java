package com.smurfhouse.service;

import com.codahale.metrics.annotation.Timed;
import com.smurfhouse.domain.GroupSearch;
import com.smurfhouse.domain.House;
import com.smurfhouse.domain.PriceHouse;
import com.smurfhouse.repository.GroupSearchRepository;
import com.smurfhouse.repository.HouseRepository;
import com.smurfhouse.repository.PriceHouseRepository;
import com.smurfhouse.scratch.ManagerIdealista;
import com.smurfhouse.scratch.model.ScratchHouse;
import com.smurfhouse.web.rest.dto.StatsSincronyzeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by fmunozse on 31/7/16.
 */
@Service
@Transactional
public class ScratchService {

    private final Logger log = LoggerFactory.getLogger(ScratchService.class);

    @Inject
    HouseRepository houseRepository;

    @Inject
    GroupSearchRepository groupSearchRepository;

    @Inject
    PriceHouseRepository priceHouseRepository;

    @Timed
    public StatsSincronyzeDTO synchronizeAll () {

        StatsSincronyzeDTO totalStats = new StatsSincronyzeDTO ();

        List<GroupSearch> groupSearches = groupSearchRepository.findAllActivated();
        for (GroupSearch groupsearch: groupSearches) {

            StatsSincronyzeDTO stats = synchronizeByGroupSearch(groupsearch);
            totalStats.houseEnded += stats.houseEnded;
            totalStats.houseAdded += stats.houseAdded;
            totalStats.houseNewPrice += stats.houseNewPrice;
        }

        return totalStats;
    }

    @Timed
    public StatsSincronyzeDTO synchronizeByGroupSearch (Long id) {
        return synchronizeByGroupSearch (groupSearchRepository.findOne(id));
    }


    public StatsSincronyzeDTO synchronizeByGroupSearch (GroupSearch groupsearch) {
        ManagerIdealista managerIdealista = new ManagerIdealista();
        StatsSincronyzeDTO stats = new StatsSincronyzeDTO ();

        log.info("Process groupSearch [{}] {} : {} ", groupsearch.getId(), groupsearch.getTitle(), groupsearch.getUrl());

        //Prepare map of Local Series -  externalId, Episodes  (in case duplicates, just take the first
        // and remove the second just in prune duplicates)
        Map<String, House> mHouses =
            groupsearch.getHouses().stream()
                .filter(episode -> episode.getKey() != null && episode.getEndDate() == null )
                .collect(Collectors.toMap(com.smurfhouse.domain.House::getKey,
                    Function.identity(),
                    (house1, house2) ->
                    {
                        houseRepository.delete(house2);
                        return house1;
                    }
                    )
                );

        log.trace("Houses in ddbb : {} ", mHouses);

        List<ScratchHouse> scratchHouses = managerIdealista.getAllHouse(groupsearch.getUrl(), new Locale("es", "ES"));

        //Check to evict that scratch dont works
        if (scratchHouses == null || scratchHouses.size()==0) {
            log.info("Not returned house doing scratching, could be issue, therefore, skipping the sync for groupSearch [{}] {} ",
                groupsearch.getId(), groupsearch.getTitle());
            return stats;
        }

        for (ScratchHouse scratchHouse : scratchHouses) {
            String key = generateKey(scratchHouse);
            log.info("Checking scratchHouse : {} - {}. Key: {}", scratchHouse.getTitle() , scratchHouse.getDetails() , key);

            if (mHouses.containsKey(key)) {
                //House scratched is already in the system.

                //Check if price has changed.
                BigDecimal actualPrice = scratchHouse.getPrice();
                House house = mHouses.get(key);
                BigDecimal ddbbPrice = house.getPrice();
                if (ddbbPrice.compareTo(actualPrice) != 0) {
                    //Price has change
                    //Update price and add new entry to history
                    house.setPrice(actualPrice);
                    PriceHouse priceHouse = createPriceHouse(actualPrice,house);
                    house.getPriceHouses().add(priceHouse);

                    houseRepository.save(house);
                    stats.houseNewPrice++;
                    //priceHouseRepository.save(priceHouse);
                    log.info("House {}-{} has new price {} ", house.getId(), house.getTitle(), actualPrice);
                }

                // remove of mHouse to evict the phase of prune.
                mHouses.remove(key);

            } else {
                // New house
                House house = createHouseFromScratchHouse (key,scratchHouse, groupsearch);
                PriceHouse priceHouse = createPriceHouse(house.getPrice(),house);

                Set prices = new HashSet<PriceHouse>();
                prices.add(priceHouse);
                house.setPriceHouses(prices);

                houseRepository.save(house);
                stats.houseAdded ++ ;
                log.info("New House added {}-{} with price: :  ", house.getId(), house.getTitle(), house.getPrice());
                log.debug("    House added {} :  ",house.toString());

            }
        }

        //phase of purge orphans (when in tvDB has been removed, also need to remove in the local ddbb)
        for (House house : mHouses.values()){
            house.setEndDate(LocalDate.now());
            houseRepository.save(house);
            stats.houseEnded++;
        }

        return stats;
    }

    private PriceHouse createPriceHouse (BigDecimal price, House house) {
        PriceHouse priceHouse = new PriceHouse();
        priceHouse.setPrice(price);
        priceHouse.setHouse(house);
        priceHouse.setWhenChanged(LocalDate.now());

        return priceHouse;
    }

    private House createHouseFromScratchHouse (String key, ScratchHouse scratchHouse, GroupSearch groupSearch) {

        House house = new House();

        house.setTitle(scratchHouse.getTitle());
        house.setPrice(scratchHouse.getPrice());
        house.setDetails(scratchHouse.getDetails());
        house.setElevator(scratchHouse.getHasElevator());
        house.setExternalLink(scratchHouse.getTitle()); //TODO check what we can do it.
        house.setFacingOutside(scratchHouse.getFacingOutside());
        house.setFloor(scratchHouse.getFloor());
        house.setGarage(scratchHouse.getHasGarage());
        house.setGroupSearch(groupSearch);
        house.setKey(key);
        house.setMeters(scratchHouse.getMeters());
        house.setNumrooms(scratchHouse.getNumberRooms());
        house.setStartDate(LocalDate.now());

        return house;
    }

    private String generateKey (ScratchHouse scratchHouse) {
        StringJoiner sj = new StringJoiner(":");
        sj.add(scratchHouse.getTitle());
        sj.add(String.valueOf(scratchHouse.getFacingOutside()));
        sj.add(scratchHouse.getFloor());
        sj.add(String.valueOf(scratchHouse.getHasElevator()));
        sj.add(String.valueOf(scratchHouse.getHasGarage()));
        sj.add(String.valueOf(scratchHouse.getMeters()));
        sj.add(String.valueOf(scratchHouse.getNumberRooms()));

        return sj.toString();
    }

}
