package com.smurfhouse.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.smurfhouse.domain.Update;
import com.smurfhouse.repository.UpdateRepository;
import com.smurfhouse.repository.search.UpdateSearchRepository;
import com.smurfhouse.web.rest.util.HeaderUtil;
import com.smurfhouse.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing Update.
 */
@RestController
@RequestMapping("/api")
public class UpdateResource {

    private final Logger log = LoggerFactory.getLogger(UpdateResource.class);

    @Inject
    private UpdateRepository updateRepository;

    @Inject
    private UpdateSearchRepository updateSearchRepository;


    /**
     * GET  /updates/{fromDate}/{toDate} : get a list of updates between the fromDate and toDate.
     *
     * @param fromDate the start of the time period of updates to get
     * @param toDate the end of the time period of Updates to get
     * @return the ResponseEntity with status 200 (OK) and the list of Updates in body
     */
    @RequestMapping(
        value = "/updates/{fromDate}/{toDate}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Update>> getByDates(
        @PathVariable(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @PathVariable(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate)  {

        List<Update> lUpdates = updateRepository.findAllByUpdateDateBetweenOrderByUpdateDateDesc(fromDate, toDate);

        return new ResponseEntity<>(lUpdates, HttpStatus.OK);
    }

    /**
     * POST  /updates : Create a new update.
     *
     * @param update the update to create
     * @return the ResponseEntity with status 201 (Created) and with body the new update, or with status 400 (Bad Request) if the update has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/updates",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Update> createUpdate(@Valid @RequestBody Update update) throws URISyntaxException {
        log.debug("REST request to save Update : {}", update);
        if (update.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("update", "idexists", "A new update cannot already have an ID")).body(null);
        }
        Update result = updateRepository.save(update);
        updateSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/updates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("update", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /updates : Updates an existing update.
     *
     * @param update the update to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated update,
     * or with status 400 (Bad Request) if the update is not valid,
     * or with status 500 (Internal Server Error) if the update couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/updates",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Update> updateUpdate(@Valid @RequestBody Update update) throws URISyntaxException {
        log.debug("REST request to update Update : {}", update);
        if (update.getId() == null) {
            return createUpdate(update);
        }
        Update result = updateRepository.save(update);
        updateSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("update", update.getId().toString()))
            .body(result);
    }

    /**
     * GET  /updates : get all the updates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of updates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/updates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Update>> getAllUpdates(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Updates");
        Page<Update> page = updateRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/updates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /updates/:id : get the "id" update.
     *
     * @param id the id of the update to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the update, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/updates/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Update> getUpdate(@PathVariable Long id) {
        log.debug("REST request to get Update : {}", id);
        Update update = updateRepository.findOne(id);
        return Optional.ofNullable(update)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /updates/:id : delete the "id" update.
     *
     * @param id the id of the update to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/updates/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteUpdate(@PathVariable Long id) {
        log.debug("REST request to delete Update : {}", id);
        updateRepository.delete(id);
        updateSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("update", id.toString())).build();
    }

    /**
     * SEARCH  /_search/updates?query=:query : search for the update corresponding
     * to the query.
     *
     * @param query the query of the update search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/updates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Update>> searchUpdates(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Updates for query {}", query);
        Page<Update> page = updateSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/updates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
