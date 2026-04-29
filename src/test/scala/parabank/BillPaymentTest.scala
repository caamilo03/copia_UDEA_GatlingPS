
package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import parabank.Data._

class BillPaymentTest extends Simulation {

  // Configuración HTTP
  val httpConf = http
    .baseUrl(url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  //  Body del payee
  val payeeBody: String =
    s"""{
       |  "name":          "Electric Company",
       |  "address": {
       |    "street":  "123 Main St",
       |    "city":    "Anytown",
       |    "state":   "CA",
       |    "zipCode": "12345"
       |  },
       |  "phoneNumber":   "555-1234",
       |  "accountNumber": "98765"
       |}""".stripMargin

  // Definición del escenario
  val billPayScenario = scenario("Bill Payment - 200 Concurrentes")
    .exec(
      // Autenticación
      http("billpay-auth")
        .get(s"/login/$username/$password")
        .check(status.is(200))
        .check(jsonPath("$.id").saveAs("customerId"))
    )
    .exec(
      // Envío del pago
      http("bill-payment")
        .post("/billpay")
        .queryParam("accountId", billPayAccountId)   // = 13566 (From account #)
        .queryParam("amount",    billPayAmount)       // = campo Amount del form
        .body(StringBody(payeeBody)).asJson
        .check(status.is(200))
        // La API devuelve el monto y el nombre del payee confirmados
        .check(jsonPath("$.amount").is(s"$billPayAmount"))
        .check(jsonPath("$.payeeName").saveAs("confirmedPayee"))
    )
    .exec(
      // Verificación del pago
      http("bill-payment-verify")
        .get(s"/accounts/$billPayAccountId/transactions/month/All/list")
        .check(status.is(200))
        .check(jsonPath("$[0]").exists)
    )

  // Modelo de carga
  setUp(
    billPayScenario.inject(
      rampUsers(billPayUsers).during(20)  // sube a 200 usuarios en 20 s
    )
  ).protocols(httpConf)

  // Aserciones
    .assertions(
      details("bill-payment").responseTime.percentile3.lte(billPayAvgMs),  // ≤ 3 s p95
      global.failedRequests.percent.lte(billPayMaxErrorPct)                 // ≤ 1 % errores
    )
}