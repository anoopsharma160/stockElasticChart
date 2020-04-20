package ElasticSearch;

import IndexNSELive.NSEBankNiftyFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import util.LengthUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

public class ElasticSearchUtil {
//        static int number=0;
    static  int bnCount=0;
    static List listPETemp= new ArrayList();

    public static boolean putData(String dataJson, String indexName) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
//                new HttpHost("10.157.251.29", 9200, "http"),
//                new HttpHost("10.157.251.29", 9201, "http")

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

// store the es data for top 5 increase in oi and increase in price

    public void storeDataElasticSearchTopstocks(Map map, String indexName ) throws ParseException, IOException {
        try {
            if ((!(map.get("Symbol").toString().contains("null")))) {
                DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
                double oiChgPerc = decimalFormat.parse((String) map.get("Increase %")).doubleValue();
                double priceChg = decimalFormat.parse((String) map.get("Chg Rs")).doubleValue();
                double volChgPerc;
                double ltp = decimalFormat.parse((String) map.get("Last Price")).doubleValue();

                if (ltp < 500) {
                    try {
                        volChgPerc = decimalFormat.parse((String) map.get("% Change")).doubleValue();
                    } catch (ParseException e) {
                        volChgPerc = 0.0;
                    }
                    map.remove("Chg %");
                    map.remove("Chg Rs");
                    map.remove("% Change");


                    if (oiChgPerc > 100) {
                        oiChgPerc = 50 + oiChgPerc % 100;
                    }
                    if (volChgPerc > 100) {
                        volChgPerc = 100 + volChgPerc % 100;
                    }
                    if (priceChg > 20) {
                        priceChg = 20 + priceChg % 100;
                    }
                    map.put("Price CHG", priceChg);
                    map.put("OI CHG %", oiChgPerc);
                    map.put("VOL CHG %", volChgPerc);
                    map.put("timestamp", System.currentTimeMillis());

                    String json = new ObjectMapper().writeValueAsString(map);
                    putData(json, indexName);
                }
            }
        }catch (NullPointerException e){
            System.out.println("Exception occured!!!!!!!!");
        }
    }


    public void storeDataElasticSearchNSEBN(String key,Map map, String indexName,String otmIndexName,String otmRationINdexName ) throws ParseException, IOException {

        map.put("Stock",key);
            DecimalFormat decimalFormat = new DecimalFormat();
        double priceLTP ;
        double currentOI ;
        double changeOI;
        double vol;
            try {
                priceLTP = decimalFormat.parse((String) map.get("LTP")).doubleValue();
                currentOI = decimalFormat.parse((String) map.get("OI")).doubleValue();
// Random Long values to change the oi for testing
String randomValue= String.valueOf(new Random().nextLong()).substring(0,3);
Double randomDoubleValue=Double.valueOf(randomValue);
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
            map.put("OI",currentOI);

//            if(bnCount++<15){
//                map.put("Chng in OI", changeOI);
//            }
//            else
            map.put("Chng in OI", changeOI);

            map.put("Volume",vol);
// Adding the Average of LTP+CHG in OI +Volume
//        map.put("AVG of 3",priceLTP+vol/100);
// adding the Price +vol/chnginOI

        try {
//Apply the CHG OI length logic to calculate the AVG 3 Value
            int lengthChgOI = String.valueOf(changeOI).length()-2;
            int lengthPrice = String.valueOf(priceLTP).length()-1;
            int diffValue = lengthChgOI - lengthPrice;
            if (diffValue > 0) {
                Double avgValue = (((priceLTP)* LengthUtil.get10Digits(diffValue) / (changeOI)));
            if (!(avgValue > 1000)){
                map.put("AVG of 3", avgValue);}
            else {
                avgValue=1000+avgValue%1000;
                map.put("AVG of 3", avgValue / 1000);
            }
            }
        }
        catch (Exception e){
            System.out.println("Exception Occured: "+e);
            map.put("AVG of 3", -1.00d);
        }
        map.put("Price and Vol",priceLTP+vol);
        map.put("timestamp",System.currentTimeMillis());
            String json=new ObjectMapper().writeValueAsString(map);

            putData(json, indexName);
// Adding data to another index OTM data
            String currentStockName= (String) map.get("Stock");
        Double currentStockStrikePrice= Double.parseDouble(String.valueOf(map.get("Strike Price")));
// note To change the value
        Double bnCurrentValue= new NSEBankNiftyFetcher().getBnCurrentValue();
//        Double bnCurrentValue= new MCBNODFetcher().getBnCurrentValue();
if(indexName.contains("bn")) {

    if (currentStockName.contains("CE") && (currentStockStrikePrice - bnCurrentValue > 0) && (currentStockStrikePrice - bnCurrentValue < 100)) {
        putData(json, otmIndexName);
        putDataRatioIndex(listPETemp, changeOI, otmRationINdexName);
    } else if (currentStockName.contains("PE") && (currentStockStrikePrice - bnCurrentValue > -100) && (currentStockStrikePrice - bnCurrentValue < 0)) {
        putData(json, otmIndexName);
        listPETemp.add(changeOI);
    }
}else{
    if (currentStockName.contains("CE") && (currentStockStrikePrice - bnCurrentValue > 0) && (currentStockStrikePrice - bnCurrentValue < 50)) {
        putData(json, otmIndexName);
        putDataRatioIndex(listPETemp, changeOI, otmRationINdexName);
    } else if (currentStockName.contains("PE") && (currentStockStrikePrice - bnCurrentValue > -50) && (currentStockStrikePrice - bnCurrentValue < 0)) {
        putData(json, otmIndexName);
        listPETemp.add(changeOI);
    }
}

    }

    static void putDataRatioIndex(List list,double chngOICE,String indexName) throws IOException {
        Double chngOIPE= (Double) list.get(0);
        if(list.size()==1){
            list.clear();
        }
        Map ratioMapData= new HashMap();
        ratioMapData.put("OTM Ratio",chngOIPE/chngOICE);
        ratioMapData.put("timestamp",System.currentTimeMillis());
        String json=new ObjectMapper().writeValueAsString(ratioMapData);
        if (!json.contains("NaN")) {
            try {
                putData(json, indexName);
                putData(json, indexName+"all");
            } catch (Exception e) {
                System.out.println("OTM Ratio Exception Occured : setting 0 value");
                ratioMapData.put("OTM Ratio", 0);
                ratioMapData.put("timestamp", System.currentTimeMillis());
                json = new ObjectMapper().writeValueAsString(ratioMapData);
                putData(json, indexName);
                putData(json, indexName+"all");
            }
        }
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
//        new ElasticSearchUtil().clearElastChartData("incpriincoitop");


        new ElasticSearchUtil().clearElastChartData("bnnseoidata");
        new ElasticSearchUtil().clearElastChartData("bnotm");
        new ElasticSearchUtil().clearElastChartData("bnotmratio");
        new ElasticSearchUtil().clearElastChartData("bnotmratioall");
//
        new ElasticSearchUtil().clearElastChartData("niftyoidata");
        new ElasticSearchUtil().clearElastChartData("niftyotm");
        new ElasticSearchUtil().clearElastChartData("niftyotmratio");
        new ElasticSearchUtil().clearElastChartData("niftyotmratioall");
//
        new ElasticSearchUtil().clearElastChartData("incpriincoitop5");

         new ElasticSearchUtil().clearElastChartData("bn_oi_history");

         new ElasticSearchUtil().clearElastChartData("niftypcrindex");
        new ElasticSearchUtil().clearElastChartData("bnpcrindex");





        // Adding time stamp
//        Response responseBnNse= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"_doc\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}}")
//        .put("http://localhost:9200/incpriincoitop");
//        System.out.println(responseBnNse.prettyPrint());
//



        //888888*************
        Response responseIncTop= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
        .put("http://localhost:9200/bnnseoidata");
        System.out.println(responseIncTop.prettyPrint());

        Response responseOTM= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/bnotm");
        System.out.println(responseOTM.prettyPrint());

        Response responseOTMRatio= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/bnotmratio");
        System.out.println(responseOTMRatio.prettyPrint());

        responseOTMRatio= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/bnotmratioall");
        System.out.println(responseOTMRatio.prettyPrint());

        responseIncTop= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/niftyoidata");
        System.out.println(responseIncTop.prettyPrint());

        responseOTM= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/niftyotm");
        System.out.println(responseOTM.prettyPrint());

        responseOTMRatio= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/niftyotmratio");
        System.out.println(responseOTMRatio.prettyPrint());

        responseOTMRatio= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/niftyotmratioall");
        System.out.println(responseOTMRatio.prettyPrint());

        Response responseBNOIHistory= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/bn_oi_history");
        System.out.println(responseBNOIHistory.prettyPrint());
        Response incPriIncOiIndex= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/incpriincoitop5");
        System.out.println(incPriIncOiIndex.prettyPrint());

        Response niftyPcrIndex= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/niftypcrindex");
        System.out.println(niftyPcrIndex.prettyPrint());

        Response bnPcrIndex= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://localhost:9200/bnpcrindex");
        System.out.println(bnPcrIndex.prettyPrint());
    }
}