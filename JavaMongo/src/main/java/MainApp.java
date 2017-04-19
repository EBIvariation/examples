/**
 * Created by sundarvenkata on 27/01/17.
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.mongodb.*;
import com.github.fge.*;
//import ga4gh.AlleleAnnotations;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.BSONObject;

import org.bson.BsonDocument;
import org.bson.Document;

import org.bson.BasicBSONObject;
import org.javers.core.metamodel.annotation.Id;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

import com.google.gson.*;
import org.opencb.biodata.models.variant.annotation.*;
import java.lang.reflect.Type;
import org.javers.core.*;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.flipkart.zjsonpatch.*;
import static org.skyscreamer.jsonassert.JSONAssert.*;


public class MainApp {

    class ctRep
    {
        private String gn;
        private String ensg;
        private String enst;
        private String codon;
        private String strand;
        private String bt;
        private String aaChange;
        private Set<Double> so;
    }

    class xrefRep
    {
        private String id;
        private String src;
    }

    class VariantAnnotRep
    {
        @Id
        private String annotId;
        private Set<ctRep> ct;
        private Set<xrefRep> xrefs;
        VariantAnnotRep () {}
    }

    public static void recreateCollection(MongoDatabase db, String collectionName, CreateCollectionOptions options)
    {
        try {

                MongoCollection<Document> myCollection = db.getCollection(collectionName);
                myCollection.drop();
        }
        catch(NoSuchElementException ex)
        {

        }
        finally {
            if (options != null) db.createCollection(collectionName, options);
            else db.createCollection(collectionName);
        }
    }

    public static void main (String[] args) throws Exception {
        ExtractAnnotations.ExtractAnnotations();
        //CreateCombinedAnnotation.CreateCombinedAnnotationSnappy();
        //CreateCombinedAnnotation.CreateCombinedAnnotationZlib();
        //CreateCombinedAnnotation.CreateCombinedAnnotationPatchSnappy();
        //CreateCombinedAnnotation.CreateCombinedAnnotationPatchZlib();
        //CreateCombinedAnnotation.CreateCombinedAnnotationZlibSep();
        //CreateCombinedAnnotation.MoveAnnotToZlibCollection();

        //DBObject options = (DBObject)JSON.parse("{'wiredTiger':{'configString':'block_compressor=zlib'}}");
        //DBObject options = com.mongodb.BasicDBObjectBuilder.start().add("wiredTiger.creationString", "block_compressor=zlib").get();
        //System.out.println(options);
        //recreateCollection(db, "randomColl", options);
        //mongoClient.close();


        //Javers javers = JaversBuilder.javers().build();

        //System.out.println(firstDoc.get("annotVer"));
        //final ObjectMapper mapper = new ObjectMapper();
        //final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();


        //CreateCollectionOptions options = new CreateCollectionOptions();
        //options.storageEngineOptions(BsonDocument.parse("{'wiredTiger':{'configString':'block_compressor=zlib'}}"));
        //db.createCollection("someRandomColl", options);

        /*Document doc1 = coll1.find(eq("_id","1_890447__A")).projection(new Document("annot",true)).first();
        Document doc2 = coll2.find(eq("_id","1_890447__A")).projection(new Document("annot",true)).first();
        System.out.println(doc1.toJson());*/

        /*for (Document doc1:
                coll1.find().projection(new Document("annot",true))) {
            String doc1JSON = doc1.toJson();
            Document doc2 = coll2.find(eq("_id", doc1.get("_id").toString())).projection(new Document("annot",true)).first();
            String doc2JSON = doc2.toJson();
            if(JSONCompare.compareJSON(doc1JSON, doc2JSON, JSONCompareMode.LENIENT).failed())
            {
                //System.out.println("Doc1 JSON:" + doc1JSON);
                System.out.println("Patch:" + com.flipkart.zjsonpatch.JsonDiff.asJson(mapper.readTree(doc1JSON), mapper.readTree(doc2JSON)));
            }
        }*/

        /*String doc1JSON = doc1.toJson();
        String doc2JSON = doc2.toJson();
        System.out.println("Cache 86 JSON:" + doc1JSON);
        System.out.println("Cache 87 JSON:" + doc2JSON);
        System.out.println("Patch:"+ com.flipkart.zjsonpatch.JsonDiff.asJson(mapper.readTree(doc1.toJson()), mapper.readTree(doc2.toJson())));
        db.createCollection("patches");*/


        /*String annotString1 = new String(Files.readAllBytes(Paths.get("/home/sundarvenkata/mystuff/work/JavaMongo/src/main/java/JsonString1.txt")));
        JsonNode nodeObj1 = mapper.readTree(annotString1);

        String annotString2 = new String(Files.readAllBytes(Paths.get("/home/sundarvenkata/mystuff/work/JavaMongo/src/main/java/JsonString2.txt")));
        JsonNode nodeObj2 = mapper.readTree(annotString2);

        String annotString3 = new String(Files.readAllBytes(Paths.get("/home/sundarvenkata/mystuff/work/JavaMongo/src/main/java/JsonString3.txt")));
        JsonNode nodeObj3 = mapper.readTree(annotString3);

        String annotString4 = new String(Files.readAllBytes(Paths.get("/home/sundarvenkata/mystuff/work/JavaMongo/src/main/java/JsonString4.txt")));
        JsonNode nodeObj4 = mapper.readTree(annotString4);*/

        //GsonBuilder builder = new GsonBuilder();
        /*builder.setFieldNamingStrategy(new FieldNamingStrategy() {
            @Override
            public String translateName(java.lang.reflect.Field f) {
                switch (f.getName())
                {
                    case "geneName": return "gn";
                    case "ensemblGeneId": return "ensg";
                    case "ensemblTranscriptId": return "enst";
                    case "biotype": return "bt";
                    case "cDnaPosition": return "cDnaPos";
                    case "cdsPosition": return "cdsPos";
                    case "aaPosition": return "aaPos";
                    case "aaChange": return "aaChange";
                    case "relativePosition": return "relPos";
                    case "consequenceTypes": return "ct";
                    case "proteinSubstitutionScores": return "so";
                }
                return f.getName();
            }
        });*/

        class ScoreDeserializer implements JsonDeserializer<Score>
        {
            @Override
            public Score deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException
            {
                Double score = Double.parseDouble(json.getAsString());
                return new Score(score, null, null);
            }
        }

        //builder.registerTypeAdapter(Score.class, new ScoreDeserializer());
        //Gson gson = builder.create();
        //VariantAnnotation annotRep1 = gson.fromJson(annotString1, VariantAnnotation.class);
        //VariantAnnotation annotRep3 = gson.fromJson(annotString3, VariantAnnotation.class);
        /*VariantAnnotRep annotRep1 = gson.fromJson(annotString1, VariantAnnotRep.class);
        VariantAnnotRep annotRep3 = gson.fromJson(annotString3, VariantAnnotRep.class);
        VariantAnnotRep annotRep4 = gson.fromJson(annotString4, VariantAnnotRep.class);

        System.out.println(javers.compare(annotRep1, annotRep3));
        System.out.println(javers.compare(annotRep1, annotRep4));

        JsonNode patchNode = com.flipkart.zjsonpatch.JsonDiff.asJson(nodeObj1, nodeObj3);
        //System.out.println(annotRep.getConsequenceTypes().get(0));
        System.out.println("Using Flipkart JSON Diff:" + patchNode.toString());
        patchNode = JsonDiff.asJson(nodeObj3, nodeObj1);
        System.out.println("Using JSON Diff:" + patchNode.toString());

        patchNode = com.flipkart.zjsonpatch.JsonDiff.asJson(nodeObj1, nodeObj4);
        //System.out.println(annotRep.getConsequenceTypes().get(0));
        System.out.println("Using Flipkart JSON Diff:" + patchNode.toString());
        patchNode = JsonDiff.asJson(nodeObj3, nodeObj1);
        System.out.println("Using JSON Diff:" + patchNode.toString());*/
        /*if (JSONCompare.compareJSON(annotString1, annotString3, JSONCompareMode.LENIENT).failed())
        {
            System.out.println("Failed");
        }
        else
        {
            System.out.println("Passed");
        }*/
    }
}
