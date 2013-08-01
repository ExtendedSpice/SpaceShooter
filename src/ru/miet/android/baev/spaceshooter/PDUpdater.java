package ru.miet.android.baev.spaceshooter;

import android.app.Activity;
import android.app.ProgressDialog;


public class PDUpdater implements Runnable{
	private ProgressDialog pd;
	private Activity o;
	private CharSequence message;
	
	public PDUpdater(ProgressDialog dialog, Activity owner){
		pd = dialog;
		o = owner;
		message = null;
	}
	
	public void UpdateMessage(CharSequence s){
		message = s;
		o.runOnUiThread(this);
	}
	
	@Override
	public void run() {
		if(message!=null)
			pd.setMessage(message);	
		else
			pd.dismiss();
	}	
}
