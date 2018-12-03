package ElasticSearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.bson.Document;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public class ElasticSearchUtil {
//        static int number=0;

    public boolean putData(String dataJson,String indexName) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")
        ));

// Clear the elastic Search Index first
//        DeleteIndexRequest deleteIndexRequest= new DeleteIndexRequest("nseindex");
//        client.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);



        IndexRequest request = new IndexRequest(
                indexName,
                "_doc");
//        String jsonString = "{" +
//                "\"user\":\"kimchy" + i + "\"," +
//                "\"postDate\":\"2018-01-30\"," +
//                "\"message\":\"trying out Elasticsearch\"" +
//                "}";
        request.source(dataJson, XContentType.JSON);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        client.close();
        System.out.println("ES : Doc Creation "+indexResponse);
        return indexResponse.status().getStatus()==201;

    }

// store the json string data to double data
public void storeDataElasticSearch(Map map, String indexName ) throws ParseException, IOException {
    if(!map.get("Symbol").toString().contains("null")) {
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        double oiChgPerc = decimalFormat.parse((String) map.get("Chg %")).doubleValue();
        double priceChg = decimalFormat.parse((String) map.get("Chg Rs")).doubleValue();
        double volChgPerc;
        try {
    volChgPerc = decimalFormat.parse((String) map.get("% Change")).doubleValue();
}
catch (ParseException e){
    volChgPerc=0.0;
}
        map.remove("Chg %");
        map.remove("Chg Rs");
        map.remove("% Change");

        map.put("Price CHG", priceChg);
        if(oiChgPerc>100){
            oiChgPerc=100+oiChgPerc%100;
        }
        if(volChgPerc>100){
            volChgPerc=100+volChgPerc%100;
        }
        map.put("OI CHG %", oiChgPerc);
        map.put("VOL CHG %", volChgPerc);
        map.put("timestamp",System.currentTimeMillis());

        String json=new ObjectMapper().writeValueAsString(map);
        putData(json, indexName);
    }
}

    public void storeDataElasticSearchNSEBN(String key,Map map, String indexName ) throws ParseException, IOException {

        map.put("Stock",key);
            DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        double priceLTP ;
        double currentOI ;
        double changeOI;
        double vol;
            try {
                priceLTP = decimalFormat.parse((String) map.get("LTP")).doubleValue();
                currentOI = decimalFormat.parse((String) map.get("OI")).doubleValue();
                 changeOI = decimalFormat.parse((String) map.get("Chng in OI")).doubleValue();
                 vol = decimalFormat.parse((String) map.get("Volume")).doubleValue();
            }
            catch (ParseException e){
                priceLTP=0.0;
                currentOI=0.0;
                changeOI=0.0;
                vol=0.0;
            }

            map.put("LTP",priceLTP);
            map.put("OI",currentOI/1000);
            map.put("Chng in OI",changeOI/1000);
            map.put("Volume",vol/1000);
        map.put("timestamp",System.currentTimeMillis());
            String json=new ObjectMapper().writeValueAsString(map);
            putData(json, indexName);

    }



    public void clearElastChartData(String indexName) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                new HttpHost("localhost", 9201, "http")
        ));

 //Clear the elastic Search Index first
        DeleteIndexRequest deleteIndexRequest= new DeleteIndexRequest(indexName);
        client.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
        client.close();

    }

    public static void main(String[] args) throws IOException {
        new ElasticSearchUtil().clearElastChartData("bnnseoidata");
    }
}