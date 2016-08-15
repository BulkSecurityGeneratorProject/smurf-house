package com.smurfhouse.scratch.htmlproxy;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by fmunozse on 9/7/16.
 */
public interface IHtmlProxy {

    String getUrlBaseProxy();

    void close();

    Document getPage(String url) throws IOException;
}
