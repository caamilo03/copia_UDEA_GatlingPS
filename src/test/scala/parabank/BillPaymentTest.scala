package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BillPaymentTest extends Simulation {

  val httpConf = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")

  val scn = scenario("HU 5: Bill Payment - Alta Concurrencia")
    .exec(
      http("bill-payment")
        .post("/billpay")
        .queryParam("accountId", Data.fromAccountId)
        .queryParam("amount", s"${Data.billPayAmount}")
        .header("Content-Type", "application/json")
        .body(StringBody(
          """{"name":"Servicios Publicos",""" +
          """"address":{"street":"Calle 1","city":"Medellin","state":"ANT","zipCode":"00000"},""" +
          """"phoneNumber":"1234567","accountNumber":"98765"}"""
        )).asJson
        .check(status.is(200))
    )
    .pause(1)
    .exec(
      http("bill-payment-verify")
        .get(s"/accounts/${Data.fromAccountId}/transactions")
        .check(status.is(200))
    )

  setUp(
    scn.inject(
      rampUsers(Data.billPayUsers).during(Data.billPayDuration)
    )
  ).protocols(httpConf)
    .assertions(
      global.failedRequests.percent.lte(Data.billPayMaxErrorPct),
      details("bill-payment").responseTime.percentile4.lte(Data.billPayMaxP99Ms)
    )
}