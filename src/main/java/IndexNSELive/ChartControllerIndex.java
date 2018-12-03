package IndexNSELive;

import ElasticSearch.ElasticSearchUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

public class ChartControllerIndex {


    void insertDatainCollection(String SymbolName, MongoDatabase db) throws ParseException, IOException {
        DecimalFormat numberFormat= (DecimalFormat) DecimalFormat.getInstance();
        MongoCollection<Document> dbCollection = db.getCollection("indexoption");


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
                newDoc.append("Price CHG %", numberFormat.parse((internalDoc.get("Chg %","0.0"))).doubleValue());
                newDoc.append("OI CHG %", numberFormat.parse(internalDoc.get("Increase %","0.0")).doubleValue());



    String volChangeString=internalDoc.getString("% Change");

    Double doubleValue;
    try{
    doubleValue=numberFormat.parse(volChangeString).doubleValue();}
    catch (Exception e){
        doubleValue=0.0;
    }
    newDoc.append("VOL CHG %", doubleValue);

// Adding first row data below


                newDoc.append("First Price CHG %", numberFormat.parse(firstDoc.get("Chg %","0.0")).doubleValue());
                newDoc.append("First OI CHG %", numberFormat.parse(firstDoc.get("Increase %","0.0")).doubleValue());


                volChangeString=firstDoc.getString("% Change");
                 numberFormat= (DecimalFormat) DecimalFormat.getInstance();
                try{
                    doubleValue=numberFormat.parse(volChangeString).doubleValue();}
                catch (Exception e){
                    doubleValue=0.0;
                }
                newDoc.append("First VOL CHG %", doubleValue);
                firstDoc.clear();

            } else {

                newDoc.append("Prev " + count + " Price CHG %",numberFormat.parse(internalDoc.get("Chg %","0.0")).doubleValue());
                newDoc.append("Prev " + count + " OI CHG %", numberFormat.parse(internalDoc.get("Increase %","0.0")).doubleValue());

                String volChangeString=internalDoc.getString("% Change");
                Double doubleValue;
                try{
                    System.out.println(volChangeString);
                    doubleValue= Double.valueOf(String.valueOf(numberFormat.parse(volChangeString)));}
                catch (Exception e){
                    doubleValue=0.0;
                }
                newDoc.append("Prev "+count+" VOL CHG %", doubleValue);

            }
                count++;

        }

        MongoCollection<Document> chartCollection = db.getCollection("indexcollectionchart");
        chartCollection.insertOne(newDoc);
        newDoc.remove("_id");
new ElasticSearchUtil().putData((newDoc.toJson()),"indexoption");
newDoc.clear();

    }

    void clearCollection(MongoDatabase db){
        MongoCollection<Document> dbCollection = db.getCollection("indexcollectionchart");
        dbCollection.drop();


    }

}
