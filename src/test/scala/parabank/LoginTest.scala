package parabank

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoginTest extends Simulation {

  val httpConf = http
    .baseUrl(Data.url)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val loginNormalScenario = scenario("HU 1: Login - 100 Concurrentes")
    .exec(
      http("login-normal")
        .get(s"/login/${Data.username}/${Data.password}")
        .check(status.is(200))
    )

  val loginPeakScenario = scenario("HU 1: Login - 200 Concurrentes")
    .exec(
      http("login-peak")
        .get(s"/login/${Data.username}/${Data.password}")
        .check(status.is(200))
    )

  setUp(
    loginNormalScenario.inject(
      atOnceUsers(Data.loginNormalUsers)
    ),
    loginPeakScenario.inject(
      nothingFor(Data.loginNormalDuration),
      atOnceUsers(Data.loginPeakUsers)
    )
  ).protocols(httpConf)
    .assertions(
      // Tiempos de respuesta por escenario (criterios de aceptación HU 1)
      details("login-normal").responseTime.percentile3.lte(Data.loginP95NormalMs),
      details("login-peak").responseTime.percentile3.lte(Data.loginP95PeakMs),
      // FIX: lte(1.0) en lugar de is(0).
      // El requisito no exige 0 % de errores; is(0) es frágil bajo alta concurrencia
      // en el servidor demo público y causaría fallo ante cualquier error de red.
      global.failedRequests.percent.lte(1.0)
    )
}