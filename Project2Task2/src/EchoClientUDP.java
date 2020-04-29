import java.net.*;
import java.io.*;

/**
 * Author: Jingxian Bao
 * Last Modified: Feb 19, 2020
 *
 * This program demonstrates a UDP client.
 *  On the client, all of the communication code will be
 * placed in a method named “add”. The client sends 1000 messages to your server in order to compute the sum
 * 1+2+3+..+1000.
 */


public class EchoClientUDP{
	DatagramSocket aSocket;
	InetAddress aHost;
	int serverPort = 6789; //server port

	//constructor
	EchoClientUDP(){
		System.out.println("Client Running"); //indicate start of the client
		try{
			aHost = InetAddress.getByName("localhost"); //server address
			aSocket = new DatagramSocket(); //initialize the socket to receive/send packets
		}catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
		}catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
		}
	}
	public static void main(String args[]){
		EchoClientUDP client = new EchoClientUDP();
		String result = null;
		for (int i = 1; i <= 1000; i++) {
			result = client.add(i);
		}
		System.out.println("Sum: "+ result);

	}
	private String add(int val){
		byte [] m = String.valueOf(val).getBytes(); //get the val in byte
		//create a DatagramPacket for sending a message to the server with address and port of the server
		DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);
		try {
			this.aSocket.send(request);//send the request
			byte[] buffer = new byte[1000];//the byte array to wrap messages
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);//instantiate a DatagramPacket to receive incoming messages
			aSocket.receive(reply); //call the receive method on the socket. It stores the message inside the byte array of the DatagramPacket passed to it.
			//create a new byte array with length = msg length
			byte[] data = new byte[reply.getLength()];
			//copy the msg from request to data
			System.arraycopy(reply.getData(), reply.getOffset(), data, 0, reply.getLength());
			//get the reply string
			String replyVal = new String(data);
			return replyVal;
		}catch (SocketException e) {System.out.println("Socket: " + e.getMessage());//print SocketException
		}catch (IOException e){System.out.println("IO: " + e.getMessage());//print IOException
		}
		return null;
	}
}