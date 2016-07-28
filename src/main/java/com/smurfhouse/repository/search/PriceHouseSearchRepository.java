package com.smurfhouse.repository.search;

import com.smurfhouse.domain.PriceHouse;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the PriceHouse entity.
 */
public interface PriceHouseSearchRepository extends ElasticsearchRepository<PriceHouse, Long> {
}
