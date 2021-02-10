package archunit.extensions

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaMethod

fun JavaClasses.topFuns(): List<JavaMethod> =
    this.flatMap { clazz -> allTopLevelFunctions(clazz) }

private fun allTopLevelFunctions(clazz: JavaClass): List<JavaMethod> =
    clazz.source.orNull()?.fileName?.orNull()?.run {
        if (this.split(".")[0] + "Kt" == clazz.simpleName)
            clazz.methods.toList()
        else emptyList()
    }.orEmpty()

fun JavaClass.topFuns(): List<JavaMethod> =
    allTopLevelFunctions(this)

fun JavaClasses.printFq() {
    println(
        this.map {
            "${if (it.packageName.isEmpty()) "ROOT" else it.packageName}.${it.simpleName}"
        }
    )
}
