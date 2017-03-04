package com.smurfhouse.scratch.htmlproxy;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import java.io.IOException;

/**
 * Created by fmunozse on 6/7/16.
 */
public class ProxySite extends AbstractHtmlProxy {

    private static final String URL_PROXY = "https://www.proxysite.com/es";

    protected WebClient webClient;

    public ProxySite() {
    }

    @Override
    public String getUrlBaseProxy() {
        return URL_PROXY;
    }

    protected HtmlPage implementLogicByProxy (String urlRequest, HtmlPage pageProxy, WebClient webClient) throws IOException {

        final HtmlForm form = pageProxy.getForms().get(0);
        final HtmlTextInput textField = form.getInputByName("d");

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

        return pageIdealista;
    }



}
