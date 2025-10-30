package customers;

import customers.domain.Address;
import customers.domain.CreditCard;
import customers.domain.Customer;
import customers.domain.Student;
import customers.repository.CustomerRepository;
import customers.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StudentRepository studentRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // create customer
        Customer customer = new Customer(101, "John doe", "johnd@acme.com", "0622341678");
        CreditCard creditCard = new CreditCard("12324564321", "Visa", "11/23");
        customer.setCreditCard(creditCard);
        customerRepository.save(customer);
        customer = new Customer(109, "John Jones", "jones@acme.com", "0624321234");
        creditCard = new CreditCard("657483342", "Visa", "09/23");
        customer.setCreditCard(creditCard);
        customerRepository.save(customer);
        customer = new Customer(66, "James Johnson", "jj123@acme.com", "068633452");
        creditCard = new CreditCard("99876549876", "MasterCard", "01/24");
        customer.setCreditCard(creditCard);
        customerRepository.save(customer);
        // get customers
        System.out.println(customerRepository.findById(66).get());
        System.out.println(customerRepository.findById(101).get());
        System.out.println("-----------All customers ----------------");
        System.out.println(customerRepository.findAll());
        // update customer
        customer = customerRepository.findById(101).get();
        customer.setEmail("jd@gmail.com");
        customerRepository.save(customer);
        System.out.println("-----------find by phone ----------------");
        System.out.println(customerRepository.findByPhone("0622341678"));
        System.out.println("-----------find by email ----------------");
        System.out.println(customerRepository.findByEmail("jj123@acme.com"));
        System.out.println("-----------find customers with a certain type of creditcard ----------------");
        List<Customer> customers = customerRepository.findByCreditCardType("Visa");
        for (Customer cust : customers) {
            System.out.println(cust);
        }

        System.out.println("-----------find by name ----------------");
        System.out.println(customerRepository.findByName("John doe"));

        studentRepository.deleteAll();
        studentRepository.saveAll(List.of(
                new Student("Leo", "987654321", "leo@email.com", new Address("1000N", "Fairfield", "52557")),
                new Student("Olamide", "987654322", "olamide@email.com", new Address("1001N", "Fairfield", "52557")),
                new Student("Zandar", "987654323", "zandar@email.com", new Address("1002N", "Fairfield", "52557")),
                new Student("Mohammed", "987654324", "mohammed@email.com", new Address("1003N", "Fairfield", "52557")),
                new Student("Orgil", "987654325", "orgil@email.com", new Address("1004N", "Fairfield", "52557"))
        ));
        System.out.println("-----------All students ----------------");
        System.out.println(studentRepository.findAll());
        System.out.println("-----------find by name ----------------");
        System.out.println(studentRepository.findByName("Leo"));
        System.out.println("-----------find by phoneNumber ----------------");
        System.out.println(studentRepository.findByPhoneNumber("987654323"));
        System.out.println("-----------find by city ----------------");
        List<Student> students = studentRepository.findByAddressCity("Fairfield");
        for (Student stud : students) {
            System.out.println(stud);
        }
    }
}
