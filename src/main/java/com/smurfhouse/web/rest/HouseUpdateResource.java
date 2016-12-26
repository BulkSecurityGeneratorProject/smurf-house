package com.smurfhouse.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.smurfhouse.domain.HouseUpdate;
import com.smurfhouse.repository.HouseUpdateRepository;
import com.smurfhouse.repository.search.HouseUpdateSearchRepository;
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
 * REST controller for managing HouseUpdate.
 */
@RestController
@RequestMapping("/api")
public class HouseUpdateResource {

    private final Logger log = LoggerFactory.getLogger(HouseUpdateResource.class);
        
    @Inject
    private HouseUpdateRepository houseUpdateRepository;
    
    @Inject
    private HouseUpdateSearchRepository houseUpdateSearchRepository;
    
    /**
     * POST  /house-updates : Create a new houseUpdate.
     *
     * @param houseUpdate the houseUpdate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new houseUpdate, or with status 400 (Bad Request) if the houseUpdate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/house-updates",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<HouseUpdate> createHouseUpdate(@Valid @RequestBody HouseUpdate houseUpdate) throws URISyntaxException {
        log.debug("REST request to save HouseUpdate : {}", houseUpdate);
        if (houseUpdate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("houseUpdate", "idexists", "A new houseUpdate cannot already have an ID")).body(null);
        }
        HouseUpdate result = houseUpdateRepository.save(houseUpdate);
        houseUpdateSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/house-updates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("houseUpdate", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /house-updates : Updates an existing houseUpdate.
     *
     * @param houseUpdate the houseUpdate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated houseUpdate,
     * or with status 400 (Bad Request) if the houseUpdate is not valid,
     * or with status 500 (Internal Server Error) if the houseUpdate couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/house-updates",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<HouseUpdate> updateHouseUpdate(@Valid @RequestBody HouseUpdate houseUpdate) throws URISyntaxException {
        log.debug("REST request to update HouseUpdate : {}", houseUpdate);
        if (houseUpdate.getId() == null) {
            return createHouseUpdate(houseUpdate);
        }
        HouseUpdate result = houseUpdateRepository.save(houseUpdate);
        houseUpdateSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("houseUpdate", houseUpdate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /house-updates : get all the houseUpdates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of houseUpdates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/house-updates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<HouseUpdate>> getAllHouseUpdates(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of HouseUpdates");
        Page<HouseUpdate> page = houseUpdateRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/house-updates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /house-updates/:id : get the "id" houseUpdate.
     *
     * @param id the id of the houseUpdate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the houseUpdate, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/house-updates/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<HouseUpdate> getHouseUpdate(@PathVariable Long id) {
        log.debug("REST request to get HouseUpdate : {}", id);
        HouseUpdate houseUpdate = houseUpdateRepository.findOne(id);
        return Optional.ofNullable(houseUpdate)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /house-updates/:id : delete the "id" houseUpdate.
     *
     * @param id the id of the houseUpdate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/house-updates/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteHouseUpdate(@PathVariable Long id) {
        log.debug("REST request to delete HouseUpdate : {}", id);
        houseUpdateRepository.delete(id);
        houseUpdateSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("houseUpdate", id.toString())).build();
    }

    /**
     * SEARCH  /_search/house-updates?query=:query : search for the houseUpdate corresponding
     * to the query.
     *
     * @param query the query of the houseUpdate search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/house-updates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<HouseUpdate>> searchHouseUpdates(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of HouseUpdates for query {}", query);
        Page<HouseUpdate> page = houseUpdateSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/house-updates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
