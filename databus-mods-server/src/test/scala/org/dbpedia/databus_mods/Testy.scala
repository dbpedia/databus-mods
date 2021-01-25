package org.dbpedia.databus_mods

import java.io.ByteArrayInputStream
import java.net.{HttpURLConnection, URL}
import java.nio.charset.StandardCharsets
import java.util.concurrent.{CompletableFuture, LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}
import java.util.function
import java.util.function.{BiConsumer, Consumer}

import org.apache.commons.io.IOUtils
import org.apache.jena.ext.com.google.common.base.Supplier
import org.apache.jena.query.DatasetFactory
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.system.ErrorHandlerFactory
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFLanguages, RDFParser, RDFWriter}
import org.apache.jena.sparql.core.DatasetGraphFactory
import org.apache.parquet.format.LogicalTypes.TimeUnits

import scala.util.Random
import org.scalatest.funsuite.AnyFunSuite

class Testy extends AnyFunSuite {

  test("read turtle") {
    val ttl =
      """<http://subject.org>  <http://property.org>
        |                "foobar" .""".stripMargin

    val model = ModelFactory.createDefaultModel()

    RDFDataMgr.read(model, new ByteArrayInputStream(ttl.getBytes(StandardCharsets.UTF_8)),RDFLanguages.NTRIPLES)

    model.write(System.out,Lang.NTRIPLES.getName)

//    val dataset = DatasetGraphFactory.create()
//
//    RDFParser.create()
//      .source(new ByteArrayInputStream(ttl.getBytes(StandardCharsets.UTF_8)))
//      .lang(RDFLanguages.TTL)
//      .errorHandler(ErrorHandlerFactory.errorHandlerStrict)
//      .base("http://example/base")
//      .parse(dataset)
//
//    RDFWriter.create()
//      .source(dataset)
//      .lang(RDFLanguages.NQUADS)
//      .output(System.out)

//    val model = ModelFactory.createDefaultModel()
//    model.read(new ByteArrayInputStream(ttl.getBytes(StandardCharsets.UTF_8)),"NTriples")
//    model.write(System.out,"NTriples")
  }


  class TestRun extends Runnable {

    def ext(): Unit = {
      Thread.sleep(2000)
      println(System.currentTimeMillis()+" TestRun.ext "+Thread.currentThread().getName)
    }


    override def run(): Unit = {
      while (true) {
        ext()
        println(System.currentTimeMillis()+" TestRun.run "+Thread.currentThread().getName)
      }
    }
  }

  test("urlConn") {
    val conn = new URL("http://localhost:9000/demo/p/g/a/v/c").openConnection().asInstanceOf[HttpURLConnection]
    println(conn.getResponseCode)
    IOUtils.copy(conn.getInputStream,System.out)
  }

  test("testRun") {
    val testRun = new TestRun

    new Thread(testRun).start()

    testRun.ext()

    Thread.sleep(15000)
  }

  test("abc") {

    val pool = new ThreadPoolExecutor(2,4,50,TimeUnit.SECONDS,new LinkedBlockingQueue[Runnable]())

    println("BOF")

    CompletableFuture.supplyAsync(new Supplier[String] {
      override def get(): String = {
        println(1)
        Thread.sleep(1000)
        println(11)
        throw new Exception("")
        "abc"
      }
    },pool).whenCompleteAsync(new BiConsumer[String,Throwable] {
      override def accept(t: String, u: Throwable): Unit = {
        println(2)
        Thread.sleep(1000)
        println(22)
      }
    })
//      thenAcceptAsync(new function.Function[String,String] {
//      override def apply(t: String): String = {
//        println(2)
//        Thread.sleep(1000)
//        println(22)
//        t.drop(1)
//      }
//    },pool)

//    future.get()



    println("EOF")
    pool.shutdown()
    pool.awaitTermination(Long.MaxValue,TimeUnit.SECONDS)
  }
}
