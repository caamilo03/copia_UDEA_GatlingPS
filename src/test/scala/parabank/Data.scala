package parabank

import scala.concurrent.duration._

object Data{
    val url = "https://parabank.parasoft.com/parabank/services/bank"
    val baseUrl = "https://parabank.parasoft.com"
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

    // HU 4: Solicitud de préstamo 
    val loanCustomerId: String = "12212"    
    val loanFromAccountId: String = "13566" 
    val loanAmount: String = "1000"         
    val loanDownPayment: String = "10"

    // HU 5: Pago de servicios 
    val billPayUsers = 200
    val billPayDuration = 10.seconds
    val billPayMaxResponseMs = 3000
    val billPayMaxErrorPercent = 1.0
    val billPayAmount = 50

}