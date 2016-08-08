//
// Created by hrk on 16/8/8.
//
#include <com_bruce_demo_studydata_fragments_jnidemo_JNICalc.h>
JNIEXPORT jint JNICALL Java_com_bruce_demo_studydata_fragments_jnidemo_JNICalc_calc
  (JNIEnv *, jclass, jint a, jint b){
   return a+b;
  }
