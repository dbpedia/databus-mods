package org.dbpedia.databus.mods.validate

import ch.qos.logback.classic.{Level, Logger}
import org.dbpedia.databus.mods.core.worker.api.ModActivityClientHttp
import org.slf4j.LoggerFactory
import picocli.AutoComplete.GenerateCompletion
import picocli.CommandLine
import picocli.CommandLine.{Command, Option}

import java.util.concurrent.Callable

object ValidationCLI extends App {

  val apacheLog = LoggerFactory.getLogger("org.apache.http").asInstanceOf[Logger]
  apacheLog.setLevel(Level.OFF)

  val exitCode = {
    if (0 == args.length)
      new CommandLine(new ValidationCLI).execute("--help")
    else
      new CommandLine(new ValidationCLI).execute(args: _*)
  }
  System.exit(exitCode)
}

@Command(name = "validate", mixinStandardHelpOptions = true, subcommands = Array(classOf[ApiHttpValidator], classOf[GenerateCompletion]))
class ValidationCLI extends Callable[Int] {

  override def call(): Int = {
    0 // SUCCESS
  }
}