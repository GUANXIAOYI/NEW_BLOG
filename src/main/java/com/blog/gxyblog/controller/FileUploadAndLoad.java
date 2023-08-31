package com.blog.gxyblog.controller;


import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import com.blog.gxyblog.Config.QiLiuCloudAttribute;
import com.blog.gxyblog.common.FileUploadCloud;
import com.blog.gxyblog.exception.BizException;
import com.blog.gxyblog.po.ResultCodeEnum;
import com.blog.gxyblog.tool.CheckFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.UUID;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/30 17:53
 * @DESCRIPTION:
 */
@Controller()
@Slf4j
@RequestMapping("/common")
public class FileUploadAndLoad {
    @Autowired
    private FileUploadCloud uploadCloud;
    @Autowired
    private QiLiuCloudAttribute qiLiuCloudAttribute;

    @PostMapping("/upload")
    public String fileUpload(@RequestParam(name = "file") MultipartFile file) {
        if (!CheckFile.checkFileSize(file.getSize(), 100, "M")) {
            return "上传文件不能大于100MB";
        }
        //文件后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        if (!suffix.equals(".image") && !suffix.equals(".jpeg") && !suffix.equals("jpg")) {
            return "请上传image和jpeg格式的图片";
        }
        //采用uuid重构名
        String fileName = UUID.randomUUID().toString() + suffix;
        try {
            uploadCloud.uploadBytes(file.getBytes(), fileName);
        } catch (IOException e) {
            throw new BizException(ResultCodeEnum.CODE_FAILURE);
        }
        log.info("文件上传成功");

        return qiLiuCloudAttribute + fileName;
    }

    @GetMapping("/load")
    public void downLoad(HttpServletResponse response, String fileUrl) {
        try {
            //获取网络图片链接
            URL url = new URL(fileUrl);
            /**
             * 在没有对使用的SSL实现类进行配置的情况下，在程序中如果正常使用
             * java.net.URL的不带 URLStreamHandler 参数的构造方法new 一个URL对象的话，
             * url.openConnection()默认是返回sun.net.www.protocol.http.HttpURLConnection
             * 类型对象。所以我们带上一个URLStreamHandler
             */
            // HttpsURLConnection httpUrl = (HttpsURLConnection) url.openConnection();
            sun.net.www.protocol.http.HttpURLConnection httpUrl = (sun.net.www.protocol.http.HttpURLConnection) url.openConnection();
            httpUrl.connect();
            InputStream inputStream = httpUrl.getInputStream();//读取网络文件
            //返回文件流
            response.setContentType("image/jpeg");
            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] bytes = new byte[1024 * 1024];
            int len = 0;
            //读取文件
            while ((len = inputStream.read(bytes)) != -1) {
                //    将图片回显给前端
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
            log.info("图片回显成功");
        } catch (IOException e) {
            throw new BizException(ResultCodeEnum.FILE_LOAD_ERROR);
        }
    }
}
