#include  <com_example_agnieszka_openvctest_OpencvNativeClass.h>

JNIEXPORT void JNICALL Java_com_example_agnieszka_openvctest_OpencvNativeClass_cannyDetect
  (JNIEnv *, jclass, jlong  addGray, jlong addrCanny){
    Mat& mGray = *(Mat*) addGray;
    Mat& mCanny = *(Mat*) addrCanny;

    cannyDetect(mGray, mCanny);

}

void cannyDetect(Mat img, Mat& canny){
    Canny(img, canny, 30, 90);
}