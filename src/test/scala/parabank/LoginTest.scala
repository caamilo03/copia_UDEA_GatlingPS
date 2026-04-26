package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class LoginTest extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // 2 Scenario Definition
  val loginNormalScenario = scenario("Login - 100 Concurrentes")
    .exec(http("login-normal")
      .get(s"/login/$username/$password")
      .check(status.is(200))
    )

  val loginPeakScenario = scenario("Login - 200 Concurrentes")
    .exec(http("login-peak")
      .get(s"/login/$username/$password")
      .check(status.is(200))
    )

  // 3 Load Scenario
  setUp(
    loginNormalScenario.inject(
      atOnceUsers(loginNormalUsers)
    ),
    loginPeakScenario.inject(
      nothingFor(loginNormalDuration),
      atOnceUsers(loginPeakUsers)
    )
  ).protocols(httpConf)
    .assertions(
      details("login-normal").responseTime.percentile3.lte(loginP95NormalMs),
      details("login-peak").responseTime.percentile3.lte(loginP95PeakMs),
      global.failedRequests.percent.is(0)
    )
}