package org.dbpedia.databus.mods.validate

import org.apache.commons.io.IOUtils
import org.dbpedia.databus.mods.core.worker.api.ModActivityClientHttp
import picocli.CommandLine.{Command, Option}

import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.concurrent.Callable

@Command(name = "httpapi", mixinStandardHelpOptions = true)
class ApiHttpValidator extends Callable[Int] {

  @Option(names = Array("-w", "--worker"), required = true)
  var endpoint: String = _

  @Option(names = Array("-i", "--dataId"), required = true)
  var dataId: String = _

  @Option(names = Array("--accessUri"), required = false)
  var accessUri: String = _

  override def call(): Int = {
      val client = new ModActivityClientHttp
      val modActivityResponse = client.send(new URI(endpoint), new URI(dataId), minDelay = 200)
      println(IOUtils.toString(modActivityResponse.data,StandardCharsets.UTF_8.name()))
    0
  }
}
