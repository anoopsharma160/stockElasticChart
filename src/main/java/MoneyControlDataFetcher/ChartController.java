package MoneyControlDataFetcher;

import ElasticSearch.ElasticSearchUtil;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChartController {


    void insertDatainCollection(String SymbolName, MongoDatabase db) throws ParseException, IOException {

        MongoCollection<Document> dbCollection = db.getCollection("stockTest");


// Query and take the latetst symbol
//        BasicDBObject whereQuery=new BasicDBObject();
//        whereQuery.put("Symbol","ACC");
//
//        DBCursor dbCursor=dbCollection.find(whereQuery);
//        System.out.println(dbCursor.next().get(""));

//        Document document = dbCollection.find(Filters.eq("Symbol", SymbolName)).first();
MongoCursor<Document> firstDocMongoCursor=dbCollection.find(Filters.eq("Symbol",SymbolName))
        .sort(Sorts.ascending("timestamp"))
        .limit(1)
        .iterator();
Document firstDoc = null;
while (firstDocMongoCursor.hasNext()) {
    firstDoc = firstDocMongoCursor.next();
}


        MongoCursor<Document> mongoCursor = dbCollection.find(Filters.eq("Symbol", SymbolName))
                .sort(Sorts.descending("timestamp"))
                .limit(4)
                .iterator();

        Document newDoc = new Document();

        int count = 0;
        while (mongoCursor.hasNext()) {

//            System.out.println(mongoCursor.next());
            Document internalDoc = mongoCursor.next();
            if (count == 0) {
                newDoc.append("Symbol", internalDoc.getString("Symbol"));
                newDoc.append("Price CHG %", Double.valueOf(internalDoc.get("Chg %","0.0")));
                newDoc.append("OI CHG %", Double.valueOf(internalDoc.get("Increase %","0.0")));


    String volChangeString=internalDoc.getString("% Change");
    DecimalFormat numberFormat= (DecimalFormat) DecimalFormat.getInstance();
    Double doubleValue;
    try{
    doubleValue=numberFormat.parse(volChangeString).doubleValue();}
    catch (NullPointerException e){
        doubleValue=0.0;
    }
    newDoc.append("VOL CHG %", doubleValue);

// Adding first row data below


                newDoc.append("First Price CHG %", Double.valueOf(firstDoc.get("Chg %","0.0")));
                newDoc.append("First OI CHG %", Double.valueOf(firstDoc.get("Increase %","0.0")));


                volChangeString=firstDoc.getString("% Change");
                 numberFormat= (DecimalFormat) DecimalFormat.getInstance();
                try{
                    doubleValue=numberFormat.parse(volChangeString).doubleValue();}
                catch (NullPointerException e){
                    doubleValue=0.0;
                }
                newDoc.append("First VOL CHG %", doubleValue);
                firstDoc.clear();

            } else {

                newDoc.append("Prev " + count + " Price CHG %",Double.valueOf(internalDoc.get("Chg %","0.0")));
                newDoc.append("Prev " + count + " OI CHG %", Double.valueOf(internalDoc.get("Increase %","0.0")));

                String volChangeString=internalDoc.getString("% Change");
                DecimalFormat numberFormat= (DecimalFormat) DecimalFormat.getInstance();
                Double doubleValue;
                try{
                    System.out.println(volChangeString);
                    doubleValue= Double.valueOf(String.valueOf(numberFormat.parse(volChangeString)));}
                catch (NullPointerException e){
                    doubleValue=0.0;
                }
                newDoc.append("Prev "+count+" VOL CHG %", doubleValue);

            }
                count++;

        }

        MongoCollection<Document> chartCollection = db.getCollection("chartCollection");
        chartCollection.insertOne(newDoc);
        newDoc.remove("_id");
new ElasticSearchUtil().putData((newDoc.toJson()),"incpriincoi");
newDoc.clear();

    }

    void clearCollection(MongoDatabase db){
        MongoCollection<Document> dbCollection = db.getCollection("chartCollection");
        dbCollection.drop();


    }

}
