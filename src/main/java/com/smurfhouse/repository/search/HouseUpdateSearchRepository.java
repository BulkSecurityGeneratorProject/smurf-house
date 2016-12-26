package com.smurfhouse.repository.search;

import com.smurfhouse.domain.HouseUpdate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the HouseUpdate entity.
 */
public interface HouseUpdateSearchRepository extends ElasticsearchRepository<HouseUpdate, Long> {
}
