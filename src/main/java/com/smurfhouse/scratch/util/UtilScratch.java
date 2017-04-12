package com.smurfhouse.scratch.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fmunozse on 6/7/16.
 */
public class UtilScratch {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static WebClient getNewWebClient () {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        webClient.setJavaScriptErrorListener(null);

        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setPopupBlockerEnabled(true);
        webClient.getOptions().setTimeout(200000);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        webClient.waitForBackgroundJavaScript(5000);

        return webClient;
    }

    public static byte[] generateHash(String stringToEncrypt) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(stringToEncrypt.getBytes());
            return messageDigest.digest();

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }



    public static String findTextElementSafeNullPointer (Element e, String selector) {
        if (e == null || selector == null) {
            return null;
        }

        Elements elements = e.select(selector);
        if (elements.size()>0) {
            return elements.first().text();
        }

        return null;
    }


    public static String findAttributeElementSafeNullPointer (Element e, String selector, String attribute) {
        if (e == null || selector == null || attribute == null) {
            return null;
        }

        Elements elements = e.select(selector);
        if (elements.size()>0) {
            return elements.first().attr(attribute);
        }

        return null;
    }



    public static String findTextFirstGroupByRegExpSafeNullPointer (String text, String regularExpresion) {
        if (text == null || regularExpresion == null)
            return null;

        //Now take first group that match with regularExpresion
        Pattern pattern = CachedPattern.compile(regularExpresion);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public static BigDecimal findBigDecimalFirstGroupByRegExpSafeNullPointer (String text, String regularExpresion, Locale locale) {
        String findedText = findTextFirstGroupByRegExpSafeNullPointer(text, regularExpresion);
        if (findedText == null) return null;

        DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance(locale);
        nf.setParseBigDecimal(true);

        return (BigDecimal)nf.parse(findedText, new ParsePosition(0));
    }


    public static Integer findIntegerFirstGroupByRegExpSafeNullPointer (String text, String regularExpresion, Locale locale) {
        BigDecimal findedNumber = findBigDecimalFirstGroupByRegExpSafeNullPointer (text,regularExpresion,locale);
        if (findedNumber == null) return null;

        return findedNumber.intValue();
    }


    public static Boolean matchTextByRegExpSafeNullPointer (String text, String regularExpresion) {
        if (text == null || regularExpresion == null)
            return null;

        Pattern pattern = CachedPattern.compile(regularExpresion);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

}
