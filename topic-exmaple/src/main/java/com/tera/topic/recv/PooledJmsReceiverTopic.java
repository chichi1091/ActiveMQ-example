package com.tera.topic.recv;

import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.UUID;

public class PooledJmsReceiverTopic {
    public static void main(String[] args) {
        JmsPoolConnectionFactory poolingFactory = new JmsPoolConnectionFactory();

        try {
            Context context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
            poolingFactory.setConnectionFactory(factory);
            Topic topic = (Topic)context.lookup("myTopicLookup");

            Connection connection = poolingFactory.createConnection("admin", "admin");
            connection.setExceptionListener(new MyExceptionListener());

            String consumerId = "consumer-" + UUID.randomUUID();
            connection.setClientID(consumerId);

            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer messageConsumer = session.createDurableSubscriber(topic, consumerId);

            TextMessage receivedMessage = (TextMessage) messageConsumer.receive();
            if (receivedMessage != null) {
                System.out.print(receivedMessage.getText());
            } else {
                System.out.println("No message received within the given timeout!");
            }

            System.out.println();

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
