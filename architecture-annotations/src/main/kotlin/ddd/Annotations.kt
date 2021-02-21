package ddd

// TODO PR in jmolecules-ddd ? or separate module

interface DDD {
    annotation class UseCase
    annotation class ApplicationService
    annotation class DomainService
    annotation class Entity
}

interface HEXA {
    annotation class Adapter
}

annotation class Pure
