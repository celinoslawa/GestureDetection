package com.example.agnieszka.openvctest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // Used in Camera selection from menu (when implemented)
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    Mat mRgba;
    Mat mHSV;
    Mat mHist;
    Mat mGray;
    Mat mCanny;
    Mat mTthres;


    static{
        System.loadLibrary("MyOpencvLibs");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }


    @Override
    //the activity is created, display openCV camera in the layout.
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.show_camera);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    //receive Image data when the camera preview starts
    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mHSV = new Mat(height, width, CvType.CV_8UC3);
        mHist = new Mat(width, width, CvType.CV_8UC1);
        mGray = new Mat(height,width, CvType.CV_8UC1);
        mCanny = new Mat(height,width, CvType.CV_8UC1);
        mTthres = new Mat(height,width, CvType.CV_8UC1);
    }

    //destroy image data when you stop camera preview
    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        // TODO Auto-generated method stub
        mRgba = inputFrame.rgba();
        //Canny detect
        Imgproc.cvtColor(mRgba, mGray,Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(mGray, mGray, new Size(3,3));
        OpencvNativeClass.cannyDetect(mGray.getNativeObjAddr(), mCanny.getNativeObjAddr());
        //Background removal
        Imgproc.cvtColor(mRgba, mHSV,Imgproc.COLOR_BGR2HSV);
        List<Mat> hsvMat = new ArrayList<>();
        Core.split(mHSV, hsvMat);
        MatOfInt histRange = new MatOfInt(180);
        //hsvMat.get(0); //hue mat
        Imgproc.calcHist(hsvMat, new MatOfInt(0), new Mat(), mHist, histRange, new MatOfFloat(0, 179));
/*
public static void calcHist(java.util.List<Mat> images,
                            MatOfInt channels,
                            Mat mask,
                            Mat hist,
                            MatOfInt histSize,
                            MatOfFloat ranges)

 */
        double average = 0.0;
        for (int h = 0; h < 180; h++)
        {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            average += (mHist.get(h, 0)[0] * h);
        }
        average = average / mHSV.size().height / mHSV.size().width;
        Imgproc.threshold(hsvMat.get(0), mTthres,average, 179.0, Imgproc.THRESH_BINARY );
        Imgproc.blur(mTthres, mTthres, new Size(5, 5));
        Imgproc.dilate(mTthres, mTthres, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(mTthres, mTthres, new Mat(), new Point(-1, -1), 3);
        Imgproc.threshold(mTthres, mTthres, average, 179.0, Imgproc.THRESH_BINARY);
        Mat foreground = new Mat(mRgba.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        mRgba.copyTo(foreground, mTthres);

        // Rotate mRgba 90 degrees
       // Core.transpose(mRgba, mRgbaT);
       // Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        //Core.flip(mRgbaF, mRgba, 1 );

        return foreground; // This function must return
    }

}
