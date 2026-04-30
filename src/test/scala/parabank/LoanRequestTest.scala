package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoanRequestTest extends Simulation {

  /*
   * Gatling persiste cookies por usuario virtual de forma automática, así que
   * el JSESSIONID que devuelve /login viaja en las llamadas siguientes.
   * Esto es lo que faltaba antes: el endpoint /requestLoan requiere sesión
   * activa, por eso devolvía 500 sistemáticamente con basicAuth solo.
   */
  val httpProtocol = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")

  val scn = scenario("HU 4: Solicitud de Prestamo")

    // Paso 1 – Login: establece sesión y obtiene customerId dinámico.
    // No hardcodeamos 12212 porque el ID puede cambiar si Parabank fue
    // reinicializado por otro proceso.
    .exec(
      http("GET - Login")
        .get(s"/login/${Data.username}/${Data.password}")
        .check(status.is(200))
        .check(jsonPath("$.id").saveAs("dynamicCustomerId"))
    )

    // Paso 2 – Obtener cuentas del cliente (correlación dinámica).
    .exec(
      http("GET - Obtener Cuentas")
        .get("/customers/${dynamicCustomerId}/accounts")
        .check(status.is(200))
        .check(jsonPath("$[0].id").saveAs("dynamicAccountId"))
    )

    .pause(1)

    // Paso 3 – Solicitar el préstamo (con sesión activa via cookie).
    .exec(
      http("POST - Request Loan")
        .post("/requestLoan")
        .queryParam("customerId",    "${dynamicCustomerId}")
        .queryParam("amount",        Data.loanAmount)
        .queryParam("downPayment",   Data.loanDownPayment)
        .queryParam("fromAccountId", "${dynamicAccountId}")
        .check(status.is(200))
    )

  // 150 usuarios concurrentes total (50 atOnce + 50 ramp + 5/s × 10 s)
  setUp(
    scn.inject(
      atOnceUsers(50),
      rampUsers(50).during(20.seconds),
      constantUsersPerSec(5).during(10.seconds)
    ).protocols(httpProtocol)
  ).assertions(
    global.responseTime.mean.lte(Data.loanMeanMs),
    details("POST - Request Loan").successfulRequests.percent.gte(Data.loanMinSuccessPct)
  )
}