package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class AccountStatementTest extends Simulation {

  val httpConf = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val scn = scenario("HU 3: Account Statement Load")
    .exec(
      http("account-statement")
        .get(s"/accounts/${Data.fromAccountId}/transactions")
        .check(status.is(200))
       
    )

  setUp(
    scn.inject(
      rampUsers(Data.statementUsers).during(10.seconds)
    )
  ).protocols(httpConf)
    .assertions(
      details("account-statement").responseTime.percentile3.lte(Data.statementP95Ms),
      global.failedRequests.percent.lte(Data.statementMaxErrorPct)
    )
}