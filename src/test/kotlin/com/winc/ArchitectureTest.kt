package com.winc

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
import ddd.Pure
import org.jmolecules.ddd.annotation.ValueObject
import org.junit.jupiter.api.Test

@AnalyzeClasses(
    packages = [""],
    importOptions = [ImportOption.DoNotIncludeJars::class, ImportOption.DoNotIncludeTests::class]
)
class ArchitectureTest {

    private val domainPortIn = "domain.port.in"
    private val domainModel = "domain.model"
    private val domainService = "domain.service"
    private val application = "application"
    private val adapter = "adapter"

    private val orderBoundedContext = "com.winc.order"

    // TODO enable after extracting packages other than orderBoundedContext
    // TODO track ArchUnit issue #222 and PR https://github.com/TNG/ArchUnit/pull/278
    @ArchTest
    val `all packages in order BC` =
        ArchRuleDefinition.classes().that()
            .resideOutsideOfPackage("$orderBoundedContext..").should()
            .containNumberOfElements(DescribedPredicate.equalTo(0))

    @ArchTest
    val itIsAnOnion: OnionArchitecture =
        onionArchitecture()
            .adapter("infra", "$orderBoundedContext.infra..")
            .adapter("cli", "$orderBoundedContext.$adapter.cli..")
            .adapter("rest", "$orderBoundedContext.$adapter.rest..")
            .adapter("persistence", "$orderBoundedContext.$adapter.persistence..")
            .applicationServices("$orderBoundedContext.$application.port..")
            .applicationServices("$orderBoundedContext.$application.service..")
            .domainModels("$orderBoundedContext.$domainModel..")
            .domainServices("$orderBoundedContext.$domainService..")
    // .withOptionalLayers(true)

    @ArchTest
    val `Entities reside in designated package` =
        ArchRuleDefinition.classes().that().haveSimpleNameEndingWith("Entity")
            .should().resideInAPackage("com.winc.order.adapter.persistence.r2dbc") // TODO bounded context as separate package
    // .should().resideInAPackage("$orderBoundedContext.$adapter.persistence.r2dbc..")

    @ArchTest
    val `DDD Entities reside in designated package` =
        ArchRuleDefinition.classes().that()
            .areAnnotatedWith(DDD.Entity::class.java)
            .should().resideInAPackage("$orderBoundedContext.$domainModel..")

    @ArchTest
    val `DDD application services (UseCases) reside in designated package` =
        ArchRuleDefinition.methods().that()
            .areAnnotatedWith(DDD.UseCase::class.java)
            .should().beDeclaredInClassesThat().resideInAnyPackage("$orderBoundedContext.$application..", "$orderBoundedContext.$domainPortIn..")

    @ArchTest
    val `DDD domain services reside in designated package` =
        ArchRuleDefinition.methods().that()
            .areAnnotatedWith(DDD.DomainService::class.java)
            .should().beDeclaredInClassesThat().resideInAPackage("$orderBoundedContext.$domainService..")

    @ArchTest
    val `DDD ValueObjects reside in designated package and have private constructors` =
        ArchRuleDefinition.classes().that()
            .areAnnotatedWith(ValueObject::class.java)
            .should().resideInAPackage("$orderBoundedContext.$domainModel.value..")
            .andShould().haveOnlyPrivateConstructors()

    @Test
    fun `Explore toplevel functions annotated with @Pure`() {
        ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
            .importPackages(orderBoundedContext)
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
