package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class LoanRequestTest extends Simulation {

  // Configuración HTTP
  val httpConf = http
    .baseUrl(url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // JSON del préstamo
  val loanRequestBody: String =
    s"""{
       |  "accountId": $loanAccountId,
       |  "amount": $loanAmount,
       |  "downPayment": $loanDownPayment
       |}""".stripMargin

  // Definición del escenario
  val loanScenario = scenario("Loan Request - 150 Concurrentes")
    .exec(
      // Autenticación del usuario antes de solicitar el préstamo. Se utilizan las mismas credenciales del login
      http("loan-auth")
        .get(s"/login/$username/$password")
        .check(status.is(200))
        // Captura el customerId que devuelve el login para usarlo en el paso 2.
        .check(jsonPath("$.id").saveAs("customerId"))
    )
    .exec(
      // Envío de la solicitud de préstamo.
      http("loan-request")
        .post("/requestloan")
        .queryParam("accountId",   loanAccountId)
        .queryParam("amount",      loanAmount)
        .queryParam("downPayment", loanDownPayment)
        .check(status.is(200))
        // Validación de que la respuesta no contenga un mensaje de error de validación.
        .check(jsonPath("$.responseDate").exists)
        .check(
          jsonPath("$.approved")               // campo booleano de la API
            .saveAs("loanApproved")            // guardado para trazabilidad
        )
    )

  // Modelo de carga
  setUp(
    loanScenario.inject(
      // Sube 150 usarios en 30 segundos
      rampUsers(loanUsers).during(30)
    )
  ).protocols(httpConf)

  // Aserciones (criterios de aceptación)
    .assertions(
      details("loan-request").responseTime.mean.lte(loanAvgMs),

      global.failedRequests.percent.lte(loanMaxErrorPct)
    )
}