package com.smurfhouse.web.rest;

import com.smurfhouse.SmurfHouseApp;
import com.smurfhouse.domain.PriceHouse;
import com.smurfhouse.repository.PriceHouseRepository;
import com.smurfhouse.repository.search.PriceHouseSearchRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the PriceHouseResource REST controller.
 *
 * @see PriceHouseResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SmurfHouseApp.class)
@WebAppConfiguration
@IntegrationTest
public class PriceHouseResourceIntTest {


    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final LocalDate DEFAULT_WHEN_CHANGED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_WHEN_CHANGED = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private PriceHouseRepository priceHouseRepository;

    @Inject
    private PriceHouseSearchRepository priceHouseSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPriceHouseMockMvc;

    private PriceHouse priceHouse;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PriceHouseResource priceHouseResource = new PriceHouseResource();
        ReflectionTestUtils.setField(priceHouseResource, "priceHouseSearchRepository", priceHouseSearchRepository);
        ReflectionTestUtils.setField(priceHouseResource, "priceHouseRepository", priceHouseRepository);
        this.restPriceHouseMockMvc = MockMvcBuilders.standaloneSetup(priceHouseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        priceHouseSearchRepository.deleteAll();
        priceHouse = new PriceHouse();
        priceHouse.setPrice(DEFAULT_PRICE);
        priceHouse.setWhenChanged(DEFAULT_WHEN_CHANGED);
    }

    @Ignore
    @Test
    @Transactional
    public void createPriceHouse() throws Exception {
        int databaseSizeBeforeCreate = priceHouseRepository.findAll().size();

        // Create the PriceHouse

        restPriceHouseMockMvc.perform(post("/api/price-houses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(priceHouse)))
                .andExpect(status().isCreated());

        // Validate the PriceHouse in the database
        List<PriceHouse> priceHouses = priceHouseRepository.findAll();
        assertThat(priceHouses).hasSize(databaseSizeBeforeCreate + 1);
        PriceHouse testPriceHouse = priceHouses.get(priceHouses.size() - 1);
        assertThat(testPriceHouse.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testPriceHouse.getWhenChanged()).isEqualTo(DEFAULT_WHEN_CHANGED);

        // Validate the PriceHouse in ElasticSearch
        PriceHouse priceHouseEs = priceHouseSearchRepository.findOne(testPriceHouse.getId());
        assertThat(priceHouseEs).isEqualToComparingFieldByField(testPriceHouse);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = priceHouseRepository.findAll().size();
        // set the field null
        priceHouse.setPrice(null);

        // Create the PriceHouse, which fails.

        restPriceHouseMockMvc.perform(post("/api/price-houses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(priceHouse)))
                .andExpect(status().isBadRequest());

        List<PriceHouse> priceHouses = priceHouseRepository.findAll();
        assertThat(priceHouses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWhenChangedIsRequired() throws Exception {
        int databaseSizeBeforeTest = priceHouseRepository.findAll().size();
        // set the field null
        priceHouse.setWhenChanged(null);

        // Create the PriceHouse, which fails.

        restPriceHouseMockMvc.perform(post("/api/price-houses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(priceHouse)))
                .andExpect(status().isBadRequest());

        List<PriceHouse> priceHouses = priceHouseRepository.findAll();
        assertThat(priceHouses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPriceHouses() throws Exception {
        // Initialize the database
        priceHouseRepository.saveAndFlush(priceHouse);

        // Get all the priceHouses
        restPriceHouseMockMvc.perform(get("/api/price-houses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(priceHouse.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].whenChanged").value(hasItem(DEFAULT_WHEN_CHANGED.toString())));
    }

    @Test
    @Transactional
    public void getPriceHouse() throws Exception {
        // Initialize the database
        priceHouseRepository.saveAndFlush(priceHouse);

        // Get the priceHouse
        restPriceHouseMockMvc.perform(get("/api/price-houses/{id}", priceHouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(priceHouse.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.whenChanged").value(DEFAULT_WHEN_CHANGED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPriceHouse() throws Exception {
        // Get the priceHouse
        restPriceHouseMockMvc.perform(get("/api/price-houses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Ignore
    @Test
    @Transactional
    public void updatePriceHouse() throws Exception {
        // Initialize the database
        priceHouseRepository.saveAndFlush(priceHouse);
        priceHouseSearchRepository.save(priceHouse);
        int databaseSizeBeforeUpdate = priceHouseRepository.findAll().size();

        // Update the priceHouse
        PriceHouse updatedPriceHouse = new PriceHouse();
        updatedPriceHouse.setId(priceHouse.getId());
        updatedPriceHouse.setPrice(UPDATED_PRICE);
        updatedPriceHouse.setWhenChanged(UPDATED_WHEN_CHANGED);

        restPriceHouseMockMvc.perform(put("/api/price-houses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPriceHouse)))
                .andExpect(status().isOk());

        // Validate the PriceHouse in the database
        List<PriceHouse> priceHouses = priceHouseRepository.findAll();
        assertThat(priceHouses).hasSize(databaseSizeBeforeUpdate);
        PriceHouse testPriceHouse = priceHouses.get(priceHouses.size() - 1);
        assertThat(testPriceHouse.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testPriceHouse.getWhenChanged()).isEqualTo(UPDATED_WHEN_CHANGED);

        // Validate the PriceHouse in ElasticSearch
        PriceHouse priceHouseEs = priceHouseSearchRepository.findOne(testPriceHouse.getId());
        assertThat(priceHouseEs).isEqualToComparingFieldByField(testPriceHouse);
    }

    @Test
    @Transactional
    public void deletePriceHouse() throws Exception {
        // Initialize the database
        priceHouseRepository.saveAndFlush(priceHouse);
        priceHouseSearchRepository.save(priceHouse);
        int databaseSizeBeforeDelete = priceHouseRepository.findAll().size();

        // Get the priceHouse
        restPriceHouseMockMvc.perform(delete("/api/price-houses/{id}", priceHouse.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean priceHouseExistsInEs = priceHouseSearchRepository.exists(priceHouse.getId());
        assertThat(priceHouseExistsInEs).isFalse();

        // Validate the database is empty
        List<PriceHouse> priceHouses = priceHouseRepository.findAll();
        assertThat(priceHouses).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPriceHouse() throws Exception {
        // Initialize the database
        priceHouseRepository.saveAndFlush(priceHouse);
        priceHouseSearchRepository.save(priceHouse);

        // Search the priceHouse
        restPriceHouseMockMvc.perform(get("/api/_search/price-houses?query=id:" + priceHouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(priceHouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].whenChanged").value(hasItem(DEFAULT_WHEN_CHANGED.toString())));
    }
}
