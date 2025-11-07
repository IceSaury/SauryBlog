package com.saury.blog.service.impl;

import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.saury.blog.config.OssProperties;
import com.saury.blog.exception.BusinessException;
import com.saury.blog.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * OSS文件上传服务实现
 *
 * @author Saury
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final OSS ossClient;
    private final OssProperties ossProperties;

    @Override
    public String uploadFile(MultipartFile file) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new BusinessException("文件名不能为空");
        }

        // 验证文件类型（仅允许图片）
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("仅支持上传图片文件");
        }

        // 验证文件大小（限制5MB）
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        // 获取文件扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 生成新文件名：prefix/日期/UUID.扩展名
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = ossProperties.getPrefix() + date + "/" + IdUtil.simpleUUID() + extension;

        try {
            // 上传文件到OSS
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getBucketName(),
                    fileName,
                    inputStream
            );
            ossClient.putObject(putObjectRequest);

            // 返回文件URL
            if (StringUtils.isNotBlank(ossProperties.getDomain())) {
                // 使用自定义域名
                return ossProperties.getDomain() + "/" + fileName;
            } else {
                // 使用默认域名
                return "https://" + ossProperties.getBucketName() + "." + 
                       ossProperties.getEndpoint() + "/" + fileName;
            }
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return;
        }

        try {
            // 从URL中提取文件名
            String fileName;
            if (fileUrl.contains(ossProperties.getDomain())) {
                fileName = fileUrl.substring(fileUrl.indexOf(ossProperties.getDomain()) + ossProperties.getDomain().length() + 1);
            } else {
                fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            }

            // 删除文件
            ossClient.deleteObject(ossProperties.getBucketName(), fileName);
            log.info("删除文件成功: {}", fileName);
        } catch (Exception e) {
            log.error("删除文件失败: {}", fileUrl, e);
        }
    }
}

