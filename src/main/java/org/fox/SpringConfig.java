package org.fox;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;

import org.fox.domain.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ExtractingResponseErrorHandler;
import org.springframework.web.client.RestTemplate;


@Configuration
@ComponentScan(basePackages = "org.fox")
public class SpringConfig {

    public final static String uiExecutor = "uiExecutor";

    @Autowired
    private Properties properties;


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ExtractingResponseErrorHandler());

        return restTemplate;
    }

    @Bean
    public ForkJoinPool forkJoinPool() {
        return new ForkJoinPool(properties.getWorkers());
    }

    @Bean
    public ScheduledExecutorService uiExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
