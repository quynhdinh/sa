package customers.repository;

import customers.domain.Student;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface StudentRepository extends MongoRepository<Student, Integer> {

    List<Student> findByName(String name);

    Student findByPhoneNumber(String phoneNumber);

    List<Student> findByAddressCity(String city);
}
