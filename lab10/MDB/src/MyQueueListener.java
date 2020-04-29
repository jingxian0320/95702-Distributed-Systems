import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;
import javax.annotation.Resource;

// This creates the mapping of this MessageListener to the appropriate Queue
@MessageDriven(mappedName = "jms/myQueue", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MyQueueListener implements MessageListener {

    // Lookup the ConnectionFactory using resource injection and assign to cf
    @Resource(name = "jms/myConnectionFactory")
    private ConnectionFactory cf;
    // lookup the Queue using resource injection and assign to q
    @Resource(name = "jms/myQueueThree")
    private Queue q;
    
    public MyQueueListener() {
    }

    /*
     * When a message is available in jms/myQueue, then onMessage is called.
     */
    public void onMessage(Message message) {
        try {
            String tmt = "";
            // Since there can be different types of Messages, make sure this is the right type.
            if (message instanceof TextMessage) {
                TextMessage tm = (TextMessage) message;
                tmt = tm.getText();
                System.out.println("MyQueueListener received: " + tmt);


                // With the ConnectionFactory, establish a Connection, and then a Session on that Connection
                Connection con = cf.createConnection();
                Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

                /*
                 * You send and receive messages to/from the queue via a session. We
                 * want to send, making us a MessageProducer Therefore create a
                 * MessageProducer for the session
                 */
                MessageProducer writer = session.createProducer(q);

                /*
                 * The message can be text, a byte stream, a Java object, or a
                 * attribute/value Map We want to send a text message. BTW, a text
                 * message can be a string, or it can be an XML object, and often a
                 * SOAP object.
                 */
                TextMessage msg = session.createTextMessage();
                msg.setText(tmt + " after processing by MyQueueListener");

                // Send the message to the destination Queue
                writer.send(msg);

                // Close the connection
                con.close();
                System.out.println("MyQueueListener sent " + tmt + " after processing by MyQueueListener to " + q.getQueueName());
            } else {
                System.out.println("I don't handle messages of this type");
            }
        } catch (JMSException e) {
            System.out.println("JMS Exception thrown" + e);
        } catch (Throwable e) {
            System.out.println("Throwable thrown" + e);
        }
    }
}
