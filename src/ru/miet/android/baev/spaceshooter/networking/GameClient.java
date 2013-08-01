package ru.miet.android.baev.spaceshooter.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.DatagramChannel;

import ru.miet.android.baev.spaceshooter.provider.Generic;

public class GameClient{
	
	public static InetAddress FindServer(){
		DatagramSocket sBroadcaster = null; 
		DatagramSocket sReceiver = null;
		
		try
	    {
			DatagramChannel channel = DatagramChannel.open();
	        //1. creating a server socket, parameter is local port number
			sBroadcaster = channel.socket();
			sBroadcaster.setBroadcast(true);
			sBroadcaster.setReuseAddress(true);
			sBroadcaster.bind(new InetSocketAddress(9000));
			
			sReceiver = channel.socket();
			sReceiver.setReuseAddress(true);
			sReceiver.setSoTimeout(500);
			sReceiver.bind(new InetSocketAddress(9001));
	         
	        //buffer to receive incoming data
	        byte[] buffer = new byte[4];
	        byte[] HELObuf = "HELO".getBytes();
	        
	        DatagramPacket HELOMessage = new DatagramPacket(HELObuf,HELObuf.length, InetAddress.getByAddress(Generic.getBroadcastAddress()), 9000);
	        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
	         	         
	        //communication loop
	        for(int i = 0; i<5; i++)
	        {
	        	// trying to get answer from broadcasting
	            sBroadcaster.send(HELOMessage);
	            try{
	            	sReceiver.receive(incoming);
	            }
	            catch(SocketTimeoutException e){
	            	continue;
	            };
	            
	            InetAddress remoteServer = InetAddress.getByAddress(incoming.getData());
	            sReceiver.disconnect();
	            sBroadcaster.disconnect();
	            
	            return remoteServer;
	        }
	    }	         
	    catch(IOException e) { 
	        sReceiver.close();
	        sBroadcaster.close();  
	    } 
		    
        return null;
	}
}
