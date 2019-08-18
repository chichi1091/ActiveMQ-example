package com.tera.topic.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SenderTopic {
    public static void main(String[] args) {
        TopicConnection connection = null;
        TopicSession session = null;
        TopicPublisher publisher = null;
        try {
            //Connectionを作成
            TopicConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createTopicConnection();
            connection.start();

            //Publisherの作成
            session = connection.createTopicSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("topic_test");
            publisher = session.createPublisher(topic);

            //メッセージの送信
            List<Map<String, Integer>> lists = IntStream.range(0, 5).mapToObj(it -> {
                return new HashMap<String, Integer>() {{
                    put("key" + it, it);
                }};
            }).collect(Collectors.toList());
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(lists);

            TextMessage msg = session.createTextMessage(json);
            publisher.publish(msg);
        } catch (JMSException | JsonProcessingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (publisher != null) publisher.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
