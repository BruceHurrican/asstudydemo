// MyServiceAIDL.aidl
package com.bruce.demo.studydata.activities.ipc;

// Declare any non-default types here with import statements

interface MyServiceAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void showTxt(String txt);
    int calculate(int a,int b);
}
