//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class TestJsoup {
//    public static void main(String[] args) throws IOException {
//        Document document=Jsoup.connect("https://www.nseindia.com/api/option-chain-indices?symbol=BANKNIFTY&expiryDate=23-Jul-2020").ignoreContentType(true)
//                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36").get();
////        System.out.println(document.text());
//        JSONObject jsonObject = new JSONObject(document.text());
//
//        JSONArray dataJsonArray= jsonObject.getJSONObject("filtered").getJSONArray("data");
//        Map map = new HashMap<>();
//        for (int i=0; i<dataJsonArray.length();i++){
//            JSONObject elem= (JSONObject) dataJsonArray.get(i);
//            int strikrPrice= elem.getInt("strikePrice");
//            for(String el: elem.keySet()){
//                Map insideMap= new HashMap();
//                if(el.equalsIgnoreCase("CE")){
//                    String newKey=strikrPrice+" "+el;
//
//                    insideMap=new ObjectMapper().readValue(elem.get("CE").toString(),Map.class);
//                    map.put(newKey,insideMap);
//                }
//                else if(el.equalsIgnoreCase("PE")){
//                    String newKey=strikrPrice+" "+el;
//                    insideMap=new ObjectMapper().readValue(elem.get("PE").toString(),Map.class);
//                    map.put(newKey,insideMap);
//                }
//            }
//
//        }
//
//        System.out.println("Printing final Map");
//        System.out.println(map);
//
//
//
//
//
//    }
//}
