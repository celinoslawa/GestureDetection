package com.example.agnieszka.openvctest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by agnieszka on 11.09.17.
 */

public class JsonReader {
    String in;
    InputStream responsesJSON;
    InputStream hog_descriptorsJSON;
    JSONObject reader = new JSONObject(in);
    JSONArray jArray = reader.getJSONArray("list");
    public JsonReader(InputStream responses, InputStream hog)
    {
        responsesJSON = responses;
        hog_descriptorsJSON = hog;
    }
    public void parsing() {

    }
}
