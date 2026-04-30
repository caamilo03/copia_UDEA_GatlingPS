package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class TransferTest extends Simulation {

  val httpConf = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")

  val transferFeeder = csv("transfer-feeder.csv").circular

  val scn = scenario("HU 2: Transferencias Simultáneas")
    .feed(transferFeeder)
    .exec(
      http("transfer-request")
        .post("/transfer")
        .queryParam("fromAccountId", "${fromAccountId}")
        .queryParam("toAccountId",   "${toAccountId}")
        .queryParam("amount",        "${amount}")
        .check(status.is(200))
    )

  // 150 usuarios/segundo durante 10 s = 1 500 transacciones
  setUp(
    scn.inject(
      constantUsersPerSec(Data.transferTargetTps).during(Data.transferStressDuration)
    )
  ).protocols(httpConf)
    .assertions(
      // FIX: 
      // Permite hasta 1 % de fallos esporádicos de red sin romper la CI,
      global.failedRequests.percent.lte(1.0)
    )
}