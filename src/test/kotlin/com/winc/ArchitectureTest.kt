package com.winc

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures.onionArchitecture
import com.winc.order.domain.model.value.ValueObject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.fail

@AnalyzeClasses(
    packages = [""],
    importOptions = [ImportOption.DoNotIncludeJars::class, ImportOption.DoNotIncludeTests::class]
)
class ArchitectureTest {

    private val domainModel = "domain.model"
    private val domainService = "domain.service"
    private val applicationServices = "application"
    private val adapter = "adapter"

    private val orderBoundedContext = "com.winc.order"

    @ArchTest
    val `all packages in order BC` =
        ArchRuleDefinition.classes().that()
            .resideOutsideOfPackage("$orderBoundedContext..").should()
            .containNumberOfElements(DescribedPredicate.equalTo(0))

    @ArchTest
    val `order BC is an onion, don't cry !` =
        onionArchitecture()
            .adapter("incoming-cli", "$orderBoundedContext.$adapter.cli..")
            .applicationServices("$orderBoundedContext.$applicationServices..")
            .domainModels("$orderBoundedContext.$domainModel..")
            .domainServices("$orderBoundedContext.$domainService..")
    // .withOptionalLayers(true)

    @ArchTest
    val `ValueObjects reside in domain model value` =
        ArchRuleDefinition.classes().that()
            .areAnnotatedWith(ValueObject::class.java).should().resideInAPackage("$orderBoundedContext.$domainModel.value..")


    // can have other junit5 tests too
    @Disabled
    fun test(): Unit = fail("failing miserably")
}

// HELPERS

fun printImportedClasses(importedClasses: JavaClasses) {
    println(importedClasses.map {
        "${
            if (it.packageName.isEmpty())
                "ROOT"
            else it.packageName
        }.${it.simpleName}"
    })
}
