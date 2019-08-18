package com.tera.topic.send;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

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
            TextMessage msg = session.createTextMessage("Hello Message!");
            publisher.publish(msg);
        } catch (JMSException e) {
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
