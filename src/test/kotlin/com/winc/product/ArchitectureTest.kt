package com.winc.product

import archunit.extensions.topFuns
import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures.OnionArchitecture
import com.tngtech.archunit.library.Architectures.onionArchitecture
import ddd.DDD
import hexa.Pure
import org.jmolecules.ddd.annotation.ValueObject
import org.junit.jupiter.api.Test

@AnalyzeClasses(
    packages = ["com.winc.product"],
    importOptions = [ImportOption.DoNotIncludeJars::class, ImportOption.DoNotIncludeTests::class]
)
class ArchitectureTest {

    private val domainModel = "domain.model"
    private val domainService = "domain.service"

    private val boundedContext = "com.winc.product"

    // TODO enable after extracting packages other than boundedContext
    // TODO track ArchUnit issue #222 and PR https://github.com/TNG/ArchUnit/pull/278
    @ArchTest
    val `all packages in BC` =
        ArchRuleDefinition.classes().that()
            .resideOutsideOfPackage("$boundedContext..").should()
            .containNumberOfElements(DescribedPredicate.equalTo(0))

    @ArchTest
    val itIsAnOnion: OnionArchitecture =
        onionArchitecture()
            .adapter("rest", "$boundedContext.adapter.inbound.rest..")
            .adapter("persistence", "$boundedContext.adapter.outbound.persistence..")
            .applicationServices("$boundedContext.application.service..")
            .applicationServices("$boundedContext.application.port.inbound..")
            .applicationServices("$boundedContext.application.port.outbound..")
            .domainModels("$boundedContext.$domainModel..")
            .domainServices("$boundedContext.$domainService..")
    // .withOptionalLayers(true)

    @ArchTest
    val `Entities reside in designated package` =
        ArchRuleDefinition.classes().that().haveSimpleNameEndingWith("Entity")
            .should().resideInAPackage("$boundedContext.adapter.outbound.persistence.data")

    @ArchTest
    val `DDD Entities reside in designated package` =
        ArchRuleDefinition.classes().that()
            .areAnnotatedWith(DDD.Entity::class.java)
            .should().resideInAPackage("$boundedContext.$domainModel..")

    @ArchTest
    val `DDD application services (UseCases) reside in designated package` =
        ArchRuleDefinition.methods().that()
            .areAnnotatedWith(DDD.UseCase::class.java)
            .should().beDeclaredInClassesThat().resideInAnyPackage(
                "$boundedContext.application.port.inbound.."
            )

    @ArchTest
    val `DDD domain services reside in designated package` =
        ArchRuleDefinition.methods().that()
            .areAnnotatedWith(DDD.DomainService::class.java)
            .should().beDeclaredInClassesThat().resideInAPackage("$boundedContext.$domainService..")

    @ArchTest
    val `DDD ValueObjects reside in designated package and have private constructors` =
        ArchRuleDefinition.classes().that()
            .areAnnotatedWith(ValueObject::class.java)
            .should().resideInAPackage("$boundedContext.$domainModel..")
            .andShould().haveOnlyPrivateConstructors()

    @Test
    fun `Explore toplevel functions annotated with @Pure`() {
        ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages(boundedContext)
            .topFuns()
            .map {
                println("topFun: $it")
                it
            }
            .filter { it.isAnnotatedWith(Pure::class.java) }
            .forEach {
                println("pure ? $it")
                it.callsFromSelf.forEach { call ->
                    println("\t" + call.target)
                }
            }
    }
}
