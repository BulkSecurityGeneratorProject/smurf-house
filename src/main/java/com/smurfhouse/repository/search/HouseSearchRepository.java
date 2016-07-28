package com.smurfhouse.repository.search;

import com.smurfhouse.domain.House;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the House entity.
 */
public interface HouseSearchRepository extends ElasticsearchRepository<House, Long> {
}
