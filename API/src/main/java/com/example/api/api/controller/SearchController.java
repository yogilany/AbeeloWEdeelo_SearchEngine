package com.example.api.api.controller;


//import com.example.api.api.model.Query;
import com.example.api.service.QuerySearch;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class SearchController {

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/search")
    public QuerySearch.Result search(@RequestParam String search) throws IOException {
        System.out.println("Entered the endpoint");
        System.out.println("Search is: " + search);

        MongoClient client = MongoClients.create("mongodb+srv://yogilany:7kkmGoukZJIOVIyK@abeelowedeelo.vduhnjn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = client.getDatabase("Dev");


        return QuerySearch.Search(search, db);
    }

}
