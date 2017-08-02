package com.example.agnieszka.openvctest;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
//import 	android.util.Size;

/**
 * Created by agnieszka on 01.08.17.
 */

public class HogDescriptor {
    private static final String TAG = "HogDescriptor";
    HOGDescriptor Hog;
    Size winSize = new Size(60, 100);
    Size blockSize = new Size(40, 40);
    Size blockStride = new Size(20, 20);
    Size cellSize = new Size(10, 10);
    int nbins = 9;
    int derivAperture = 1;
    double winSigma = -1.0;
    int histogramNormType = 0;
    double L2HysThreshold = 0.2;
    boolean gammaCorrection = true;
    int nlevels = 64;
    MatOfFloat descriptors;
    Mat frameB = new Mat();


    public HogDescriptor()
    {
        Hog = new HOGDescriptor(new org.opencv.core.Size(60, 100),new org.opencv.core.Size(40, 40), new org.opencv.core.Size(20, 20),new org.opencv.core.Size(10, 10), nbins, derivAperture,
                winSigma, histogramNormType, L2HysThreshold, gammaCorrection, nlevels);
    }

    void compute(Mat frame)
    {
        Imgproc.resize(frame, frameB, winSize,1,1, Imgproc.INTER_AREA);
        Hog.compute(frameB, descriptors);
    }


    MatOfFloat getDescriptors()
    {
        return this.descriptors;
    }
    Mat getFrameB()
    {
        return this.frameB;
    }
}
