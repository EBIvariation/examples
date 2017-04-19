import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.BsonDocument;
import org.bson.Document;
import com.flipkart.zjsonpatch.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;


/**
 * Created by sundarvenkata on 06/02/17.
 */
public class CreateCombinedAnnotation {
    public static void CreateCombinedAnnotationSnappy ()
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s:27017/admin",System.getenv("MONGODEV_UNAME"), System.getenv("MONGODEV_PASS"), System.getenv("MONGODEV_HOST"))));
        MongoDatabase db = mongoClient.getDatabase("eva_testing");

        MainApp.recreateCollection(db, "var_annot_comb_85_87_snappy", null);

        MongoCollection<Document> annotColl1 = db.getCollection("variants_annot_87_87");
        MongoCollection<Document> annotColl2 = db.getCollection("variants_annot_85_85");
        MongoCollection<Document> combinedColl = db.getCollection("var_annot_comb_85_87_snappy");

        int counter = 0;
        for (Document doc:
                annotColl2.find()) {
            String idString = doc.get("_id").toString();
            Document coll1AnnotObj = (Document)(annotColl1.find(eq("_id",idString)).first());
            Document combinedDoc = new Document();
            Document combinedAnnotDoc = new Document();

            coll1AnnotObj.remove("_id");
            doc.remove("_id");

            combinedAnnotDoc.put("ver1", coll1AnnotObj);
            combinedAnnotDoc.put("ver2", doc);
            combinedDoc.put("_id", idString);
            combinedDoc.put("annot", combinedAnnotDoc);
            combinedColl.insertOne(combinedDoc);
            counter += 1;
        }

        mongoClient.close();
    }

    public static void CreateCombinedAnnotationZlib ()
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s:27017/admin",System.getenv("MONGODEV_UNAME"), System.getenv("MONGODEV_PASS"), System.getenv("MONGODEV_HOST"))));
        MongoDatabase db = mongoClient.getDatabase("eva_testing");

        CreateCollectionOptions options = new CreateCollectionOptions();
        options.storageEngineOptions(BsonDocument.parse("{'wiredTiger':{'configString':'block_compressor=zlib'}}"));
        MainApp.recreateCollection(db, "var_annot_comb_85_87_zlib", options);

        MongoCollection<Document> annotColl1 = db.getCollection("variants_annot_87_87");
        MongoCollection<Document> annotColl2 = db.getCollection("variants_annot_85_85");
        MongoCollection<Document> combinedColl = db.getCollection("var_annot_comb_85_87_zlib");

        int counter = 0;
        for (Document doc:
                annotColl2.find()) {
            String idString = doc.get("_id").toString();
            Document coll1AnnotObj = (Document)(annotColl1.find(eq("_id",idString)).first());
            Document combinedDoc = new Document();
            Document combinedAnnotDoc = new Document();

            coll1AnnotObj.remove("_id");
            doc.remove("_id");

            combinedAnnotDoc.put("ver1", coll1AnnotObj);
            combinedAnnotDoc.put("ver2", doc);
            combinedDoc.put("_id", idString);
            combinedDoc.put("annot", combinedAnnotDoc);
            combinedColl.insertOne(combinedDoc);
            counter += 1;
        }

        mongoClient.close();
    }

    public static void CreateCombinedAnnotationZlibSep ()
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s:27017/admin",System.getenv("MONGODEV_UNAME"), System.getenv("MONGODEV_PASS"), System.getenv("MONGODEV_HOST"))));
        MongoDatabase db = mongoClient.getDatabase("eva_testing");

        CreateCollectionOptions options = new CreateCollectionOptions();
        options.storageEngineOptions(BsonDocument.parse("{'wiredTiger':{'configString':'block_compressor=zlib'}}"));
        MainApp.recreateCollection(db, "var_annot_comb_85_87_zlib_sep", options);

        MongoCollection<Document> annotColl1 = db.getCollection("variants_annot_87_87");
        MongoCollection<Document> annotColl2 = db.getCollection("variants_annot_85_85");
        MongoCollection<Document> combinedColl = db.getCollection("var_annot_comb_85_87_zlib_sep");

        int counter = 0;
        for (Document doc:
                annotColl2.find()) {
            String idString = doc.get("_id").toString();
            Document coll1AnnotObj = (Document)(annotColl1.find(eq("_id",idString)).first());
            Document combinedDoc = new Document();
            Document combinedAnnotDoc = new Document();


            combinedDoc.put("_id", idString + "_85_85");
            combinedDoc.put("ct", doc.get("ct"));
            combinedDoc.put("xrefs", doc.get("xrefs"));
            combinedColl.insertOne(combinedDoc);

            combinedDoc.put("_id", idString + "_87_87");
            combinedDoc.put("ct", coll1AnnotObj.get("ct"));
            combinedDoc.put("xrefs", coll1AnnotObj.get("xrefs"));
            combinedColl.insertOne(combinedDoc);
            counter += 1;
        }

        mongoClient.close();
    }

    public static void CreateCombinedAnnotationPatchSnappy () throws IOException
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s:27017/admin",System.getenv("MONGODEV_UNAME"), System.getenv("MONGODEV_PASS"), System.getenv("MONGODEV_HOST"))));
        MongoDatabase db = mongoClient.getDatabase("eva_testing");

        MainApp.recreateCollection(db, "var_annot_comb_85_87_patch_snappy", null);

        MongoCollection<Document> annotColl1 = db.getCollection("variants_annot_87_87");
        MongoCollection<Document> annotColl2 = db.getCollection("variants_annot_85_85");
        MongoCollection<Document> combinedColl = db.getCollection("var_annot_comb_85_87_patch_snappy");

        final ObjectMapper mapper = new ObjectMapper();

        int counter = 0;
        for (Document doc:
                annotColl2.find()) {
            String idString = doc.get("_id").toString();
            Document coll1AnnotObj = (Document)(annotColl1.find(eq("_id",idString)).first());
            coll1AnnotObj.remove("_id");
            doc.remove("_id");
            String patchString = JsonDiff.asJson(mapper.readTree(doc.toJson()), mapper.readTree(coll1AnnotObj.toJson())).toString();

            Document combinedDoc = new Document();
            Document combinedAnnotDoc = new Document();

            combinedAnnotDoc.put("ver1", coll1AnnotObj);
            combinedAnnotDoc.put("ver2", patchString);
            combinedDoc.put("_id", idString);
            combinedDoc.put("annot", combinedAnnotDoc);
            combinedColl.insertOne(combinedDoc);
            counter += 1;
        }

        mongoClient.close();
    }

    public static void CreateCombinedAnnotationPatchZlib () throws IOException
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s:27017/admin",System.getenv("MONGODEV_UNAME"), System.getenv("MONGODEV_PASS"), System.getenv("MONGODEV_HOST"))));
        MongoDatabase db = mongoClient.getDatabase("eva_testing");

        CreateCollectionOptions options = new CreateCollectionOptions();
        options.storageEngineOptions(BsonDocument.parse("{'wiredTiger':{'configString':'block_compressor=zlib'}}"));
        MainApp.recreateCollection(db, "var_annot_comb_85_87_patch_zlib", options);

        MongoCollection<Document> annotColl1 = db.getCollection("variants_annot_87_87");
        MongoCollection<Document> annotColl2 = db.getCollection("variants_annot_85_85");
        MongoCollection<Document> combinedColl = db.getCollection("var_annot_comb_85_87_patch_zlib");

        final ObjectMapper mapper = new ObjectMapper();

        int counter = 0;
        for (Document doc:
                annotColl2.find()) {
            String idString = doc.get("_id").toString();
            Document coll1AnnotObj = (Document)(annotColl1.find(eq("_id",idString)).first());
            coll1AnnotObj.remove("_id");
            doc.remove("_id");
            String patchString = JsonDiff.asJson(mapper.readTree(doc.toJson()), mapper.readTree(coll1AnnotObj.toJson())).toString();

            Document combinedDoc = new Document();
            Document combinedAnnotDoc = new Document();

            combinedAnnotDoc.put("ver1", coll1AnnotObj);
            combinedAnnotDoc.put("ver2", patchString);
            combinedDoc.put("_id", idString);
            combinedDoc.put("annot", combinedAnnotDoc);
            combinedColl.insertOne(combinedDoc);
            counter += 1;
        }

        mongoClient.close();
    }

    public static void MoveAnnotToZlibCollection () throws IOException
    {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%s@%s:27017/admin",System.getenv("MONGODEV_UNAME"), System.getenv("MONGODEV_PASS"), System.getenv("MONGODEV_HOST"))));
        MongoDatabase db = mongoClient.getDatabase("eva_testing");

        //CreateCollectionOptions options = new CreateCollectionOptions();
        //options.storageEngineOptions(BsonDocument.parse("{'wiredTiger':{'configString':'block_compressor=zlib'}}"));
        //MainApp.recreateCollection(db, "variants_annot_85_85_zlib", options);
        //MainApp.recreateCollection(db, "variants_annot_85_85_zlib", options);

        //MongoCollection<Document> annotColl1 = db.getCollection("variants_annot_87_87");
        MongoCollection<Document> annotColl2 = db.getCollection("variants_annot_85_85");
        //MongoCollection<Document> annotColl1Zlib = db.getCollection("variants_annot_87_87_zlib");
        MongoCollection<Document> annotColl2Zlib = db.getCollection("variants_annot_85_85_zlib");


        for (Document doc: annotColl2.find()) {annotColl2Zlib.insertOne(doc);}

        mongoClient.close();
    }
}
