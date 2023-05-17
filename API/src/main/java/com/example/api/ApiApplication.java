package com.example.api;
import com.example.api.service.QuerySearch;
import com.mongodb.client.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;


@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ApiApplication.class, args);

        System.out.println("Server started!");
//        MongoClient client = MongoClients.create("mongodb+srv://yogilany:7kkmGoukZJIOVIyK@abeelowedeelo.vduhnjn.mongodb.net/?retryWrites=true&w=majority");
//        MongoDatabase db = client.getDatabase("Dev");

//        QuerySearch querySearch = new QuerySearch();
        // recieve the result from the querySearch
//        QuerySearch.Result result = querySearch.Search("Hello World", db);
        // print the result
//        System.out.println(result.print());


    }

}
