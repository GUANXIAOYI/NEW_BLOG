package com.blog.gxyblog.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.blog.gxyblog.common.ArticleDoc;
import com.blog.gxyblog.entity.Article;
import com.blog.gxyblog.exception.BizException;
import com.blog.gxyblog.po.ResultCodeEnum;
import com.blog.gxyblog.service.impl.ArticleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.blog.gxyblog.common.ArticleDoc.*;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/8/1 20:46
 * @DESCRIPTION:
 */
@Slf4j
@SpringBootTest
class CreateIndexLibrary {

    @Resource
    private RestHighLevelClient client;

    @Autowired
    private ArticleServiceImpl articleService;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Test
    void test(){
        System.out.println(rabbitTemplate);
    }

    @Test
    void createIndex() throws IOException {
        //判断索引库是否存在
        GetIndexRequest getIndexRequest = new GetIndexRequest(INDICES_NAME);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!exists) {
            System.out.println("索引库已存在不能重复创建");
            return;
        }
        //    创建索引库
        CreateIndexRequest request = new CreateIndexRequest(INDICES_NAME);
        request.source(ARTICLE_INDEX_DOC, XContentType.JSON);
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
            log.info("创建索引库成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void delIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(INDICES_NAME);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (!exists) {
            throw new BizException(ResultCodeEnum.ES_INDICES_NULL);
        }
        DeleteIndexRequest delRequest = new DeleteIndexRequest(INDICES_NAME);
        client.indices().delete(delRequest, RequestOptions.DEFAULT);
    }

    //ES全文检索
    @Test
    void selByFullText() throws IOException {
        String text = "测试";
        //构建ES查询对象
        SearchRequest request = new SearchRequest(INDICES_NAME);
        //DSL语句
        request.source().query(QueryBuilders.matchQuery(INDICES_FIELD, text));

        request.source().highlighter(new HighlightBuilder().field(HIGH_FIELD_TITLE)
                .field(HIGH_FIELD_DESCRIPTION).field(HIGH_FIELD_CONTENT).requireFieldMatch(false));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        parseDoc(response);
    }

    private void parseDoc(SearchResponse response) {
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            //获取高亮Map
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            String title = highlightFields.get(HIGH_FIELD_TITLE).getFragments()[0].toString();
            String description = highlightFields.get(HIGH_FIELD_DESCRIPTION).getFragments()[0].toString();
            String content = highlightFields.get(HIGH_FIELD_CONTENT).getFragments()[0].toString();
            Article article = JSONUtil.toBean(sourceAsString, Article.class);
            article.setTitle(title);
            article.setContent(content);
            article.setDescription(description);
            System.out.println(article);
        }
    }

    @Test
    void insertDOc() throws IOException {
        //批量新增doc
        BulkRequest bulkRequest = new BulkRequest();
        //给索引库插入MYSQL数据
        List<Article> list = articleService.list();
        for (Article article : list) {
            //判断es是否已经插入这条数据
            GetRequest request = new GetRequest(INDICES_NAME, article.getId().toString());
            GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
            if (documentFields.isExists()) {
                log.info("词条数据已经存在");
                continue;
            }
            bulkRequest.add(new IndexRequest(INDICES_NAME)
                    .id(article.getId().toString())
                    .source(JSONUtil.toJsonStr(article), XContentType.JSON));
        }
        client.bulk(bulkRequest, RequestOptions.DEFAULT);
        log.info("数据保存成功");
    }

    //使用ES查询出对象
    @Test
    void selectDoc() throws IOException {
        //构建es查询条件
        GetRequest request = new GetRequest(INDICES_NAME, "1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String str = response.getSourceAsString();
        Article article = JSONUtil.toBean(str, Article.class);
        System.out.println(article);
        log.info("数据查询成功");
    }
}
