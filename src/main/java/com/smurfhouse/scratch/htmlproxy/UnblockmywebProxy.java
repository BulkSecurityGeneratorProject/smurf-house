package com.smurfhouse.scratch.htmlproxy;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by fmunozse on 6/7/16.
 */
public class UnblockmywebProxy extends AbstractHtmlProxy {

    private static final String URL_UNBLOCKEDMYWEB_PROXY = "http://www.unblockmyweb.com/";

    protected WebClient webClient;

    public UnblockmywebProxy () {
    }

    @Override
    public String getUrlBaseProxy() {
        return URL_UNBLOCKEDMYWEB_PROXY;
    }

    protected Document implementLogicByProxy (String urlRequest, HtmlPage pageProxy, WebClient webClient) throws IOException {

        final HtmlForm form = pageProxy.getForms().get(0);
        final HtmlSubmitInput button = form.getInputByValue("Go!");
        final HtmlTextInput textField = form.getInputByName("u");

        // Change the value of the text field
        textField.setValueAttribute(urlRequest);

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
