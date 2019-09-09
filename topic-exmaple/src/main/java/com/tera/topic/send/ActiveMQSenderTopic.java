package com.tera.topic.send;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import javax.jms.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActiveMQSenderTopic {
    public static void main(String[] args) {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createTopic("TEST.TOPIC");

            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            List<Map<String, Integer>> lists = IntStream.range(0, 5).mapToObj(it -> {
                return new HashMap<String, Integer>() {{
                    put("key" + it, it);
                }};
            }).collect(Collectors.toList());
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(lists);
            TextMessage message = session.createTextMessage(json);
            long time = 60 * 1000;
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, time);

            System.out.println(LocalDateTime.now());
            System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
            producer.send(message);

            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}
