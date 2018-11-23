package MoneyControlDataFetcher;

import ElasticSearch.ElasticSearchUtil;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public class DBController {

    public void execute(int sleepTime) throws IOException, InterruptedException, ParseException {
Thread.sleep(sleepTime*1000);
            MongoClient mongoClient = new MongoClient("localhost");
            MongoDatabase db = mongoClient.getDatabase("test");
//            DB db = mongoClient.getDB("test");
            MongoCollection<Document> dbCollection = db.getCollection("stockTest");

            Map<String, Map<String, String>> map = new MoneyControllerScrapper().getMappedData();

            for (String key : map.keySet()) {
                Map<String, String> internalMap = map.get(key);
                System.out.println(internalMap);
                internalMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
                BasicDBObject basicDBObject = new BasicDBObject();
                basicDBObject.putAll(internalMap);
//                dbCollection.insertOne(basicDBObject);

                Document document = new Document();
                document.putAll(internalMap);
                dbCollection.insertOne(document);
            }

//
//            FindIterable<BasicDBObject> cursorDocMap = dbCollection.find();
//            while (cursorDocMap.has) {
//                System.out.println(cursorDocMap.next());
//            }

            MongoCursor<Document> mongoCursor = dbCollection.find().iterator();
//2 Clear the Chart Collection before adding new data to it
            new ChartController().clearCollection(db);


    new ElasticSearchUtil().clearElastChartData("incpriincoi");


// 3 Create second Collection with last 3 data points
            for (String symbolName : map.keySet()) {
                new ChartController().insertDatainCollection(symbolName, db);

            }
            map.clear();
            mongoClient.close();
        }
    }

