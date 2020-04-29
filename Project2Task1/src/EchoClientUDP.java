import java.net.*;
import java.io.*;

/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a simple echo UDP client.
 * The client is able to read user input and send this msg
 * to the server. The same msg will be received from the
 * server.
 */


public class EchoClientUDP{
    public static void main(String args[]){
        System.out.println("Client Running"); //indicate start of the client
        DatagramSocket aSocket = null; //declare variable for socket
        try {
            InetAddress aHost = InetAddress.getByName("localhost"); //server address
            int serverPort = 6789; //server port
            aSocket = new DatagramSocket(); //initialize the socket to receive/send packets
            String nextLine; //initialize string nextLine
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in)); //get typed from user keyboard input
            while ((nextLine = typed.readLine()) != null) { //while not reaching the end of typed
                byte [] m = nextLine.getBytes(); //get the nexeLine in byte
                //create a DatagramPacket for sending a message to the server with address and port of the server
                DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
                aSocket.send(request);//send the request
                if (nextLine.equals("quit!")){
                    System.out.println("Client Quiting!");
                    break;
                }
                byte[] buffer = new byte[1000];//the byte array to wrap messages
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);//instantiate a DatagramPacket to receive incoming messages
                aSocket.receive(reply); //call the receive method on the socket. It stores the message inside the byte array of the DatagramPacket passed to it.
                //create a new byte array with length = msg length
                byte[] data = new byte[reply.getLength()];
                //copy the msg from reply to data
                System.arraycopy(reply.getData(), reply.getOffset(), data, 0, reply.getLength());
                System.out.println("Reply: " + new String(data));//print out the reply
            }

        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
        }finally {if(aSocket != null) aSocket.close();}//close the socket if succeed
    }
}