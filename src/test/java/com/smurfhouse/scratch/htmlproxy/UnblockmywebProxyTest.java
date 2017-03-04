package com.smurfhouse.scratch.htmlproxy;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.smurfhouse.scratch.util.UtilScratch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by fmunozse on 10/7/16.
 */
public class UnblockmywebProxyTest {
    public static final String  URL_REQUEST = "http://www.google.es";

    @Ignore
    @Test
    public void implementLogicByProxy() throws Exception {

        WebClient webClient = UtilScratch.getNewWebClient();
        UnblockmywebProxy htmlproxy = new UnblockmywebProxy ();

        try {
            HtmlPage pageProxy = webClient.getPage( htmlproxy.getUrlBaseProxy() );

            HtmlPage page = htmlproxy.implementLogicByProxy(URL_REQUEST, pageProxy, webClient);
            Document doc = Jsoup.parse(page.asXml());

            assertNotNull(doc);
            assertEquals(doc.title(), "Google");

        } catch (IOException e) {
            htmlproxy.close();
            throw e;
        }

    }

}
