package ddd

// TODO PR in jmolecules-ddd ? or separate module

interface DDD {
    annotation class UseCase
    annotation class ApplicationService
    annotation class Command
    annotation class Event
    annotation class Aggregate
    annotation class Entity
    annotation class DomainService
}
