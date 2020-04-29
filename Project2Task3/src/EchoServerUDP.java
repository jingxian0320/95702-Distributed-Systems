import java.math.BigInteger;
import java.net.*;
import java.io.*;

import java.net.*;
import java.io.*;
import java.util.HashMap;

/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a UDP server.
 * The server will carry out the correct computation (add or subtract or
 * view) using the sum associated with the ID found in each request.
 * If the server receives an ID that it has not seen before, that ID will initially be
 * associated with a sum of 0.
 */


public class EchoServerUDP{
    public static void main(String args[]){
        System.out.println("Server Running"); //indicate start of the server
        DatagramSocket aSocket = null; //declare variable for socket
        byte[] buffer = new byte[1000]; //the byte array to wrap messages
        HashMap<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
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
                //retrieve id, operation, val from byte arr
                String[] m = new String(data).split(",");
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
                //change result from int to byte arr
                byte[] replyArray = replyString.getBytes();
                //create a DatagramPacket for sending a message to the client with address and port of the client we are sending the message to
                DatagramPacket reply = new DatagramPacket(replyArray,
                        replyArray.length, request.getAddress(), request.getPort());
                //print the reply string
                System.out.println("Result: "+replyString);
                //socket sends out the reply
                aSocket.send(reply);
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());//print SocketException
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());//print IOException
        }finally {if(aSocket != null) aSocket.close();}//close the socket if succeed
    }
}