package parabank

import scala.concurrent.duration._

object Data{
    val url = "https://parabank.parasoft.com/parabank/services/bank"
    val username = "john"
    val password = "demo"
    val fromAccountId = "15120"
    val toAccountId = "15231"
    val amount = "1"

    // Historia 1: Login
    val loginNormalUsers = 100
    val loginPeakUsers = 200
    val loginNormalDuration: FiniteDuration = 10.seconds
    val loginPeakDuration: FiniteDuration = 10.seconds
    val loginP95NormalMs = 2000
    val loginP95PeakMs = 5000

    // Historia 2: Transferencias simultaneas
    val transferStressDuration: FiniteDuration = 10.seconds
    val transferRampUpDuration: FiniteDuration = 5.seconds
    val transferTargetTps = 150

    // Historia 3: Estado de cuenta
    val statementAccountId = fromAccountId
    val statementUsers = 200
    val statementDuration: FiniteDuration = 10.seconds
    val statementP95Ms = 3000
    val statementMaxErrorPercent = 1.0

    // Historia 4: Solicitud de préstamo bajo carga
    val loanUsers          = 150
    val loanAccountId      = 12345
    val loanAmount         = 5000
    val loanDownPayment    = 1000
    val loanAvgMs          = 5000
    val loanMaxErrorPct    = 2

    // Historia 5: Pago de servicios con concurrencia alta
    val billPayUsers       = 200
    val billPayAccountId   = 12345
    val billPayAmount      = 150
    val billPayAvgMs       = 3000
    val billPayMaxErrorPct = 1

}