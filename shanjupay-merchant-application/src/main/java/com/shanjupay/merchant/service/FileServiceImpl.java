package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.QiniuUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class FileServiceImpl implements FileService{

    @Value("${oss.qiniu.url}")
    private String qiniuUrl;
    @Value("${oss.qiniu.accessKey}")
    private String accessKey;
    @Value("${oss.qiniu.secretKey}")
    private String secretKey;
    @Value("${oss.qiniu.bucket}")
    private String bucket;

    /**
     * 上传文件
     *
     * @param bytes    文件字节数组
     * @param fileName 文件名
     * @return 文件访问路径（绝对的url）
     * @throws BusinessException
     */
    @Override
    public String upload(byte[] bytes, String fileName) throws BusinessException {
        //调用common 下的工具类
        //String accessKey,String secretKey,String bucket, byte[] bytes,String fileName
        try {
            QiniuUtils.upload2qiniu(accessKey,secretKey,bucket,bytes,fileName);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_100106);
        }
        //上传成功返回文件的访问地址（绝对路径）
        return qiniuUrl+fileName;
    }
}
