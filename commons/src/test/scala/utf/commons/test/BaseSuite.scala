package utf.commons.test

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.concurrent.Eventually
import org.scalatest._
import org.slf4j.MDC

/**
  * Created by Sławomir Kluz on 12/08/2017.
  */
abstract class BaseSuite extends FlatSpec with LazyLogging with BeforeAndAfterAll with Matchers with Eventually with Inside with GivenWhenThen {

  override def beforeAll(): Unit = {
    super.beforeAll()
    MDC.put("suiteId", suiteId)
  }

  override def withFixture(test: NoArgTest): Outcome = {
    logTestStart(test.name)
    super.withFixture(test)
  }

  private def logTestStart(name: String): Unit = {
    val width = 100
    val margin = (width-2-name.length)/2
    val borderLine = s"+${"-"*(width-2)}+"
    val nameLine = s"|${" "*(margin+name.length%2)}$name${" "*margin}|"
    logger.info(borderLine)
    logger.info(nameLine)
    logger.info(borderLine)
  }

}
