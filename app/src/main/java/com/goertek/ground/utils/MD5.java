package com.goertek.ground.utils;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5 {

    public interface OnMD5Listener{
        void OnCompleted(String md5);
    }

    /**
     * 获取文件的md5值
     * @param path 文件的全路径名称
     * @return
     */
    public static String getFileMd5(String path){
        return getFileMd5(new File(path));
    }

    public static String getFileMd5(File file){
        FileInputStream fis = null;
        try {
            // 获取一个文件的特征信息，签名信息。
            if (file == null)
                return "";

            if (!file.exists())
                return "";
            // md5
            MessageDigest digest = MessageDigest.getInstance("md5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb  = new StringBuffer();
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                // System.out.println(str);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getStringMd5(String string) {
        try {
            return getBytesMd5(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getBytesMd5(byte[] bytes) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public void getFileMd5Ansyc(String path, final OnMD5Listener onMD5Listener){
        new GetFileMd5Task(path, onMD5Listener).execute();
    }

    public class GetFileMd5Task extends AsyncTask<String, Integer, String> {
        private OnMD5Listener mOnMD5Listener;
        String mFilePath;
        public GetFileMd5Task(String path, OnMD5Listener onMD5Listener)
        {
            mFilePath = path;
            mOnMD5Listener = onMD5Listener;
        }

        protected String doInBackground(String... params) {
            return MD5.getFileMd5(mFilePath);
        }

        protected void onPostExecute(String md5) {
            if (mOnMD5Listener != null)
                mOnMD5Listener.OnCompleted(md5);
        }
    }
}
