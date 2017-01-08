package com.berzellius.integrations.playground;

import com.berzellius.integrations.playground.batch.CallsImportBatchConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;

/**
 * Created by berz on 06.01.2017.
 */
@TestConfiguration
@ComponentScan(
        excludeFilters = {
             @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CallsImportBatchConfiguration.class)
            ,@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = Application.class)
            ,@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebInitializer.class)
        },
        includeFilters = {
             //@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TestBeans.class)
        }
)
@EnableAutoConfiguration
@TestPropertySource("classpath:application.properties")
public class TestApplication {
    public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(TestApplication.class, args);
    }
}

