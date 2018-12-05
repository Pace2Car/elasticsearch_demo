package com.pace2car.elastic.service;

import com.pace2car.elastic.entity.Blog;

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
    Blog findBlogById(Integer id);
}
