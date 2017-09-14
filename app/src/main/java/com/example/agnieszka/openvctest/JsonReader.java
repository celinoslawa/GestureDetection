package com.example.agnieszka.openvctest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.TrainData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.opencv.ml.Ml.COL_SAMPLE;

/**
 * Created by agnieszka on 11.09.17.
 */

public class JsonReader {

    private static final String TAG = "JsonReader";

    JSONArray jArrayRES;
    JSONArray jArrayHOG;

    /*List<ArrayList<Double>> hogList = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> hogElementList = new ArrayList<Double>();*/
    double hogElement;

    /*List<ArrayList<Integer>> respList = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> respElementList = new ArrayList<Integer>();*/
    int respElement;

    InputStream responsesJSON;
    InputStream hog_descriptorsJSON;
    ByteArrayOutputStream byteArrayOutputStreamRES = new ByteArrayOutputStream();
    ByteArrayOutputStream byteArrayOutputStreamHOG = new ByteArrayOutputStream();

    Mat hogMat;
    Mat responsesMat;



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
            jArrayRES = new JSONArray(byteArrayOutputStreamRES.toByteArray());
            jArrayHOG = new JSONArray(byteArrayOutputStreamHOG.toByteArray());

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
/*
    List<ArrayList<Double>> getHogList()
    {
        return hogList;
    }

    List<ArrayList<Integer>> getRespList()
    {
        return respList;
    }
*/
    Mat getHogMat()
    {
        return hogMat;
    }
    Mat getResponsesMat()
    {
        return responsesMat;
    }

}
