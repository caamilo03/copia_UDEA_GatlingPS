package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoanRequestTest extends Simulation {

  // 1. Usamos la URL centralizada de Data.scala
  val httpConf = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")
  // 2. Definición del escenario con parámetros centralizados
  val scn = scenario("HU 4: Loan Request - Carga con Rampa")
    .exec(
      http("request-loan")
        .post("/requestLoan")
        .queryParam("customerId", "12212") 
        .queryParam("amount", "10000")
        .queryParam("downPayment", "1000")
        .queryParam("fromAccountId", "12345") 
        .check(status.is(200))
    )
// 3. Configuración de Inyección y Aserciones
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