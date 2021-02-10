package ddd

// TODO PR in jmolecules-ddd ? or separate module

internal interface DDD {
    annotation class ApplicationService
    annotation class DomainService
    annotation class Entity
}

internal typealias UseCase = DDD.ApplicationService

internal annotation class Pure
