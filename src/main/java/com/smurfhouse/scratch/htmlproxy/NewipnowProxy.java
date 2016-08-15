package com.smurfhouse.scratch.htmlproxy;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by fmunozse on 6/7/16.
 */
public class NewipnowProxy extends AbstractHtmlProxy {

    private static final String URL_PROXY = "http://newipnow.com/";

    protected WebClient webClient;

    public NewipnowProxy() {
    }

    @Override
    public String getUrlBaseProxy() {
        return URL_PROXY;
    }

    protected Document implementLogicByProxy (String urlRequest, HtmlPage pageProxy, WebClient webClient) throws IOException {

        final HtmlForm form = pageProxy.getForms().get(0);
        final HtmlTextInput textField = form.getInputByName("nin_u");

        // Change the value of the text field
        textField.setValueAttribute(urlRequest + "\n");

        DomElement button = pageProxy.createElement("button");
        button.setAttribute("type", "submit");
        form.appendChild(button);

        // Now submit the form by clicking the button and get back the second page.
        HtmlPage pageIdealista = null;
        try {
            pageIdealista = button.click();

        } catch (IOException e) {
            close();
            throw e;
        }

        return Jsoup.parse(pageIdealista.asXml());
    }

}
