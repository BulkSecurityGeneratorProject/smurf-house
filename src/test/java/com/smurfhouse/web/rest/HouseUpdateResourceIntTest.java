package com.smurfhouse.web.rest;

import com.smurfhouse.SmurfHouseApp;
import com.smurfhouse.domain.HouseUpdate;
import com.smurfhouse.repository.HouseUpdateRepository;
import com.smurfhouse.repository.search.HouseUpdateSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smurfhouse.domain.enumeration.HouseUpdateOperation;

/**
 * Test class for the HouseUpdateResource REST controller.
 *
 * @see HouseUpdateResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SmurfHouseApp.class)
@WebAppConfiguration
@IntegrationTest
public class HouseUpdateResourceIntTest {


    private static final HouseUpdateOperation DEFAULT_OPERATION = HouseUpdateOperation.NEW;
    private static final HouseUpdateOperation UPDATED_OPERATION = HouseUpdateOperation.DELETE;

    @Inject
    private HouseUpdateRepository houseUpdateRepository;

    @Inject
    private HouseUpdateSearchRepository houseUpdateSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restHouseUpdateMockMvc;

    private HouseUpdate houseUpdate;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        HouseUpdateResource houseUpdateResource = new HouseUpdateResource();
        ReflectionTestUtils.setField(houseUpdateResource, "houseUpdateSearchRepository", houseUpdateSearchRepository);
        ReflectionTestUtils.setField(houseUpdateResource, "houseUpdateRepository", houseUpdateRepository);
        this.restHouseUpdateMockMvc = MockMvcBuilders.standaloneSetup(houseUpdateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        houseUpdateSearchRepository.deleteAll();
        houseUpdate = new HouseUpdate();
        houseUpdate.setOperation(DEFAULT_OPERATION);
    }

    @Test
    @Transactional
    public void createHouseUpdate() throws Exception {
        int databaseSizeBeforeCreate = houseUpdateRepository.findAll().size();

        // Create the HouseUpdate

        restHouseUpdateMockMvc.perform(post("/api/house-updates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(houseUpdate)))
                .andExpect(status().isCreated());

        // Validate the HouseUpdate in the database
        List<HouseUpdate> houseUpdates = houseUpdateRepository.findAll();
        assertThat(houseUpdates).hasSize(databaseSizeBeforeCreate + 1);
        HouseUpdate testHouseUpdate = houseUpdates.get(houseUpdates.size() - 1);
        assertThat(testHouseUpdate.getOperation()).isEqualTo(DEFAULT_OPERATION);

        // Validate the HouseUpdate in ElasticSearch
        HouseUpdate houseUpdateEs = houseUpdateSearchRepository.findOne(testHouseUpdate.getId());
        assertThat(houseUpdateEs).isEqualToComparingFieldByField(testHouseUpdate);
    }

    @Test
    @Transactional
    public void checkOperationIsRequired() throws Exception {
        int databaseSizeBeforeTest = houseUpdateRepository.findAll().size();
        // set the field null
        houseUpdate.setOperation(null);

        // Create the HouseUpdate, which fails.

        restHouseUpdateMockMvc.perform(post("/api/house-updates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(houseUpdate)))
                .andExpect(status().isBadRequest());

        List<HouseUpdate> houseUpdates = houseUpdateRepository.findAll();
        assertThat(houseUpdates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllHouseUpdates() throws Exception {
        // Initialize the database
        houseUpdateRepository.saveAndFlush(houseUpdate);

        // Get all the houseUpdates
        restHouseUpdateMockMvc.perform(get("/api/house-updates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(houseUpdate.getId().intValue())))
                .andExpect(jsonPath("$.[*].operation").value(hasItem(DEFAULT_OPERATION.toString())));
    }

    @Test
    @Transactional
    public void getHouseUpdate() throws Exception {
        // Initialize the database
        houseUpdateRepository.saveAndFlush(houseUpdate);

        // Get the houseUpdate
        restHouseUpdateMockMvc.perform(get("/api/house-updates/{id}", houseUpdate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(houseUpdate.getId().intValue()))
            .andExpect(jsonPath("$.operation").value(DEFAULT_OPERATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingHouseUpdate() throws Exception {
        // Get the houseUpdate
        restHouseUpdateMockMvc.perform(get("/api/house-updates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHouseUpdate() throws Exception {
        // Initialize the database
        houseUpdateRepository.saveAndFlush(houseUpdate);
        houseUpdateSearchRepository.save(houseUpdate);
        int databaseSizeBeforeUpdate = houseUpdateRepository.findAll().size();

        // Update the houseUpdate
        HouseUpdate updatedHouseUpdate = new HouseUpdate();
        updatedHouseUpdate.setId(houseUpdate.getId());
        updatedHouseUpdate.setOperation(UPDATED_OPERATION);

        restHouseUpdateMockMvc.perform(put("/api/house-updates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedHouseUpdate)))
                .andExpect(status().isOk());

        // Validate the HouseUpdate in the database
        List<HouseUpdate> houseUpdates = houseUpdateRepository.findAll();
        assertThat(houseUpdates).hasSize(databaseSizeBeforeUpdate);
        HouseUpdate testHouseUpdate = houseUpdates.get(houseUpdates.size() - 1);
        assertThat(testHouseUpdate.getOperation()).isEqualTo(UPDATED_OPERATION);

        // Validate the HouseUpdate in ElasticSearch
        HouseUpdate houseUpdateEs = houseUpdateSearchRepository.findOne(testHouseUpdate.getId());
        assertThat(houseUpdateEs).isEqualToComparingFieldByField(testHouseUpdate);
    }

    @Test
    @Transactional
    public void deleteHouseUpdate() throws Exception {
        // Initialize the database
        houseUpdateRepository.saveAndFlush(houseUpdate);
        houseUpdateSearchRepository.save(houseUpdate);
        int databaseSizeBeforeDelete = houseUpdateRepository.findAll().size();

        // Get the houseUpdate
        restHouseUpdateMockMvc.perform(delete("/api/house-updates/{id}", houseUpdate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean houseUpdateExistsInEs = houseUpdateSearchRepository.exists(houseUpdate.getId());
        assertThat(houseUpdateExistsInEs).isFalse();

        // Validate the database is empty
        List<HouseUpdate> houseUpdates = houseUpdateRepository.findAll();
        assertThat(houseUpdates).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchHouseUpdate() throws Exception {
        // Initialize the database
        houseUpdateRepository.saveAndFlush(houseUpdate);
        houseUpdateSearchRepository.save(houseUpdate);

        // Search the houseUpdate
        restHouseUpdateMockMvc.perform(get("/api/_search/house-updates?query=id:" + houseUpdate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(houseUpdate.getId().intValue())))
            .andExpect(jsonPath("$.[*].operation").value(hasItem(DEFAULT_OPERATION.toString())));
    }
}
