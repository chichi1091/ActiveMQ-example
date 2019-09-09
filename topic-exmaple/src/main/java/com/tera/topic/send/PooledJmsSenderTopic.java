package com.tera.topic.send;

import org.apache.activemq.ScheduledMessage;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.time.LocalDateTime;

public class PooledJmsSenderTopic {
    public static void main(String[] args) {
        JmsPoolConnectionFactory poolingFactory = new JmsPoolConnectionFactory();

        try {
            Context context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
            poolingFactory.setConnectionFactory(factory);
            Destination topic = (Destination)context.lookup("myTopicLookup");

            final String messagePayload = "Hello World";

            Connection connection = poolingFactory.createConnection("admin", "admin");
            connection.setExceptionListener(new MyExceptionListener());

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer messageProducer = session.createProducer(topic);

            TextMessage message = session.createTextMessage(messagePayload);
            long time = 60 * 1000;
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, time);

            System.out.println(LocalDateTime.now());
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.MINUTE, 5);
//            Date date = calendar.getTime();
//            message.setJMSDeliveryTime(date.getTime());

            messageProducer.send(message, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);

            connection.close();
        } catch (Exception exp) {
            System.out.println("Caught exception, exiting.");
            exp.printStackTrace(System.out);
            System.exit(1);
        } finally {
            poolingFactory.stop();
        }
    }

    private static class MyExceptionListener implements ExceptionListener {
        @Override
        public void onException(JMSException exception) {
            System.out.println("Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
