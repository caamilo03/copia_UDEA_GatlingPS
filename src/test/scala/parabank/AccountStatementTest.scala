package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class AccountStatementTest extends Simulation {

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // 2 Scenario Definition
  val scn = scenario("Account Statement Load")
    .exec(http("account-statement")
      .get(s"/accounts/$statementAccountId")
      .check(status.is(200))
      .check(jsonPath("$.id").exists)
    )

  // 3 Load Scenario
  setUp(
    scn.inject(
      atOnceUsers(statementUsers)
    )
  ).protocols(httpConf)
    .assertions(
      details("account-statement").responseTime.percentile3.lte(statementP95Ms),
      global.failedRequests.percent.lte(statementMaxErrorPercent)
    )
}
