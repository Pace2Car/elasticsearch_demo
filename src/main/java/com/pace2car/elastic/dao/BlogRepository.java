package com.pace2car.elastic.dao;

import com.pace2car.elastic.entity.Blog;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.Repository;

/**
 * @author Pace2Car
 * @date 2018/12/5 16:17
 */
@org.springframework.stereotype.Repository
public interface BlogRepository extends Repository<Blog, String> {

    /**
     * 根据id查文章
     * @param id
     * @return
     */
    @Query("{\"bool\" : {\"must\" : {\"term\" : {\"id\" : \"?0\"}}}}")
    Blog findBlogById(Integer id);

    /**
     * 根据标题查找博客文章
     * @param title
     * @return
     */
    Blog findBlogByTitle(String title);
}
