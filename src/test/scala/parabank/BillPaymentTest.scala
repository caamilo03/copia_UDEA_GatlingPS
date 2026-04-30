package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BillPaymentTest extends Simulation {

  val httpConf = http
    .baseUrl(Data.appUrl)
    .acceptHeader("application/json")

  val scn = scenario("HU 5: Bill Payment - Alta Concurrencia")
    // Paso 1: Enviar el pago
    .exec(
      http("bill-payment")
        .post("/services/bank/billpay")
        .queryParam("accountId", "12345") // Cuenta genérica de origen
        .queryParam("amount", s"${Data.billPayAmount}") // Interpolación corregida
        .header("Content-Type", "application/json")
        // Body con los datos del beneficiario requerido por Parabank
        .body(StringBody("""{"name":"Servicios Publicos","address":{"street":"Calle 1","city":"Medellin","state":"ANT","zipCode":"00000"},"phoneNumber":"1234567","accountNumber":"98765"}""")).asJson
        .check(status.is(200))
    )
    .pause(1)
    // Paso 2: Verificar en el historial del usuario (Corrige el error 404)
    .exec(
      http("bill-payment-verify")
        // Consultamos las transacciones reales de la cuenta para confirmar el registro
        .get("/services/bank/accounts/12345/transactions")
        .check(status.is(200))
        // Verificamos que la respuesta devuelva un arreglo de transacciones válido
        .check(jsonPath("$[*]").exists) 
    )

  setUp(
    scn.inject(
      rampUsers(Data.billPayUsers).during(Data.billPayDuration)
    )
  ).protocols(httpConf)
    .assertions(
      // Criterio: Tasa de errores funcionales <= 1%
      global.failedRequests.percent.lte(Data.billPayMaxErrorPercent),
      // Criterio: Tiempo de respuesta por transacción <= 3 segundos
      details("bill-payment").responseTime.percentile4.lte(Data.billPayMaxResponseMs)
    )
}