package com.blog.gxyblog.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/30 17:06
 * @DESCRIPTION:
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "qiniucloud")
public class QiLiuCloudAttribute {
    private String accesskey;
    private String secretkey;
    private String hostsname;
    private String bucketnaem;

}
