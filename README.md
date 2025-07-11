# Transport-IDE

![](./images/social_preview.png)

The goal of **Transport-IDE** is to make the life of *transportation infrastructure* and *control strategy engineers* easier.
We believe that this engineering domain needs new tools and methodologies to develop better systems with higher performance in shorter time and at lower lifetime cost.
In particular, we think that transportation system engineers must be supported better in

1. analyzing requirements for specific application scenarios (e.g. travel time and energy consumption) as well as
2. deriving, verifying, and validating principle solution variants (including transportation, charge, and maintenance infrastructure as well as control strategy).

**Transport-IDE** is an *open source software project* initiated by Dr. Georg Hackenberg, Professor for Industrial Informatics, School of Engineering, [University of Applied Sciences Upper Austria](https://fh-ooe.at/) and Dominik Ascher, Research Associate, Distributed Artificial Intelligence Laboratory, [Technical University of Berlin](https://www.tu.berlin/).

## Features

Here is a list of currently supported features of Transport-IDE:

- ⭐ *System modeling*
  - **Driving infrastructure** through *intersections* and *segments*
  - **Charging infrastructure** through *stations*
  - **Fleet configurations** through *vehicles*
  - **Load scenarios** through *demands*
- ⭐ *Control strategies*
  - **Random strategy** throwing the dice for every decision
  - **Manual strategy** asking the user for every choice to be made
  - **Greedy strategy** behaving randomly unless demand can be dropped
  - **Smart strategy** based on shortest path algorithm
- ⭐ *Scenario simulations*
  - **Discrete-event semantics** for minimal computational effort
  - **Monte-Carlo experiments** for statistical behavior analysis
  - **Multi-threaded executions** for maximum computation speeds

Feel free to request new features via GitHub issues!

## Screenshots

Here are some screenshots of Transport-IDE in action:

<img src="./modules/fx/doc/editor-flat.png" width="33.33%"/><img src="./modules/fx/doc/editor-deep.png" width="33.33%"/><img src="./modules/fx/doc/simulator.png" width="33.33%"/>

## Requirements

To build and run the project you need to following software packages:

- [OpenJDK](https://openjdk.org/)<br/>provides a **Java compiler and runtime environment**
- [Apache Maven](https://maven.apache.org/)<br/>provides a **dependency management and build configuration tool**

To work on the code you can use the following software package:

- [Eclipse IDE for Java Developers](https://www.eclipse.org/)<br/>provides an **integrated development environment**

## Modules

This software packages comprises three modules and their dependencies.
The following diagram provides an overview of the software modules included.
Technically, the software modules are implemented as Java Jigsaw and Apache Maven modules.

```mermaid
flowchart RL
    
    core["<code>mbse.transport.core</code><br/>Core functionality"]
    cli["<code>mbse.transport.cli</code><br/>Command line interface"]
    fx["<code>mbse.transport.fx</code><br/>New JavaFX-based GUI"]
    swing["<code>mbse.transport.swing</code><br/>Old Swing-based GUI"]

    cli --> core
    fx --> core
    swing --> core
```

Learn more about the modules here: [`Core`](./modules/core/), [`CLI`](./modules/cli/), [`FX`](./modules/fx/), and [`Swing`](./modules/swing/).

## Folders

Here is a brief description of the top-level folder structure of this repository:

* 📁 [**Modules**](../modules/)<br/>Contains the source code of the various software modules.
* 📁 [**Models**](../models/)<br/>Contains sample system models used for testing and demonstration.
* 📁 [**Prototypes**](../prototypes/)<br/>Contains prototypes of new functionalities to be added later.
* 📁 [**Manuscripts**](../manuscripts/)<br/>Contains source code of publications written in Latex.
* 📁 [**Images**](../images/)<br/>Contains image material used in the documentation.

## Documents

And here are some top-level documents to check out before getting started:

* 📄 [**LICENSE.md**](./LICENSE.md)<br/>Contains information about the license, under which this software is shipped.
* 📄 [**CANGELOG.md**](./CHANGELOG.md)<br/>Contains information about software versions and changes included.
* 📄 [**CONTRIBUTING.md**](./CONTRIBUTING.md)<br/>Contains information for contributors to this software project.

## Publications

And, finally, here is a list of publications related to this software project:

1. [**Next-Generation Design Tools for Intelligent Transportation Systems**<br/>`MODELSWARD 2025`](https://www.scitepress.org/PublishedPapers/2025/131832/)
1. [**A Discrete Event Formalism for Fast Simulation of On-Demand Transportation Systems**<br/>`ISDA 2024`](https://link.springer.com/chapter/10.1007/978-3-031-64850-2_17)
1. [**Model-Based Design of Integrated Transportation Systems Using Approximate Dynamic Programming**<br/>`ITSC 2023`](https://ieeexplore.ieee.org/abstract/document/10422359)
1. [**The passenger extension of the TRANSP-0 system design framework**<br/>`MT-ITS 2017`](https://ieeexplore.ieee.org/abstract/document/8005676)
1. [**The TRANSP-0 framework for integrated transportation and power system design**<br/>`ITSC 2016`](https://ieeexplore.ieee.org/abstract/document/7795669)
1. [**Integrated transportation and power system modeling**<br/>`ICCVE 2015`](https://ieeexplore.ieee.org/abstract/document/7447633)
1. [**Early estimation of multi-objective traffic flow**<br/>`ICCVE 2014`](https://ieeexplore.ieee.org/abstract/document/7297511)

See [PUBLICATIONS.md](./PUBLICATIONS.md) for information about the workshops, conferences, and journals as well as article abstracts and Bibtex entries.