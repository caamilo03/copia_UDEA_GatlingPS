package parabank

import scala.concurrent.duration._

object Data {

  // Base URLs 
  val url     = "https://parabank.parasoft.com/parabank/services/bank"
  val baseUrl = "https://parabank.parasoft.com"

  // Credenciales 
  val username = "john"
  val password  = "demo"

  //  Cuentas por defecto 
  val fromAccountId = "15120"
  val toAccountId   = "15231"
  val amount        = "1"

  //  HU 1: Login 
  val loginNormalUsers:    Int            = 100
  val loginPeakUsers:      Int            = 200
  val loginNormalDuration: FiniteDuration = 10.seconds
  val loginPeakDuration:   FiniteDuration = 10.seconds
  val loginP95NormalMs:    Int            = 2000
  val loginP95PeakMs:      Int            = 5000

  //  HU 2: Transferencias simultáneas 
  val transferStressDuration: FiniteDuration = 10.seconds
  val transferRampUpDuration: FiniteDuration =  5.seconds
  val transferTargetTps:      Int            = 150

  //  HU 3: Estado de cuenta 
  val statementAccountId:   String         = fromAccountId
  val statementUsers:       Int            = 200
  val statementDuration:    FiniteDuration = 10.seconds
  val statementP95Ms:       Int            = 3000
  val statementMaxErrorPct: Double         = 1.0

  //  HU 4: Solicitud de préstamo 
  val loanCustomerId:    String = "12212"
  val loanFromAccountId: String = fromAccountId
  val loanAmount:        String = "1000"
  val loanDownPayment:   String = "1"
  val loanMeanMs:        Int    = 5000
  val loanMinSuccessPct: Double = 98.0

  //  HU 5: Pago de servicios 
  val billPayUsers:       Int            = 200
  val billPayDuration:    FiniteDuration = 10.seconds
  val billPayMaxP99Ms:    Int            = 3000
  val billPayMaxErrorPct: Double         = 1.0
  val billPayAmount:      Int            = 50
}