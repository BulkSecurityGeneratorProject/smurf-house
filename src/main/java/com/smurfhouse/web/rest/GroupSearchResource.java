package com.smurfhouse.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.smurfhouse.domain.GroupSearch;
import com.smurfhouse.repository.GroupSearchRepository;
import com.smurfhouse.repository.search.GroupSearchSearchRepository;
import com.smurfhouse.service.ScratchService;
import com.smurfhouse.web.rest.dto.StatsSincronyzeDTO;
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

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing GroupSearch.
 */
@RestController
@RequestMapping("/api")
public class GroupSearchResource {

    private final Logger log = LoggerFactory.getLogger(GroupSearchResource.class);

    @Inject
    private GroupSearchRepository groupSearchRepository;

    @Inject
    private GroupSearchSearchRepository groupSearchSearchRepository;

    @Inject
    private ScratchService scratchService;

    /**
     * POST /group-searches/{id}/sync -> Updates an existing episode.
     * 	param:  id
     */
    @RequestMapping(value = "/group-searches/{id}/sync",
        method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StatsSincronyzeDTO> sync(@PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to sync for groupSearch {} " , id );

        GroupSearch groupSearch = groupSearchRepository.findOne(id);
        return Optional.ofNullable(groupSearch)
            .map(result -> new ResponseEntity<>(
                scratchService.synchronizeByGroupSearch(id),
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        /*
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert("Updated -  " + stats.toString(), "groupSearch"))
            .body(stats);
            */
    }

    /**
     * POST  /group-searches : Create a new groupSearch.
     *
     * @param groupSearch the groupSearch to create
     * @return the ResponseEntity with status 201 (Created) and with body the new groupSearch, or with status 400 (Bad Request) if the groupSearch has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/group-searches",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GroupSearch> createGroupSearch(@Valid @RequestBody GroupSearch groupSearch) throws URISyntaxException {
        log.debug("REST request to save GroupSearch : {}", groupSearch);
        if (groupSearch.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("groupSearch", "idexists", "A new groupSearch cannot already have an ID")).body(null);
        }
        GroupSearch result = groupSearchRepository.save(groupSearch);
        groupSearchSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/group-searches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("groupSearch", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /group-searches : Updates an existing groupSearch.
     *
     * @param groupSearch the groupSearch to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated groupSearch,
     * or with status 400 (Bad Request) if the groupSearch is not valid,
     * or with status 500 (Internal Server Error) if the groupSearch couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/group-searches",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GroupSearch> updateGroupSearch(@Valid @RequestBody GroupSearch groupSearch) throws URISyntaxException {
        log.debug("REST request to update GroupSearch : {}", groupSearch);
        if (groupSearch.getId() == null) {
            return createGroupSearch(groupSearch);
        }
        GroupSearch result = groupSearchRepository.save(groupSearch);
        groupSearchSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("groupSearch", groupSearch.getId().toString()))
            .body(result);
    }

    /**
     * GET  /group-searches : get all the groupSearches.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of groupSearches in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/group-searches",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<GroupSearch>> getAllGroupSearches(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of GroupSearches");
        Page<GroupSearch> page = groupSearchRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/group-searches");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /group-searches/:id : get the "id" groupSearch.
     *
     * @param id the id of the groupSearch to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the groupSearch, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/group-searches/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GroupSearch> getGroupSearch(@PathVariable Long id) {
        log.debug("REST request to get GroupSearch : {}", id);
        GroupSearch groupSearch = groupSearchRepository.findOne(id);
        return Optional.ofNullable(groupSearch)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /group-searches/:id : delete the "id" groupSearch.
     *
     * @param id the id of the groupSearch to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/group-searches/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteGroupSearch(@PathVariable Long id) {
        log.debug("REST request to delete GroupSearch : {}", id);
        groupSearchRepository.delete(id);
        groupSearchSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("groupSearch", id.toString())).build();
    }

    /**
     * SEARCH  /_search/group-searches?query=:query : search for the groupSearch corresponding
     * to the query.
     *
     * @param query the query of the groupSearch search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/group-searches",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<GroupSearch>> searchGroupSearches(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of GroupSearches for query {}", query);
        Page<GroupSearch> page = groupSearchSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/group-searches");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
