package com.example.agnieszka.openvctest;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

/**
 * Created by agnieszka on 01.08.17.
 */

public class HogDescriptor {
    HOGDescriptor Hog;
    Size winSize = new Size(60, 100);
    Size blockSize = new Size(40, 40);
    Size blockStride = new Size(20, 20);
    Size cellSize = new Size(10, 10);
    int nbins = 9;
    int derivAperture = 1;
    double winSigma = -1.;
    int histogramNormType = 0;
    double L2HysThreshold = 0.2;
    boolean gammaCorrection = true;
    int nlevels = 64;
    MatOfFloat descriptors;


    public HogDescriptor()
    {
        Hog = new HOGDescriptor(winSize,blockSize, blockStride, cellSize, nbins, derivAperture,
                winSigma, histogramNormType, L2HysThreshold, gammaCorrection, nlevels);

    }

    void compute(Mat frame)
    {
        Imgproc.resize(frame, frame, winSize);
        Hog.compute(frame, descriptors);
    }


    MatOfFloat getDescriptors()
    {
        return descriptors;
    }
}
