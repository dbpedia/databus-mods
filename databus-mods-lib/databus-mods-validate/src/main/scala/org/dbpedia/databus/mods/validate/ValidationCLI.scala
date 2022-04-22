package org.dbpedia.databus.mods.validate

import org.dbpedia.databus.mods.validate.worker.ValidateWorker
import picocli.AutoComplete.GenerateCompletion
import picocli.CommandLine
import picocli.CommandLine.{Command, Option}

import java.util.concurrent.Callable

object ValidationCLI extends App {
  val exitCode = new CommandLine(new ValidationCLI).execute(args: _*)
  System.exit(exitCode)
}

@Command(name= "validate", mixinStandardHelpOptions = true, subcommands = Array(classOf[ValidateWorker],classOf[GenerateCompletion]))
class ValidationCLI extends Callable[Int] {

  override def call(): Int = {
    val exitCode = 0
    exitCode
  }
}