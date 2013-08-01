package ru.miet.android.baev.spaceshooter.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import ru.miet.android.baev.spaceshooter.provider.Generic;

public class GameServer {
	private static InetAddress localAddress;
	private static Thread threadSonar;
	private static Sonar sonar;
	private static boolean wasStarted = false;
	
	public static void Start() throws IOException{
		wasStarted = true;
        localAddress = InetAddress.getByAddress(Generic.getIPAddress());
        
        sonar = new Sonar();
        threadSonar = new Thread(sonar);
        threadSonar.start();
		// Other server stuff here
	}
	public static void Close(){
		if(wasStarted){
			sonar.Stop();
			try {
				threadSonar.join();
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	public static InetAddress GetAddress(){
		return localAddress;
	}

	private static class Sonar implements Runnable{
		private boolean flagContinue = true;
		
		public void Stop(){
			flagContinue = false;
		}
		
		@Override
		public void run() {
			DatagramSocket sBroadcaster = null; 
			DatagramSocket sReceiver = null;
			
			try
		    {
				DatagramChannel channel = DatagramChannel.open();
		        //1. creating a server socket, parameter is local port number
				sBroadcaster = channel.socket();
				sBroadcaster.setReuseAddress(true);
				sBroadcaster.bind(new InetSocketAddress(9001));
				
				sReceiver = channel.socket();
				sReceiver.setReuseAddress(true);
				sReceiver.bind(new InetSocketAddress(9000));
		         
		        //buffer to receive incoming data
		        byte[] buffer = new byte[16];
		        byte[] BOOPbuf = Generic.getIPAddress();
		        
		        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
		        
		        //communication loop
		        while(flagContinue)
		        {
		        	sReceiver.receive(incoming);
		            sBroadcaster.send(new DatagramPacket(BOOPbuf, BOOPbuf.length, incoming.getAddress(), incoming.getPort()));		            
		        }
		    }	         
		    catch(Exception e) { }
            sReceiver.close();
            sBroadcaster.close();
		}        
	}
}
