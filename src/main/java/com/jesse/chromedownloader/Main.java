package com.jesse.chromedownloader;

import com.github.kevinsawicki.http.HttpRequest;
import com.sun.webkit.network.URLs;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
    private static final String FETCH_URL = "https://chromedriver.storage.googleapis.com/?delimiter=/&prefix=";
    public static void main(String[] args) {
        //url = https://chromedriver.storage.googleapis.com/?delimiter=/&prefix=
        try {
            System.out.println("Fetching...");
            String response = HttpRequest.get(URLs.newURL(FETCH_URL)).useProxy("127.0.0.1", 1080).body();
            Document document = DocumentHelper.parseText(response);
            List elements = document.getRootElement().elements("CommonPrefixes");
            for (Object obj:elements) {
                Element element = (Element) obj;
                //\d+(\.\d+){0,*}
                String value = element.element("Prefix").getStringValue();
                value = value.substring(0, value.length()-1);
                String partten ="\\d+(\\.\\d+)*";
                boolean isMatch = Pattern.matches(partten, value);
                if (isMatch){
                    if (Pattern.matches("\\d+(\\.\\d+){0,1}", value)){
                        //from 2.4 ~ 2.46
                        //the max supported version for 2.46 is Chrome 73
                        System.out.println(value);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
