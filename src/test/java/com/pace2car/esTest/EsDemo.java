package com.pace2car.esTest;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * es基本API测试
 * @author Pace2Car
 * @date 2018/12/4 15:38
 */
public class EsDemo {

    // ES集群
    private Settings settings = null;
    // ES服务器的客户端
    private TransportClient client = null;

    @Before
    public void setUp() throws Exception {
        //初始化、创建实例
        settings = Settings.builder().put("cluster.name", "my-application").build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.44.128"), 9300));
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    /**
     * 增
     *
     * @throws IOException
     */
    @Test
    public void testAddDoc() throws IOException {
        //创建文档
        XContentBuilder doc = XContentFactory.jsonBuilder()
                .startObject().field("id", "1")
                .field("title", "elasticsearch学习笔记")
                .field("content", "es的学习笔记和踩坑合集")
                .field("postdate", "2018-12-04")
                .field("url", "https://pace2car.com/2018/11/23/Elasticsearch%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/")
                .endObject();
        //添加文档
        IndexResponse response = client.prepareIndex("javademo", "blog", "1")
                .setSource(doc).get();
        System.out.println(response.status());
    }

    /**
     * 查
     */
    @Test
    public void testEsGet() {
        //数据查询
        GetResponse response = client.prepareGet("javademo", "blog", "1").execute().actionGet();
        System.out.println(response.getSourceAsString());
    }

    /**
     * 删
     */
    @Test
    public void testDel() {
        //删除文档
        DeleteResponse response = client.prepareDelete("javademo", "blog", "1").get();
        System.out.println(response.status());
    }

    /**
     * 改
     */
    @Test
    public void testUpdate() throws IOException, ExecutionException, InterruptedException {
        //创建请求文档
        UpdateRequest request = new UpdateRequest();
        request.index("javademo").type("blog").id("1")
                .doc(
                        XContentFactory.jsonBuilder()
                                .startObject()
                                .field("title", "elasticsearch学习笔记_2")
                                .endObject()
                );
        //更新文档
        UpdateResponse response = client.update(request).get();
        System.out.println(response.status());
    }

    /**
     * 有则修改，无则添加
     */
    @Test
    public void testUpsert() throws IOException, ExecutionException, InterruptedException {
        IndexRequest request1 = new IndexRequest("javademo", "blog", "2")
                .source(
                        XContentFactory.jsonBuilder()
                                .startObject().field("id", "100")
                                .field("title", "elasticsearch学习笔记_3")
                                .field("content", "es的学习笔记和踩坑合集")
                                .field("postdate", "2018-12-04")
                                .field("url", "https://pace2car.com/2018/11/23/Elasticsearch%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/")
                                .endObject()
                );

        UpdateRequest request2 = new UpdateRequest("javademo", "blog", "2")
                .doc(
                        XContentFactory.jsonBuilder()
                                .startObject()
                                .field("id", "102")
                                .endObject()
                ).upsert(request1);

        UpdateResponse response = client.update(request2).get();
        System.out.println(response.status());
    }

    /**
     * 多个查询
     */
    @Test
    public void testMultiGet() {
        // 创建multiGet操作实例
        MultiGetResponse responses = client.prepareMultiGet()
                .add("javademo", "blog", "2")
                .add("lib7", "user", "1")
                .get();

        for (MultiGetItemResponse item : responses) {
            GetResponse response = item.getResponse();
            if (response != null && response.isExists()) {
                System.out.println(response.getSourceAsString());
            }
        }
    }

    /**
     * 批量操作
     */
    @Test
    public void testBulk() throws IOException {
        // 创建bulk操作
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        // 装入操作项
        bulkRequestBuilder.add(client.prepareIndex("javademo", "blog", "3")
                .setSource(
                        XContentFactory.jsonBuilder()
                                .startObject().field("id", "1001")
                                .field("title", "elasticsearch学习笔记_4")
                                .field("content", "es的学习笔记和踩坑合集")
                                .field("postdate", "2018-12-04")
                                .field("url", "https://pace2car.com/2018/11/23/Elasticsearch%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/")
                                .endObject()
                ));

        bulkRequestBuilder.add(client.prepareIndex("javademo", "blog", "4")
                .setSource(
                        XContentFactory.jsonBuilder()
                                .startObject().field("id", "1002")
                                .field("title", "elasticsearch学习笔记_5")
                                .field("content", "es的学习笔记和踩坑合集")
                                .field("postdate", "2018-12-04")
                                .field("url", "https://pace2car.com/2018/11/23/Elasticsearch%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/")
                                .endObject()
                ));

        BulkResponse responses = bulkRequestBuilder.get();
        System.out.println(responses.status());
        if (responses.hasFailures()) {
            System.err.println("操作失败");
        }
    }

    /**
     * 查询删除
     */
    @Test
    public void testQueryDelete() {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("title", "_4"))
                .source("javademo")
                .get();
        long counts = response.getDeleted();
        System.out.println(counts);
    }

    /**
     * 查询所有match_all
     */
    @Test
    public void testMatchAll() {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(queryBuilder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * 使用match查询
     */
    @Test
    public void testMatch() {
        QueryBuilder builder = QueryBuilders.matchQuery("title", "_3");

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * 多匹配查询multimatch
     */
    @Test
    public void testMultiMatch() {
        QueryBuilder builder = QueryBuilders.multiMatchQuery("学习", "title", "content");

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * term查询
     */
    @Test
    public void testTerm() {
        QueryBuilder builder = QueryBuilders.termQuery("title", "学习");

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * terms查询
     */
    @Test
    public void testTerms() {
        QueryBuilder builder = QueryBuilders.termsQuery("title", "_3", "_5");

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * range范围查询
     */
    @Test
    public void testRange() {
        QueryBuilder builder = QueryBuilders.rangeQuery("postdate")
                .from("1900-01-01")
                .to("2020-01-01").format("yyyy-MM-dd");

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * prefix前缀查询
     */
    @Test
    public void testPrefix() {
        QueryBuilder builder = QueryBuilders.prefixQuery("title", "elastic");

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * wildcard通配符查询
     */
    @Test
    public void testWildcard() {
        QueryBuilder builder = QueryBuilders.wildcardQuery("title", "*学习*");

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * fuzzy模糊查询
     */
    @Test
    public void testFuzzy() {
        QueryBuilder builder = QueryBuilders.fuzzyQuery("title", "学习");

        SearchResponse response = client.prepareSearch("javademo")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * type查询
     */
    @Test
    public void testType() {
        QueryBuilder builder = QueryBuilders.typeQuery("user");

        SearchResponse response = client.prepareSearch("lib7")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * ids查询
     */
    @Test
    public void testIds() {
        QueryBuilder builder = QueryBuilders.idsQuery().addIds("1", "3", "5");

        SearchResponse response = client.prepareSearch("lib7")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * 聚合查询
     * max/min/avg/sum/..自如变化
     * cardinality 求基数
     */
    @Test
    public void testAgg() {
        AggregationBuilder builder = AggregationBuilders.max("aggMax").field("age");

        SearchResponse response = client.prepareSearch("lib7")
                .addAggregation(builder).get();

        Max aggMax = response.getAggregations().get("aggMax");
        System.out.println(aggMax.getValue());
    }

    /**
     * query_string测试
     */
    @Test
    public void testQueryString() {
        //指定确定field的
//        QueryBuilder builder = QueryBuilders.commonTermsQuery("name", "陈");
        //精确匹配的
//        QueryBuilder builder = QueryBuilders.queryStringQuery("+篮球 -爬山");
        // 不那么精确的
        QueryBuilder builder = QueryBuilders.simpleQueryStringQuery("+篮球 -爬山");

        SearchResponse response = client.prepareSearch("lib7")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * boolQuery组合查询
     */
    @Test
    public void testBoolQuery() {
        QueryBuilder builder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("interests", "篮球"))
                .mustNot(QueryBuilders.matchQuery("interests", "爬山"))
                .should(QueryBuilders.matchQuery("address", "成都"))
                .filter(QueryBuilders.rangeQuery("birthday").gte("1999-01-01").lte("2099-01-01").format("yyyy-MM-dd"));

        SearchResponse response = client.prepareSearch("lib7")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * constantscore不计分数
     */
    @Test
    public void testConstantScore() {
        QueryBuilder builder = QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("name", "test"));

        SearchResponse response = client.prepareSearch("lib7")
                .setQuery(builder).setSize(3).get();

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String key : sourceAsMap.keySet()) {
                System.out.println(key + "=" + sourceAsMap.get(key));
            }
        }
    }

    /**
     * agg terms分组聚合统计
     */
    @Test
    public void testAggTermsBucket() {
        AggregationBuilder builder = AggregationBuilders.terms("terms").field("age");

        SearchResponse response = client.prepareSearch("lib7")
                .addAggregation(builder).execute().actionGet();

        Terms terms = response.getAggregations().get("terms");

        for (Terms.Bucket entry : terms.getBuckets()) {
            System.out.println(entry.getKey() + "=" + entry.getDocCount());
        }
    }

    /**
     * filter过滤聚合
     */
    @Test
    public void testFilterBucket() {
        QueryBuilder query = QueryBuilders.termQuery("age", "22");

        AggregationBuilder builder = AggregationBuilders.filter("filter", query);

        SearchResponse response = client.prepareSearch("lib7")
                .addAggregation(builder).execute().actionGet();

        Filter filter = response.getAggregations().get("filter");

        System.out.println(filter.getDocCount());
    }

    /**
     * filters多条件聚合
     */
    @Test
    public void testFiltersBucket() {
        AggregationBuilder builder = AggregationBuilders.filters("filters",
                new FiltersAggregator.KeyedFilter("跑步", QueryBuilders.termQuery("interests", "跑步")),
                new FiltersAggregator.KeyedFilter("喝水", QueryBuilders.termQuery("interests", "喝水")));

        SearchResponse response = client.prepareSearch("lib7")
                .addAggregation(builder).execute().actionGet();

        Filters filters = response.getAggregations().get("filters");

        for (Filters.Bucket entry : filters.getBuckets()) {
            System.out.println(entry.getKey() + "=" + entry.getDocCount());
        }
    }

    /**
     * range范围聚合
     */
    @Test
    public void testRangeBucket() {
        AggregationBuilder builder = AggregationBuilders.range("range")
                .field("age")
                .addUnboundedFrom(25)
                .addUnboundedTo(30)
                .addRange(20, 25)
                .addRange(25, 30);

        SearchResponse response = client.prepareSearch("lib7")
                .addAggregation(builder).execute().actionGet();

        Range range = response.getAggregations().get("range");

        for (Range.Bucket entry : range.getBuckets()) {
            System.out.println(entry.getKey() + "=" + entry.getDocCount());
        }
    }

    /**
     * missing判空聚合
     */
    @Test
    public void testMissingBucket() {
        AggregationBuilder builder = AggregationBuilders.missing("missing").field("birthday");

        SearchResponse response = client.prepareSearch("lib7")
                .addAggregation(builder).execute().actionGet();

        Aggregation aggregation = response.getAggregations().get("missing");

        System.out.println(aggregation.toString());
    }


}
