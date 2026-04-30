package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoanRequestTest extends Simulation {

  val httpProtocol = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")

  // ─── Pre-escenario: reiniciar la BD de Parabank ─────────────────────────────
  // BillPaymentTest (que corre antes por orden alfabético) debita $50 × 200
  // usuarios = $10 000 de la cuenta 15120, agotando su saldo. Sin este reset,
  // el POST /requestLoan devuelve HTTP 500 por fondos insuficientes.
  val initScn = scenario("Reiniciar BD Parabank")
    .exec(
      http("POST - initializeDB")
        .post("/initializeDB")
    )

  val loanScn = scenario("HU 4: Solicitud de Prestamo")

    // Paso 1 – obtener cuentas del cliente (correlación dinámica requerida)
    .exec(
      http("GET - Obtener Cuentas")
        .get("/customers/" + Data.loanCustomerId + "/accounts")
        .basicAuth(Data.username, Data.password)
        .check(status.is(200))
        .check(jsonPath("$[0].id").saveAs("dynamicAccountId"))
    )

    .pause(1)

    // Paso 2 – solicitar préstamo con la cuenta obtenida dinámicamente
    .exec(
      http("POST - Request Loan")
        .post("/requestLoan")
        .basicAuth(Data.username, Data.password)
        .queryParam("customerId",    Data.loanCustomerId)
        .queryParam("amount",        Data.loanAmount)
        .queryParam("downPayment",   Data.loanDownPayment)
        .queryParam("fromAccountId", "${dynamicAccountId}")
        .check(status.is(200))
    )

  setUp(
    // 1. Primero: resetear la BD con un solo usuario para restaurar saldos
    initScn.inject(atOnceUsers(1)),
    // 2. Después de 5 s (tiempo para que el reset complete), lanzar la carga
    loanScn.inject(
      nothingFor(5.seconds),
      atOnceUsers(50),
      rampUsers(50).during(20.seconds),
      constantUsersPerSec(5).during(10.seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.mean.lte(Data.loanMeanMs),
      details("POST - Request Loan").successfulRequests.percent.gte(Data.loanMinSuccessPct)
    )
}