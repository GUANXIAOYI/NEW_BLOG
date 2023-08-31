package com.blog.gxyblog.Config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/8/1 20:30
 * @DESCRIPTION:
 */
@Configuration
public class EsClient {
    public static  final String EXCHANGE_PUT = "exchange_put";
    public static  final String EXCHANGE_PUT_KEY = "exchange_put_common";
    public static  final String EXCHANGE_UPDATE = "exchange_update";
    public static  final String EXCHANGE_UPDATE_KEY = "exchange_update_key";
    public static  final String EXCHANGE_DEL = "exchange_del";
    public static  final String EXCHANGE_DEL_KEY = "exchange_del_common";

    @Value("${es.host}")
    private String host;
    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(host)
        ));
    }
}

