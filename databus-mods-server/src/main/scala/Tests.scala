import java.io.ByteArrayInputStream
import java.net.URL
import java.nio.charset.StandardCharsets
import java.sql.DriverManager
import java.util.stream.StreamSupport

import org.aksw.jena_sparql_api.ext.virtuoso.VirtuosoBulkLoad
import org.apache.jena.query.ReadWrite
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdfconnection.RDFConnectionFactory
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.vocabulary.RDF
import sun.security.x509.UniqueIdentity

import scala.collection.JavaConverters.{asJavaIterableConverter, asScalaIteratorConverter}
import scala.util.Random

