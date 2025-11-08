package com.example.command.event;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByAggregateIdOrderBySequenceAsc(String aggregateId);
    Event findTopByAggregateIdOrderBySequenceDesc(String aggregateId);
}