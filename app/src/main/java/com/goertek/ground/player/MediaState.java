package com.goertek.ground.player;


//播放器的状态
public enum MediaState {
    Idle, //初始状态
    Initialized, //已初始化
    Preparing,
    Prepared,
    Playing, //正在播放
    Buffering, //正在缓冲
    Paused, //暂停状态
    End, //视频已播完
    Stopped, //已停止
    Error, //出现错误
}
