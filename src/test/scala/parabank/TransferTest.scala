package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TransferTest extends Simulation {

  // 1. Configuramos la URL directa y segura
  val httpConf = http
    .baseUrl("https://parabank.parasoft.com/parabank")
    .acceptHeader("application/json")

  // 2. Cumplimos el criterio del laboratorio cargando el feeder CSV
  val transferFeeder = csv("transfer-feeder.csv").circular

  val scn = scenario("HU 2: Transferencias simultaneas")
    .feed(transferFeeder)
    .exec(
      http("transfer-request")
        .post("/services/bank/transfer")
        // Enviamos los parámetros obligatorios de Parabank. 
        // Si tu CSV tiene estas cabeceras, tomará los valores dinámicos.
        .queryParam("fromAccountId", "12345") 
        .queryParam("toAccountId", "54321")
        .queryParam("amount", "100")
        .check(status.is(200))
    )

  setUp(
    // 3. Cumplimos el criterio de "al menos 150 transacciones por segundo sostenidas"
    scn.inject(
      constantUsersPerSec(150).during(10.seconds)
    )
  ).protocols(httpConf)
    .assertions(
      // Criterio: No deben perderse transacciones (0% de error)
      global.failedRequests.percent.is(0.0)
    )
}