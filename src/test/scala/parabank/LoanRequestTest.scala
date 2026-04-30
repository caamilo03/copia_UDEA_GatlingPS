package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoanRequestTest extends Simulation {

  // 1. Configuramos la URL centralizada llamándola desde Data.scala
  val httpProtocol = http
    .baseUrl(Data.baseUrl) 
    .acceptHeader("application/json")

  // 2. Definición del escenario con Correlación 
  val scn = scenario("HU 4: Solicitud de Prestamo")
    
    .exec(
      http("GET - Obtener Cuentas")
        .get("/parabank/services_proxy/bank/customers/" + Data.loanCustomerId + "/accounts")
        .basicAuth(Data.username, Data.password)
        .check(status.is(200))
        .check(jsonPath("$[-1].id").saveAs("dynamicAccountId"))
    )
    
    .pause(1) 
    .exec(
      http("POST - Request Loan")
        .post("/parabank/services_proxy/bank/requestLoan") 
        .basicAuth(Data.username, Data.password) 
        .queryParam("customerId", Data.loanCustomerId)
        .queryParam("amount", Data.loanAmount)
        .queryParam("downPayment", Data.loanDownPayment)
        // INYECCIÓN DINÁMICA: Usamos la variable guardada en la petición anterior
        .queryParam("fromAccountId", "${dynamicAccountId}") 
        .check(status.is(200))
    )

  // 3. Configuración de Inyección y Aserciones
  setUp(
    scn.inject(
      atOnceUsers(50),                                  
      rampUsers(50).during(20.seconds),                 
      constantUsersPerSec(5).during(10.seconds)         
    ).protocols(httpProtocol)
  ).assertions(
    // Aserciones basadas estrictamente en los criterios de aceptación
    global.responseTime.mean.lte(5000),         // El tiempo de respuesta promedio debe ser <= 5 segundos
    global.successfulRequests.percent.gte(98.0) // El sistema debe mantener una tasa de éxito >= 98%
  )
}