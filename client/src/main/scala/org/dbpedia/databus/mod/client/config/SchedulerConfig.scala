package org.dbpedia.databus.mod.client.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
class SchedulerConfig extends SchedulingConfigurer {
  /**
   * The pool size.
   */
  final private val POOL_SIZE = 2

  /**
   * Configures the scheduler to allow multiple pools.
   *
   * @param taskRegistrar The task registrar.
   */
  override def configureTasks(taskRegistrar: ScheduledTaskRegistrar): Unit = {
    val threadPoolTaskScheduler = new ThreadPoolTaskScheduler
    threadPoolTaskScheduler.setPoolSize(POOL_SIZE)
    threadPoolTaskScheduler.setThreadNamePrefix("scheduled-task-pool-")
    threadPoolTaskScheduler.initialize()
    taskRegistrar.setTaskScheduler(threadPoolTaskScheduler)
  }
}