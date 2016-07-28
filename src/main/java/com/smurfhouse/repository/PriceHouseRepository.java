package com.smurfhouse.repository;

import com.smurfhouse.domain.PriceHouse;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PriceHouse entity.
 */
@SuppressWarnings("unused")
public interface PriceHouseRepository extends JpaRepository<PriceHouse,Long> {

}
