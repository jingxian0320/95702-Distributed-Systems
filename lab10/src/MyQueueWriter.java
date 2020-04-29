import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.jms.*;
import javax.naming.*;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "MyQueueWriter", urlPatterns = {"/MyQueueWriter"})
public class MyQueueWriter extends HttpServlet {

    // Lookup the ConnectionFactory using resource injection and assign to cf
    @Resource(name = "jms/myConnectionFactory")
    private ConnectionFactory cf;
    // lookup the Queue using resource injection and assign to q
    @Resource(name = "jms/myQueue")
    private Queue q;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String val = request.getParameter("simpleTextMessage");
        try {
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
            msg.setText(val);

            // Send the message to the destination Queue
            writer.send(msg);

            // Close the connection
            con.close();

            out.println("<HTML><BODY><H1>Wrote " + val + " to queue</H1>");
            out.println("</BODY></HTML>");
            System.out.println("Servlet sent " + val + " to Queue One");
        } catch (Exception e) {
            System.out.println("Servlet through exception " + e);
        } finally {
            out.close();
        }
    }
}
