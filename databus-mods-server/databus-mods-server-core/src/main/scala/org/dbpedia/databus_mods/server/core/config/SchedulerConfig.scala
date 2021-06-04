package org.dbpedia.databus_mods.server.core.config

import java.util.Date

import org.dbpedia.databus_mods.server.core.mods.online.OnlineCheckService
import org.dbpedia.databus_mods.server.core.service.TaskService
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.{Trigger, TriggerContext}
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
class SchedulerConfig(onlineCheckService: OnlineCheckService,
                      taskService: TaskService,
                      @Value("${mod-server.schedule.task-updates}") updateDelay: String) extends SchedulingConfigurer {

  override def configureTasks(taskRegistrar: ScheduledTaskRegistrar): Unit = {
    val taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(10);
    taskScheduler.initialize();
    taskRegistrar.setTaskScheduler(taskScheduler);
    // online-check
    taskRegistrar.addTriggerTask(new Runnable {
      override def run(): Unit = {
        onlineCheckService.update()
      }
    },
      new Trigger {
        override def nextExecutionTime(triggerContext: TriggerContext): Date = {
          onlineCheckService.getNextExecutionTime(triggerContext.lastActualExecutionTime())
        }
      })
    // task updates
    if(updateDelay.toInt > 0) {
      taskRegistrar.addFixedDelayTask(new Runnable {
        override def run(): Unit = {
          taskService.update()
        }
      }, updateDelay.toInt)
    }
  }
}

