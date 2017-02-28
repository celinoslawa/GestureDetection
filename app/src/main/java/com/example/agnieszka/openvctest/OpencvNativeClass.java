package com.example.agnieszka.openvctest;

/**
 * Created by agnieszka on 28.02.17.
 */

public class OpencvNativeClass {
    public native static void cannyDetect(long matAddrGray, long matAddrCanny);
}
