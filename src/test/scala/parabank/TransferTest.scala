package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class TransferTest extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // 2 CSV Feeder
  val transferFeeder = csv("transfer-feeder.csv").circular

  // 3 Scenario Definition
  val scn = scenario("Transfer Test")
    .feed(transferFeeder)
    .exec(http("transfer-request")
      .post("/transfer")
      .queryParam("fromAccountId", "${fromAccountId}")
      .queryParam("toAccountId", "${toAccountId}")
      .queryParam("amount", "${amount}")
      .check(status.is(200))
      .check(regex("(?i)(successfully transferred|transferred|in)").exists)
    )

  // 4 Load Scenario
  setUp(
    scn.inject(
      atOnceUsers(transferTargetTps)
    )
  ).protocols(httpConf)
    .assertions(
      global.allRequests.count.is(transferTargetTps.toLong),
      global.failedRequests.percent.is(0),
      details("transfer-request").successfulRequests.percent.is(100)
    )
}
