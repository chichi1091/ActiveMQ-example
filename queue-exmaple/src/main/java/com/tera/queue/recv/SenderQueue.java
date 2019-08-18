package com.tera.queue.recv;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class SenderQueue {
    public static void main(String[] args) {
        QueueConnection connection = null;
        QueueSession session = null;
        QueueSender sender = null;
        try {
            //Connectionを作成
            QueueConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createQueueConnection();
            connection.start();

            //Senderの作成
            session = connection.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("queue_test");
            sender= session.createSender(queue);

            //メッセージの送信
            TextMessage msg = session.createTextMessage("Hello Message!");
            sender.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sender != null) sender.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
