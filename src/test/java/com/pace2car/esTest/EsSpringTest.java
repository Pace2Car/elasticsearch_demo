package com.pace2car.esTest;

import com.pace2car.elastic.service.BlogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Pace2Car
 * @date 2018/12/5 16:42
 */
@ContextConfiguration("classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class EsSpringTest {

    @Autowired
    private BlogService blogService;

    @Test
    public void testFind() {
        blogService.findBlogById(1);
    }
}
