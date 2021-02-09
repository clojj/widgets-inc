package com.winc

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures.onionArchitecture
import com.winc.order.infra.springJpaEntities
import ddd.Pure
import ddd.Service
import ddd.UseCase
import org.jmolecules.ddd.annotation.Entity
import org.jmolecules.ddd.annotation.ValueObject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@AnalyzeClasses(
    packages = [""],
    importOptions = [ImportOption.DoNotIncludeJars::class, ImportOption.DoNotIncludeTests::class]
)
class ArchitectureTest {

    private val domainModel = "domain.model"
    private val domainService = "domain.service"
    private val application = "application"
    private val adapter = "adapter"

    private val orderBoundedContext = "com.winc.order"

    // TODO enable after extracting package "ddd"
    // @ArchTest
    val `all packages in order BC` =
        ArchRuleDefinition.classes().that()
            .resideOutsideOfPackage("$orderBoundedContext..").should()
            .containNumberOfElements(DescribedPredicate.equalTo(0))

    @ArchTest
    val `order BC is an onion, don't cry !` =
        onionArchitecture()
            .adapter("infra", "$orderBoundedContext.infra..")
            .adapter("cli", "$orderBoundedContext.$adapter.cli..")
            .adapter("persistence", "$orderBoundedContext.$adapter.persistence..")
            .applicationServices("$orderBoundedContext.$application..")
            .domainModels("$orderBoundedContext.$domainModel..")
            .domainServices("$orderBoundedContext.$domainService..")
    // .withOptionalLayers(true)

    @ArchTest
    val `JPA Entities reside in designated package` =
        ArchRuleDefinition.classes().that()
            .areAnnotatedWith(javax.persistence.Entity::class.java)
            .should().resideInAPackage(springJpaEntities) // TODO bounded context as separate package
            // .should().resideInAPackage("$orderBoundedContext.$adapter.persistence.spring_jpa..")

    @ArchTest
    val `DDD Entities reside in designated package` =
        ArchRuleDefinition.classes().that()
            .areAnnotatedWith(Entity::class.java)
            .should().resideInAPackage("$orderBoundedContext.$domainModel..")

    @ArchTest
    val `DDD application services (UseCases) reside in designated package` =
        ArchRuleDefinition.methods().that()
            .areAnnotatedWith(UseCase::class.java)
            .should().beDeclaredInClassesThat().resideInAPackage("$orderBoundedContext.$application..")

    @ArchTest
    val `DDD domain services reside in designated package` =
        ArchRuleDefinition.methods().that()
            .areAnnotatedWith(Service::class.java)
            .should().beDeclaredInClassesThat().resideInAPackage("$orderBoundedContext.$domainService..")

    @ArchTest
    val `DDD ValueObjects reside in designated package and have private constructors` =
        ArchRuleDefinition.classes().that()
            .areAnnotatedWith(ValueObject::class.java)
            .should().resideInAPackage("$orderBoundedContext.$domainModel.value..")
            .andShould().haveOnlyPrivateConstructors()

    @Test
    fun `funs`() {
        val classes = ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS).importPackages(orderBoundedContext)
        for (clazz in classes) {
            clazz.methods.filter { it.isAnnotatedWith(Pure::class.java) }.forEach {
                it.callsFromSelf.forEach {
                    println(it.target)
                    println(it.targetOwner.fullName)
                }
            }
        }
    }

    // can have other junit5 tests too
    @Disabled
    fun test(): Unit = fail("failing miserably")
}

// HELPERS

fun printImportedClasses(importedClasses: JavaClasses) {
    println(
        importedClasses.map {
            "${
            if (it.packageName.isEmpty())
                "ROOT"
            else it.packageName
            }.${it.simpleName}"
        }
    )
}
