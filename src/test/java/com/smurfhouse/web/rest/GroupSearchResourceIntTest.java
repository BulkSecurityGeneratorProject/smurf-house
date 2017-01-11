package com.smurfhouse.web.rest;

import com.smurfhouse.SmurfHouseApp;
import com.smurfhouse.domain.GroupSearch;
import com.smurfhouse.domain.enumeration.Provider;
import com.smurfhouse.repository.GroupSearchRepository;
import com.smurfhouse.repository.search.GroupSearchSearchRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the GroupSearchResource REST controller.
 *
 * @see GroupSearchResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SmurfHouseApp.class)
@WebAppConfiguration
@IntegrationTest
public class GroupSearchResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_URL = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Provider DEFAULT_PROVIDER = Provider.IDEALISTA;
    private static final Provider UPDATED_PROVIDER = Provider.IDEALISTA;

    private static final BigDecimal DEFAULT_MAX_LIMIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_MAX_LIMIT_PRICE = new BigDecimal(2);

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    @Inject
    private GroupSearchRepository groupSearchRepository;

    @Inject
    private GroupSearchSearchRepository groupSearchSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restGroupSearchMockMvc;

    private GroupSearch groupSearch;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GroupSearchResource groupSearchResource = new GroupSearchResource();
        ReflectionTestUtils.setField(groupSearchResource, "groupSearchSearchRepository", groupSearchSearchRepository);
        ReflectionTestUtils.setField(groupSearchResource, "groupSearchRepository", groupSearchRepository);
        this.restGroupSearchMockMvc = MockMvcBuilders.standaloneSetup(groupSearchResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        groupSearchSearchRepository.deleteAll();
        groupSearch = new GroupSearch();
        groupSearch.setTitle(DEFAULT_TITLE);
        groupSearch.setUrl(DEFAULT_URL);
        groupSearch.setProvider(DEFAULT_PROVIDER);
        groupSearch.setMaxLimitPrice(DEFAULT_MAX_LIMIT_PRICE);
        groupSearch.setActivated(DEFAULT_ACTIVATED);
    }

    @Ignore
    @Test
    @Transactional
    public void createGroupSearch() throws Exception {
        int databaseSizeBeforeCreate = groupSearchRepository.findAll().size();

        // Create the GroupSearch

        restGroupSearchMockMvc.perform(post("/api/group-searches")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(groupSearch)))
                .andExpect(status().isCreated());

        // Validate the GroupSearch in the database
        List<GroupSearch> groupSearches = groupSearchRepository.findAll();
        assertThat(groupSearches).hasSize(databaseSizeBeforeCreate + 1);
        GroupSearch testGroupSearch = groupSearches.get(groupSearches.size() - 1);
        assertThat(testGroupSearch.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testGroupSearch.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testGroupSearch.getProvider()).isEqualTo(DEFAULT_PROVIDER);
        assertThat(testGroupSearch.getMaxLimitPrice()).isEqualTo(DEFAULT_MAX_LIMIT_PRICE);
        assertThat(testGroupSearch.isActivated()).isEqualTo(DEFAULT_ACTIVATED);

        // Validate the GroupSearch in ElasticSearch
        GroupSearch groupSearchEs = groupSearchSearchRepository.findOne(testGroupSearch.getId());
        assertThat(groupSearchEs).isEqualToComparingFieldByField(testGroupSearch);
    }


    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupSearchRepository.findAll().size();
        // set the field null
        groupSearch.setTitle(null);

        // Create the GroupSearch, which fails.

        restGroupSearchMockMvc.perform(post("/api/group-searches")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(groupSearch)))
                .andExpect(status().isBadRequest());

        List<GroupSearch> groupSearches = groupSearchRepository.findAll();
        assertThat(groupSearches).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupSearchRepository.findAll().size();
        // set the field null
        groupSearch.setUrl(null);

        // Create the GroupSearch, which fails.

        restGroupSearchMockMvc.perform(post("/api/group-searches")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(groupSearch)))
                .andExpect(status().isBadRequest());

        List<GroupSearch> groupSearches = groupSearchRepository.findAll();
        assertThat(groupSearches).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMaxLimitPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = groupSearchRepository.findAll().size();
        // set the field null
        groupSearch.setMaxLimitPrice(null);

        // Create the GroupSearch, which fails.

        restGroupSearchMockMvc.perform(post("/api/group-searches")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(groupSearch)))
                .andExpect(status().isBadRequest());

        List<GroupSearch> groupSearches = groupSearchRepository.findAll();
        assertThat(groupSearches).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGroupSearches() throws Exception {
        // Initialize the database
        groupSearchRepository.saveAndFlush(groupSearch);

        // Get all the groupSearches
        restGroupSearchMockMvc.perform(get("/api/group-searches?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(groupSearch.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
                .andExpect(jsonPath("$.[*].provider").value(hasItem(DEFAULT_PROVIDER.toString())))
                .andExpect(jsonPath("$.[*].maxLimitPrice").value(hasItem(DEFAULT_MAX_LIMIT_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())));
    }

    @Test
    @Transactional
    public void getGroupSearch() throws Exception {
        // Initialize the database
        groupSearchRepository.saveAndFlush(groupSearch);

        // Get the groupSearch
        restGroupSearchMockMvc.perform(get("/api/group-searches/{id}", groupSearch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(groupSearch.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.provider").value(DEFAULT_PROVIDER.toString()))
            .andExpect(jsonPath("$.maxLimitPrice").value(DEFAULT_MAX_LIMIT_PRICE.intValue()))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingGroupSearch() throws Exception {
        // Get the groupSearch
        restGroupSearchMockMvc.perform(get("/api/group-searches/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Ignore
    @Test
    @Transactional
    public void updateGroupSearch() throws Exception {
        // Initialize the database
        groupSearchRepository.saveAndFlush(groupSearch);
        groupSearchSearchRepository.save(groupSearch);
        int databaseSizeBeforeUpdate = groupSearchRepository.findAll().size();

        // Update the groupSearch
        GroupSearch updatedGroupSearch = new GroupSearch();
        updatedGroupSearch.setId(groupSearch.getId());
        updatedGroupSearch.setTitle(UPDATED_TITLE);
        updatedGroupSearch.setUrl(UPDATED_URL);
        updatedGroupSearch.setProvider(UPDATED_PROVIDER);
        updatedGroupSearch.setMaxLimitPrice(UPDATED_MAX_LIMIT_PRICE);
        updatedGroupSearch.setActivated(UPDATED_ACTIVATED);

        restGroupSearchMockMvc.perform(put("/api/group-searches")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedGroupSearch)))
                .andExpect(status().isOk());

        // Validate the GroupSearch in the database
        List<GroupSearch> groupSearches = groupSearchRepository.findAll();
        assertThat(groupSearches).hasSize(databaseSizeBeforeUpdate);
        GroupSearch testGroupSearch = groupSearches.get(groupSearches.size() - 1);
        assertThat(testGroupSearch.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testGroupSearch.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testGroupSearch.getProvider()).isEqualTo(UPDATED_PROVIDER);
        assertThat(testGroupSearch.getMaxLimitPrice()).isEqualTo(UPDATED_MAX_LIMIT_PRICE);
        assertThat(testGroupSearch.isActivated()).isEqualTo(UPDATED_ACTIVATED);

        // Validate the GroupSearch in ElasticSearch
        GroupSearch groupSearchEs = groupSearchSearchRepository.findOne(testGroupSearch.getId());
        assertThat(groupSearchEs).isEqualToComparingFieldByField(testGroupSearch);
    }

    @Test
    @Transactional
    public void deleteGroupSearch() throws Exception {
        // Initialize the database
        groupSearchRepository.saveAndFlush(groupSearch);
        groupSearchSearchRepository.save(groupSearch);
        int databaseSizeBeforeDelete = groupSearchRepository.findAll().size();

        // Get the groupSearch
        restGroupSearchMockMvc.perform(delete("/api/group-searches/{id}", groupSearch.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean groupSearchExistsInEs = groupSearchSearchRepository.exists(groupSearch.getId());
        assertThat(groupSearchExistsInEs).isFalse();

        // Validate the database is empty
        List<GroupSearch> groupSearches = groupSearchRepository.findAll();
        assertThat(groupSearches).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchGroupSearch() throws Exception {
        // Initialize the database
        groupSearchRepository.saveAndFlush(groupSearch);
        groupSearchSearchRepository.save(groupSearch);

        // Search the groupSearch
        restGroupSearchMockMvc.perform(get("/api/_search/group-searches?query=id:" + groupSearch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupSearch.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].provider").value(hasItem(DEFAULT_PROVIDER.toString())))
            .andExpect(jsonPath("$.[*].maxLimitPrice").value(hasItem(DEFAULT_MAX_LIMIT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())));
    }
}
