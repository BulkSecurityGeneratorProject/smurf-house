package com.smurfhouse.repository.search;

import com.smurfhouse.domain.GroupSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the GroupSearch entity.
 */
public interface GroupSearchSearchRepository extends ElasticsearchRepository<GroupSearch, Long> {
}
