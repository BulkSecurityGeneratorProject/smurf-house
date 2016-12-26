package com.smurfhouse.scratch;

import com.smurfhouse.scratch.htmlproxy.IHtmlProxy;
import com.smurfhouse.scratch.htmlproxy.NewipnowProxy;
import com.smurfhouse.scratch.model.PageParsed;
import com.smurfhouse.scratch.model.ScratchHouse;
import com.smurfhouse.scratch.scratch.IdealistaScratch;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by fmunozse on 6/7/16.
 */
public class ManagerIdealista {

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

                houses.addAll(pageParsed.getHousesCurrentPage());
                next = proxy.getUrlBaseProxy() +  pageParsed.getNextUrl();

                break;

            } while (pageParsed.getNextUrl() != null);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            proxy.close();
        }

        return houses;
    }

}
