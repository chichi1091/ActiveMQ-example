package com.tera.topic.send;

import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

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