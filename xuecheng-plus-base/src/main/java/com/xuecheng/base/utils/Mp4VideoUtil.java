package com.xuecheng.base.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class Mp4VideoUtil extends VideoUtil {

    /**
     * ffmpeg的安装位置
     */
    String ffmpegPath;
    String videoPath;
    String mp4Name;
    String mp4FolderPath;
    public Mp4VideoUtil(String ffmpegPath, String videoPath, String mp4Name, String mp4FolderPath){
        super(ffmpegPath);
        this.ffmpegPath = ffmpegPath;
        this.videoPath = videoPath;
        this.mp4Name = mp4Name;
        this.mp4FolderPath = mp4FolderPath;
    }

    /**
     * 清除已生成的mp4
     * @param mp4Path 文件路径
     */
    private void clearMp4(String mp4Path){
        //删除原来已经生成的m3u8及ts文件
        File mp4File = new File(mp4Path);
        if(mp4File.exists() && mp4File.isFile()){
            boolean isDelete = mp4File.delete();
            if (!isDelete){
                throw new RuntimeException("文件删除失败");
            }
        }
    }
    /**
     * 视频编码，生成mp4文件
     * @return 成功返回success，失败返回控制台日志
     */
    public String generateMp4(){
        //清除已生成的mp4
        clearMp4(mp4FolderPath);
        List<String> commend = new ArrayList<>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(videoPath);
        commend.add("-c:v");
        commend.add("libx264");
        commend.add("-y");
        commend.add("-s");
        commend.add("1280x720");
        commend.add("-pix_fmt");
        commend.add("yuv420p");
        commend.add("-b:a");
        commend.add("63k");
        commend.add("-b:v");
        commend.add("753k");
        commend.add("-r");
        commend.add("18");
        commend.add(mp4FolderPath);
        String outstring = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            //将标准输入流和错误输入流合并，通过标准输入流程读取信息
            builder.redirectErrorStream(true);
            Process p = builder.start();
            outstring = waitFor(p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Boolean checkVideoTime = this.checkVideoTime(videoPath, mp4FolderPath);
        if(!checkVideoTime){
            return outstring;
        }else{
            return "success";
        }
    }

    public static void main(String[] args) {
        //ffmpeg的路径
        String ffmpegPath = "D:\\environments\\ffmpeg\\ffmpeg.exe";
        //源avi视频的路径
        String videoPath = "E:\\data\\123.avi";
        //转换后mp4文件的名称
        String mp4Name = "123.mp4";
        //转换后mp4文件的路径
        String mp4Path = "E:\\data\\123.mp4";
        //创建工具类对象
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath,videoPath,mp4Name,mp4Path);
        //开始视频转换，成功将返回success
        String s = videoUtil.generateMp4();
        System.out.println(s);
    }
}