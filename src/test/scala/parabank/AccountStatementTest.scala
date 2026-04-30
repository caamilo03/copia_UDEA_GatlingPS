package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class AccountStatementTest extends Simulation {

  // 1. Configuramos la URL directa para evitar errores de variables
  val httpConf = http
    .baseUrl("https://parabank.parasoft.com/parabank")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // 2. Definición del escenario
  val scn = scenario("HU 3: Account Statement Load")
    .exec(
      http("account-statement")
        // Usamos la cuenta 12345 y llamamos al endpoint de transacciones (estado de cuenta)
        .get("/services/bank/accounts/12345/transactions") 
        .check(status.is(200))
        // Verificamos que la respuesta sea una lista de transacciones válida
        .check(jsonPath("$[*]").exists)
    )

  // 3. Configuración de Inyección y Aserciones
  setUp(
    // Usamos rampUsers para inyectar 200 usuarios gradualmente durante 10 segundos
    // cumpliendo la regla de usar diferentes métodos de inyección.
    scn.inject(
      rampUsers(200).during(10.seconds)
    )
  ).protocols(httpConf)
    .assertions(
      // El tiempo de respuesta (percentil 95) debe ser <= 3000 ms (3 segundos)
      details("account-statement").responseTime.percentile3.lte(3000),
      // La tasa de error no debe superar el 1%
      global.failedRequests.percent.lte(1.0)
    )
}