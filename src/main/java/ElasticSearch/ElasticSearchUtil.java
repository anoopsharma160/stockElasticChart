package ElasticSearch;

import IndexNSELive.MCBNODFetcher;
import IndexNSELive.NSEBankNiftyFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import util.LengthUtil;
import util.PropUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

public class ElasticSearchUtil {
//        static int number=0;
    static  int bnCount=0;
    static List listPETemp= new ArrayList();
    static String elasticSearchIp;

    static {
        try {
            elasticSearchIp = PropUtils.getPropValue("elastic.hostname");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean putData(String dataJson, String indexName) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
//                new HttpHost("10.157.251.29", 9200, "http"),
//                new HttpHost("10.157.251.29", 9201, "http")

        new HttpHost(elasticSearchIp, 9200, "http"),
                new HttpHost(elasticSearchIp, 9201, "http")
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
//        System.out.println("ES : Doc Creation "+indexResponse);
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


    public void storeDataElasticSearchNSEBN(String key,Map map, String indexName,String otmIndexName,String otmRationINdexName, String bnBarCharIndex ) throws ParseException, IOException {

        map.put("Stock",key);
            DecimalFormat decimalFormat = new DecimalFormat();
        double priceLTP ;
        double currentOI ;
        double changeOI;
        double vol;
            try {
                priceLTP = decimalFormat.parse((String) map.get("lastPrice")).doubleValue();
                currentOI = decimalFormat.parse((String) map.get("openInterest")).doubleValue();
// Random Long values to change the oi for testing
String randomValue= String.valueOf(new Random().nextLong()).substring(0,3);
Double randomDoubleValue=Double.valueOf(randomValue);
                 changeOI = decimalFormat.parse((String) map.get("changeinOpenInterest")).doubleValue();
                 vol = decimalFormat.parse((String) map.get("totalTradedVolume")).doubleValue();
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
// Adding data to bat chart index

        putData(json,bnBarCharIndex);
// Adding data to another index OTM data
        String currentStockName= (String) map.get("Stock");
        Double currentStockStrikePrice= Double.parseDouble(String.valueOf(map.get("strikePrice")));
// note To change the value
        Double bnCurrentValue= decimalFormat.parse((String) map.get("underlyingValue")).doubleValue();
//        Double bnCurrentValue= new NSEBankNiftyFetcher().getBnCurrentValue();
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

    public void storeDataESNifty(String key,Map map, String indexName,String otmIndexName,String otmRationINdexName, String barChartIndex ) throws ParseException, IOException {

        map.put("Stock",key);
        DecimalFormat decimalFormat = new DecimalFormat();
        double priceLTP ;
        double currentOI ;
        double changeOI;
        double vol;
        try {
            priceLTP = decimalFormat.parse((String) map.get("lastPrice")).doubleValue();
            currentOI = decimalFormat.parse((String) map.get("openInterest")).doubleValue();
// Random Long values to change the oi for testing
            String randomValue= String.valueOf(new Random().nextLong()).substring(0,3);
            Double randomDoubleValue=Double.valueOf(randomValue);
            changeOI = decimalFormat.parse((String) map.get("changeinOpenInterest")).doubleValue();
            vol = decimalFormat.parse((String) map.get("totalTradedVolume")).doubleValue();
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
// adding data to barChartIndex
//        deleteIndex(barChartIndex);
        putData(json, barChartIndex);
// Adding data to another index OTM data
        String currentStockName= (String) map.get("Stock");
        Double currentStockStrikePrice= Double.parseDouble(String.valueOf(map.get("strikePrice")));
// note To change the value
//        Double bnCurrentValue= new NSEBankNiftyFetcher().getBnCurrentValue();
        Double bnCurrentValue= decimalFormat.parse((String) map.get("underlyingValue")).doubleValue();
//        Double bnCurrentValue= new MCBNODFetcher().getBnCurrentValue();
        if(indexName.contains("bn")) {

            if (currentStockName.contains("CE") && (currentStockStrikePrice - bnCurrentValue > 0) && (currentStockStrikePrice - bnCurrentValue < 100)) {
                putData(json, otmIndexName);
//                putDataRatioIndex(listPETemp, changeOI, otmRationINdexName);
            } else if (currentStockName.contains("PE") && (currentStockStrikePrice - bnCurrentValue > -100) && (currentStockStrikePrice - bnCurrentValue < 0)) {
                putData(json, otmIndexName);
                listPETemp.add(changeOI);
            }
        }else{
            if (currentStockName.contains("CE") && (currentStockStrikePrice - bnCurrentValue > 0) && (currentStockStrikePrice - bnCurrentValue < 50)) {
                putData(json, otmIndexName);
//                putDataRatioIndex(listPETemp, changeOI, otmRationINdexName);
            } else if (currentStockName.contains("PE") && (currentStockStrikePrice - bnCurrentValue > -50) && (currentStockStrikePrice - bnCurrentValue < 0)) {
                putData(json, otmIndexName);
                listPETemp.add(changeOI);
            }
        }

    }

    static void putDataRatioIndex(List list,double chngOICE,String indexName) throws IOException {
        Double chngOIPE= (Double) list. get(0);
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

    public void clearIndexData(String indexName) throws IOException, InterruptedException {
        Thread.sleep(2000);
        String query ="{\n" +
                " \"query\": {\n" +
                " \"match_all\": {}\n" +
                " }\n" +
                "}";
        Response response = RestAssured.given().log().all().body(query).contentType(ContentType.JSON).post("http://"+elasticSearchIp+":9200/"+indexName+"/_delete_by_query?conflicts=proceed");
        System.out.println(response.prettyPrint());


    }

    public void deleteIndex(String indexName) throws IOException, InterruptedException {
        Thread.sleep(2000);
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(elasticSearchIp, 9200, "http"),
                new HttpHost(elasticSearchIp, 9201, "http")
        ));

 //Clear the elastic Search Index first
        DeleteIndexRequest deleteIndexRequest= new DeleteIndexRequest(indexName);
        client.indices().delete(deleteIndexRequest,RequestOptions.DEFAULT);
        client.close();

    }

    public void addIndex(String indexName) throws IOException, InterruptedException {
        Thread.sleep(2000);
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(elasticSearchIp, 9200, "http"),
                new HttpHost(elasticSearchIp, 9201, "http")
        ));

        //Clear the elastic Search Index first
        IndexRequest indexRequest= new IndexRequest(indexName);
//        RequestOptions requestOptions = RequestOptions.Builder.
//        client.indices().create();
        client.close();

    }

    public Response importSavedObject(String fileName){
        String host = "http://"+elasticSearchIp+":5601";
        String apiEndPoint = "/api/saved_objects/_import?overwrite=true";
        Response response = RestAssured.given().contentType("multipart/form-data")
                .header("kbn-xsrf","true")
                .multiPart("file",new File(fileName))
                .post(host+apiEndPoint);
        return response;


    }
    public void addIndex(){

        // Adding time stamp
        Response responseBnNse= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"_doc\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}}")
        .put("http://"+elasticSearchIp+":9200/incpriincoitop");
        System.out.println(responseBnNse.prettyPrint());





        Response responseIncTop= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
        .put("http://"+elasticSearchIp+":9200/bnnseoidata");
        System.out.println(responseIncTop.prettyPrint());

        Response responseOTM= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/bnotm");
        System.out.println(responseOTM.prettyPrint());

        Response responseOTMRatio= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/bnotmratio");
        System.out.println(responseOTMRatio.prettyPrint());

        responseOTMRatio= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/bnotmratioall");
        System.out.println(responseOTMRatio.prettyPrint());

        responseIncTop= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/niftyoidata");
        System.out.println(responseIncTop.prettyPrint());

        responseOTM= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/niftyotm");
        System.out.println(responseOTM.prettyPrint());

        responseOTMRatio= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/niftyotmratio");
        System.out.println(responseOTMRatio.prettyPrint());

        responseOTMRatio= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/niftyotmratioall");
        System.out.println(responseOTMRatio.prettyPrint());

        Response responseBNOIHistory= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/bn_oi_history");
        System.out.println(responseBNOIHistory.prettyPrint());
        Response incPriIncOiIndex= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/incpriincoitop5");
        System.out.println(incPriIncOiIndex.prettyPrint());

        Response niftyBarChartIndex= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/niftybarchart");
        System.out.println(niftyBarChartIndex.prettyPrint());

        Response bnBarChartIndex= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}")
                .put("http://"+elasticSearchIp+":9200/bnbarchart");
        System.out.println(bnBarChartIndex.prettyPrint());

        Response niftyPcrIndex= RestAssured.given().contentType("application/json").body("{\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"timestamp\": {\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"publisher\": {\n" +
                "          \"type\": \"text\",\n" +
                "          \"fielddata\": true\n" +
                "        }\n" +
                "    }\n" +
                "  }\n" +
                "}")
                .put("http://"+elasticSearchIp+":9200/niftypcrindex");
        System.out.println(niftyPcrIndex.prettyPrint());

        Response bnPcrIndex= RestAssured.given().contentType("application/json").body("{\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"timestamp\": {\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"publisher\": {\n" +
                "          \"type\": \"text\",\n" +
                "          \"fielddata\": true\n" +
                "        }\n" +
                "    }\n" +
                "  }\n" +
                "}")
                .put("http://"+elasticSearchIp+":9200/bnpcrindex");
        System.out.println(bnPcrIndex.prettyPrint());
    }

    public static void main(String[] args) throws IOException {
        String []index_list = {"bnnseoidata","bnotm","bnotmratio","bnotmratioall","niftyoidata","niftyotm","niftyotmratio"
        ,"niftyotmratioall","incpriincoitop5","bn_oi_history","niftypcrindex","bnpcrindex","niftybarchart","bnbarchart"};
//        new ElasticSearchUtil().deleteIndex("incpriincoitop");
//        Response response = new ElasticSearchUtil().importSavedObject("src/main/resources/elasticSearchBackup/export.ndjson");
//        System.out.println("Import data Response : "+response.getStatusLine());
//        System.out.println("Import data Response : "+response.getStatusCode());
//        System.out.println("API Respoonse : "+response.prettyPrint());
        ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil();
    try {
        for (int i = 0; i <index_list.length ; i++) {
            System.out.println("Clearing data for index : "+index_list[i]);
            elasticSearchUtil.clearIndexData(index_list[i]);
        }

//        Deleting index
        for (int i = 0; i <index_list.length ; i++) {
            System.out.println("Deleting index : "+index_list[i]);
            Thread.sleep(1000);
            elasticSearchUtil.deleteIndex(index_list[i]);
        }
        elasticSearchUtil.addIndex();
//8


    }
    catch (Exception e){
        System.out.println("Exception catched!!!");
        System.out.println("Index not found in elasticsearch");
        elasticSearchUtil.addIndex();
    }



    }
}