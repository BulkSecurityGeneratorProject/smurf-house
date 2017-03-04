package com.smurfhouse.scratch;

import com.smurfhouse.scratch.model.ScratchHouse;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by fmunozse on 9/7/16.
 */
public class ManagerIdealistaTest {
    public static final String URL_SANJUANBAUTISTA = "http://www.idealista.com/venta-viviendas/madrid/ciudad-lineal/san-juan-bautista/?ordenado-por=precio-asc";

    //@Ignore
    @Test
    public void getAllHouse() throws Exception {
        ManagerIdealista managerIdealista = new ManagerIdealista();
        List<ScratchHouse> houses =  managerIdealista.getAllHouse(URL_SANJUANBAUTISTA, Locale.getDefault());

        for (ScratchHouse house: houses) {
            System.out.println(house);
        }

        assertNotNull(houses);
        assertTrue(houses.size()>0);
    }


}
