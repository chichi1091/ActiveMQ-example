package com.tera.topic.recv;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.LocalDateTime;

public class ReceiverTopic {
    public static void main(String[] args) {
        TopicConnection connection = null;
        TopicSession session = null;
        TopicSubscriber subscriber = null;
        try {
            //Connectionを作成
            TopicConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createTopicConnection();
            connection.start();

            //Subscriberの作成
            session = connection.createTopicSession(false,TopicSession.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("topic_test");
            subscriber= session.createSubscriber(topic);

            //メッセージの受信
            TextMessage msg = (TextMessage) subscriber.receive();
            System.out.println(msg.getText());
            System.out.println(LocalDateTime.now());
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (subscriber != null) subscriber.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
