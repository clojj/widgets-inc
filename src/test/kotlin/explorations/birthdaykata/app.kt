package explorations.birthdaykata

import arrow.core.Either
import arrow.core.computations.either
import arrow.fx.coroutines.parTraverseEither
import kotlinx.coroutines.*
import java.time.LocalDate

interface Env : EmployeeRepository,
    BirthdayService,
    EmailService

// TODO UseCase for concurrent processing
//  unit-of-work: get employee -> create message -> send

suspend fun main() {
    val env: Env = object : Env,
        EmployeeRepository by FileEmployeeRepository("input.txt"),
        BirthdayService by BirthdayServiceInterpreter(),
        EmailService by SmtpEmailService("localhost", 8080) {}

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    scope.launch {
        val results = env.sendGreetingsUseCase(date = LocalDate.parse("1975-09-11"))
    }

    // println(results)
    delay(3500)
}

suspend fun Env.sendGreetingsUseCase(date: LocalDate): Either<Throwable, Int> {

    val result = either<Throwable, Int> {
        val allEmployees: List<Employee> = allEmployees().bind()
        val greetings: List<EmailMessage> = birthdayMessages(allEmployees, date).bind()
        val results: List<String> = greetings.parTraverseEither(Dispatchers.IO) {
            delay(2000)
            val greeting = sendGreeting(it)
            delay(1000)
            println(greeting)
            greeting
        }.bind()
        results.size
    }
    result.fold({ println(it.message) }) { println("sent $it emails successfully") }
    return result
}

