package com.smurfhouse.scratch.htmlproxy;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.smurfhouse.scratch.util.UtilScratch;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by fmunozse on 10/7/16.
 */
public class UnblockmywebProxyTest {
    public static final String  URL_REQUEST = "http://www.google.es";

    @Test
    public void implementLogicByProxy() throws Exception {

        WebClient webClient = UtilScratch.getNewWebClient();
        UnblockmywebProxy htmlproxy = new UnblockmywebProxy ();

        try {
            HtmlPage pageProxy = webClient.getPage( htmlproxy.getUrlBaseProxy() );

            Document doc = htmlproxy.implementLogicByProxy(URL_REQUEST, pageProxy, webClient);
            assertNotNull(doc);
            assertEquals(doc.title(), "Google");

        } catch (IOException e) {
            htmlproxy.close();
            throw e;
        }

    }

}
