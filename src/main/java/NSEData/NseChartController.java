package NSEData;

import ElasticSearch.ElasticSearchUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class NseChartController {


public     void insertDatainCollection(String SymbolName, MongoDatabase db) throws IOException {

        MongoCollection<Document> dbCollection = db.getCollection("nseCollection");


// Query and take the latetst symbol
//        BasicDBObject whereQuery=new BasicDBObject();
//        whereQuery.put("Symbol","ACC");
//
//        DBCursor dbCursor=dbCollection.find(whereQuery);
//        System.out.println(dbCursor.next().get(""));

//        Document document = dbCollection.find(Filters.eq("Symbol", SymbolName)).first();

// First Row data of stock
MongoCursor<Document> firstRowCursor=dbCollection.find(Filters.eq("symbol",SymbolName))
        .sort(Sorts.ascending("timestamp"))
        .limit(1)
        .iterator();
Document firstDoc=null;
while (firstRowCursor.hasNext()){
    firstDoc=firstRowCursor.next();
}

        MongoCursor<Document> mongoCursor = dbCollection.find(Filters.eq("symbol", SymbolName))
                .sort(Sorts.descending("timestamp"))
                .limit(4)
                .iterator();

        Document newDoc = new Document();

        int count = 0;
        while (mongoCursor.hasNext()) {
//            System.out.println(mongoCursor.next());
            Document internalDoc = mongoCursor.next();
            if (count == 0) {
                newDoc.append("Symbol", internalDoc.getString("symbol"));
                newDoc.append("Price CHG %", Double.valueOf(internalDoc.get("per","0.0")));
                newDoc.append("Traded Volume", Double.valueOf(internalDoc.get("trdVol","0.0")));


                newDoc.append("First Price CHG %", Double.valueOf(firstDoc.get("per","0.0")));
                newDoc.append("First Traded Volume", Double.valueOf(firstDoc.get("trdVol","0.0")));

            } else {

                newDoc.append("Prev " + count + " Price CHG %",Double.valueOf(internalDoc.get("per","0.0")));
                newDoc.append("Prev " + count + " Traded Vol ", Double.valueOf(internalDoc.get("trdVol","0.0")));
            }
            count++;
        }

        MongoCollection<Document> chartCollection = db.getCollection("NSEChartCollection");
        chartCollection.insertOne(newDoc);
    System.out.println("JSON Odc Type "+newDoc.remove("_id"));
    //Adding timestamp for query in dashboard
    newDoc.put("timestamp",new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(System.currentTimeMillis()));
new ElasticSearchUtil().putData(newDoc.toJson(),"nseindex");

    }

    void clearCollection(MongoDatabase db){
        MongoCollection<Document> dbCollection = db.getCollection("NSEChartCollection");
        dbCollection.drop();


    }

}
