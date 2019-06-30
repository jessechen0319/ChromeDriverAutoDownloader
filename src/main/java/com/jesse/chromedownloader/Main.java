package com.jesse.chromedownloader;

import com.github.kevinsawicki.http.HttpRequest;
import com.sun.webkit.network.URLs;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {
    private static final String FETCH_URL = "https://chromedriver.storage.googleapis.com/?delimiter=/&prefix=";
    private static final int INITIAL_VERSION = 43;
    public static void main(String[] args) {
        //url = https://chromedriver.storage.googleapis.com/?delimiter=/&prefix=
        try {
            System.out.println("Fetching...");
            String response = HttpRequest.get(URLs.newURL(FETCH_URL)).useProxy("127.0.0.1", 1080).body();
            Document document = DocumentHelper.parseText(response);
            List elements = document.getRootElement().elements("CommonPrefixes");
            Map<String, String> dataCache = new HashMap<String, String>();

            for (Object object:elements
                 ) {
                Element element = (Element) object;
                String value = element.element("Prefix").getStringValue();
                value = value.substring(0, value.length()-1);
                String partten ="\\d+(\\.\\d+)*";
                if(Pattern.matches(partten, value)){
                    String urlString = "https://chromedriver.storage.googleapis.com/"+value+"/notes.txt";
                    String versionNotes = HttpRequest.get(URLs.newURL(urlString)).useProxy("127.0.0.1", 1080).body();
                    dataCache.put(value, versionNotes);
                }

            }

            System.out.println(dataCache.keySet().size());

            for (String driverVersion:dataCache.keySet()){
                String notes = dataCache.get(driverVersion);
                String[] lines = notes.split(System.getProperty("line.separator"));
                System.out.println("verison" + driverVersion);
                if (lines.length > 1){
                    //Supports Chrome v71-73
                    String line2 = lines[1];
                    try {
                        Integer lowestSupportedVersion = Integer.valueOf(line2.split("v")[1].split("-")[0]);
                        System.out.println("=============" + lowestSupportedVersion);
                        Integer highestSupportedVersion = Integer.valueOf(line2.split("v")[1].split("-")[1]);
                        System.out.println("------------" + highestSupportedVersion);
                        System.out.println("------------------------------------------------");
                        if (INITIAL_VERSION + 1 >= lowestSupportedVersion && INITIAL_VERSION + 1 <= highestSupportedVersion) {
                            System.out.println(INITIAL_VERSION + 1 + "=" + driverVersion);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

//            for (Object obj:elements) {
//                Element element = (Element) obj;
//                //\d+(\.\d+){0,*}
//                String value = element.element("Prefix").getStringValue();
//                value = value.substring(0, value.length()-1);
//                String partten ="\\d+(\\.\\d+)*";
//                boolean isMatch = Pattern.matches(partten, value);
//                if (isMatch){
//                    if (Pattern.matches("\\d+(\\.\\d+){0,1}", value)){
//                        //from 2.4 ~ 2.46
//                        //the max supported version for 2.46 is Chrome 73
//                        String[] versions = value.split("\\.");
//                            //https://chromedriver.storage.googleapis.com/2.46/notes.txt
//                            String versionNotes;
//                            if (dataCache.get(value)==null){
//                                String urlString = "https://chromedriver.storage.googleapis.com/"+value+"/notes.txt";
//                                versionNotes = HttpRequest.get(URLs.newURL(urlString)).useProxy("127.0.0.1", 1080).body();
//                                dataCache.put(value, versionNotes);
//                            } else {
//                                versionNotes = dataCache.get(value);
//                            }
//
//                            String[] lines = versionNotes.split(System.getProperty("line.separator"));
//                            if (lines.length > 1) {
//                                //Supports Chrome v71-73
//                                String line2 = lines[1];
//                                try {
//                                    Integer lowestSupportedVersion = Integer.valueOf(line2.split("v")[1].split("-")[0]);
//                                    System.out.println("============="+lowestSupportedVersion);
//                                    Integer highestSupportedVersion = Integer.valueOf(line2.split("v")[1].split("-")[0]);
//                                    System.out.println("============="+highestSupportedVersion);
//                                    if (INITIAL_VERSION+1 >= lowestSupportedVersion && INITIAL_VERSION+1 <= highestSupportedVersion) {
//                                        System.out.println(INITIAL_VERSION+1+"="+value);
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                    } else {
//                        //process from 74 to the latest version
//                    }
//                }
//            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
