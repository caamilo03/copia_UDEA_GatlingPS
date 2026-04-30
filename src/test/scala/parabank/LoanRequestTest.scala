package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoanRequestTest extends Simulation {

  val httpConf = http
    .baseUrl("https://parabank.parasoft.com/parabank")
    .acceptHeader("application/json")

  val scn = scenario("HU 4: Loan Request - Carga con Rampa")
    .exec(
      http("request-loan")
        .post("/services/bank/requestLoan")
        .queryParam("customerId", "12212") 
        .queryParam("amount", "10000")
        .queryParam("downPayment", "1000")
        .queryParam("fromAccountId", "12345") 
        // Validamos únicamente que el servidor procese la petición sin caerse (200 OK)
        .check(status.is(200))
    )

  setUp(
    scn.inject(
      rampUsers(150).during(10.seconds)
    )
  ).protocols(httpConf)
    .assertions(
      global.failedRequests.percent.lte(2.0),
      details("request-loan").responseTime.mean.lte(5000) 
    )
}