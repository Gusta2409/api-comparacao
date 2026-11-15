package br.edu.tcc.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ConfiguracaoDemonstracao {
 @Bean
 public ThreadPoolTaskExecutor executorDemonstracao() {
  var executor = new ThreadPoolTaskExecutor();
  executor.setCorePoolSize(4);
  executor.setMaxPoolSize(4);
  executor.setQueueCapacity(100);
  executor.setThreadNamePrefix("busca-simulada-");
  executor.setWaitForTasksToCompleteOnShutdown(true);
  executor.setAwaitTerminationSeconds(35);
  return executor;
 }
}
