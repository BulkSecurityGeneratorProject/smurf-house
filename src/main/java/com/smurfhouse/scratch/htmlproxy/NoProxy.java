package com.smurfhouse.scratch.htmlproxy;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by fmunozse on 6/7/16.
 */
public class NoProxy extends AbstractHtmlProxy {

    public static final String URL_NO_PROXY = "http://www.idealista.com";


    public NoProxy() {
    }

    @Override
    public String getUrlBaseProxy() {
        return URL_NO_PROXY;
    }


    @Override
    protected Document implementLogicByProxy(String urlRequest, HtmlPage pageProxy, WebClient webClient) throws IOException {
        return getPage(urlRequest);
    }



}
