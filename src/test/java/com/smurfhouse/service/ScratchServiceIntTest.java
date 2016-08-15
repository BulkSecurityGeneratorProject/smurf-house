package com.smurfhouse.service;

import com.smurfhouse.SmurfHouseApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by fmunozse on 31/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SmurfHouseApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class ScratchServiceIntTest {

    @Inject
    ScratchService scratchService;

    @Test
    public void testSynchronize() {

        //scratchService.synchronize();

    }

}
