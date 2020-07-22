package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParseJSON {
    public static String bnCurrentValue;

    public static String getBnCurrentValue(){
        return bnCurrentValue;
    }
    public static Map getMap() throws IOException {
        Document document= Jsoup.connect("https://www.nseindia.com/api/option-chain-indices?symbol=BANKNIFTY&expiryDate=23-Jul-2020").ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36").get();
//        System.out.println(document.text());
        JSONObject jsonObject = new JSONObject(document.text());

        JSONArray dataJsonArray= jsonObject.getJSONObject("filtered").getJSONArray("data");
        Map<String,Map> map = new HashMap<>();
        for (int i=0; i<dataJsonArray.length();i++){
            JSONObject elem= (JSONObject) dataJsonArray.get(i);
            int strikrPrice= elem.getInt("strikePrice");
            for(String el: elem.keySet()){
                Map <String,String>insideMap= new HashMap();
                if(el.equalsIgnoreCase("CE")){
                    String newKey=strikrPrice+" "+el;

                    insideMap=new ObjectMapper().readValue(elem.get("CE").toString(),new TypeReference<HashMap<String,String>>(){});
                    map.put(newKey,insideMap);
                    bnCurrentValue=insideMap.get("underlyingValue");
                }
                else if(el.equalsIgnoreCase("PE")){
                    String newKey=strikrPrice+" "+el;
                    insideMap=new ObjectMapper().readValue(elem.get("PE").toString(),new TypeReference<HashMap<String,String>>(){});
                    map.put(newKey,insideMap);
                    bnCurrentValue=insideMap.get("underlyingValue");
                }

            }

        }

        System.out.println("Printing final Map");
        System.out.println(map);

return map;

    }

    }


