error id: file:///C:/Users/Camilo/copia_UDEA_GatlingPS/src/test/scala/parabank/Data.scala:scala/Predef.String#
file:///C:/Users/Camilo/copia_UDEA_GatlingPS/src/test/scala/parabank/Data.scala
empty definition using pc, found symbol in pc: scala/Predef.String#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -scala/concurrent/duration/String#
	 -String#
	 -scala/Predef.String#
offset: 1126
uri: file:///C:/Users/Camilo/copia_UDEA_GatlingPS/src/test/scala/parabank/Data.scala
text:
```scala
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

    // HU 4: Solicitud de préstamo 
    // ID de cliente por defecto para el usuario 'john' en Parabank
  val loanCustomerId: Str@@ing = "12212"    
  
  // Cuenta de origen por defecto. (Ver "Nota de QA" más abajo)
  val loanFromAccountId: String = "13566" 
  
  val loanAmount: String = "1000"         
  
  // El downPayment debe ser un valor conservador para asegurar que siempre sea menor 
  // al saldo de la cuenta y evitar el error 500 por fondos insuficientes.
  val loanDownPayment: String = "10"

    // HU 5: Pago de servicios 
    val billPayUsers = 200
    val billPayDuration = 10.seconds
    val billPayMaxResponseMs = 3000
    val billPayMaxErrorPercent = 1.0
    val billPayAmount = 50

}
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/Predef.String#