package com.smurfhouse.scratch.htmlproxy;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.smurfhouse.scratch.util.UtilScratch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fmunozse on 9/7/16.
 */
public abstract class AbstractHtmlProxy implements IHtmlProxy {

    protected static final Logger log = Logger.getLogger( AbstractHtmlProxy.class.getName() );

    protected WebClient webClient;

    @Override
    public void close() {
        if (webClient != null) {
            webClient.close();
        }
        webClient = null;
    }

    public abstract String getUrlBaseProxy ();

    @Override
    public Document getPage(String urlRequest) throws IOException {

        log.log(Level.INFO, "Getting page {0} ", urlRequest );

        if (urlRequest == null)
            return null;

        HtmlPage pageIdealista = null;

        //In case detect first access ... them create webClient and implement the logic to pass by proxyPage
        if (webClient == null) {
            log.log(Level.FINE, "Detected first access to {0}. Create webClient", urlRequest );

            webClient = UtilScratch.getNewWebClient();

            try {
                HtmlPage pageProxy = webClient.getPage( getUrlBaseProxy() );
                log.log(Level.FINE, "Content of pageProxy {0}", pageProxy.asXml() );


                pageIdealista= implementLogicByProxy(urlRequest, pageProxy, webClient);
                log.log(Level.FINE, "Content of urlRequest {0}", pageIdealista.asXml());

            } catch (IOException e) {
                close();
                throw e;
            }
        } else {

            try {

                pageIdealista = webClient.getPage(urlRequest);
                log.log(Level.FINE, "Content of urlRequest {0}", pageIdealista.asXml());

            } catch (IOException e) {
                close();
                throw e;
            }

        }

        return Jsoup.parse(pageIdealista.asXml());

    }

    protected abstract HtmlPage implementLogicByProxy (String urlRequest, HtmlPage pageProxy,  WebClient webClient) throws IOException;

}
