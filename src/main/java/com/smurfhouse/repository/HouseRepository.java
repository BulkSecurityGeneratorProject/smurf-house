package com.smurfhouse.repository;

import com.smurfhouse.domain.House;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the House entity.
 */
@SuppressWarnings("unused")
public interface HouseRepository extends JpaRepository<House,Long> {

}
