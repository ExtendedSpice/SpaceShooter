package ru.miet.android.baev.spaceshooter.provider;

import java.io.IOException;
import java.net.InetAddress;

import ru.miet.android.baev.spaceshooter.ActivityGame;
import ru.miet.android.baev.spaceshooter.PDUpdater;
import ru.miet.android.baev.spaceshooter.networking.GameClient;
import ru.miet.android.baev.spaceshooter.networking.GameServer;
import android.app.ProgressDialog;

public class Networking implements Runnable {
	private PDUpdater target; 
	
	public Networking(ActivityGame o, ProgressDialog pd){
		target = new PDUpdater(pd, o);
	}

	@Override
	public void run() {
		
		target.UpdateMessage("Searching for server");
		InetAddress s = GameClient.FindServer();
		
		if(s == null){
			target.UpdateMessage("Server not found.");
			
			try {
				GameServer.Start();
				target.UpdateMessage("Started locally at "+ GameServer.GetAddress());
			} catch (IOException e) {
				target.UpdateMessage("Error occured starting locally.");
			}
		}
		else 
		{
			target.UpdateMessage("Server found at " + s);
		}		
	}
}
