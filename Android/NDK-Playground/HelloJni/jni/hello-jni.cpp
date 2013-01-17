/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>


#define  LOG_TAG    "HelloJNI"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


extern "C" {

		jstring  Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
        jobject thiz );

		JNIEXPORT void JNICALL Java_com_example_hellojni_ProcessedImage_processBitmap(JNIEnv* env, jobject obj, jobject bitmap);
};


inline int32_t color(int pColorR, int pColorG, int pColorB) {
     return 0xFF000000 | ((pColorB << 16) & 0x00FF0000)
                       | ((pColorG << 8) & 0x0000FF00)
                       | (pColorR & 0x000000FF);
}

static void fillBitmap( AndroidBitmapInfo*  info, void*  pixels)
{
    int  yy;
    for (yy = 0; yy < info->height; yy++) {
        uint32_t*  line = (uint32_t*)pixels;
		int xx;
		for (xx = 0; xx < info->width; xx++) {
			// set white color
			line[xx] = color(255, 0, 255);
		}
		pixels = (char*)pixels + info->stride;
    }
}

jstring  Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
	LOGI("entered Method: stringFromJNI");
    return env->NewStringUTF("Hello from JNI !");
}

JNIEXPORT void JNICALL Java_com_example_hellojni_ProcessedImage_processBitmap(JNIEnv* env, jobject obj, jobject bitmap)
{

	//LOGI("entered Method: processBitmap");
    AndroidBitmapInfo  info;
    void*              pixels;
    int                ret;
    static int         init;
    if (!init) {
        init = 1;
    }

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGB_565 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    /* Now fill the values with a nice little plasma */
    fillBitmap(&info, pixels);

    AndroidBitmap_unlockPixels(env, bitmap);

}


