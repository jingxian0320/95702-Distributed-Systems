import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a TCP server.
 * The server will carry out the correct computation (add or subtract or
 * view) using the sum associated with the ID found in each request.
 * If the server receives an ID that it has not seen before, that ID will initially be
 * associated with a sum of 0.
 */

public class EchoServerTCP {
    public static void main(String args[]) {
        System.out.println("Server Running"); //indicate start of the server
        Socket clientSocket = null;
        HashMap<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
        try {
            int serverPort = 7777; // the server port we are using
            
            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Forever,
             *   wait for a new connection
             *   read a line from the socket
             */
            while(true){
                /*
                 * Block waiting for a new connection request from a client.
                 * When the request is received, "accept" it, and the rest
                 * the tcp protocol handshake will then take place, making
                 * the socket ready for reading and writing.
                 */
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.

                // Set up "in" to read from the client socket
                Scanner in;
                in = new Scanner(clientSocket.getInputStream());


                // Set up "out" to write to the client socket
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                    while (in.hasNextLine()){
                        String data = in.nextLine();
                        //retrieve id, operation, val from byte arr
                        String[] m = data.split(",");
                        int cal_id = Integer.valueOf(m[0]);
                        if (!resultMap.containsKey(cal_id)){
                            resultMap.put(cal_id, 0);
                        }
                        int ops = Integer.valueOf(m[1]);
                        String replyString = "";
                        switch(ops){
                            case (1)://add
                                resultMap.put(cal_id,resultMap.get(cal_id)+Integer.valueOf(m[2]));
                                replyString = "OK";
                                break;
                            case (2)://subtract
                                resultMap.put(cal_id,resultMap.get(cal_id)-Integer.valueOf(m[2]));
                                replyString = "OK";
                                break;
                            case (3)://view
                                replyString = String.valueOf(resultMap.get(cal_id));
                                break;
                        }
                        out.println(replyString);
                        out.flush();
                        //print the reply string
                        System.out.println("Result: "+replyString);
                    }
                }


            
        // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
            
        // If quitting (typically by you sending quit signal) clean up sockets
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }
}
