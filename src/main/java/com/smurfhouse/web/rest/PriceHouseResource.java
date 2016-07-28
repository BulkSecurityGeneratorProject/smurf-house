package com.smurfhouse.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.smurfhouse.domain.PriceHouse;
import com.smurfhouse.repository.PriceHouseRepository;
import com.smurfhouse.repository.search.PriceHouseSearchRepository;
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
 * REST controller for managing PriceHouse.
 */
@RestController
@RequestMapping("/api")
public class PriceHouseResource {

    private final Logger log = LoggerFactory.getLogger(PriceHouseResource.class);
        
    @Inject
    private PriceHouseRepository priceHouseRepository;
    
    @Inject
    private PriceHouseSearchRepository priceHouseSearchRepository;
    
    /**
     * POST  /price-houses : Create a new priceHouse.
     *
     * @param priceHouse the priceHouse to create
     * @return the ResponseEntity with status 201 (Created) and with body the new priceHouse, or with status 400 (Bad Request) if the priceHouse has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/price-houses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PriceHouse> createPriceHouse(@Valid @RequestBody PriceHouse priceHouse) throws URISyntaxException {
        log.debug("REST request to save PriceHouse : {}", priceHouse);
        if (priceHouse.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("priceHouse", "idexists", "A new priceHouse cannot already have an ID")).body(null);
        }
        PriceHouse result = priceHouseRepository.save(priceHouse);
        priceHouseSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/price-houses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("priceHouse", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /price-houses : Updates an existing priceHouse.
     *
     * @param priceHouse the priceHouse to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated priceHouse,
     * or with status 400 (Bad Request) if the priceHouse is not valid,
     * or with status 500 (Internal Server Error) if the priceHouse couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/price-houses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PriceHouse> updatePriceHouse(@Valid @RequestBody PriceHouse priceHouse) throws URISyntaxException {
        log.debug("REST request to update PriceHouse : {}", priceHouse);
        if (priceHouse.getId() == null) {
            return createPriceHouse(priceHouse);
        }
        PriceHouse result = priceHouseRepository.save(priceHouse);
        priceHouseSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("priceHouse", priceHouse.getId().toString()))
            .body(result);
    }

    /**
     * GET  /price-houses : get all the priceHouses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of priceHouses in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/price-houses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PriceHouse>> getAllPriceHouses(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of PriceHouses");
        Page<PriceHouse> page = priceHouseRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/price-houses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /price-houses/:id : get the "id" priceHouse.
     *
     * @param id the id of the priceHouse to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the priceHouse, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/price-houses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PriceHouse> getPriceHouse(@PathVariable Long id) {
        log.debug("REST request to get PriceHouse : {}", id);
        PriceHouse priceHouse = priceHouseRepository.findOne(id);
        return Optional.ofNullable(priceHouse)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /price-houses/:id : delete the "id" priceHouse.
     *
     * @param id the id of the priceHouse to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/price-houses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePriceHouse(@PathVariable Long id) {
        log.debug("REST request to delete PriceHouse : {}", id);
        priceHouseRepository.delete(id);
        priceHouseSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("priceHouse", id.toString())).build();
    }

    /**
     * SEARCH  /_search/price-houses?query=:query : search for the priceHouse corresponding
     * to the query.
     *
     * @param query the query of the priceHouse search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/price-houses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PriceHouse>> searchPriceHouses(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of PriceHouses for query {}", query);
        Page<PriceHouse> page = priceHouseSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/price-houses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
