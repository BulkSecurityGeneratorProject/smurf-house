package com.smurfhouse.web.rest;

import com.smurfhouse.SmurfHouseApp;
import com.smurfhouse.domain.Update;
import com.smurfhouse.repository.UpdateRepository;
import com.smurfhouse.repository.search.UpdateSearchRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the UpdateResource REST controller.
 *
 * @see UpdateResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SmurfHouseApp.class)
@WebAppConfiguration
@IntegrationTest
public class UpdateResourceIntTest {


    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_NEWS = 1;
    private static final Integer UPDATED_NEWS = 2;

    private static final Integer DEFAULT_DELETES = 1;
    private static final Integer UPDATED_DELETES = 2;

    private static final Integer DEFAULT_PRICE_UPDATES = 1;
    private static final Integer UPDATED_PRICE_UPDATES = 2;

    @Inject
    private UpdateRepository updateRepository;

    @Inject
    private UpdateSearchRepository updateSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restUpdateMockMvc;

    private Update update;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UpdateResource updateResource = new UpdateResource();
        ReflectionTestUtils.setField(updateResource, "updateSearchRepository", updateSearchRepository);
        ReflectionTestUtils.setField(updateResource, "updateRepository", updateRepository);
        this.restUpdateMockMvc = MockMvcBuilders.standaloneSetup(updateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        updateSearchRepository.deleteAll();
        update = new Update();
        update.setUpdateDate(DEFAULT_UPDATE_DATE);
        update.setNews(DEFAULT_NEWS);
        update.setDeletes(DEFAULT_DELETES);
        update.setPriceUpdates(DEFAULT_PRICE_UPDATES);
    }

    @Test
    @Transactional
    public void createUpdate() throws Exception {
        int databaseSizeBeforeCreate = updateRepository.findAll().size();

        // Create the Update

        restUpdateMockMvc.perform(post("/api/updates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(update)))
                .andExpect(status().isCreated());

        // Validate the Update in the database
        List<Update> updates = updateRepository.findAll();
        assertThat(updates).hasSize(databaseSizeBeforeCreate + 1);
        Update testUpdate = updates.get(updates.size() - 1);
        assertThat(testUpdate.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
        assertThat(testUpdate.getNews()).isEqualTo(DEFAULT_NEWS);
        assertThat(testUpdate.getDeletes()).isEqualTo(DEFAULT_DELETES);
        assertThat(testUpdate.getPriceUpdates()).isEqualTo(DEFAULT_PRICE_UPDATES);

        // Validate the Update in ElasticSearch
        Update updateEs = updateSearchRepository.findOne(testUpdate.getId());
        assertThat(updateEs).isEqualToComparingFieldByField(testUpdate);
    }

    @Test
    @Transactional
    public void checkUpdateDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = updateRepository.findAll().size();
        // set the field null
        update.setUpdateDate(null);

        // Create the Update, which fails.

        restUpdateMockMvc.perform(post("/api/updates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(update)))
                .andExpect(status().isBadRequest());

        List<Update> updates = updateRepository.findAll();
        assertThat(updates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUpdates() throws Exception {
        // Initialize the database
        updateRepository.saveAndFlush(update);

        // Get all the updates
        restUpdateMockMvc.perform(get("/api/updates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(update.getId().intValue())))
                .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
                .andExpect(jsonPath("$.[*].news").value(hasItem(DEFAULT_NEWS)))
                .andExpect(jsonPath("$.[*].deletes").value(hasItem(DEFAULT_DELETES)))
                .andExpect(jsonPath("$.[*].priceUpdates").value(hasItem(DEFAULT_PRICE_UPDATES)));
    }

    @Test
    @Transactional
    public void getUpdate() throws Exception {
        // Initialize the database
        updateRepository.saveAndFlush(update);

        // Get the update
        restUpdateMockMvc.perform(get("/api/updates/{id}", update.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(update.getId().intValue()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.news").value(DEFAULT_NEWS))
            .andExpect(jsonPath("$.deletes").value(DEFAULT_DELETES))
            .andExpect(jsonPath("$.priceUpdates").value(DEFAULT_PRICE_UPDATES));
    }

    @Test
    @Transactional
    public void getNonExistingUpdate() throws Exception {
        // Get the update
        restUpdateMockMvc.perform(get("/api/updates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUpdate() throws Exception {
        // Initialize the database
        updateRepository.saveAndFlush(update);
        updateSearchRepository.save(update);
        int databaseSizeBeforeUpdate = updateRepository.findAll().size();

        // Update the update
        Update updatedUpdate = new Update();
        updatedUpdate.setId(update.getId());
        updatedUpdate.setUpdateDate(UPDATED_UPDATE_DATE);
        updatedUpdate.setNews(UPDATED_NEWS);
        updatedUpdate.setDeletes(UPDATED_DELETES);
        updatedUpdate.setPriceUpdates(UPDATED_PRICE_UPDATES);

        restUpdateMockMvc.perform(put("/api/updates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedUpdate)))
                .andExpect(status().isOk());

        // Validate the Update in the database
        List<Update> updates = updateRepository.findAll();
        assertThat(updates).hasSize(databaseSizeBeforeUpdate);
        Update testUpdate = updates.get(updates.size() - 1);
        assertThat(testUpdate.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testUpdate.getNews()).isEqualTo(UPDATED_NEWS);
        assertThat(testUpdate.getDeletes()).isEqualTo(UPDATED_DELETES);
        assertThat(testUpdate.getPriceUpdates()).isEqualTo(UPDATED_PRICE_UPDATES);

        // Validate the Update in ElasticSearch
        Update updateEs = updateSearchRepository.findOne(testUpdate.getId());
        assertThat(updateEs).isEqualToComparingFieldByField(testUpdate);
    }

    @Test
    @Transactional
    public void deleteUpdate() throws Exception {
        // Initialize the database
        updateRepository.saveAndFlush(update);
        updateSearchRepository.save(update);
        int databaseSizeBeforeDelete = updateRepository.findAll().size();

        // Get the update
        restUpdateMockMvc.perform(delete("/api/updates/{id}", update.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean updateExistsInEs = updateSearchRepository.exists(update.getId());
        assertThat(updateExistsInEs).isFalse();

        // Validate the database is empty
        List<Update> updates = updateRepository.findAll();
        assertThat(updates).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUpdate() throws Exception {
        // Initialize the database
        updateRepository.saveAndFlush(update);
        updateSearchRepository.save(update);

        // Search the update
        restUpdateMockMvc.perform(get("/api/_search/updates?query=id:" + update.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(update.getId().intValue())))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].news").value(hasItem(DEFAULT_NEWS)))
            .andExpect(jsonPath("$.[*].deletes").value(hasItem(DEFAULT_DELETES)))
            .andExpect(jsonPath("$.[*].priceUpdates").value(hasItem(DEFAULT_PRICE_UPDATES)));
    }
}
