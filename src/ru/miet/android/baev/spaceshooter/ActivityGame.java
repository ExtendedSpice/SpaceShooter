package ru.miet.android.baev.spaceshooter;

import ru.miet.android.baev.spaceshooter.provider.Sounds;
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

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ActivityGame extends Activity {

	@Override
	protected void onResume() {
		super.onResume();
		gameScene.Start();
		Sounds.populateSounds(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		gameScene.SetState(true, false);
		Sounds.release();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		//GameClient.Close();
		//Sounds.release();
		gameScene.SetState(false,false);
	}

	private ViewGame gameView;
	private GameScene gameScene;
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        Display d = getWindowManager().getDefaultDisplay();
        gameView = new ViewGame(this);
        gameScene = new GameScene(gameView, new Point(d.getWidth(),d.getHeight()));
        
        gameView.setOnTouchListener(gameScene);        
        setContentView(gameView);
    }
	
	private class ViewGame extends View {

		public ViewGame(Context context) {
			super(context);
			this.setBackgroundColor(Color.BLACK);
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			gameScene.Draw(canvas);
		}	
	}
}
