package com.xuecheng.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 此文件作为视频文件处理父类，提供：
 * 1、查看视频时长
 * 2、校验两个视频的时长是否相等
 *
 * @author Administrator
 */
public class VideoUtil {

    String ffmpegPath;

    public VideoUtil(String ffmpegPath){
        this.ffmpegPath = ffmpegPath;
    }


    /**
     * 检查视频时间是否一致
     * @param source source
     * @param target target
     * @return Boolean
     */
    public Boolean checkVideoTime(String source, String target) {
        String sourceTime = getVideoTime(source);
        //取出时分秒
        sourceTime = sourceTime.substring(0,sourceTime.lastIndexOf("."));
        String targetTime = getVideoTime(target);
        //取出时分秒
        targetTime = targetTime.substring(0,targetTime.lastIndexOf("."));
        return sourceTime.equals(targetTime);
    }

    /**
     * Boolean
     * @param videoPath videoPath
     * @return String
     */
    public String getVideoTime(String videoPath) {
        /*
        ffmpeg -i  lucene.mp4
         */
        List<String> commend = new ArrayList<>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(videoPath);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            //将标准输入流和错误输入流合并，通过标准输入流程读取信息
            builder.redirectErrorStream(true);
            Process p = builder.start();
            String outstring = waitFor(p);
            System.out.println(outstring);
            int start = outstring.trim().indexOf("Duration: ");
            if(start>=0){
                int end = outstring.trim().indexOf(", start:");
                if(end>=0){
                    String time = outstring.substring(start+10,end);
                    if(!"".equals(time)){
                        return time.trim();
                    }
                }
            }

        } catch (Exception ex) {

            ex.printStackTrace();

        }
        return null;
    }

    public String waitFor(Process p) {
        InputStream in = null;
        InputStream error;
        String result = "error";
        int exitValue = -1;
        StringBuilder outputString = new StringBuilder();
        try {
            in = p.getInputStream();
            error = p.getErrorStream();
            boolean finished = false;
            //每次休眠1秒，最长执行时间10分种
            int maxRetry = 600;
            int retry = 0;
            while (!finished) {
                if (retry > maxRetry) {
                    return "error";
                }
                try {
                    while (in.available() > 0) {
                        Character c = (char) in.read();
                        outputString.append(c);
                        System.out.print(c);
                    }
                    while (error.available() > 0) {
                        Character c = (char) in.read();
                        outputString.append(c);
                        System.out.print(c);
                    }
                    //进程未结束时调用exitValue将抛出异常
                    exitValue = p.exitValue();
                    finished = true;

                } catch (IllegalThreadStateException e) {
                    Thread.sleep(1000);
                    retry++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return outputString.toString();

    }


    public static void main(String[] args) throws IOException {
        //ffmpeg的安装位置
        String ffmpegPath = "D:\\Program Files\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        VideoUtil videoUtil = new VideoUtil(ffmpegPath);
        String videoTime = videoUtil.getVideoTime("E:\\ffmpeg_test\\1.avi");
        System.out.println(videoTime);
    }
}
