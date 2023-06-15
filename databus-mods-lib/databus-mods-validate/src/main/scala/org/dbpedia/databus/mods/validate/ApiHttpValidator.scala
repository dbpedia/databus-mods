package org.dbpedia.databus.mods.validate

import org.apache.commons.io.IOUtils
import org.dbpedia.databus.mods.core.worker.api.ModActivityClientHttp
import picocli.CommandLine.{Command, Option}

import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.concurrent.Callable

@Command(name = "httpapi", mixinStandardHelpOptions = true)
class ApiHttpValidator extends Callable[Int] {

  @Option(
    names = Array("-w", "--worker-endpoint"),
    required = true,
    description = Array("The URL of the mod worker API endpoint '.../activity'")
  )
  var endpoint: String = _

  @Option(
    names = Array("-i", "--dataId"),
    required = true,
    description = Array("A (Databus) DataID IRI")
  )
  var dataId: String = _

  @Option(
    names = Array("--accessUri"),
    required = false,
    description = Array("An alternative access URI of the file referenced by the given DataID")
  )
  var accessUri: String = _

  override def call(): Int = {
      val client = new ModActivityClientHttp
      val modActivityResponse = client.send(new URI(endpoint), new URI(dataId), minDelay = 200)
      println(IOUtils.toString(modActivityResponse.data,StandardCharsets.UTF_8.name()))
    0
  }
}
