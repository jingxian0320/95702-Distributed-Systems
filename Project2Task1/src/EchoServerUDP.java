import java.net.*;
import java.io.*;


/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a simple echo UDP server.
 * The server is able to receive the msg from client and
 * send the same msg back to the client
 */

public class EchoServerUDP{
    public static void main(String args[]){
        System.out.println("Server Running"); //indicate start of the server
        DatagramSocket aSocket = null; //declare variable for socket
        byte[] buffer = new byte[1000]; //the byte array to wrap messages
        try{
            aSocket = new DatagramSocket(6789);  //initialize the socket listens to port 6789
            while(true){  //runs until some error or a termination message from the client
                DatagramPacket request = new DatagramPacket(buffer, buffer.length); //instantiate a DatagramPacket to receive incoming messages
                //call the receive method on the socket. It stores the message inside the byte array of the DatagramPacket passed to it.
                aSocket.receive(request);
                //create a new byte array with length = msg length
                byte[] data = new byte[request.getLength()];
                //copy the msg from request to data
                System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
                //create a DatagramPacket for sending a message to the client with address and port of the client we are sending the message to
                DatagramPacket reply = new DatagramPacket(data,
                        request.getLength(), request.getAddress(), request.getPort());
                //retrieve the request string
                String requestString = new String(data);
                //print the request string
                System.out.println("Echoing: "+requestString);
                //socket sends out the reply
                aSocket.send(reply);
                if (requestString.equals("quit!")){
                    System.out.println("Server Quiting!");
                    break;
                }
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());//print IOException
        }finally {if(aSocket != null) aSocket.close();}//close the socket if succeed
    }
}