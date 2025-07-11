# `Core` module

In the following, we describe the contents of the core module. The core module comprises the model itself, a controller interface and several controller implementations, and a statistics class.

## `Model` classes

The model represents the concepts for modeling intelligent transportation systems and distinguishes structures and entities.
The structures include 3D-vectors as well as locations and location-times.
The entities include intersections and segments (i.e. the driving infrastructure), stations (i.e. the charging infrastructure), as well as vehicles and demands.

```mermaid
classDiagram
    direction LR

    class Intersection {
        <<Entity>>
        Name: String
        Radius: Double
        ComputeRadius()
    }
    class Segment {
        <<Entity>>
        Speed: Double
        Lanes: Double
        Length: Double
        AngleX: Double
        AngleY: Double
        AngleZ: Double
        ComputeLength()
        ComputeTangent()
        ComputeNormal()
        ComputeAngleX()
        ComputeAngleY()
        ComputeAngleZ()
    }
    class Location {
        <<Structure>>
        Distance: Double
        ComputeCoordinate()
    }
    class LocationTime {
        <<Structure>>
        Time: Double
    }
    class Station {
        <<Entity>>
        Speed: Double
    }
    class Vehicle {
        <<Entity>>
        BatteryCapacity: Double
        BatteryLevel: Double
        DemandCapacity: Double
        DemandLevel: Double
        Length: Double
        Speed: Double
    }
    class Demand {
        <<Entity>>
        Size: Double
    }
    class Vector {
        <<Structure>>
        X: Double
        Y: Double
        Z: Double
    }
    
    Segment --> "1" Intersection:Start
    Segment --> "1" Intersection:End
    Station --> "1" Location
    Vehicle --> "1" Location:Initial
    Vehicle --> "1" Location:Current
    Demand --> "1" LocationTime:Pick
    Demand --> "1" LocationTime:Drop
    Demand --> "1" LocationTime:Current
    LocationTime --> "1" Location
    Location --> "1" Segment

    Intersection --> "1" Vector:Coordinate
    Segment --> "1" Vector:Tangent
    Segment --> "1" Vector:Normal
    Location --> "1" Vector:Coordinate

    style Vector fill:white,stroke:gray
    style Location fill:white,stroke:gray
    style LocationTime fill:white,stroke:gray
```

## `Controller` classes

The controller interface is responsible for plugging different control algorithms into the simulation engine.
Through this mechanism, the simulation engine is decoupled from the control strategies, that drive the system behavior.
The module also contains different implementations of the controller interface such as a random controller or a JGraphT-based controller.

```mermaid
classDiagram
    direction LR

    class Controller {
        <<Interface>>
        SelectDemand(Vehicle v, Demand d) Boolean
        SelectStation(Vehicle v, Station s) Boolean
        UnselectStation(Vehicle v) Boolean
        SelectSegment(Vehicle v) Segment
        SelectSpeed(Vehicle v) Double
        SelectMaximumSpeedSelectionTimeout(Vehicle v) Double
        SelectMaximumStationSelectionTimeout(Vehicle v) Double
    }
    class RandomController {

    }
    class GreedyController {

    }
    class SmartController {
        GetMinimumDistance(Location a, Location b) Double
        GetMinimumStationDistance(Location l) Double
    }
    class ManualController {

    }
    class JGraphT {
        
    }
    class Swing {
        
    }

    <<Library>> JGraphT
    <<Library>> Swing

    RandomController --|> Controller
    GreedyController --|> Controller
    SmartController --|> Controller
    ManualController --|> Controller
    
    SwitchableController --|> Controller
    SwitchableController --> "*" Controller:Controllers
    SwitchableController --> "1" Controller:Active

    JGraphT <-- SmartController
    Swing <-- ManualController

    style JGraphT fill:white,stroke:gray
    style Swing fill:white,stroke:gray

    style Controller fill:yellow,stroke:orange
```

## `Statistics` classes

The statistics class is responsible for collecting performance data during simulation experiments.
The performance data is necessary to compare infrastructure and control algorithm designs in specific situations.
The class assumes that performance data is collected only in specific events such as a vehicle passing a road crossing.

```mermaid
classDiagram
    direction LR
    
    class Statistics {
        RecordCrossing(Vehicle v, Segment p, Segment n, Double t)
        RecordPickupDecline(Vehicle v, Demand d, Double t)
        RecordPickupAccept(Vehicle v, Demand d, Double t)
        RecordDropoff(Vehicle v, Demand d, Double t)
        RecordSpeed(Vehicle v, Double s, Double t)
        RecordDistance(Vehicle v, Double d, Double t)
        RecordStep(Double s, Double t)
    }
```