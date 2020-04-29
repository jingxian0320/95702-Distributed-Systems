import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a TCP client.
 * the client may request either an “add” or “subtract” or “view”
 * operation be performed by the server. In addition, each request will pass along an integer ID. Thus, the
 * client will form a packet with the following values: ID, operation (add or subtract or view), and value (if
 * the operation is other than view).
 * The client will be menu driven and
 * will repeatedly ask the user for the user ID, operation, and value (if not a view request). When the
 * operation is “view”, the value held on the server is returned. When the operation is “add” or “subtract”
 * the server performs the operation and simply returns “OK”. During execution, the client will display
 * each returned value from the server to the user. This returned value will be either “OK” or a value (if a
 * view request was made).
 */

public class EchoClientTCP {
    Socket clientSocket;
    int serverPort = 7777;
    //constructor
    EchoClientTCP(){
        System.out.println("Client Running"); //indicate start of the client
        try{
            clientSocket = new Socket("localhost", serverPort);
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
        }
    }
    private void close(){
        try {
            if (clientSocket != null) {
                clientSocket.close();
                System.out.println("Socket Closed.");
            }
        } catch (IOException e) {
            // ignore exception on close
        }
    }

    public static void main(String args[]) {
        EchoClientTCP client = new EchoClientTCP();
        Scanner s = new Scanner(System.in);
        char running = 'y';
        while(running == 'y'){
            System.out.print("Please provide an ID: ");
            int cal_id = s.nextInt();
            System.out.println("1. add");
            System.out.println("2. subtract");
            System.out.println("3. view");
            System.out.print("Please choose an operation to perform: ");
            int ops = s.nextInt();
            String result = null;
            int val;
            switch (ops){
                case(1):
                    System.out.print("Please provide a number:");
                    val = s.nextInt();
                    result = client.process(cal_id + ",1,"+val);
                    break;
                case(2):
                    System.out.print("Please provide a number:");
                    val = s.nextInt();
                    result = client.process(cal_id + ",2,"+val);
                    break;
                case(3):
                    result = client.process(cal_id + ",3");
                    break;
            }
            System.out.println(result);
            s.nextLine();//clear inputs
            System.out.print("Enter y to continue: ");
            running = s.nextLine().charAt(0);
        }
        client.close();
    }
    private String process(String requestString){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //bufferedReader to read response
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))); //writer to send request
            //send requestString to server
            out.println(requestString);
            out.flush();
            String data = in.readLine(); // read a line of data from the stream
            return data;
        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
        }
        return null;
    }
}