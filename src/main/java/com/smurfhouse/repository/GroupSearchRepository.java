package com.smurfhouse.repository;

import com.smurfhouse.domain.GroupSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the GroupSearch entity.
 */
@SuppressWarnings("unused")
public interface GroupSearchRepository extends JpaRepository<GroupSearch,Long> {

    @Query("select groupSearch from GroupSearch groupSearch where groupSearch.activated=true")
    List<GroupSearch> findAllActivated();
}
