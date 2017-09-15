package com.example.agnieszka.openvctest;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by agnieszka on 15.09.17.
 */

public class JsonR {
    private static final String TAG = "JsonR";

    //JSONArray jArrayRES;
    //JSONArray jArrayHOG;


    InputStream responsesJSON;
    InputStream hog_descriptorsJSON;

    Mat hogMat;
    Mat responsesMat;

    JsonReader responsesReader;
    JsonReader hogReader;

    List<ArrayList<Double>> hogList = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> hogElementList = new ArrayList<Double>();
    double hogElement;

    List<ArrayList<Integer>> respList = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> respElementList = new ArrayList<Integer>();
    int respElement;

    public JsonR(InputStream responses, InputStream hog)
    {
        responsesJSON = responses;
        hog_descriptorsJSON = hog;

    }

    void parsing() throws IOException {
        Log.v(TAG,"Parsing JSON");
        responsesReader = new JsonReader(new InputStreamReader(responsesJSON, "UTF-8"));
        hogReader =new JsonReader(new InputStreamReader(hog_descriptorsJSON, "UTF-8"));

        responsesReader.beginArray();
        while(responsesReader.hasNext())
        {
            responsesReader.beginArray();
            while (responsesReader.hasNext())
            {
                respElement = responsesReader.nextInt();
                respElementList.add(respElement);
            }
            respList.add(respElementList);
        }
        responsesReader.endArray();

        hogReader.beginArray();
        while(hogReader.hasNext())
        {
            hogReader.beginArray();
            while (hogReader.hasNext())
            {
                hogElement = hogReader.nextDouble();
                hogElementList.add(hogElement);
            }
            hogList.add(hogElementList);
        }
        hogReader.endArray();

    }

    void convertToMat()
    {
        hogMat = new Mat(hogElementList.size(), hogList.size(), CvType.CV_32FC1);
        responsesMat = new Mat(jArrayRES.getJSONArray(0).length(),jArrayRES.length(), CvType.CV_8SC1);
        for (int i = 0; i < hogList.size(); i++)
        {
            for (int j = 0; j < hogElementList.size(); j++)
            {

                hogMat.put(i,j, hogList.get(i));

            }
        }

    }




    /*
        public void parsing()
    {
        Log.v(TAG,"Parsing JSON");
        try {
            // Parse the data into jsonobject to get original data in form of json.
           // jArrayRES = new JSONArray(byteArrayOutputStreamRES.toByteArray());
            //jArrayHOG = new JSONArray(byteArrayOutputStreamHOG.toByteArray());
            jArrayRES = new JSONArray();
            jArrayHOG = new JSONArray();
            hogMat = new Mat(jArrayHOG.getJSONArray(0).length(), jArrayHOG.length(), CvType.CV_32FC1);
            responsesMat = new Mat(jArrayRES.getJSONArray(0).length(),jArrayRES.length(), CvType.CV_8SC1);

            // Parsing responses

            for (int i = 0; i < jArrayRES.length(); i++)
            {
                for (int j = 0; j < jArrayRES.getJSONArray(i).length(); j++)
                {
                    respElement = jArrayRES.getJSONArray(i).getInt(j);
                   // respElementList.add(respElement);
                    responsesMat.put(i,j,respElement);
                }
               // respList.add(respElementList);

            }
            //Pasing HOG vectors

            for (int i = 0; i < jArrayHOG.length(); i++)
            {
                for (int j = 0; j < jArrayHOG.getJSONArray(i).length(); j++)
                {
                    hogElement = jArrayHOG.getJSONArray(i).getDouble(j);
                   // hogElementList.add(hogElement);
                    hogMat.put(i,j,hogElement);
                }
               // hogList.add(hogElementList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TrainData trainDara()
    {
        TrainData handTrainData = org.opencv.ml.TrainData.create(hogMat,COL_SAMPLE,responsesMat);
        return handTrainData;
    }

    List<ArrayList<Double>> getHogList()
    {
        return hogList;
    }

    List<ArrayList<Integer>> getRespList()
    {
        return respList;
    }

    Mat getHogMat()
    {
        return hogMat;
    }
    Mat getResponsesMat()
    {
        return responsesMat;
    }

}
     */

    //=============================consructor

   /* responsesJSON = responses;
    hog_descriptorsJSON = hog;

    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;
    JsonReader reader = null;
    try{
        reader = new JsonReader(responsesJSON);
    }

       /* int ctr;
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
        }*/
}
