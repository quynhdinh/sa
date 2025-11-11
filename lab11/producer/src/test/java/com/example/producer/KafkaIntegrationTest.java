package com.example.producer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "orders" })
public class KafkaIntegrationTest {

    @Autowired
    EmbeddedKafkaBroker embeddedKafka;

    private Consumer<String, String> consumer;
    private KafkaProducer<String, String> producer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(), new StringDeserializer());
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "orders");

        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        producer = new KafkaProducer<>(producerProps, new StringSerializer(), new StringSerializer());
    }

    @AfterEach
    void tearDown() {
        if (producer != null) producer.close();
        if (consumer != null) consumer.close();
    }

    @Test
    void testProduceAndConsume() throws Exception {
        // send a simple message
        producer.send(new ProducerRecord<>("orders", "key1", "hello-world")).get();

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "orders");
        assertThat(record).isNotNull();
        assertThat(record.value()).isEqualTo("hello-world");
    }
}