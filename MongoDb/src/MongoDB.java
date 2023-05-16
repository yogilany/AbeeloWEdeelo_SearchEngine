import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;


public class MongoDB {
    static MongoClient client = MongoClients.create("mongodb+srv://yogilany:7kkmGoukZJIOVIyK@abeelowedeelo.vduhnjn.mongodb.net/?retryWrites=true&w=majority");
    static MongoDatabase db = client.getDatabase("Dev");


    public static void main(String[] args) {
        System.out.print("Hello from MongoDB!");


    }

    // create a method that return the db object
    public static MongoDatabase getDB(){
        return db;
    }


}
