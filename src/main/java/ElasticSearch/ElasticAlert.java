package ElasticSearch;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ElasticAlert {
    public static void main(String[] args) {
        ElasticAlert.triggerAlert(String.valueOf(System.currentTimeMillis()),String.valueOf(System.currentTimeMillis()-100000));
    }
    public static void triggerAlert(String currentTime,String pastTime){

        Response response= RestAssured.given().contentType(ContentType.JSON).
                body("{\"aggs\":{\"2\":{\"date_histogram\":{\"field\":\"timestamp\",\"interval\":\"1s\",\"time_zone\":\"Asia/Kolkata\",\"min_doc_count\":1},\"aggs\":{\"3\":{\"terms\":{\"field\":\"Stock.keyword\",\"size\":25,\"order\":{\"_key\":\"desc\"}},\"aggs\":{\"1\":{\"avg\":{\"field\":\"Chng in OI\"}}}}}}},\"size\":0,\"_source\":{\"excludes\":[]},\"stored_fields\":[\"*\"],\"script_fields\":{},\"docvalue_fields\":[{\"field\":\"timestamp\",\"format\":\"date_time\"}],\"query\":{\"bool\":{\"must\":" +
                        "[{\"range\":{\"timestamp\":{\"gte\":"+pastTime+",\"lte\":"+currentTime+",\"format\":\"epoch_millis\"}}}],\"filter\":[{\"match_all\":{}},{\"match_all\":{}}],\"should\":[],\"must_not\":[]}},\"timeout\":\"30000ms\"}")
                .post("http://localhost:9200/bnnseoidata/_search");

        System.out.println(response.prettyPrint());

        Map<Object, List<Float>> alertmap= new LinkedHashMap<>();

        System.out.println(response.jsonPath().getList("aggregations.2.buckets"));
        int listLength=response.jsonPath().getList("aggregations.2.buckets").size();

        for (int j = listLength-1; j >=0 ; j--) {
            List<Map> latestDataList = response.jsonPath().getList("aggregations.2.buckets[" + (listLength - 1) + "].3.buckets");


//data to processed
            latestDataList = response.jsonPath().getList("aggregations.2.buckets[" + j + "].3.buckets");
            for (int i = 0; i < latestDataList.size(); i++) {
                String key = (String) latestDataList.get(i).get("key");
                Float keyValue = (Float) ((Map) latestDataList.get(i).get("1")).get("value");

                if (alertmap.containsKey(key)) {
                    List list = alertmap.get(key);
                    list.add(keyValue);
                    alertmap.put(key, list);
                } else {
                    List<Float> valueList = new LinkedList<>();
                    valueList.add(keyValue);
                    alertmap.put(latestDataList.get(i).get("key"), valueList);
                }
            }


        }
// Final Alert Map
        System.out.println("Final Alert map");
        System.out.println(alertmap);
        sendAlert(alertmap);
    }

    public static void sendAlert(Map<Object,List<Float>> map){

        String positiveDiff="Positive OI CHG \n\n";
        String negativeDiff="Negative OI CHG\n\n";
        boolean isPositiveAlert=false;
        boolean isNegativeAlert=false;
        for (Object key :map.keySet()){
            List<Float> list=map.get(key);
            float diff = 0;
       try {
           diff= list.get(0)-list.get(1);
       }
       catch (IndexOutOfBoundsException e){
           System.out.println("Index level exception occured, skipped that key: "+key);
       }

            if(diff>10000){
                positiveDiff+=key+" Diff Value: "+diff+"\n";
                isPositiveAlert=true;
            }
            if(diff<-5000){
                negativeDiff+=key+" Diff Value: "+diff+"\n";
                isNegativeAlert=true;
            }
        }
        // Sending alert to telegram
        Response response=null;
        if(isPositiveAlert)
         response= RestAssured.given().queryParam("chat_id","654771852").queryParam("text",positiveDiff)
                .get("https://api.telegram.org/bot826838727:AAEhTJT4xQsTRxuF5wxtgNELjSJgjwkvYro/sendMessage");
        System.out.println(response.prettyPrint());

        if(isNegativeAlert)
            response= RestAssured.given().queryParam("chat_id","654771852").queryParam("text",negativeDiff)
                    .get("https://api.telegram.org/bot826838727:AAEhTJT4xQsTRxuF5wxtgNELjSJgjwkvYro/sendMessage");
        System.out.println(response.prettyPrint());
    }
}
