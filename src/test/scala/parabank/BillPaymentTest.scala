package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BillPaymentTest extends Simulation {

  // Usamos la URL directa para evitar el error de variable no encontrada
  val httpConf = http
    .baseUrl("https://parabank.parasoft.com/parabank") 
    .acceptHeader("application/json")

  val scn = scenario("HU 5: Bill Payment - Alta Concurrencia")
    .exec(
      http("bill-payment")
        .post("/services/bank/billpay")
        .queryParam("accountId", "12345")
        .queryParam("amount", s"${Data.billPayAmount}")
        .header("Content-Type", "application/json")
        .body(StringBody("""{"name":"Servicios Publicos","address":{"street":"Calle 1","city":"Medellin","state":"ANT","zipCode":"00000"},"phoneNumber":"1234567","accountNumber":"98765"}""")).asJson
        .check(status.is(200))
    )
    .pause(1)
    .exec(
      http("bill-payment-verify")
        .get("/services/bank/accounts/12345/transactions")
        .check(status.is(200))
        .check(jsonPath("$[*]").exists) 
    )

  setUp(
    scn.inject(
      rampUsers(Data.billPayUsers).during(Data.billPayDuration)
    )
  ).protocols(httpConf)
    .assertions(
      global.failedRequests.percent.lte(Data.billPayMaxErrorPercent),
      details("bill-payment").responseTime.percentile4.lte(Data.billPayMaxResponseMs)
    )
}