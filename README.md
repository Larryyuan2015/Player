# Player
rtsp player

视频流可以保存为MP4文件。


20170819
NDK编译问题
折腾了一个晚上，jni目录调整了一下路径，ndk-build命令就编译链接不过，原来是上次编译生成的文件没有删除。
所以链接时出错。
./../../../arm-linux-androideabi/bin\ld: error: E:/2017/Player/app/obj/local/armeabi-v7a/libstdc++.a:1:1: syntax error, unexpected '!', expecting $end

C:/Users/harryyuan/AppData/Local/Android/ndk-bundle/build//../toolchains/arm-lin
ux-androideabi-4.9/prebuilt/windows-x86_64/lib/gcc/arm-linux-androideabi/4.9.x/.
./../../../arm-linux-androideabi/bin\ld: error: E:/2017/Player/app/obj/local/armeabi-v7a/libstdc++.a: not an object or archive

clang++.exe: error: linker command failed with exit code 1 (use -v to see invocation)

另外，ndk跟app同时编译，需要改3个地方才能编译过。
1, build.gradle文件两个地方
externalNativeBuild {
    ndkBuild {
        arguments "NDK_APPLICATION_MK=src/main/cpp/Application.mk"
        cppFlags "-frtti", "-fexceptions"
        abiFilters "armeabi-v7a"
    }
}

externalNativeBuild {
    ndkBuild {
        path 'src/main/cpp/Android.mk'
    }
}
如果只用到编译的ndk库,下面的可以去掉,
//    sourceSets {
//        main {
//            jniLibs.srcDirs = ['libs']
//        }
//    }

2,在Application.mk里也要改一下,否则编译还是会出错.
是去新build目录中间文件载入.(所以上面的库位置指定也可以去掉)
APP_STL := gnustl_static
## undefined reference to '__atomic_fetch_add_4 error
APP_LDFLAGS := -latomic

//因为参数变化，这样直接ndk-build命令是编译不过