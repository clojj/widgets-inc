package explorations.birthdaykata

import arrow.core.*
import arrow.typeclasses.Semigroup

interface EmployeeRepository {

    suspend fun allEmployees(): Either<Throwable, List<Employee>>
}

class FileEmployeeRepository(fileName: String) : EmployeeRepository {

    private val file = this::class.java.getResource("/explorations/$fileName")

    override suspend fun allEmployees(): Either<Throwable, List<Employee>> =
        readFile()(file.readText())

    private fun readFile(): suspend (String) -> Either<Throwable, List<Employee>> = { text: String ->
        val employees =
            text.lines()
                .drop(1)
                .filter { it.isNotEmpty() }
                .map(employeeParser)

        val validatedEmployees: Validated<NonEmptyList<String>, List<Employee>> = sequence(employees)
        validatedEmployees.fold({ EmployeeRepositoryException(it).left() }, { it.right() })
    }

    private fun sequence(input: List<ValidationResult<Employee>>): ValidationResult<List<Employee>> =
        input.sequenceValidated(Semigroup.nonEmptyList())

    data class EmployeeRepositoryException(val errors: Nel<String>) : RuntimeException("Error reading from repo: $errors")

    companion object {

        val employeeParser: (String) -> ValidationResult<Employee> = { row ->
            val parts = row.split(", ")
            val lastName = parts.getOrNull(0)
            val firstName = parts.getOrNull(1)
            val dateOfBirth = parts.getOrNull(2)
            val email = parts.getOrNull(3)
            Employee(firstName, lastName, dateOfBirth, email)
        }
    }
}
