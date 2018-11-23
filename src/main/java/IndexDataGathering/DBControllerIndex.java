package IndexDataGathering;

import ElasticSearch.ElasticSearchUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public class DBControllerIndex {

    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        new DBControllerIndex().execute(0);
    }

    public void execute(int sleepTime) throws IOException, InterruptedException, ParseException {
Thread.sleep(sleepTime*1000);
            MongoClient mongoClient = new MongoClient("localhost");
            MongoDatabase db = mongoClient.getDatabase("test");
//            DB db = mongoClient.getDB("test");
            MongoCollection<Document> dbCollection = db.getCollection("indexoption");

            Map<String, Map<String, String>> map = new MoneyControllerIndexFetcher().getMappedData();

            for (String key : map.keySet()) {
                Map<String, String> internalMap = map.get(key);
                internalMap.put("Symbol",internalMap.get("Symbol")+" "+internalMap.get("Strike Price")+" "+internalMap.get("Option Type"));
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
            new ChartControllerIndex().clearCollection(db);


    new ElasticSearchUtil().clearElastChartData("indexoption");


// 3 Create second Collection with last 3 data points
            for (String symbolName : map.keySet()) {
                Map<String, String> internalMap = map.get(symbolName);
//                symbolName=symbolName+internalMap.get("Strike Price")+" "+internalMap.get("Option Type");
                System.out.println("Symbol name in insertion: "+symbolName);
                new ChartControllerIndex().insertDatainCollection(symbolName, db);

                Document document = new Document();
                document.putAll(internalMap);
                document.remove("_id");
                new ElasticSearchUtil().storeDataElasticSearch(internalMap,"indexoidata");
            }
            map.clear();
            mongoClient.close();
        }
    }

