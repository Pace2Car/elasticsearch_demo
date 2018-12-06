package com.pace2car.elastic.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author Pace2Car
 * @date 2018/12/5 16:05
 */
@Document(indexName = "javademo", type = "blog")
public class Blog implements Serializable {

    @Id
    @Field(index = false, store = true, type = FieldType.Integer)
    private Integer id;

    @Field(analyzer = "ik", store = true, searchAnalyzer = "ik", type = FieldType.Text)
    private String title;

    @Field(analyzer = "ik", store = true, searchAnalyzer = "ik", type = FieldType.Text)
    private String content;

    @Field(analyzer = "standard", store = true, searchAnalyzer = "standard", type = FieldType.Date)
    private String postdate;

    @Field(analyzer = "standard", store = true, searchAnalyzer = "standard", type = FieldType.Text)
    private String url;

    public Blog() {
    }

    public Blog(Integer id, String title, String content, String postdate, String url) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postdate = postdate;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", postdate='" + postdate + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
