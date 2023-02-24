package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/02/24 10:44
 */
public class BigFileTest {

    @Test
    public void testChunkUpload() {
        File srcFile = new File("E:\\data\\test.mp4");
        // 1.上传文件分块
        int chunkSize = 1024 * 1024 * 10;
        int chunkNum = (int) Math.ceil(srcFile.length() * 1.0 / chunkSize);
        // 2.一块一块的上传
        File uploadDir = new File("E:\\data\\chunk");
        if (!uploadDir.exists()){
            uploadDir.mkdirs();
        }

        File mergefile = new File("E:\\data\\test_01.mp4");
        byte[] bytes = new byte[1024];
        int len = -1;
        try {
            FileInputStream inputStream = new FileInputStream(srcFile);
            for (int i = 0; i < chunkNum; i++) {
                File destFile = new File(uploadDir.getAbsolutePath() + "\\" + i);
                FileOutputStream outputStream  = new FileOutputStream(destFile);
                while ((len = inputStream.read(bytes)) != -1){
                    outputStream.write(bytes, 0, len);
                    if (destFile.length() >= chunkSize){
                        break;
                    }
                }
                outputStream.close();
            }
            System.out.println("上传成功!");

            // 3.按分块顺序合并文件
            if (!mergefile.exists()){
                mergefile.createNewFile();
            }
            File[] files = uploadDir.listFiles();
            // 分块文件排序
            assert files != null;
            Arrays.sort(files, (Comparator.comparingInt(o -> Integer.parseInt(o.getName()))));
            FileOutputStream outputStream = new FileOutputStream(mergefile);
            for (File f : files) {
                RandomAccessFile rAccessFile = new RandomAccessFile(f, "r");
                while ((len = rAccessFile.read(bytes)) != -1){
                    outputStream.write(bytes, 0, len);
                }
                rAccessFile.close();
                f.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 4.校验文件
        try {
            FileInputStream srcInputStream = new FileInputStream(srcFile);
            FileInputStream fileInputStream = new FileInputStream(mergefile);
            String srcFileMd5 = DigestUtils.md5Hex(srcInputStream);
            String mergeFileMd5 = DigestUtils.md5Hex(fileInputStream);
            if (!srcFileMd5.equals(mergeFileMd5)){
                System.out.println("文件上传失败!");
            }
            System.out.println(srcFileMd5 + " = " + mergeFileMd5);
            System.out.println("文件合并成功!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
