package com.xuecheng.media;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.FilterInputStream;

/**
 * minio上传、删除、查询文件
 * @author HeJin
 * @version 1.0
 * @since 2023/02/23 15:04
 */
public class MinIOTest {

    /**
     * minion配置
     */
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://127.0.0.1:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    /**
     * minion文件上传测试
     */
    @Test
    public void upload() {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("\\dev\\redis-5.0.bat")
                    .filename("C:\\Users\\Administrator\\Desktop\\redis-5.0.bat")
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传成功!");
        } catch (Exception e) {
            System.out.println("上传失败!");
        }
    }

    /**
     * minion文件删除测试
     */
    @Test
    public void delete() {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket("testbucket")
                    .object("dev/redis-5.0.bat")
                    .build();
            minioClient.removeObject(removeObjectArgs);
            System.out.println("删除成功!");
        } catch (Exception e) {
            System.out.println("删除失败!");
        }
    }

    /**
     * minio下载文件
     */
    @Test
    public void query() {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("dev/redis-5.0.bat")
                .build();
        try (FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
             FileOutputStream outputStream = new FileOutputStream("e:\\redis-5.0.bat")) {
            if (inputStream != null){
                IOUtils.copy(inputStream, outputStream);
            }
            System.out.println("下载成功");
        } catch (Exception e) {
            System.out.println("下载失败!");
        }
    }

}
