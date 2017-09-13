package com.example.agnieszka.openvctest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by agnieszka on 11.09.17.
 */

public class JsonReader {

    private static final String TAG = "JsonReader";
    List<ArrayList<Double>> hogList = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> hogElementList = new ArrayList<Double>();
    double hogElement;

    List<ArrayList<Integer>> respList = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> respElementList = new ArrayList<Integer>();
    int respElement;

    InputStream responsesJSON;
    InputStream hog_descriptorsJSON;
    ByteArrayOutputStream byteArrayOutputStreamRES = new ByteArrayOutputStream();
    ByteArrayOutputStream byteArrayOutputStreamHOG = new ByteArrayOutputStream();

    public JsonReader(InputStream responses, InputStream hog)
    {

        responsesJSON = responses;
        hog_descriptorsJSON = hog;

        int ctr;
        int ctr1;
        try {
            ctr = responsesJSON.read();
            ctr1 = hog_descriptorsJSON.read();
            while ((ctr & ctr1) != -1) {
                byteArrayOutputStreamRES.write(ctr);
                ctr = responsesJSON.read();
                byteArrayOutputStreamHOG.write(ctr1);
                ctr1 = hog_descriptorsJSON.read();
            }
            responsesJSON.close();
            hog_descriptorsJSON.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parsing()
    {
        Log.v(TAG,"Parsing JSON");
        try {
            // Parse the data into jsonobject to get original data in form of json.
            JSONArray jArrayRES = new JSONArray(byteArrayOutputStreamRES.toByteArray());
            JSONArray jArrayHOG = new JSONArray(byteArrayOutputStreamHOG.toByteArray());

            // Parsing responses

            for (int i = 0; i < jArrayRES.length(); i++)
            {
                for (int j = 0; j < jArrayRES.getJSONArray(i).length(); j++)
                {
                    respElement = jArrayRES.getJSONArray(i).getInt(j);
                    respElementList.add(respElement);
                }
                respList.add(respElementList);

            }
            //Pasing HOG vectors

            for (int i = 0; i < jArrayHOG.length(); i++)
            {
                for (int j = 0; j < jArrayHOG.getJSONArray(i).length(); j++)
                {
                    hogElement = jArrayHOG.getJSONArray(i).getDouble(j);
                    hogElementList.add(hogElement);
                }
                hogList.add(hogElementList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<ArrayList<Double>> getHogList()
    {
        return hogList;
    }

    List<ArrayList<Integer>> hetRespList()
    {
        return respList;
    }

}
