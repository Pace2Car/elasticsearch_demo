package com.pace2car.elastic.service.impl;

import com.pace2car.elastic.dao.BlogRepository;
import com.pace2car.elastic.entity.Blog;
import com.pace2car.elastic.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Pace2Car
 * @date 2018/12/5 16:37
 */
@Service("blogService")
public class BlogServiceImpl implements BlogService {

    @Resource
    private BlogRepository blogRepository;

    @Override
    public Blog findBlogById(Integer id) {
        return blogRepository.findBlogById(id);
    }
}
