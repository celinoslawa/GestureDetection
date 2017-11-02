package com.example.agnieszka.openvctest;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.ml.TrainData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.ml.Ml.COL_SAMPLE;

/**
 * Created by agnieszka on 15.09.17.
 */

public class JsonR {
    private static final String TAG = "JsonR";

    //JSONArray jArrayRES;
    //JSONArray jArrayHOG;


   // InputStream responsesJSON;
    InputStream hog_descriptorsJSON;

    Mat hogMat;
    Mat responsesMat;

    //JsonReader responsesReader;
    JsonReader hogReader;

    List<ArrayList<Double>> hogList = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> hogElementList = new ArrayList<Double>();
    double hogElement;

   /* List<ArrayList<Integer>> respList = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> respElementList = new ArrayList<Integer>();
    int respElement;*/

    public JsonR(InputStream responses, InputStream hog)
    {
       // responsesJSON = responses;
        hog_descriptorsJSON = hog;

    }

    void parsing() throws IOException {
        Log.v(TAG,"Parsing JSON");
       // responsesReader = new JsonReader(new InputStreamReader(responsesJSON, "UTF-8"));
        hogReader =new JsonReader(new InputStreamReader(hog_descriptorsJSON, "UTF-8"));

        /*responsesReader.beginArray();
        while(responsesReader.hasNext())
        {
            responsesReader.beginArray();
            while (responsesReader.hasNext())
            {
                respElement = responsesReader.nextInt();
                //Log.v(TAG,"responsesReader.nextInt() = " + responsesReader.nextInt());
                //Log.v(TAG,"respElement = " + respElement);
                respElementList.add(respElement);
            }
            responsesReader.endArray();
            //Log.v(TAG,"respElementList = " + respElementList.get(0));
            respList.add(respElementList);
            respElementList.clear();
        }
        responsesReader.endArray();*/

        hogReader.beginArray();
        while(hogReader.hasNext())
        {
            hogElementList.clear();
            hogReader.beginArray();
            while (hogReader.hasNext())
            {
                hogElement = hogReader.nextDouble();
                hogElementList.add(hogElement);
                //Log.v(TAG,"hogElement = " + hogElement);
            }
            hogReader.endArray();
           // Log.v(TAG,"hogElementList = " + hogElementList.get(1));
            hogList.add(hogElementList);
        }
        hogReader.endArray();
        //****DEBUG****
        //Log.v(TAG,"Last one respElement: " + respElement);
        //Log.v(TAG,"Element from  hogElementList: " + hogElementList.get(1));
        //Log.v(TAG,"hogList = " + hogList.get(1));


    }

    void convertToMat()
    {
        Log.v(TAG,"Parsing JSON ------ convert To MAt");
        hogMat = new Mat(hogElementList.size(), hogList.size(), CvType.CV_32FC1);
        responsesMat = new Mat(1,1240, CvType.CV_32S);
        hogElementList.clear();

        for (int i = 0; i < hogList.size(); i++)
        {
            hogElementList.clear();
            hogElementList = hogList.get(i);
           // Log.v(TAG,"Element from  hogList: " + hogList.get(i));
            for (int j = 0; j < hogElementList.size(); j++)
            {
                hogMat.put(i,j, hogElementList.get(j));
            //    Log.v(TAG,"Element from  hogElementList: " + hogElementList.get(j));

            }
        }
        //Log.v(TAG, "Read from Json: DESCRIPTORS: width: " + hogMat.width()+ "  height: " + hogMat.height() );

       // Log.v(TAG,"Get element form hogMAt: " + hogMat.get(1,1));
        int k = 0;
        for(int h = 0; h < 1240; h++)
        {
            if(h%124 == 0 && h>0)
            {
                k += 1;
                // k -= 1;
            }
            responsesMat.put(0,h, k);
            //Log.v(TAG, "Get K  : " + k);


            //Log.v(TAG,"Parsing JSON ------ convert resp To MAt II   k = " + k);
        }
        //Log.v(TAG, "Get Responses : " + responsesMat.dump());
       /* Log.v(TAG, "Get Responses 124 : " + responsesMat.toString());
        Log.v(TAG, "Get Responses 248 : " + responsesMat.get(0,248));
        Log.v(TAG, "Get Responses 372 : " + responsesMat.get(0,372));
        Log.v(TAG, "Get Responses 496 : " + responsesMat.get(0,496));
        Log.v(TAG, "Get Responses 620 : " + responsesMat.get(0,620));
        Log.v(TAG, "Get Responses 744 : " + responsesMat.get(0,744));
        Log.v(TAG, "Get Responses 868 : " + responsesMat.get(0,868));
        Log.v(TAG, "Get Responses 992 : " + responsesMat.get(0,992));
        Log.v(TAG, "Get Responses 1116 : " + responsesMat.get(0,1116));*/
       // Log.v(TAG, "Get Responses 1116 : " + responsesMat.);

/*
            for (int h = 0; h < respElementList.size(); h++)
            {
                Log.v(TAG,"Parsing JSON ------ convert resp To MAt II   ");
                respElement = respElementList.get(h);
                responsesMat.put(g,h, respElement);
                Log.v(TAG,"RespElementList: " + respElementList.get(h));

            }
 */
    }

    TrainData traindata()
    {
        TrainData traineddata = TrainData.create(hogMat, COL_SAMPLE,responsesMat);
        return traineddata;
    }

    Mat getHogMat()
    {
        //Log.v(TAG,"HOG MAT from Json: " +hogMat );
        return hogMat;
    }

    Mat getResponsesMat()
    {
        return responsesMat;
    }

}
