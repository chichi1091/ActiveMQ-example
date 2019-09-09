package com.tera.queue.recv;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.LocalDateTime;

public class ReceiverQueue {
    public static void main(String[] args) {
        QueueConnection connection = null;
        QueueSession session = null;
        QueueReceiver receiver = null;
        try {
            //Connectionを作成
            QueueConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createQueueConnection();
            connection.start();

            //Receiverの作成
            session = connection.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("queue_test");
            receiver = session.createReceiver(queue);

            //メッセージの受信
            TextMessage msg = (TextMessage) receiver.receive();
            System.out.println(msg.getText());
            System.out.println(LocalDateTime.now());
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                receiver.close();
                session.close();
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
