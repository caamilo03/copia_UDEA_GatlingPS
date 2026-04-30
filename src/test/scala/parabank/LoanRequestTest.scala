package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoanRequestTest extends Simulation {

  // Usamos la URL directa para evitar el error de variable no encontrada
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
        .check(status.is(200))
        .check(jsonPath("$.providerName").exists) 
    )

  setUp(
    scn.inject(
      rampUsers(Data.loanUsers).during(Data.loanDuration)
    )
  ).protocols(httpConf)
    .assertions(
      global.failedRequests.percent.lte(Data.loanMaxErrorPercent),
      details("request-loan").responseTime.mean.lte(Data.loanMaxMeanMs) 
    )
}