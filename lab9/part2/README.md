Run Kafka
`docker run -d -p 9092:9092 --name kafka-broker apache/kafka:latest`

`./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic product-events --from-beginning`