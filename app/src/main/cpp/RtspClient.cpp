/*
 * RtspClient.cpp
 *
 *  Created on: 2014-3-16
 *      Author: ny
 */
#include <jni.h>
#include <android/log.h>
#define  LOG_TAG    "rtspclient"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

extern "C" {

#include "include/libavcodec/avcodec.h"
#include "include/libavformat/avformat.h"
#include "include/libswscale/swscale.h"
#include "FFmpeg.h"

//add dvr
#include "include/libavdevice/avdevice.h"
#include "include/libavfilter/avfilter.h"
#include "include/libavutil/avutil.h"
#include "include/libavutil/time.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
}
const char * rtspURL;  //data source url
const char * outputPath; //the file pathname to save the video stream
const char * outputURL; //the rtmp server url to forward

const char * inputUrl;//temp for demo forward

FFmpeg * ffmpeg;

//add dvr
static AVFormatContext *i_fmt_ctx;
static AVStream *i_video_stream;

static AVFormatContext *o_fmt_ctx;
static AVStream *o_video_stream;
static bool bStop = false;
//static int bStop = 1;
static int videoStreamIndex = -1;

extern "C" {
int shareVideo(char * out_filename);
int rtsp2mp4(char * outputFile);

void Java_com_goertek_ground_player_CameraLiveView_initialWithUrl(JNIEnv *env,
		jobject thisz, jstring url) {
	rtspURL = env->GetStringUTFChars(url, NULL);
	LOGI("%s", rtspURL);
	ffmpeg = new FFmpeg();
	ffmpeg->initial((char *) rtspURL, env);
	//调用java的方法，设置bitmap的width和height
	/**
	 *public void setBitmapSize(int width, int height) {
	 *    Log.i(TAG, "setsize");
	 *    mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	 *}
	 *调用这个方法之后bitmap！=null，绘图线程就会启动
	 */
	LOGI("%d", (int) ffmpeg->width);
	LOGI("%d", (int) ffmpeg->height);
	jclass cls = env->GetObjectClass(thisz);
	jmethodID mid = env->GetMethodID(cls, "setBitmapSize", "(II)V"); //调用java的方法
	env->CallVoidMethod(thisz, mid, (int) ffmpeg->width, (int) ffmpeg->height);
}

void Java_com_goertek_ground_player_CameraLiveView_play(JNIEnv *env, jobject thisz,
		jobject bitmap) {

	ffmpeg->h264Decodec(bitmap);
}

void Java_com_goertek_ground_player_CameraLiveView_stop(JNIEnv *env, jobject thisz) {

	//ffmpeg->stopDecodec();
}

int Java_com_goertek_ground_player_CameraLiveView_startRecVideo(JNIEnv *env, jobject thisz,
 		jstring outputpath) {

        bStop = 0;
        outputPath = env->GetStringUTFChars(outputpath, NULL);
        LOGI("startRecVideo %s", outputPath);
        //ffmpeg->startRecVideo((char *)outputPath);
        LOGI("startRecVideo 0 %d", (int)bStop);
        rtsp2mp4((char *)outputPath);
        LOGI("startRecVideo 1 %d", (int)bStop);

        return 0;
 }

int Java_com_goertek_ground_player_CameraLiveView_stopRecVideo(JNIEnv *env, jobject thisz) {

        bStop = 1;
        //ffmpeg->stopRecVideo();
        LOGI("stopRecVideo %d", (int)bStop);
        return 0;
 }

int Java_com_goertek_ground_player_CameraLiveView_nstartShareVideo(JNIEnv *env, jobject thisz,
 		jstring inputurl, jstring outurl) {

        bStop = 0;
        outputURL = env->GetStringUTFChars(outurl, NULL);
        //inputUrl = env->GetStringUTFChars(inputUrl, NULL);//temp for demo
        //LOGI("startShareVideo %s", inputUrl);
        LOGI("startShareVideo %s", outputURL);
        shareVideo((char *)outputURL);
        //shareVideo((char *)inputUrl,(char *)outputUrl); //temp for demo
        LOGI("startRecVideo 1 %d", (int)bStop);

        return 0;
 }

int Java_com_goertek_ground_player_CameraLiveView_nstopShareVideo(JNIEnv *env, jobject thisz) {

        bStop = 1;
        //ffmpeg->stopShareVideo();
        LOGI("stopShareVideo %d", (int)bStop);
        return 0;
 }

//add dvr feature
//static unsigned __stdcall rtsp2mp4(char * outputFile)
int rtsp2mp4(char * outputFile)
{
    //avcodec_register_all();
    //av_register_all();
    //avformat_network_init();
	int err;
	LOGI("rtsp2mp4 %s", rtspURL);

    /* should set to NULL so that avformat_open_input() allocate a new one */
    i_fmt_ctx = NULL;
    //char rtspUrl[] = "rtsp://192.168.42.1:554/live";
    //const char *filename = "file:///storage/emulated/0/100.mp4";
    if (err = avformat_open_input(&i_fmt_ctx, rtspURL, NULL, NULL)!=0)
    {
        LOGI("avformat_open_input1= %s", av_err2str(err));
        fprintf(stderr, "could not open input file\n");
        return -1;
    }
    	LOGI("avformat_open_input2= %s", av_err2str(err));
    if (err = avformat_find_stream_info(i_fmt_ctx, NULL)<0)
    {
        LOGI("avformat_find_stream_info1= %s", av_err2str(err));
        fprintf(stderr, "could not find stream info\n");
        return -1;
    }
    LOGI("avformat_find_stream_info2= %d", err);
    //av_dump_format(i_fmt_ctx, 0, argv[1], 0);

    /* find first video stream */
    for (unsigned i=0; i<i_fmt_ctx->nb_streams; i++)
    {
        if (i_fmt_ctx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO)
        {
            i_video_stream = i_fmt_ctx->streams[i];
            videoStreamIndex = i;
            break;
        }
    }
    if (i_video_stream == NULL)
    {
        fprintf(stderr, "didn't find any video stream\n");
        return -1;
    }

    err= avformat_alloc_output_context2(&o_fmt_ctx, NULL, NULL, outputFile);
    LOGI("avformat_alloc_output_context2= %d", err);
    /*
    * since all input files are supposed to be identical (framerate, dimension, color format, ...)
    * we can safely set output codec values from first input file
    */
    o_video_stream = avformat_new_stream(o_fmt_ctx, NULL);
    {
        AVCodecContext *c;
        c = o_video_stream->codec;
        c->bit_rate = 400000;
        c->codec_id = i_video_stream->codec->codec_id;
        c->codec_type = i_video_stream->codec->codec_type;
        c->time_base.num = i_video_stream->time_base.num;
        c->time_base.den = i_video_stream->time_base.den;
        fprintf(stderr, "time_base.num = %d time_base.den = %d\n", c->time_base.num, c->time_base.den);
        c->width = i_video_stream->codec->width;
        c->height = i_video_stream->codec->height;
        c->pix_fmt = i_video_stream->codec->pix_fmt;
        printf("%d %d %d", c->width, c->height, c->pix_fmt);
        c->flags = i_video_stream->codec->flags;
        c->flags |= CODEC_FLAG_GLOBAL_HEADER;
        c->me_range = i_video_stream->codec->me_range;
        c->max_qdiff = i_video_stream->codec->max_qdiff;

        c->qmin = i_video_stream->codec->qmin;
        c->qmax = i_video_stream->codec->qmax;

        c->qcompress = i_video_stream->codec->qcompress;
    }

    err = avio_open(&o_fmt_ctx->pb, outputFile, AVIO_FLAG_WRITE);
    LOGI("avio_open1= %d", err);

    err = avformat_write_header(o_fmt_ctx, NULL);
    LOGI("avformat_write_header= %d", err);
    int last_pts = 0;
    int last_dts = 0;

    int64_t pts, dts;
    LOGI("rtsp2mp4 bStop1 %d", (int)bStop);
    while (!bStop)
    {
        AVPacket i_pkt;
        av_init_packet(&i_pkt);
        i_pkt.size = 0;
        i_pkt.data = NULL;
        if (err = av_read_frame(i_fmt_ctx, &i_pkt) <0 )
        {
            LOGI("av_read_frame= %s", av_err2str(err));
            break;
        }
        //if (i_pkt.stream_index != videoStreamIndex)
        //    continue;

        LOGI("av_read_frame 1= %d", err);
        /*
        * pts and dts should increase monotonically
        * pts should be >= dts
        */
        i_pkt.flags |= AV_PKT_FLAG_KEY;
        pts = i_pkt.pts;
        i_pkt.pts += last_pts;
        dts = i_pkt.dts;
        i_pkt.dts += last_dts;
        i_pkt.stream_index = 0;

        //printf("%lld %lld\n", i_pkt.pts, i_pkt.dts);
        static int num = 1;
        printf("frame %d\n", num++);
        LOGI("while frame %d\n", num);
        av_interleaved_write_frame(o_fmt_ctx, &i_pkt);
        //av_free_packet(&i_pkt);
        //av_init_packet(&i_pkt);
        //sleep(10);//modified dvr
        av_usleep(10);
        LOGI("rtsp2mp4 bStop2 %d", (int)bStop);
    }
    last_dts += dts;
    last_pts += pts;
        LOGI("rtsp2mp4 bStop2 %d", (int)bStop);
    avformat_close_input(&i_fmt_ctx);
        LOGI("avformat_close_input= %d", err);
    err = av_write_trailer(o_fmt_ctx);
        LOGI("av_write_trailer= %d", err);
    err = avcodec_close(o_fmt_ctx->streams[0]->codec);
        LOGI("avcodec_close= %d", err);
    av_freep(&o_fmt_ctx->streams[0]->codec);
    av_freep(&o_fmt_ctx->streams[0]);

    err = avio_close(o_fmt_ctx->pb);
        LOGI("avio_close= %d", err);
    av_free(o_fmt_ctx);
        LOGI("av_free= %d", err);
    return 0;
}


int shareVideo(char * out_filename)
//int shareVideo(char * in_filename, char * out_filename)
{
    AVOutputFormat *ofmt = NULL;
    //输入对应一个AVFormatContext，输出对应一个AVFormatContext
    //（Input AVFormatContext and Output AVFormatContext）
    AVFormatContext *ifmt_ctx = NULL, *ofmt_ctx = NULL;
    AVPacket pkt;
    //const char *in_filename, *out_filename;
    const char *in_filename;
    int ret, i;
    int videoindex=-1;
    int frame_index=0;
    int64_t start_time=0;
    //in_filename  = "rtsp://192.168.42.1:554/live";
    //in_filename  = "test.mp4"; //输入URL（Input file URL）
    in_filename  = rtspURL;

    //out_filename = "rtmp://192.168.253.1/oflaDemo/test";//输出 URL（Output URL）[RTMP]
    //out_filename = "rtp://233.233.233.233:6666";//输出 URL（Output URL）[UDP]

    //need it for wifi and 4G can't work together in android. so wifi need reconnect the rtmp server here.
    av_register_all();
    //Network
    avformat_network_init();
    //输入（Input）
    if ((ret = avformat_open_input(&ifmt_ctx, in_filename, 0, 0)) < 0) {
        printf( "Could not open input file.");
        goto end;
    }
    if ((ret = avformat_find_stream_info(ifmt_ctx, 0)) < 0) {
        printf( "Failed to retrieve input stream information");
        goto end;
    }

    for(i=0; i<ifmt_ctx->nb_streams; i++)
        if(ifmt_ctx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO){
            videoindex=i;
            break;
        }

    av_dump_format(ifmt_ctx, 0, in_filename, 0);

    //输出（Output）
    avformat_alloc_output_context2(&ofmt_ctx, NULL, "flv", out_filename); //RTMP
    //avformat_alloc_output_context2(&ofmt_ctx, NULL, "mpegts", out_filename);//UDP

    if (!ofmt_ctx) {
        printf( "Could not create output context\n");
        ret = AVERROR_UNKNOWN;
        goto end;
    }
    ofmt = ofmt_ctx->oformat;
    for (i = 0; i < ifmt_ctx->nb_streams; i++) {
        //根据输入流创建输出流（Create output AVStream according to input AVStream）
        AVStream *in_stream = ifmt_ctx->streams[i];
        AVStream *out_stream = avformat_new_stream(ofmt_ctx, in_stream->codec->codec);
        if (!out_stream) {
            printf( "Failed allocating output stream\n");
            ret = AVERROR_UNKNOWN;
            goto end;
        }
        //复制AVCodecContext的设置（Copy the settings of AVCodecContext）
        ret = avcodec_copy_context(out_stream->codec, in_stream->codec);
        if (ret < 0) {
            printf( "Failed to copy context from input to output stream codec context\n");
            goto end;
        }
        out_stream->codec->codec_tag = 0;
        if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
            out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;
    }
    //Dump Format------------------
    av_dump_format(ofmt_ctx, 0, out_filename, 1);
    //打开输出URL（Open output URL）
    if (!(ofmt->flags & AVFMT_NOFILE)) {
        ret = avio_open(&ofmt_ctx->pb, out_filename, AVIO_FLAG_WRITE);
        if (ret < 0) {
            printf( "Could not open output URL '%s'", out_filename);
            goto end;
        }
    }
    //写文件头（Write file header）
    ret = avformat_write_header(ofmt_ctx, NULL);
    if (ret < 0) {
        printf( "Error occurred when opening output URL\n");
        goto end;
    }

    start_time=av_gettime();
    while (1) {
        AVStream *in_stream, *out_stream;
        //获取一个AVPacket（Get an AVPacket）
        ret = av_read_frame(ifmt_ctx, &pkt);
        if (ret < 0)
            break;
        //FIX：No PTS (Example: Raw H.264)
        //Simple Write PTS
        if(pkt.pts==AV_NOPTS_VALUE){
            //Write PTS
            AVRational time_base1=ifmt_ctx->streams[videoindex]->time_base;
            //Duration between 2 frames (us)
            int64_t calc_duration=(double)AV_TIME_BASE/av_q2d(ifmt_ctx->streams[videoindex]->r_frame_rate);
            //Parameters
            pkt.pts=(double)(frame_index*calc_duration)/(double)(av_q2d(time_base1)*AV_TIME_BASE);
            pkt.dts=pkt.pts;
            pkt.duration=(double)calc_duration/(double)(av_q2d(time_base1)*AV_TIME_BASE);
        }
        //Important:Delay
        if(pkt.stream_index==videoindex){
            AVRational time_base=ifmt_ctx->streams[videoindex]->time_base;
            AVRational time_base_q={1,AV_TIME_BASE};
            int64_t pts_time = av_rescale_q(pkt.dts, time_base, time_base_q);
            int64_t now_time = av_gettime() - start_time;
            if (pts_time > now_time)
                av_usleep(pts_time - now_time);

        }

        in_stream  = ifmt_ctx->streams[pkt.stream_index];
        out_stream = ofmt_ctx->streams[pkt.stream_index];
        /* copy packet */
        //转换PTS/DTS（Convert PTS/DTS）
        pkt.pts = av_rescale_q_rnd(pkt.pts, in_stream->time_base, out_stream->time_base, (AVRounding)(AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
        pkt.dts = av_rescale_q_rnd(pkt.dts, in_stream->time_base, out_stream->time_base, (AVRounding)(AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
        pkt.duration = av_rescale_q(pkt.duration, in_stream->time_base, out_stream->time_base);
        pkt.pos = -1;
        //Print to Screen
        if(pkt.stream_index==videoindex){
            printf("Send %8d video frames to output URL\n",frame_index);
            frame_index++;
        }
        //ret = av_write_frame(ofmt_ctx, &pkt);
        ret = av_interleaved_write_frame(ofmt_ctx, &pkt);

        if (ret < 0) {
            printf( "Error muxing packet\n");
            break;
        }

        av_free_packet(&pkt);

    }
    //写文件尾（Write file trailer）
    av_write_trailer(ofmt_ctx);
end:
    avformat_close_input(&ifmt_ctx);
    /* close output */
    if (ofmt_ctx && !(ofmt->flags & AVFMT_NOFILE))
        avio_close(ofmt_ctx->pb);
    avformat_free_context(ofmt_ctx);
    if (ret < 0 && ret != AVERROR_EOF) {
        printf( "Error occurred.\n");
        return -1;
    }
    return 0;
}
}

