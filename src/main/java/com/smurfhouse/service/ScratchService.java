package com.smurfhouse.service;

import com.codahale.metrics.annotation.Timed;
import com.smurfhouse.domain.*;
import com.smurfhouse.domain.enumeration.HouseUpdateOperation;
import com.smurfhouse.repository.*;
import com.smurfhouse.scratch.ManagerIdealista;
import com.smurfhouse.scratch.model.ScratchHouse;
import com.smurfhouse.web.rest.dto.StatsSincronyzeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Inject
    UpdateRepository updateRepository;

    @Inject
    HouseUpdateRepository houseUpdateRepository;

    @Scheduled(cron = "0 0 22 * * ?")
    //@Scheduled(fixedRate = 60000)
    @Timed
    public void synchronizeAll () {

        StatsSincronyzeDTO totalStats = new StatsSincronyzeDTO ();

        List<GroupSearch> groupSearches = groupSearchRepository.findAllActivated();
        for (GroupSearch groupsearch: groupSearches) {

            StatsSincronyzeDTO stats = synchronizeByGroupSearch(groupsearch);
            totalStats.houseEnded += stats.houseEnded;
            totalStats.houseAdded += stats.houseAdded;
            totalStats.houseNewPrice += stats.houseNewPrice;
        }

        log.info("synchronizeAll - total stats {} ", totalStats);
    }

    @Timed
    public StatsSincronyzeDTO synchronizeByGroupSearch (Long id) {
        return synchronizeByGroupSearch (groupSearchRepository.findOne(id));
    }


    public StatsSincronyzeDTO synchronizeByGroupSearch (GroupSearch groupsearch) {

        //Scratch manager
        ManagerIdealista managerIdealista = new ManagerIdealista();

        //Stats
        StatsSincronyzeDTO stats = new StatsSincronyzeDTO ();

        //Prepare update entity tracking
        Update update = new Update();
        update.setUpdateDate(LocalDate.now());
        update.setGroupSearch(groupsearch);
        update.setNews(0);
        update.setDeletes(0);
        update.setPriceUpdates(0);
        updateRepository.save(update);


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
                        //houseRepository.delete(house2);
                        return house1;
                    }
                    )
                );

        log.debug("Houses in ddbb : {} ", mHouses);
        log.debug("Houses in ddbb : {} ", groupsearch.getHouses());

        List<ScratchHouse> scratchHouses = managerIdealista.getAllHouse(groupsearch.getUrl(), new Locale("es", "ES"));

        //Check to evict that scratch dont works
        if (scratchHouses == null || scratchHouses.size()==0) {
            log.info("Not returned house doing scratching, could be issue, therefore, skipping the sync for groupSearch [{}] {} ",
                groupsearch.getId(), groupsearch.getTitle());

            update.setError(true);
            updateRepository.save(update);
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

                    //UpdateHouse tracking
                    HouseUpdate houseUpdate = new HouseUpdate();
                    houseUpdate.setUpdate(update);
                    houseUpdate.setHouse(house);
                    houseUpdate.setOperation(HouseUpdateOperation.PRICEUPDATE);
                    houseUpdateRepository.save(houseUpdate);
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

                //UpdateHouse tracking
                HouseUpdate houseUpdate = new HouseUpdate();
                houseUpdate.setUpdate(update);
                houseUpdate.setHouse(house);
                houseUpdate.setOperation(HouseUpdateOperation.NEW);
                houseUpdateRepository.save(houseUpdate);

            }
        }

        //phase of purge orphans (when search idalista has been ended, also need to end in the local ddbb)
        for (House house : mHouses.values()){
            house.setEndDate(LocalDate.now());
            houseRepository.save(house);
            stats.houseEnded++;

            log.info("House ended {}-{} with price: :  ", house.getId(), house.getTitle(), house.getPrice());
            log.debug("    House ended {} :  ",house.toString());

            //UpdateHouse tracking
            HouseUpdate houseUpdate = new HouseUpdate();
            houseUpdate.setUpdate(update);
            houseUpdate.setHouse(house);
            houseUpdate.setOperation(HouseUpdateOperation.DELETE);
            houseUpdateRepository.save(houseUpdate);
        }

        //Update tracking
        update.setNews(stats.houseAdded);
        update.setDeletes(stats.houseEnded);
        update.setPriceUpdates(stats.houseNewPrice);
        updateRepository.save(update);

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
        house.setExternalLink(scratchHouse.getUrl());
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
        sj.add(String.valueOf(scratchHouse.getFloor()));
        sj.add(String.valueOf(scratchHouse.getHasElevator()));
        sj.add(String.valueOf(scratchHouse.getHasGarage()));
        sj.add(String.valueOf(scratchHouse.getMeters()));
        sj.add(String.valueOf(scratchHouse.getNumberRooms()));

        return sj.toString();
    }

}
