package hexa

interface HEXA {
    annotation class Infra
    annotation class Config
    annotation class AdapterInbound
    annotation class AdapterOutbound
    annotation class Application
    @Target(AnnotationTarget.TYPEALIAS, AnnotationTarget.CLASS)
    annotation class PortInbound
    @Target(AnnotationTarget.TYPEALIAS, AnnotationTarget.CLASS)
    annotation class PortOutbound
    annotation class Domain
}

annotation class Pure
