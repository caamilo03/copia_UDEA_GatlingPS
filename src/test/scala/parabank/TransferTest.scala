package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TransferTest extends Simulation {

  // 1. Usamos la URL centralizada de Data.scala
  val httpConf = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")

  val transferFeeder = csv("transfer-feeder.csv").circular

  // 2. Definición del escenario con parámetros centralizados
  val scn = scenario("HU 2: Transferencias simultaneas")
    .feed(transferFeeder)
    .exec(
      http("transfer-request")
        .post("/transfer")
        .queryParam("fromAccountId", Data.fromAccountId) 
        .queryParam("toAccountId", Data.toAccountId)
        .queryParam("amount", s"${Data.amount}")
        .check(status.is(200))
    )
// 3. Configuración de Inyección y Aserciones
  setUp(
    scn.inject(
      constantUsersPerSec(Data.transferTargetTps).during(Data.transferStressDuration)
    )
  ).protocols(httpConf)
    .assertions(
      global.failedRequests.percent.is(0.0)
    )
}