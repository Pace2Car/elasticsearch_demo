package com.pace2car.elastic.service;

import com.pace2car.elastic.entity.Blog;

import java.util.List;

/**
 * @author Pace2Car
 * @date 2018/12/5 16:33
 */
public interface BlogService {
    /**
     * 根据id查找文章
     * @param id
     * @return
     */
    Blog queryBlogById(String id);

    /**
     * 根据标题查文章
     * @param title
     * @return
     */
    Blog findBlogByTitle(String title);

    /**
     * 根据标题查找所有文章
     * @return
     */
    List<Blog> findAllByTitle(String title);
}
