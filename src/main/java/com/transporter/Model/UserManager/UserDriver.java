package com.transporter.Model.UserManager;

import com.transporter.Model.Commands;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by anurag on 13-Sep-17.
 */
public class UserDriver {

   public static KafkaConsumer<String, Commands> consumer;

    public static void main(String[] args) {
        try {


            Properties props2 = new Properties();
            props2.put("bootstrap.servers", "localhost:9092");
            props2.put("group.id", "commander");
            props2.put("acks", "all");
            //If the request fails, the producer can automatically retry,
            props2.put("retries", 0);
            //Specify buffer size in config
            props2.put("batch.size", 16384);
            //Reduce the no of requests less than 0
            props2.put("linger.ms", 1);
            //The buffer.memory controls the total amount of memory available to the producer for buffering.
            props2.put("buffer.memory", 33554432);
            props2.put("key.deserializer",
                    "org.apache.kafka.common.serialization.StringDeserializer");
            props2.put("schema.registry.url", "http://localhost:8081");
            props2.put("specific.avro.reader", "true");
            props2.put("value.deserializer", KafkaAvroDeserializer.class);
            consumer = new KafkaConsumer<String, Commands>(props2);

            //Start the consumer to resolve requests
            consumer.subscribe(Arrays.asList("command1"));

            while (true) {
                ConsumerRecords<String, Commands> records = consumer.poll(1000);
                for (ConsumerRecord<String, Commands> record : records) {
                    System.out.println(record.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

