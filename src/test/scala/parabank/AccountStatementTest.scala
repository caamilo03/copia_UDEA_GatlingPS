package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class AccountStatementTest extends Simulation {

  // 1. Usamos la URL centralizada de Data.scala
  val httpConf = http
    .baseUrl(Data.url) 
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // 2. Definición del escenario
  val scn = scenario("HU 3: Account Statement Load")
    .exec(
      http("account-statement")
        .get(s"/accounts/${Data.fromAccountId}/transactions") 
        .check(status.is(200))
        .check(jsonPath("$[*]").exists)
    )

  // 3. Configuración de Inyección y Aserciones 
  setUp(
    scn.inject(
      rampUsers(Data.statementUsers).during(10.seconds)
    )
  ).protocols(httpConf)
    .assertions(
      details("account-statement").responseTime.percentile3.lte(Data.statementP95Ms),
      global.failedRequests.percent.lte(Data.statementMaxErrorPercent)
    )
}