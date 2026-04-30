package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoanRequestTest extends Simulation {

  /*
   * FIX: Se usa Data.url (REST directo) en lugar de Data.baseUrl + services_proxy.
   * El proxy SOAP devolvía HTTP 500 en todos los POST bajo carga concurrente.
   */
  val httpProtocol = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")

  val scn = scenario("HU 4: Solicitud de Prestamo")

    // Paso 1 – obtener la primera cuenta del cliente y guardar su ID
    .exec(
      http("GET - Obtener Cuentas")
        .get("/customers/" + Data.loanCustomerId + "/accounts")
        .basicAuth(Data.username, Data.password)
        .check(status.is(200))
        // FIX: $[0].id en lugar de $[-1].id (más compatible con la implementación
        //      de JsonPath de Gatling y evita índices negativos que pueden fallar)
        .check(jsonPath("$[0].id").saveAs("dynamicAccountId"))
    )

    .pause(1)

    // Paso 2 – solicitar el préstamo usando la cuenta obtenida dinámicamente
    .exec(
      http("POST - Request Loan")
        .post("/requestLoan")                         // FIX: ruta REST directa
        .basicAuth(Data.username, Data.password)
        .queryParam("customerId",   Data.loanCustomerId)
        .queryParam("amount",       Data.loanAmount)
        .queryParam("downPayment",  Data.loanDownPayment)
        .queryParam("fromAccountId", "${dynamicAccountId}")   // inyección dinámica
        .check(status.is(200))
    )

  // Inyección: 150 usuarios concurrentes en total (criterio de aceptación HU 4)
  setUp(
    scn.inject(
      atOnceUsers(50),                               //  50 usuarios de golpe
      rampUsers(50).during(20.seconds),              //  50 usuarios en rampa
      constantUsersPerSec(5).during(10.seconds)      //  50 usuarios más (5/s × 10 s)
    ).protocols(httpProtocol)
  ).assertions(
    global.responseTime.mean.lte(Data.loanMeanMs),          // promedio ≤ 5 000 ms
    global.successfulRequests.percent.gte(Data.loanMinSuccessPct)  // éxito ≥ 98 %
  )
}