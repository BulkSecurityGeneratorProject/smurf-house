package com.smurfhouse.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.smurfhouse.domain.House;
import com.smurfhouse.repository.HouseRepository;
import com.smurfhouse.repository.search.HouseSearchRepository;
import com.smurfhouse.web.rest.util.HeaderUtil;
import com.smurfhouse.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing House.
 */
@RestController
@RequestMapping("/api")
public class HouseResource {

    private final Logger log = LoggerFactory.getLogger(HouseResource.class);
        
    @Inject
    private HouseRepository houseRepository;
    
    @Inject
    private HouseSearchRepository houseSearchRepository;
    
    /**
     * POST  /houses : Create a new house.
     *
     * @param house the house to create
     * @return the ResponseEntity with status 201 (Created) and with body the new house, or with status 400 (Bad Request) if the house has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/houses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<House> createHouse(@Valid @RequestBody House house) throws URISyntaxException {
        log.debug("REST request to save House : {}", house);
        if (house.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("house", "idexists", "A new house cannot already have an ID")).body(null);
        }
        House result = houseRepository.save(house);
        houseSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/houses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("house", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /houses : Updates an existing house.
     *
     * @param house the house to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated house,
     * or with status 400 (Bad Request) if the house is not valid,
     * or with status 500 (Internal Server Error) if the house couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/houses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<House> updateHouse(@Valid @RequestBody House house) throws URISyntaxException {
        log.debug("REST request to update House : {}", house);
        if (house.getId() == null) {
            return createHouse(house);
        }
        House result = houseRepository.save(house);
        houseSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("house", house.getId().toString()))
            .body(result);
    }

    /**
     * GET  /houses : get all the houses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of houses in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/houses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<House>> getAllHouses(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Houses");
        Page<House> page = houseRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/houses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /houses/:id : get the "id" house.
     *
     * @param id the id of the house to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the house, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/houses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<House> getHouse(@PathVariable Long id) {
        log.debug("REST request to get House : {}", id);
        House house = houseRepository.findOne(id);
        return Optional.ofNullable(house)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /houses/:id : delete the "id" house.
     *
     * @param id the id of the house to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/houses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteHouse(@PathVariable Long id) {
        log.debug("REST request to delete House : {}", id);
        houseRepository.delete(id);
        houseSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("house", id.toString())).build();
    }

    /**
     * SEARCH  /_search/houses?query=:query : search for the house corresponding
     * to the query.
     *
     * @param query the query of the house search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/houses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<House>> searchHouses(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Houses for query {}", query);
        Page<House> page = houseSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/houses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
