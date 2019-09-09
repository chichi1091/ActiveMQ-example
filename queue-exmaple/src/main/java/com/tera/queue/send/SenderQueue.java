package com.tera.queue.send;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

import javax.jms.*;
import java.time.LocalDateTime;

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
            long delay = 30 * 1000;
            long period = 10 * 1000;
            int repeat = 9;
            msg.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
            msg.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
            msg.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);

            System.out.println(LocalDateTime.now());
            sender.send(msg);
            System.out.println(LocalDateTime.now());
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
