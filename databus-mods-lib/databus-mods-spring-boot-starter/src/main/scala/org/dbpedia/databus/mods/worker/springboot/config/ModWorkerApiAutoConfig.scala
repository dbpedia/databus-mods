package org.dbpedia.databus.mods.worker.springboot.config

import java.util

import javax.annotation.Priority
import org.dbpedia.databus.mods.worker.springboot.controller.{PollingBasedWorkerApi, WorkerApi}
import org.dbpedia.databus.mods.worker.springboot.service.{ModActivity, ActivityService, DefaultActivityProcessor}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.servlet.handler.{BeanNameUrlHandlerMapping, SimpleUrlHandlerMapping}

@Configuration
class ModWorkerApiAutoConfig() {


  private val log = LoggerFactory.getLogger(classOf[ModWorkerApiAutoConfig])

  @Bean
  def activityService(): ActivityService = {
    new ActivityService()
  }

  @Bean
  @ConditionalOnMissingBean(Array(classOf[ModActivity]))
  def activityProcessor(): ModActivity = {
    new DefaultActivityProcessor()
  }

  @Bean
  def workerApi(): WorkerApi = {
    new PollingBasedWorkerApi(activityService())
  }
}
