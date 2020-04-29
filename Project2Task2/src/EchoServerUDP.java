import java.math.BigInteger;
import java.net.*;
import java.io.*;

import java.net.*;
import java.io.*;

/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a UDP server.
 The server will hold an integer value sum, initialized to 0, and will receive requests from
 the client - each of which includes a value to be added to the sum. Upon each request,
 the server will return the new sum as a response to the client. On the server side
 console, upon each visit by the client, the new sum will be displayed.
 */

public class EchoServerUDP{
	public static void main(String args[]){
		System.out.println("Server Running"); //indicate start of the server
		DatagramSocket aSocket = null; //declare variable for socket
		byte[] buffer = new byte[1000]; //the byte array to wrap messages
		int result = 0; //int variable to store the result sum
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
				//retrieve int value from byte arr
				int val = Integer.valueOf(new String(data));
				//calculate result
				result = result + val;
				//change result from int to byte arr
				byte[] replyArray = String.valueOf(result).getBytes();
				//create a DatagramPacket for sending a message to the client with address and port of the client we are sending the message to
				DatagramPacket reply = new DatagramPacket(replyArray,
						replyArray.length, request.getAddress(), request.getPort());
				//retrieve the request string
				String resultString = String.valueOf(result);
				//print the request string
				System.out.println("Sum: "+resultString);
				//socket sends out the reply
				aSocket.send(reply);
			}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());//print SocketException
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());//print IOException
		}finally {if(aSocket != null) aSocket.close();}//close the socket if succeed
	}
}