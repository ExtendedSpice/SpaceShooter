package ru.miet.android.baev.spaceshooter.provider;

import java.io.IOException;
import java.util.Random;

import ru.miet.android.baev.spaceshooter.ship.SpaceShip.Model;
import android.content.Context;
import android.graphics.Paint;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;

public class Generic {
    public static Random RND = new Random();
    public static Paint paintContour;
    public static Paint paintText;
    public static Paint paintDot;
    public static float paintWidth;
    
    private static Vibrator VIBRATOR;
    private static boolean allowVibrate = true;
    public static boolean clearScene;
    public static Model startupShip;

    private static Context c;
    
    public static void Init(int DisplayHeight, Context context)
    {
    	c = context;
    	
        paintContour = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintContour.setARGB(255, 255, 255, 255);
        paintContour.setStyle(Paint.Style.STROKE);
        paintContour.setStrokeCap(Paint.Cap.ROUND);
        paintContour.setStrokeJoin(Paint.Join.ROUND);
        paintContour.setDither(true);

        paintWidth = DisplayHeight / 250f;

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setARGB(255, 255, 255, 255);
        paintText.setTextSize(paintWidth * 10);

        paintDot = new Paint();
        paintDot.setARGB(255, 255, 255, 255);
        paintDot.setStyle(Paint.Style.STROKE);
        paintDot.setStrokeCap(Paint.Cap.SQUARE);
        paintDot.setStrokeWidth(paintWidth * 1.5f);
        
        VIBRATOR = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        Sprites.Init(context);
    }

    public static byte[] getBroadcastAddress(){
	    WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    return intToByte(broadcast);
	}    
    public static byte[] getIPAddress() throws IOException{
    	WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
    	int ip = wifi.getConnectionInfo().getIpAddress();
	    return intToByte(ip);
    }    
    private static byte[] intToByte(int arg){
    	byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((arg >> k * 8) & 0xFF);
	    return quads;
    }
    
    public static void Vibrate(long arg){
    	if(allowVibrate)
    		VIBRATOR.vibrate(arg);
    }
    public static void setVibrations(boolean set){
    	allowVibrate = set;
    }
}
