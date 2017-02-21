import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by sundarvenkata on 06/02/17.
 */

public class ExtractAnnotations {
    public static void ExtractAnnotations()
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s:27017/admin",System.getenv("MONGODEV_UNAME"), System.getenv("MONGODEV_PASS"), System.getenv("MONGODEV_HOST"))));
        MongoDatabase db = mongoClient.getDatabase("eva_testing");

        MongoCollection<Document> coll1 = db.getCollection("variants_hsap_87_87");
        MongoCollection<Document> coll2 = db.getCollection("variants_hsap_85_85");

        MainApp.recreateCollection(db, "variants_annot_85_85", null);
        MainApp.recreateCollection(db, "variants_annot_87_87", null);

        MongoCollection<Document> annotColl1 = db.getCollection("variants_annot_87_87");
        MongoCollection<Document> annotColl2 = db.getCollection("variants_annot_85_85");

        int counter = 0;
        for (Document doc:
                coll2.find()) {
            String idString = doc.get("_id").toString();
            Document coll2AnnotObj = (Document)(doc.get("annot"));
            Document coll1AnnotObj = (Document)(coll1.find(eq("_id",idString)).first().get("annot"));
            coll2AnnotObj.put("_id", idString);
            coll2AnnotObj.put("chr", doc.get("chr"));
            coll2AnnotObj.put("start", doc.get("start"));
            coll2AnnotObj.put("end", doc.get("end"));

            coll1AnnotObj.put("_id", idString);
            coll1AnnotObj.put("chr", doc.get("chr"));
            coll1AnnotObj.put("start", doc.get("start"));
            coll1AnnotObj.put("end", doc.get("end"));

            annotColl1.insertOne(coll1AnnotObj);
            annotColl2.insertOne(coll2AnnotObj);
            counter += 1;
        }

        mongoClient.close();
    }
}

