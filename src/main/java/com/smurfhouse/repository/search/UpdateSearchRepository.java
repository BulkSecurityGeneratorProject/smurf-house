package com.smurfhouse.repository.search;

import com.smurfhouse.domain.Update;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Update entity.
 */
public interface UpdateSearchRepository extends ElasticsearchRepository<Update, Long> {
}
