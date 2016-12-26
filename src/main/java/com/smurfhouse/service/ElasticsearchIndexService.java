package com.smurfhouse.service;

import com.codahale.metrics.annotation.Timed;
import com.smurfhouse.domain.*;
import com.smurfhouse.repository.*;
import com.smurfhouse.repository.search.*;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Service
public class ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    @Inject
    private GroupSearchRepository groupSearchRepository;

    @Inject
    private GroupSearchSearchRepository groupSearchSearchRepository;

    @Inject
    private HouseRepository houseRepository;

    @Inject
    private HouseSearchRepository houseSearchRepository;

    @Inject
    private HouseUpdateRepository houseUpdateRepository;

    @Inject
    private HouseUpdateSearchRepository houseUpdateSearchRepository;

    @Inject
    private PriceHouseRepository priceHouseRepository;

    @Inject
    private PriceHouseSearchRepository priceHouseSearchRepository;

    @Inject
    private UpdateRepository updateRepository;

    @Inject
    private UpdateSearchRepository updateSearchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserSearchRepository userSearchRepository;

    @Inject
    private ElasticsearchTemplate elasticsearchTemplate;

    @Async
    @Timed
    public void reindexAll() {
        reindexForClass(GroupSearch.class, groupSearchRepository, groupSearchSearchRepository);
        reindexForClass(House.class, houseRepository, houseSearchRepository);
        reindexForClass(HouseUpdate.class, houseUpdateRepository, houseUpdateSearchRepository);
        reindexForClass(PriceHouse.class, priceHouseRepository, priceHouseSearchRepository);
        reindexForClass(Update.class, updateRepository, updateSearchRepository);
        reindexForClass(User.class, userRepository, userSearchRepository);

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    @Transactional
    @SuppressWarnings("unchecked")
    private <T> void reindexForClass(Class<T> entityClass, JpaRepository<T, Long> jpaRepository,
                                                          ElasticsearchRepository<T, Long> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (IndexAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            try {
                Method m = jpaRepository.getClass().getMethod("findAllWithEagerRelationships");
                elasticsearchRepository.save((List<T>) m.invoke(jpaRepository));
            } catch (Exception e) {
                elasticsearchRepository.save(jpaRepository.findAll());
            }
        }
        log.info("Elasticsearch: Indexed all rows for " + entityClass.getSimpleName());
    }
}
