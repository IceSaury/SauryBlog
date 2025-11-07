package com.saury.blog.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS文件上传服务
 *
 * @author Saury
 */
public interface OssService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件URL
     */
    String uploadFile(MultipartFile file);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    void deleteFile(String fileUrl);
}

