/*	
	SFTP Client - Dhairya Trivedi
	This implements a client to interact with a server based on the SFTP (Simple File Transfer Protocol).
	This file reads and sends the commands to the server and then waits for a response, upon which it is 
	output to the terminal. The accompanying readme specifies the exact operation of this communication.
*/
import java.io.*;
import java.net.*;

class SFTPClient { 
    
    //MAIN CODE
    public static void main(String argv[]) throws Exception 
    { 
    	//Global Variables
        String userInput; 
        String serverResponse;
        Boolean connected = true;

        //Main loop
        while(connected) {

        	//Initiating the connection with the server on localhost : Port 6789
        	Socket clientSocket = new Socket("localhost", 6789); 
	        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
	        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

			//Reading input from user
	        userInput = inFromUser.readLine();

	        //DONE command closes the server connection
	        if(userInput.equals("DONE")){
	        	connected = false;
	        }

	        //Send user data to server
	        outToServer.writeBytes(userInput + '\n');

	        //Decode server response and display it
	        serverResponse = inFromServer.readLine();
	 	    System.out.println("FROM SERVER: " + serverResponse);
	 	    while(inFromServer.ready()){
	 	    	serverResponse = inFromServer.readLine();
	 	    	System.out.println(serverResponse);
	 	    }

	 	    //Close connection
	 	    clientSocket.close();
	 	}
    } 
} 
