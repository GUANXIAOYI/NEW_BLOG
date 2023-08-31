package com.blog.gxyblog.common;

import com.blog.gxyblog.Config.QiLiuCloudAttribute;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/30 17:28
 * @DESCRIPTION:
 */
@Component
public class FileUploadCloud {
    @Resource
    private QiLiuCloudAttribute qiLiuCloudAttribute;

    public String uploadBytes(byte[] bytes,String fileName) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = qiLiuCloudAttribute.getAccesskey();
        String secretKey = qiLiuCloudAttribute.getSecretkey();
        String bucket = qiLiuCloudAttribute.getBucketnaem();
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = fileName;
        try {
            // byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return qiLiuCloudAttribute.getHostsname() + putRet.key;//key是上传文件的名字
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
    }
}
