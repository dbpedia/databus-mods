package org.dbpedia.databus_mods.server.core.demo

import java.util.concurrent.{ConcurrentMap, TimeUnit}

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

import scala.util.Random

@Component
class ModRepo {

  @Bean
  def getModRepo: ModRepo = {
    new ModRepo
  }

  val cacheLoader: LoadingCache[String, Option[Model]] = {
    CacheBuilder
      .newBuilder
      .maximumSize(10000)
      .expireAfterWrite(30, TimeUnit.SECONDS)
      .build(new CacheLoader[String, Option[Model]] {
        def load(key: String): Option[Model] = {
          if (key == "p/g/a/v/c") {
            None
          } else {
            Thread.sleep(Random.nextInt(5000)+200)
            val model = ModelFactory.createDefaultModel()
            model.add(
              ResourceFactory.createResource("http://subject.org"),
              ResourceFactory.createProperty("http://property.org"), "foobar")
            Some(model)
          }
        }
      })
  }

  val cacheMap: ConcurrentMap[String,Option[Model]] = cacheLoader.asMap()
}
