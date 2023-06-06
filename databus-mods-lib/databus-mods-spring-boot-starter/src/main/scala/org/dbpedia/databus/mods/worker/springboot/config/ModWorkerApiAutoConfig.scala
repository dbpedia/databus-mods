package org.dbpedia.databus.mods.worker.springboot.config

import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityMetadataBuilder, ModActivityRequest}
import org.dbpedia.databus.mods.worker.springboot.controller.{ActivityController, ActivityControllerImpl, ActivityControllerPollImpl}
import org.dbpedia.databus.mods.worker.springboot.service.{ActivityExecutionService, LocalResultService, ResultService}
import org.dbpedia.databus.mods.worker.springboot.{EnableModWorkerApi, ModWorkerApiProfile}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration}

import java.io.File
import scala.collection.JavaConverters._


@Configuration
class ModWorkerApiAutoConfig() {

  private val log = LoggerFactory.getLogger(classOf[ModWorkerApiAutoConfig])

  @Bean
  @ConditionalOnMissingBean(Array(classOf[ModActivity]))
  def getModActivity: ModActivity = (request: ModActivityRequest, builder: ModActivityMetadataBuilder) => {
    builder.withStatSummary("1.0").build()
  }

  @Bean
  def getActivityService(modActivity: ModActivity): ActivityExecutionService = {
    new ActivityExecutionService(modActivity)
  }

  @Value("${result.dir:files}")
  var baseDirPath: String = _

  @Value("${api.http.retry-after:0}")
  var retryAfter: Int = 0

  @Bean
  @ConditionalOnMissingBean(Array(classOf[ResultService]))
  def resultService(): ResultService = {
    new LocalResultService(new File(baseDirPath))
  }

  @Bean
  def getActivityController(
    context: ApplicationContext,
    activityExecutionService: ActivityExecutionService
  ): ActivityController = {

    val annotation = context.getBeansWithAnnotation(classOf[EnableModWorkerApi]).keySet().asScala.map(
      key => context.findAnnotationOnBean(key, classOf[EnableModWorkerApi])
    ).headOption

    if (annotation.isDefined) {
      annotation.get.profile() match {
        case ModWorkerApiProfile.Http => new ActivityControllerImpl
        case ModWorkerApiProfile.HttpPoll => new ActivityControllerPollImpl(activityExecutionService)
      }
    } else {
      new ActivityControllerImpl
    }
  }
}
