# Project `mbse.transport` (aka **Transport-IDE**)

The project `mbse.transport` develops a software-based toolkit for modeling, simulating, and optimizing intelligent transportation systems.

## Architecture Principles

* Apache Maven is used as the dependency management and build configuration system.
* The project uses a multi-module structure with one core and several application modules.
* The core module contains common functionalities required by all the application modules.
* The application modules contain the CLI and GUI application-specific functionalities instead.

## Coding Guidelines

* Each **Java module** must provide a `module-info.java` file with a module-level Javadoc comment briefly explaining the purpose, features, and structure of the module.
* Each **Java package** must provide a `package-info.java` file with a package-level Javadoc comment briefly explaining the purpose, features, and structure of the package.
* Each **Java class** must provide a class-level Javadoc comment briefly explaining the purpose of the class and providing basic usage examples.
* Each **Java interface** must provide a interface-level Javadoc comment briefly explaining the purpose of the interface.

## Assistant Instructions

* Always derive a detailed task specification before suggesting changes.
* Always adhere to the architecture principles and the coding guidelines.