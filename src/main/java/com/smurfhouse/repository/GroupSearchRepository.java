package com.smurfhouse.repository;

import com.smurfhouse.domain.GroupSearch;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the GroupSearch entity.
 */
@SuppressWarnings("unused")
public interface GroupSearchRepository extends JpaRepository<GroupSearch,Long> {

}
