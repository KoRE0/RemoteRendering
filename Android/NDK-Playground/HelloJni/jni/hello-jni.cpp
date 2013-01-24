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


// socket
#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#define  LOG_TAG    "HelloJNI"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


extern "C" {

		jstring  Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
        jobject thiz );
		jstring  Java_com_example_hellojni_HelloJni_stringFromServer( JNIEnv* env,
		        jobject thiz );
		jstring Java_com_example_hellojni_HelloJni_initiateTcpConnection(JNIEnv* env, jobject javaThis);

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


jstring Java_com_example_hellojni_HelloJni_initiateTcpConnection(JNIEnv* env, jobject javaThis){
    int tcp_socket = socket(AF_INET, SOCK_STREAM,0);
    if(tcp_socket < 0){
        return env->NewStringUTF("ERROR CREATING SOCKET");
    }
    const char* server_host = "192.168.178.31";
    unsigned short server_port = 7777;

    struct sockaddr_in server_tcp_addr;
    server_tcp_addr.sin_family = AF_INET;
    server_tcp_addr.sin_port = htons(server_port);
    struct hostent *hostp = gethostbyname(server_host);
    memcpy((char *)&server_tcp_addr.sin_addr.s_addr, hostp->h_addr, hostp->h_length);
    socklen_t slen = sizeof(server_tcp_addr);
    if(connect(tcp_socket,(struct sockaddr*)&server_tcp_addr, slen) < 0){ //fails here
        close(tcp_socket);
        return env->NewStringUTF("ERROR CONNECTING TO SERVER");
    }

    char* message = "hello from android!";
    send(tcp_socket, &message, sizeof(message),0);


    return env->NewStringUTF("TCP message sent!");
}



jstring  Java_com_example_hellojni_HelloJni_stringFromServer( JNIEnv* env,
                                                  jobject thiz )
{
	LOGI("entered Method: stringFromServer");
	int sockfd;
	int len;
	int port = 7777;
	struct sockaddr_un address;
	struct sockaddr_in server;
	int result;
	char ch = 'A';

/*  Create a socket for the client.  */

	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	LOGI("Create a socket for the client.");
	if(sockfd < 0)
		LOGE("Creating socket failed.");
/*  Name the socket, as agreed with the server.  */



//	address.sun_family = AF_INET;
//	strcpy(address.sun_path, "192.168.178.31");
//	len = sizeof(address);
	memset( &server, 0, sizeof (server));
	server.sin_family = AF_INET;
	server.sin_port = htons(7777);
	//server.sin_addr.s_addr = inet_addr("192.168.178.31");
	uint32_t addr = inet_addr("192.168.178.31");
	memcpy( (char *)&server.sin_addr.s_addr, &addr, sizeof(addr));
	LOGI("Name the socket, as agreed with the server.");

/*  Now connect our socket to the server's socket.  */

	if(connect(sockfd, (struct sockaddr *)&server, sizeof(server)) < 0){
		LOGE("ERROR while connecting");
		perror("oops: client1");
		exit(1);
	}
	LOGI("Connection success!");


/*  We can now read/write via sockfd.  */
	LOGI("Sending data...");
	write(sockfd, &ch, 1);
	LOGI("Reading data...");
	read(sockfd, &ch, 1);
	LOGI("... done");
	/*
	printf("char from server = %c\n", ch);
	*/
	close(sockfd);
    return env->NewStringUTF(&ch);
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
        LOGE("Bitmap format is not RGB_8888 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    /* Now fill the values with a nice little plasma */
    fillBitmap(&info, pixels);

    AndroidBitmap_unlockPixels(env, bitmap);

}


