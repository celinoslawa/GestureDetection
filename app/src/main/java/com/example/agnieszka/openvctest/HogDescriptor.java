package com.example.agnieszka.openvctest;

import android.util.Log;

import org.opencv.core.CvType;
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
    HOGDescriptor Hog; // = new HOGDescriptor() ;
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
    boolean signedGradient = true;
    MatOfFloat descriptors = new MatOfFloat();
    //Mat descriptors;
    Mat frameB = new Mat();
   // Mat returnMat;


    public HogDescriptor()
    {
        //Size _winSize, Size _blockSize, Size _blockStride, Size _cellSize, int _nbins, int _derivAperture, double _winSigma, int _histogramNormType, double _L2HysThreshold, boolean _gammaCorrection, int _nlevels, boolean _signedGradient
       Hog = new HOGDescriptor(winSize ,blockSize,blockStride ,cellSize, nbins, derivAperture,
               winSigma, histogramNormType, L2HysThreshold, gammaCorrection, nlevels, signedGradient);
        //descriptors = new Mat(1, 1152, CvType.CV_32F );
    }

    void compute(Mat frame)
    {
        Imgproc.resize(frame, frameB, winSize,1,1, Imgproc.INTER_AREA);
       // Imgproc.blur(frameB, frameB, new Size(5, 5));
        Hog.compute(frameB,descriptors);

        //Log.v(TAG, "DESCRIPTORS: " + descriptors);

       // Hog.compute(frameB, descriptors);
    }


    Mat getDescriptors()
    {
        descriptors.convertTo(descriptors, CvType.CV_32F );
        Log.v(TAG, "HogDescriptor: DESCRIPTORS: width: " + descriptors.width()+ "  height: " + descriptors.height()  + " TYPE: " + descriptors.type());
        return descriptors;
    }
    Mat getFrameB()
    {
        return this.frameB;
    }
}
