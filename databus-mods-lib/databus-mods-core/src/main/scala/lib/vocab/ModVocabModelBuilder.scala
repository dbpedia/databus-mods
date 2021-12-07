package lib.vocab

import org.apache.jena.rdf.model.{Model, ModelFactory}

class ModVocabModelBuilder {

  private val model = ModelFactory.createDefaultModel()

  def inheritModStatisticsDerivedFrom() : Unit = {

  }

  def inheritModEnrichmentDerivedFrom(): Unit = {

  }

  def inheritModStatistics(): Unit = {

  }

  def build: Model = {

    model
  }
}
