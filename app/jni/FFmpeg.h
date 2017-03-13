/*
 * FFmpeg.h
 *
 *  Created on: 2014年2月25日
 *      Author: ny
 */

#ifndef FFMPEG_H_
#define FFMPEG_H_

#include <jni.h>
#include <android/bitmap.h>

#include <android/log.h>
#define  LOG_TAG    "jniTest"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libswscale/swscale.h>
}
class FFmpeg {
public:
	FFmpeg();
	int initial(char * url, JNIEnv *e);
	int h264Decodec(jobject & bitmap);
	void fillPicture(AndroidBitmapInfo* info, void *pixels, AVPicture *rgbPicture);
	virtual ~FFmpeg();
	friend class Video;
	int width;
	int height;
private:
	AVFormatContext *pFormatCtx;
	AVCodecContext *pCodecCtx;
	AVFrame *pFrame;
	AVPacket packet;
	AVPicture picture;
	SwsContext * pSwsCtx;
	int videoStream;

	char * rtspURL;
	JNIEnv *env;
};

#endif /* FFMPEG_H_ */
