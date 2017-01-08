package com.smurfhouse.scratch;

import com.smurfhouse.scratch.htmlproxy.IHtmlProxy;
import com.smurfhouse.scratch.htmlproxy.NewipnowProxy;
import com.smurfhouse.scratch.model.PageParsed;
import com.smurfhouse.scratch.model.ScratchHouse;
import com.smurfhouse.scratch.scratch.IdealistaScratch;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by fmunozse on 6/7/16.
 */
public class ManagerIdealista {

    private final Logger log = LoggerFactory.getLogger(ManagerIdealista.class);

    public static final String URL_IDEALISTA = "http://www.idealista.com/venta-viviendas/madrid/ciudad-lineal/san-juan-bautista/?ordenado-por=precio-asc";

    static {
        //java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

    }

    public List<ScratchHouse> getAllHouse (String url, Locale locale) {

        List<ScratchHouse> houses = new LinkedList<>();
        IHtmlProxy proxy = new NewipnowProxy();
        IdealistaScratch scratch = new IdealistaScratch();

        Document doc = null;
        PageParsed pageParsed = null;
        String next = url;

        try {
            do {
                doc = proxy.getPage(next);
                pageParsed = scratch.parsePage(next, doc,locale);

                List<ScratchHouse> l = pageParsed.getHousesCurrentPage();
                if (l != null && l.size() > 0 ) {
                    houses.addAll(l);

                    log.debug("adding : {} ", l);

                    //Check if max boundari
                    ScratchHouse lastHouse = l.get(l.size()-1);
                    if (new BigDecimal("300000").compareTo(lastHouse.getPrice()) < 0 ) {
                        log.debug("exit by max price : {} ", lastHouse.getPrice());
                        break;
                    }

                    //next = proxy.getUrlBaseProxy() +  pageParsed.getNextUrl();
                    next =  pageParsed.getNextUrl();
                    log.info("Nex page: {} ", next);
                }

            } while (pageParsed.getNextUrl() != null);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            proxy.close();
        }

        return houses;
    }

}
