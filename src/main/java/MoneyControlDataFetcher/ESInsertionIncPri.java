package MoneyControlDataFetcher;

import ElasticSearch.ElasticSearchUtil;
import IndexNSELive.NSEBankNiftyFetcher;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Map;

public class ESInsertionIncPri {

    public void execute(int sleepTime) throws IOException, InterruptedException, ParseException {
        Thread.sleep(sleepTime * 1000);
        Map<String, Map<String, String>> map=new MoneyControllerScrapper().getMappedData();

        for (String key : map.keySet()) {
            if(key!=""&&key!=null) {
                Map<String, String> internalMap = map.get(key);
                System.out.println(internalMap.get("Symbol"));
                    new ElasticSearchUtil().storeDataElasticSearchTopstocks(internalMap, "incpriincoitop5");

            }



        }
        map.clear();
}

    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        new ESInsertionIncPri().execute(0);
    }
    }
