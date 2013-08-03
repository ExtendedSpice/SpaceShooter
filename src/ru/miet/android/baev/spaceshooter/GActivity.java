package ru.miet.android.baev.spaceshooter;

import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Sounds;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip.Model;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class GActivity extends Activity implements OnClickListener{	

	private GScene gScene;
	private GView gView;
	private boolean gameStage = false; // True for game stage; false for menu stage 
	private int displayH;
	private int displayW;
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                
        Display d = getWindowManager().getDefaultDisplay();
        displayH = d.getHeight();
        displayW = d.getWidth();
        if( displayH > displayW){
        	int t = displayH;
        	displayH = displayW;
        	displayW = t;
        }
        
        Generic.Init(displayH, this); 		
        Sounds.initSounds(displayH);
        
        startAsMenu();
	}
	
	private void startAsMenu(){
		Sounds.release();
		Sounds.populateSounds(this);
		
		gameStage = false;
		
		setContentView(R.layout.activity_menu);
        
 		gView = new GView(this);
 		((FrameLayout)findViewById(R.id.preview_layout)).addView(gView); 		
        
 		findViewById(R.id.button1).setOnClickListener(this);
        
        LinearLayout ll = (LinearLayout) findViewById(R.id.ship_layout);        
        for(Model m : Model.values()){
        	Button b = new Button(this);
        	b.setText(m.name());
        	b.setTag(m);
        	b.setOnClickListener(this);
        	ll.addView(b);
        }        
        gScene = new GScene(gView, new Point((int)(displayW*0.75f), (int)(displayH*0.75f)));
        
        gScene.SetState(true, true);
	}
	private void startAsGame(){
		Sounds.release();
		Sounds.populateSounds(this);
		
		gameStage = true;
		gView = new GView(this);
        gScene = new GScene(gView, new Point(displayW, displayH));
        
        gView.setOnTouchListener(gScene);        
        setContentView(gView);
        
        gScene.SeedForGamePlay();
        gScene.SetState(true, true);
	}

	// Input control methods
	@Override
	public void onClick(View v) {
		if(v.getTag() == null){
			if(Generic.startupShip!= null){
				gScene.SetState(false, false);
				Generic.setVibrations(((ToggleButton)findViewById(R.id.toggleButton1)).isChecked());
				Sounds.setSounds(((ToggleButton)findViewById(R.id.toggleButton2)).isChecked());
				
				gScene.ClearScene();
				startAsGame();
			}
		}
		else {
			Generic.startupShip = (Model) v.getTag();
			gScene.AddNewShip(Generic.startupShip, new Color(), true, true);
		}	
	}
	@Override
	public void onBackPressed() {
		if(gameStage)
		{
			// implement 'return to menu' code here - DONE
			gScene.SetState(false, false);
			gScene.ClearScene();
			startAsMenu();
		}
		else
		{
			// just exiting activity
			super.onBackPressed();
		}
	}
		
	// State control methods
	@Override
	protected void onResume() {
		super.onResume();
		gScene.SetState(true, true);
		Sounds.populateSounds(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(gameStage)
			gScene.SetState(true, false);
		else
			gScene.SetState(false, false);
		Sounds.release();
	}	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		gScene.SetState(false,false);
		Sounds.release();
	}	
	
	// View class
	private class GView extends View {
		public GView(Context context) {
			super(context);
			this.setBackgroundColor(Color.BLACK);
		}
		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			gScene.Draw(canvas);
		}	
	}
}
