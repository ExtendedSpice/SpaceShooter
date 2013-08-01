package ru.miet.android.baev.spaceshooter;

import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Sounds;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip.Model;
import ru.miet.android.baev.spaceshooter.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ActivityMenu extends Activity implements OnClickListener {

    @SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		
		Display d = getWindowManager().getDefaultDisplay();
 		int h = d.getHeight();
 		if(h>d.getWidth())
 			h = d.getWidth();
 		Generic.Init(h, this);
 		
        Sounds.initSounds(h);
        Sounds.populateSounds(this);
 		
 		menuScene = new MenuScene(menuView, new Point((int)(d.getWidth()*0.75f),(int)(d.getHeight()*0.75f)));
 		
		menuScene.ClearScene();
		menuScene.Start();
	}
	@Override
	protected void onStop() {
		super.onStop();
		menuScene.Close();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Sounds.release();
	}

	private MenuScene menuScene;
	private ViewMenu menuView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //RequestWindowFeature(WindowFeatures.NoTitle);
        //Window.AddFlags(WindowManagerFlags.Fullscreen);
        //Window.AddFlags(WindowManagerFlags.KeepScreenOn);
        
        setContentView(R.layout.activity_menu);
        
 		menuView = new ViewMenu(this);
 		((FrameLayout)findViewById(R.id.preview_layout)).addView(menuView); 		
        
 		findViewById(R.id.button1).setOnClickListener(this);
        
        LinearLayout ll = (LinearLayout) findViewById(R.id.ship_layout);        
        for(Model m : Model.values()){
        	Button b = new Button(this);
        	b.setText(m.name());
        	b.setTag(m);
        	b.setOnClickListener(this);
        	ll.addView(b);
        }
    }    


	@Override
	public void onClick(View v) {
		// TODO Launching game activity
		if(v.getTag() == null){
			if(Generic.startupShip!= null){
				menuScene.Close();
				Generic.setVibrations(((ToggleButton)findViewById(R.id.toggleButton1)).isChecked());
				Sounds.setSounds(((ToggleButton)findViewById(R.id.toggleButton2)).isChecked());
				//Generic.clearScene = ((ToggleButton)findViewById(R.id.toggleButton2)).isChecked();
				startActivity(new Intent(this,ActivityGame.class));	
			}
		}
		else {
			Generic.startupShip = (Model) v.getTag();
			menuScene.AddNewShip(Generic.startupShip, new Color());
		}
	}
	
	private class ViewMenu extends View{

		public ViewMenu(Context context) {
			super(context);
			this.setBackgroundColor(Color.BLACK);
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			menuScene.Draw(canvas);
		}	
	}
}
