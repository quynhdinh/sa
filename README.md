## Software Architecture

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
2. *Hexagonal* = ports and adapters architecture: separates the core business logic from external concerns (e.g., UI, databases) through the use of ports (interfaces) and adapters (implementations).
3. *Onion architecture*: emphasizes the separation of concerns and the dependency inversion principle, with the core business logic at the center and external dependencies on the outer layers.
4. *Clean architecture*: focuses on the separation of concerns, with the core business logic independent of frameworks, databases, and UI, allowing for easier testing and maintenance.
They all have similar ideas: Domain layer at the center.
5. [*Pipe and filter architecture*](https://learn.microsoft.com/en-us/azure/architecture/patterns/pipes-and-filters): data is processed through a series of independent components (filters) connected by data streams (pipes). COR(Chain of responsibility) is similar.
6. *Master-slave architecture*: a distributed architecture where one node (master) controls one or more nodes (slaves), often used for load balancing and data replication.
7. *Microkernel architecture*: a modular architecture where a core system (microkernel) provides essential services, and additional functionality is added through plugins or modules. OSGi.
Domain layer is separated from application layer and data layer.

Patterns
- Event sourcing
- CQRS

Project idea:
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