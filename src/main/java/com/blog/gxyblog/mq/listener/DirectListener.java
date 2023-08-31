package com.blog.gxyblog.mq.listener;

import cn.hutool.json.JSONUtil;
import com.blog.gxyblog.common.EsUtils;
import com.blog.gxyblog.dto.ArticleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.blog.gxyblog.Config.EsClient.*;
import static com.blog.gxyblog.tool.RedisConstants.ARTICLE_CODE_KEY;
import static com.blog.gxyblog.tool.RedisConstants.COMMON_TTL;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/8/6 0:50
 * @DESCRIPTION:
 */
@Component
@Slf4j
public class DirectListener {
    @Autowired
    private EsUtils esUtils;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //监听同步增加redis和es数据
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "mq-put-queue"),
            exchange = @Exchange(name = EXCHANGE_PUT, type = ExchangeTypes.DIRECT),
            key = {EXCHANGE_PUT_KEY}
    ))
    public void listenerRedisPut(ArticleDto articleDto) {
        String key = ARTICLE_CODE_KEY + articleDto.getId();
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(articleDto), COMMON_TTL, TimeUnit.SECONDS);
        //同步插入到EsDoc
        esUtils.insertByMysql(articleDto);
        log.info("mq同步mysql数据插入ES成功");
    }


    //同步监听ES和redis并删除
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "mq-del-queue"),
            exchange = @Exchange(name = EXCHANGE_DEL, type = ExchangeTypes.DIRECT),
            key = {EXCHANGE_DEL_KEY}
    ))
    public void listenerDel(Long id) {
        String key = ARTICLE_CODE_KEY + id;
        esUtils.delByMysql(id);
        stringRedisTemplate.delete(key);
        log.info("mq同步mysql数据删除ES和redis成功");
    }

    //更新ES胡春
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "mq-update-queue"),
            exchange = @Exchange(name = EXCHANGE_UPDATE, type = ExchangeTypes.DIRECT),
            key = {EXCHANGE_UPDATE_KEY}
    ))
    public void listenerUpdate(ArticleDto articleDto){
        String key = ARTICLE_CODE_KEY + articleDto.getId();
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(articleDto));
        //删除旧的doc
        esUtils.delByMysql(articleDto.getId());
        //给es添加新的对像
        esUtils.insertByMysql(articleDto);
        log.info("mq同步mysql数据更新ES和redis成功");

    }
}

