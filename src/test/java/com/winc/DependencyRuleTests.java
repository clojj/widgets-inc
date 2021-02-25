package com.winc;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.winc.archunit.HexagonalArchitecture;
import org.junit.jupiter.api.Test;

class DependencyRuleTests {

    @Test
    void validateProductContextArchitecture() {
        HexagonalArchitecture.boundedContext("com.winc.product")

                .withDomainLayer("domain")

                .withAdaptersLayer("adapter")
                .incoming("inbound")
                .outgoing("outbound")
                .and()

                .withApplicationLayer("application")
                .services("service")
                .incomingPorts("port.inbound")
                .outgoingPorts("port.outbound")
                .and()

                .withConfiguration("config")
                .check(new ClassFileImporter().importPackages("com.winc.product"));
    }

}
