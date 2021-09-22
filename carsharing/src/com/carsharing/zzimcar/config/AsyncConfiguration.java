package com.carsharing.zzimcar.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    private static int TASK_CORE_POOL_SIZE = 10;	// 기본 Thread 수
    private static int TASK_MAX_POOL_SIZE = 20;	// 최대 Thread 수
    private static int TASK_QUEUE_CAPACITY = 10;	// QUEUE 수
    private static String EXECUTOR_BEAN_NAME = "asyncExecutor";

	@Bean(name = "asyncExecutor")
	@Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(TASK_CORE_POOL_SIZE);
        executor.setMaxPoolSize(TASK_MAX_POOL_SIZE);
        executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
        executor.setThreadNamePrefix("Asynch-");
        executor.setBeanName(EXECUTOR_BEAN_NAME);
        executor.initialize();

        System.out.println("#### Init asyncExecutor ################################################### ");
        return executor;
    }




}
