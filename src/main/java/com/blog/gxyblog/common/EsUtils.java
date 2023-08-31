package com.blog.gxyblog.common;

import cn.hutool.json.JSONUtil;
import com.blog.gxyblog.dto.ArticleDto;
import com.blog.gxyblog.entity.Article;
import com.blog.gxyblog.exception.BizException;
import com.blog.gxyblog.po.ResultCodeEnum;
import com.blog.gxyblog.service.impl.ArticleServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.blog.gxyblog.common.ArticleDoc.INDICES_NAME;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/8/4 12:22
 * @DESCRIPTION:
 */
@Component
@Slf4j
public class EsUtils {
    @Autowired
    private ArticleServiceImpl articleService;
    @Resource
    private RestHighLevelClient restHighLevelClient;

    //批量更新Doc数据
    @SneakyThrows
    public String updateByMysql() {
        List<Article> articles = articleService.list();
        BulkRequest request = new BulkRequest();
        for (Article article : articles) {
            //判断是否已经加入这条数据
            GetRequest getRequest = new GetRequest(INDICES_NAME, article.getId().toString());
            GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            if (documentFields.isExists()) {
                log.info("这条数据已经存在");
                continue;
            }
            request.add(new IndexRequest(INDICES_NAME).id(article.getId().toString())
                    .source(JSONUtil.toJsonStr(article), XContentType.JSON));
        }
        restHighLevelClient.bulk(request,RequestOptions.DEFAULT);
        return "批量更新ES文档成功";

    }

    //es全文检索查询方法
    public List<Article> selByEs(String text) {
        //设置请查询条件
        SearchRequest request = new SearchRequest(INDICES_NAME);
        request.source().query(QueryBuilders.matchQuery(ArticleDoc.INDICES_FIELD, text));
        request.source().highlighter(new HighlightBuilder().field(ArticleDoc.HIGH_FIELD_TITLE)
                .field(ArticleDoc.HIGH_FIELD_CONTENT).field(ArticleDoc.HIGH_FIELD_DESCRIPTION)
                .requireFieldMatch(false)
        );
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return analysisDoc(response);
        } catch (IOException e) {
            throw new BizException(ResultCodeEnum.ES_SEARCH_ERROR);
        }
    }

    //同步mysql插入对象
    public void insertByMysql(ArticleDto articleDto) {
        Article articleDoc = (Article) articleDto;
        BulkRequest bulkRequest = new BulkRequest();
        BulkRequest request = bulkRequest.add(new IndexRequest(INDICES_NAME).id(articleDoc.getId().toString())
                .source(JSONUtil.toJsonStr(articleDoc), XContentType.JSON));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            log.info("同步插入MYSQL数据成功");
        } catch (IOException e) {
            log.error("ES插入DOC失败id:{},标题:{}", articleDoc.getId(), articleDoc.getTitle());
            throw new BizException(ResultCodeEnum.ES_INSERT_ERROR);
        }
    }

    //同步删除MYSQL信息
    public void delByMysql(Long id) {
        DeleteRequest deleteRequest = new DeleteRequest(INDICES_NAME, id.toString());
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("同步删除MYSQL数据成功");
        } catch (IOException e) {
            log.error("ES插入DOC失败id:{}", id);
            e.printStackTrace();
        }
    }

    //解析Es查询出来的对象
    private List<Article> analysisDoc(SearchResponse response) {
        List<Article> articles = new ArrayList<>();
        //解析查询对象
        SearchHits searchHits = response.getHits();
        for (SearchHit hit : searchHits) {
            String str = hit.getSourceAsString();
            Article article = JSONUtil.toBean(str, Article.class);
            //获取高亮自当
            Map<String, HighlightField> fields = hit.getHighlightFields();
            article.setTitle(fields.get(ArticleDoc.HIGH_FIELD_TITLE).getFragments()[0].toString());
            article.setTitle(fields.get(ArticleDoc.HIGH_FIELD_TITLE).getFragments()[0].toString());
            article.setTitle(fields.get(ArticleDoc.HIGH_FIELD_TITLE).getFragments()[0].toString());
            articles.add(article);
        }
        return articles;
    }
}
