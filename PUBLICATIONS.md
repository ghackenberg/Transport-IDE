# Publications

This page summarizes our publications on modeling, simulation, and optimization of transportation systems.
First, we provide a summary of workshops, conferences, and journals before providing article titles, abstracts, and Bibtex entries.

## Workshops, conferences, and journals

In the past, we contributed to the following workshops, conferences, and journals:

* [**MODELSWARD**<br/>International Conference on Model-Based Software and Systems Engineering](https://modelsward.scitevents.org/)
* [**ISDA**<br/>International Conference on Intelligent Systems Design and Applications](https://www.mirlabs.org/newsite/conferences.html)
* [**MT-ITS**<br/>International Conference on Models and Technologies for Intelligent Transportation Systems](https://ieeexplore.ieee.org/xpl/conhome/1808904/all-proceedings)
* [**ITSC**<br/>International Conference on Intelligent Transportation Systems](https://ieee-itsc.org/)
* [**ICCVE**<br/>International Conference on Connected Vehicles and Expo](https://ieeexplore.ieee.org/xpl/conhome/1802714/all-proceedings)

## Titles, abstracts, and Bibtex entries

Below you find the abstracts and Bibtex entries for our articles in chronological order.

#### [Next-Generation Design Tools for Intelligent Transportation Systems `MODELSWARD 2025`](https://www.scitepress.org/PublishedPapers/2025/131832/)

Intelligent Transportation Systems (ITS) promote new transportation paradigms such as connected and autonomous vehicles (CAV), multi-modal and demand-responsive transport systems, and enable the transportation electrification by sustainable operation of electric vehicles. Methods and tools are needed to explore the possible design space for emerging transportation paradigms, which support evaluation of system design alternatives and verification of system properties. In this work, we propose a model- and simulation-based systems engineering framework for capturing design decisions and evaluating control strategies for ITS design. In addition to capturing and evaluating different design decisions, the proposed solution allows users to guide design decisions by systematic comparison and evaluation of system configurations and control strategies.

```bibtex
@Conference{ascher_hackenberg_2025,
  author       = {Dominik Ascher and Georg Hackenberg},
  title        = {Next-Generation Design Tools for Intelligent Transportation Systems},
  booktitle    = {Proceedings of the 13th International Conference on Model-Based Software and Systems Engineering - Volume 1: MODELSWARD},
  year         = {2025},
  pages        = {234-241},
  publisher    = {SciTePress},
  organization = {INSTICC},
  doi          = {10.5220/0013183200003896},
  isbn         = {978-989-758-729-0},
}
```

#### [A Discrete Event Formalism for Fast Simulation of On-Demand Transportation Systems `ISDA 2024`](https://link.springer.com/chapter/10.1007/978-3-031-64850-2_17)

The interconnection between intelligent transportation systems (ITS) and services poses fundamental challenges with respect to establishing most efficient structures as well as intelligent and integrated control strategies addressing new transportation paradigms such as autonomous vehicles, mobility-on-demand as well as the transportation electrification. Systematic and integrated methods for efficiently modeling, simulating and evaluating these systems are required, which are able to address aforementioned challenges. For this, we present a discrete event formalism for fast simulation of on-demand transportation systems, which provides a formalization of essential static properties, dynamic state functions, as well as events concisely describing transportation system design space in the scope of emerging transportation paradigms. Our formalism allows for reduction of computational complexity by defining domain-relevant events impacting the design problem at hand, while omitting unimportant states negatively impacting simulation performance and modeling effort. The formalism can serve as the foundation for deriving well-defined designs of transportation systems as well as locally and globally optimal control strategies.

```bibtex
@InProceedings{ascher_hackenberg_2024,
  author    = {Ascher, Dominik and Hackenberg, Georg},
  editor    = {Abraham, Ajith and Bajaj, Anu and Hanne, Thomas and Siarry, Patrick and Ma, Kun},
  title     = {A Discrete Event Formalism for Fast Simulation of On-Demand Transportation Systems},
  booktitle = {Intelligent Systems Design and Applications},
  year      = {2024},
  publisher = {Springer Nature Switzerland},
  address   = {Cham},
  pages     = {185--197},
  isbn      = {978-3-031-64850-2}
}
```

#### [Model-Based Design of Integrated Transportation Systems Using Approximate Dynamic Programming `ITSC 2023`](https://ieeexplore.ieee.org/abstract/document/10422359)

Based on recent developments and technological advances, the complexity and number of systems and services for intelligent transportation systems (ITS) has risen dramatically. The interactions between these systems and services require systematic development methods for their integrated structural design as well as their behavioral planning in terms of integrated control strategies, which can address multiple as well as heterogenous goals. To address this situation, in this work, we propose an approach for integrated modeling of heterogenous ITS systems and their integrated evaluation using approximate dynamic programming (ADP). For this, we introduce a methodology which incorporates the necessary design principles and requirements to address above challenges. Then, we present an integrated systems modeling technique which is used for problem formulation. In addition to problem formulation, it provides a generalist problem solution framework using ADP, which can be applied to multi-domain and heterogenous model problems. To demonstrate domain-specific application of our approach and systems modeling technique, we provide an exemplary model problem and its solution for an autonomous mobility on demand system in a demand responsive transport (DRT) scenario.

```bibtex
@InProceedings{ascher_hackenberg_albayrak_2023,
  author    = {Ascher, Dominik and Hackenberg, Georg and Albayrak, Sahin},
  booktitle = {2023 IEEE 26th International Conference on Intelligent Transportation Systems (ITSC)}, 
  title     = {Model-Based Design of Integrated Transportation Systems Using Approximate Dynamic Programming}, 
  year      = {2023},
  pages     = {4443-4450},
  doi       = {10.1109/ITSC57777.2023.10422359}}
```

#### [The passenger extension of the TRANSP-0 system design framework `MT-ITS 2017`](https://ieeexplore.ieee.org/abstract/document/8005676)

In recent years, potentially disruptive transportation paradigms such as mobility-on-demand, autonomous vehicles and the transportation electrification have seen increased attention for designing intelligent transportation systems (ITS). These paradigms represent unifying approaches in addressing sustainable alignment between transportation demand and supply in the context of rising world passenger transport energy use and increasing negative environmental impacts. It is crucial to establish integrated planning and control strategies for both transportation and power systems, as the aforementioned paradigms exert interrelated and concurrent effects on these systems. To evaluate future transportation and power system design options coherently and explore the potential effects of the aforementioned paradigms, in this work, we extend the design space of a formal system design framework for integrated transportation and power system design to cover autonomous mobility-on-demand systems. In that, our approach addresses both planning and control strategies for the aforementioned paradigms within integrated transportation and power systems. For this, we define static design parameters and dynamic model properties for our system design abstraction and discuss potential requirements in terms of objectives and constraints.

```bibtex
@InProceedings{ascher_hackenberg_2017,
  author    = {Ascher, Dominik and Hackenberg, Georg},
  booktitle = {2017 5th IEEE International Conference on Models and Technologies for Intelligent Transportation Systems (MT-ITS)},
  title     = {The passenger extension of the TRANSP-0 system design framework},
  year      = {2017},
  pages     = {256-261},
  doi       = {10.1109/MTITS.2017.8005676}
}
```

#### [The TRANSP-0 framework for integrated transportation and power system design `ITSC 2016`](https://ieeexplore.ieee.org/abstract/document/7795669)

Increasing penetration of decentralized energy production as well as the propagation of new transportation paradigms such as transportation electrification, autonomous vehicles and mobility-on-demand will require interrelated key changes to current transportation and power systems. To diminish negative environmental impacts and achieve longterm sustainability, close integration between transportation and power systems is necessary and integrated planning, operation and control strategies have to be established. In this paper, we present TRANSP-0, a system design framework for rapid formulation and evaluation of design options within integrated transportation and power systems. Firstly, we present the TRANSP-0 design space in terms of the static parameters for integrated subsystem design. Secondly, we visit the dynamic properties of subsystem design by formulating the underlying optimal control problem. Thirdly, we establish the requirements to integrated control strategies in terms of objectives and constraints of the described optimal control problem. Finally, we conclude with an outlook on the future scope of the proposed system design framework.

```bibtex
@InProceedings{ascher_hackenberg_2016,
  author    = {Ascher, Dominik and Hackenberg, Georg},
  booktitle = {2016 IEEE 19th International Conference on Intelligent Transportation Systems (ITSC)},
  title     = {The TRANSP-0 framework for integrated transportation and power system design},
  year      = {2016},
  pages     = {945-952},
  doi       = {10.1109/ITSC.2016.7795669}
}
```
#### [Integrated transportation and power system modeling `ICCVE 2015`](https://ieeexplore.ieee.org/abstract/document/7447633)

Undesired impacts on the power system caused by electric vehicles (EVs) remain a major challenge that needs to be addressed to guarantee their safe widespread adaption. In this context, efficiently balancing power demand within power grids represents a fundamental issue of vehicle-to-grid (V2G) applications of EV. Here, existing modeling approaches cover only a limited set of features required to achieve integrated multi-objective transportation and power system modeling. In this paper, we investigate an approach for integrated modeling of transportation and power systems, emphasizing microscopic models of electric devices within the power system and individual cars on the traffic network which make up the transportation system. The approach allows for rapid variation and evaluation of scenarios of both transportation and power systems by varying their configuration, composition and underlying objectives. We apply our approach to different incremental small-scale V2G example scenarios. Obtained simulation results show the feasibility of the approach in terms of the employed modeling technique. Based on simulation results, we conclude with an evaluation of our approach with respect to its validity, novelty, efficiency and applicability.

```bibtex
@InProceedings{ascher_hackenberg_2015,
  author    = {Ascher, Dominik and Hackenberg, Georg},
  booktitle = {2015 International Conference on Connected Vehicles and Expo (ICCVE)},
  title     = {Integrated transportation and power system modeling},
  year      = {2015},
  pages     = {379-384},
  doi       = {10.1109/ICCVE.2015.23}
}
```

#### [Early estimation of multi-objective traffic flow `ICCVE 2014`](https://ieeexplore.ieee.org/abstract/document/7297511)

Intelligent Transportation Systems (ITS) have come a long way targeting problems such as increasing emissions and growing vehicle numbers. Current approaches address a variety of objectives including congestion management, collision avoidance, energy-efficiency and emission reduction. However, respective solutions typically are designed for and tailored to a predefined set of objectives. Consequently, the effects of drastically changing objectives cannot be assessed easily. To address this situation we present a lightweight approach to estimating multi-objective traffic flow early in systems engineering using non-deterministic models and stochastic optimization techniques. We demonstrate the feasibility of the framework using a basic traffic scenario and conclude with future work.

```bibtex
@InProceedings{ascher_hackenberg_2014,
  author    = {Ascher, Dominik and Hackenberg, Georg},
  booktitle = {2014 International Conference on Connected Vehicles and Expo (ICCVE)},
  title     = {Early estimation of multi-objective traffic flow},
  year      = {2014},
  pages     = {1056-1057},
  doi       = {10.1109/ICCVE.2014.7297511}
}
```