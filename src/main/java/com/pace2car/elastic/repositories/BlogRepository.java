package com.pace2car.elastic.repositories;

import com.pace2car.elastic.entity.Blog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Pace2Car
 * @date 2018/12/5 16:17
 */
@Repository
public interface BlogRepository extends ElasticsearchRepository<Blog, String> {

    /**
     * 根据id查文章
     * @param id
     * @return
     */
    Blog queryBlogById(String id);

    /**
     * 根据标题查找博客文章
     * @param title
     * @return
     */
    Blog findBlogByTitle(String title);
}
