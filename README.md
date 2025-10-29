## Software Architecture
make a table of contents for the following notes:
# Table of Contents
- [Table of Contents](#table-of-contents)
- [Day 1: Introduction to Software Architecture](#day-1-introduction-to-software-architecture)
- [Day 2: Domain-Driven Design (DDD)](#day-2-domain-driven-design-ddd)
- [Day 3: Databases](#day-3-databases)

# Day 1: Introduction to Software Architecture
- There is no silver bullet [Fred Brooks, 1986](https://worrydream.com/refs/Brooks_1986_-_No_Silver_Bullet.pdf)
- Software Architecture is the important stuff - whatever it might be
Design, Architecture are two ends of a spectrum. Architecture is about doing the requirements right, Design is about doing the right requirements.
State:
States at the client(stateless)
- Infinite scalability. 
- No network calls
State in the db(stateless)
- Every time you need something, you go to the db.
State in the server(stateful)
- Every time you need something, you go to the server.(HTTP session)
1. *5 layer architecture*
- Presentation layer(UI layer)
- Service layer(Application layer)
- Domain layer(Business logic layer)
- Data access layer(Repository layer)
- Integration layer(External systems)
Service layer is also called application layer.
1. *Hexagonal* = ports and adapters architecture: separates the core business logic from external concerns (e.g., UI, databases) through the use of ports (interfaces) and adapters (implementations).
2. *Onion architecture*: emphasizes the separation of concerns and the dependency inversion principle, with the core business logic at the center and external dependencies on the outer layers.
3. *Clean architecture*: focuses on the separation of concerns, with the core business logic independent of frameworks, databases, and UI, allowing for easier testing and maintenance.
They all have similar ideas: Domain layer at the center.
1. [*Pipe and filter architecture*](https://learn.microsoft.com/en-us/azure/architecture/patterns/pipes-and-filters): data is processed through a series of independent components (filters) connected by data streams (pipes). COR(Chain of responsibility) is similar.
2. *Master-slave architecture*: a distributed architecture where one node (master) controls one or more nodes (slaves), often used for load balancing and data replication.
3. *Microkernel architecture*: a modular architecture where a core system (microkernel) provides essential services, and additional functionality is added through plugins or modules. OSGi.
Domain layer is separated from application layer and data layer.

**Ivory tower architect**: focuses on high-level design without considering practical implementation details.

# Day 2: Domain-Driven Design (DDD)

Ubiquitous language: a common language shared by all team members, including developers and domain experts, to ensure clear communication and understanding of the domain.

Anemic Domain Model: a design anti-pattern where domain objects contain little or no business logic, leading to a separation of data and behavior. Central brain.
Rich Domain Model: a design approach where domain objects encapsulate both data and behavior, promoting a more cohesive and expressive representation of the domain.

**Entity:** an object that has a distinct identity that runs through time and different states.

**Value Object:** an object that is defined by its attributes rather than a unique identity. Immutable.

When in doubt, make it an value object.

Instead of a large entity class, we strive for a small and simple entity class with many value objects.

**Domain Service:** a stateless service that encapsulates domain logic that doesn't naturally fit within an entity or value object.

**Domain Events:** a way to represent state changes in the system, allowing for better decoupling and communication between different parts of the application.

# Day 3: Databases
- Relational Databases
- NoSQL Databases
- Key-Value Stores
- Document Databases
- Column-Family Stores
- Graph Databases

**Patterns**
- Event sourcing
- CQRS

**Project idea:**
- Kafka
- Microservices
- Event sourcing
- Integration pattern

Help me to write a project idea using those 4 above points.
Project Idea: Event-Driven E-Commerce Platform
- Utilize Kafka for real-time data streaming and communication between microservices.
- Implement microservices architecture to ensure scalability and maintainability.
- Apply event sourcing to capture and store all changes to the application state as a sequence of events.
- Use integration patterns to connect various services and ensure smooth data flow across the platform.