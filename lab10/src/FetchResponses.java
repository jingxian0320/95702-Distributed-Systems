import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "FetchResponses", urlPatterns = {"/FetchResponses"})

public class FetchResponses extends HttpServlet {

    // Lookup the ConnectionFactory using resource injection and assign to cf
    @Resource(name = "jms/myConnectionFactory")
    private ConnectionFactory cf;
    // lookup the Queue using resource injection and assign to q
    @Resource(name = "jms/myQueueThree")
    private Queue q;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            // With the ConnectionFactory, establish a Connection, and then a Session on that Connection
            Connection con = cf.createConnection();
            Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            con.start();

            MessageConsumer reader = session.createConsumer(q);

            ArrayList<String> responseList = new ArrayList<String>();
            System.out.println("Reading from " + q.getQueueName());
            while (true){
                Message message = reader.receive(1000);
                TextMessage textmsg =(TextMessage) message;
                if (message == null){
                    System.out.println("no more msg received");
                    break;
                }
                else{
                    System.out.println(textmsg.getText() + " received");
                    message.acknowledge();
                    responseList.add(textmsg.getText());
                }
            }
            session.close();
            // Close the connection
            con.close();

            if (responseList.size() == 0){
                out.println("<HTML><BODY><H1>No Message Available</H1>");
                out.println("</BODY></HTML>");
            }
            else{
                out.println("<HTML><BODY>");
                for (String e: responseList){
                    out.println("<H1>" + e + "</H1>");
                }
                out.println("</BODY></HTML>");
            }
        } catch (Exception e) {
            System.out.println("Servlet through exception " + e);
        } finally {
            out.close();
        }
    }
}
