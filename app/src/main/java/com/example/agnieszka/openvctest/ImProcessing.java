package com.example.agnieszka.openvctest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by agnieszka on 10.07.17.
 */

public class ImProcessing {

    private static final String TAG = "ImProcessing";
    int width;
    int height;
    /*wybranie 5 pkt które znajdją sie na rence

                 a
               f   g
             b   c   d

                 e
    */
    double[] a = new double[3];
    double[] b = new double[3];
    double[] c = new double[3];
    double[] d = new double[3];
    double[] e = new double[3];
    double[] f = new double[3];
    double[] g = new double[3];
    int aX;
    int aY;
    int bX;
    int bY;
    int cX;
    int cY;
    int dX;
    int dY;
    int eX;
    int eY;
    int fX;
    int fY;
    int gX;
    int gY;
    Scalar avUpperT ;
    Scalar avLowerT ;


    Mat mHSV = new Mat(height, width, CvType.CV_8UC3);
    Mat mTresh = new Mat(width, width, CvType.CV_8UC1);
    //Mat mGray = new Mat(height,width, CvType.CV_8UC1);
    //Mat mCanny = new Mat(height,width, CvType.CV_8UC1);
    Mat mTthres = new Mat(height,width, CvType.CV_8UC1);
    //Mat foreground = new Mat(mRgba.size(), CvType.CV_32SC1, new Scalar(255, 255, 255));
    double average;
    double averageP;
    double[] pointColor = new double[4];

    public ImProcessing(int widthM, int heightM)
    {
        width = widthM;
        height = heightM;
        cY = height / 2;
        aY = cY / 2;
        eY = cY + aY;
        bY = cY;
        dY = cY;
        cX = width / 2;
        aX = cX;
        eX = cX;
        bX = cX / 2;
        dX = cX + bX;
        fY = aY + (cY - aY)/2;
        gY = fY;
        fX = bX + (cX-bX)/2;
        gX = cX + (dX-cX)/2;
        aY=aY-100;
        bX=bX +50;
        dX=dX-50;
        //Log.v(TAG, "Point e = " + eY + " X " + eX);
        Log.v(TAG, "Point a = " + aY + " X " + aX);
        Log.v(TAG, "Point b = " + bY + " X " + bX);
        Log.v(TAG, "Point c = " + cY + " X " + cX);
        Log.v(TAG, "Point d = " + dY + " X " + dX);
        Log.v(TAG, "Point f = " + fY + " X " + fX);
        Log.v(TAG, "Point g = " + gY + " X " + gX);
        pointColor[0]=255;
        pointColor[1]=0;
        pointColor[2]=0;
        pointColor[3]=0;

    }



    Mat backgroungRemove(Mat mRgba, boolean calibration)
    {
        //Background removal
        Imgproc.cvtColor(mRgba, mHSV,Imgproc.COLOR_BGR2HSV);
        List<Mat> hsvMat = new ArrayList<>();
        Core.split(mHSV, hsvMat);
        a = mHSV.get(aX,aY);

        b = mHSV.get(bX,bY);
        c = mHSV.get(cX,cY);
        d = mHSV.get(dX,dY);
        e = mHSV.get(eX,eY);
        f = mHSV.get(fX,fY);
        g = mHSV.get(gX,gY);
        if(calibration)
        {
            calibrationOfTreshold();
        }
        Core.inRange(mHSV, avLowerT, avUpperT, mTresh);
        Imgproc.blur(mTresh, mTresh, new Size(5, 5));
        Imgproc.dilate(mTresh, mTresh, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(mTresh, mTresh, new Mat(), new Point(-1, -1), 3);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mTresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        //mRgba.copyTo(foreground, mTthres);
        for(int i=0; i<contours.size(); i++)
        {
            if(Imgproc.contourArea(contours.get(i))>= 7000){
                List<MatOfPoint> scontours = new ArrayList<MatOfPoint>();
                scontours.add(contours.get(i));
                Imgproc.drawContours(mRgba,scontours,-1, new Scalar(255,0,0),5);
            }
        }
        /*MatOfInt histRange = new MatOfInt(180);
        //hsvMat.get(0); //hue mat
        Imgproc.calcHist(hsvMat, new MatOfInt(0), new Mat(), mHist, histRange, new MatOfFloat(0, 179));

        //compute average for threshold
        average = 0.0;
        for (int h = 0; h < 180; h++)
        {
            average += (mHist.get(h, 0)[0] * h);
        }
        average = average / mHSV.size().height / mHSV.size().width;
        Log.v(TAG, "Hist average for H=" + average);

        Imgproc.threshold(hsvMat.get(0), mTthres,average, 179.0, Imgproc.THRESH_BINARY );

        Imgproc.blur(mTthres, mTthres, new Size(5, 5));
        Imgproc.dilate(mTthres, mTthres, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(mTthres, mTthres, new Mat(), new Point(-1, -1), 3);
        //Imgproc.threshold(mTthres, mTthres, average, 179.0, Imgproc.THRESH_BINARY);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mTthres, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        //mRgba.copyTo(foreground, mTthres);
        for(int i=0; i<contours.size(); i++)
        {
            if(Imgproc.contourArea(contours.get(i))>= 7000){
                List<MatOfPoint> scontours = new ArrayList<MatOfPoint>();
                scontours.add(contours.get(i));
                Imgproc.drawContours(mRgba,scontours,-1, new Scalar(255,0,0),5);
            }
        }*/
        if(calibration)
        {
            Imgproc.rectangle(mRgba, new Point(aY - 5, aX - 5), new Point(aY + 5, aX + 5), new Scalar(0, 0, 0));
            Imgproc.rectangle(mRgba, new Point(bY - 5, bX - 5), new Point(bY + 5, bX + 5), new Scalar(0, 0, 0));
            Imgproc.rectangle(mRgba, new Point(cY - 5, cX - 5), new Point(cY + 5, cX + 5), new Scalar(0, 0, 0));
            Imgproc.rectangle(mRgba, new Point(dY - 5, dX - 5), new Point(dY + 5, dX + 5), new Scalar(0, 0, 0));
            Imgproc.rectangle(mRgba, new Point(eY - 5, eX - 5), new Point(eY + 5, eX + 5), new Scalar(0, 0, 0));
            Imgproc.rectangle(mRgba, new Point(fY - 5, fX - 5), new Point(fY + 5, fX + 5), new Scalar(0, 0, 0));
            Imgproc.rectangle(mRgba, new Point(gY - 5, gX - 5), new Point(gY + 5, gX + 5), new Scalar(0, 0, 0));
            mRgba.put(aY, aX, pointColor);
            mRgba.put(bY, bX, pointColor);
            mRgba.put(cY, cX, pointColor);
            mRgba.put(dY, dX, pointColor);
            mRgba.put(eY, eX, pointColor);
        }
        return mRgba;
    }

    void calibrationOfTreshold()
    {
        ArrayList<Double> values = new ArrayList<Double>();
        double[] upper = new double[3];
        double[] lower = new double[3];


        //Log.v(TAG, "AverageT = " + this.averageT[2]);
        for(int i=0; i < 3; i++)
        {
            values.add(this.a[i]);
            values.add(this.b[i]);
            values.add(this.c[i]);
            values.add(this.d[i]);
            values.add(this.e[i]);
            values.add(this.f[i]);
            values.add(this.g[i]);
            Collections.sort(values);
            lower[i] = values.get(0);
            upper[i] = values.get(6);
            Log.v(TAG, "A = " + this.a[i] + "   B = " + this.b[i] + "   C = " + this.c[i] + "   D = " + this.d[i] + "   E = " + this.e[i] + "   F = " + this.f[i] + "   G = " + this.g[i] );
            values.clear();
        }
        this.avUpperT = new Scalar(upper[0] + 20, upper[1] + 20, 255);
        this.avLowerT = new Scalar(lower[0], lower[1], 0);
        Log.v(TAG, "Upper value for H=" + avUpperT.val[0] + "   S=" + avUpperT.val[1] + "  V=" + avUpperT.val[2]);
        Log.v(TAG, "Lower value for H=" + avLowerT.val[0] + "   S=" + avLowerT.val[1] + "  V=" + avLowerT.val[2]);

    }





}
