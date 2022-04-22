package org.dbpedia.databus.mods.worker.springboot.config

import org.dbpedia.databus.mods.model.ModActivity
import org.dbpedia.databus.mods.worker.springboot.EnableModWorkerApi
import org.dbpedia.databus.mods.worker.springboot.controller.{BasicWorkerApi, PollingBasedWorkerApi, WorkerApi, WorkerApiProfile}
import org.dbpedia.databus.mods.worker.springboot.service.{ActivityService, DefaultModActivity, LocalResultService, ResultService}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration}

import java.io.File

@Configuration
class ModWorkerApiAutoConfig() {

  private val log = LoggerFactory.getLogger(classOf[ModWorkerApiAutoConfig])

  @Bean
  @ConditionalOnMissingBean(Array(classOf[ModActivity]))
  def getModActivity(): ModActivity = {
    new DefaultModActivity()
  }

  @Bean
  def getActivityService(): ActivityService = {
    new ActivityService
  }

  @Value("${result.dir:files}")
  var baseDirPath: String = _

  @Bean
  @ConditionalOnMissingBean(Array(classOf[ResultService]))
  def resultService(): ResultService = {
    new LocalResultService(new File(baseDirPath))
  }

  @Bean
  def defString(context: ApplicationContext): WorkerApi = {
    import scala.collection.JavaConverters._

    val annotation = context.getBeansWithAnnotation(classOf[EnableModWorkerApi]).keySet().asScala.map(
      key => context.findAnnotationOnBean(key, classOf[EnableModWorkerApi])
    ).headOption

    if (annotation.isDefined) {
      annotation.get.profile() match {
        case WorkerApiProfile.Basic => new BasicWorkerApi
        case WorkerApiProfile.Polling => new PollingBasedWorkerApi(getActivityService())
      }
    } else {
      new BasicWorkerApi
    }
  }

  //  @Bean
  //  def workerApi(): WorkerApi = {
  //    new PollingBasedWorkerApi(activityService())
  //  }
}
