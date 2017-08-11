package com.example.agnieszka.openvctest;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.os.CountDownTimer;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;

import java.io.File;
import java.io.InputStream;

import static com.example.agnieszka.openvctest.MainActivity.AppStatusE.CALIBRATION;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";
    private static final String TAT = "CountDownTimer::Activity";
    private static final String SVM = "SVM : ";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    // Used in Camera selection from menu (when implemented)
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    public enum AppStatusE{
        CALIBRATION,
        DETECTION
    }

    private ImProcessing imProc;
    private HogDescriptor hog;
    private CountDownTimer countDownTimer;
    private TextView textViewTime;
    private AppStatusE appStatus;
    SVM svm = org.opencv.ml.SVM.create();
    //public SVM self = new SVM();
    //SVM svm = new SVM();
    //SVM svm = SVM.create();
    Mat mRgba;
    Mat mMask;
    //String filename = "/app/src/main/res/SVM/digits_svm.dat";
    //InputStream is = this.getResources().openRawResource(+R.drawable.digits_svm);
    //File datasetFile = new File(Environment.getExternalStorageDirectory(), "digits_svm.dat");
    float resp;
    //long addr = '';



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

        textViewTime = (TextView) findViewById(R.id.textViewTime);

        appStatus = CALIBRATION;

        //svm = new SVM(addr);
        //SVM svm = org.opencv.ml.SVM.create();
        InputStream is = this.getResources().openRawResource(+R.drawable.digits_svm);
        //svm.create();

        svm.load(String.valueOf(is));

    }


    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(""+ millisUntilFinished/1000 );
                Log.v(TAT, millisUntilFinished/1000 + " sek");
            }

            @Override
            public void onFinish() {

                textViewTime.setText("0");
                Log.v(TAT, "onFinish");
                appStatus = AppStatusE.DETECTION;
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
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
        stopCountDownTimer();

    }

    //receive Image data when the camera preview starts
    public void onCameraViewStarted(int width, int height) {
        appStatus = CALIBRATION;
        startCountDownTimer();

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mMask = new Mat(height, width, CvType.CV_8SC1);
        imProc = new ImProcessing(height,width);
        hog = new HogDescriptor();

    }

    //destroy image data when you stop camera preview
    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mMask = imProc.backgroungRemove(mRgba, appStatus);

        if(appStatus == CALIBRATION ){
            mRgba = imProc.drawCalibrationPoints(mRgba);
        }
        else {
            hog.compute(mMask);
            resp = svm.predict(hog.getDescriptors());
            Log.v(SVM, "Predicted value : " + resp);
            //HOG + SVM
        }
        mRgba = imProc.drawContours(mRgba,mMask);
        return mRgba;
    }

}
