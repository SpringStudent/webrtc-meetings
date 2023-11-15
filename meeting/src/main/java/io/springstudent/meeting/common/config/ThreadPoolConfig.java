package io.springstudent.meeting.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class ThreadPoolConfig {

    @Bean("uploadModelExecutor")
    public ThreadPoolTaskExecutor uploadModelExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(5);
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setThreadNamePrefix("upload-model-executor-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
