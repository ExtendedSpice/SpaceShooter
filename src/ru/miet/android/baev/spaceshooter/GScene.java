package ru.miet.android.baev.spaceshooter;

import ru.miet.android.baev.spaceshooter.provider.Camera;
import ru.miet.android.baev.spaceshooter.provider.GUI;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.provider.SSS;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip.Model;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

public class GScene implements View.OnTouchListener{

	private View sceneView;
    private Point display;

    private boolean continueRedrawing = false;
    private boolean continueUpdating = false;

    private SSTimer timer;
    private float FRAMETIME = 16;

    private long timeDraw = 0;
    private long timeUpdate = 0;

    public GScene(View contentView, Point d)
    {
        display = d;
        sceneView = contentView;

        timer = new SSTimer((long)FRAMETIME, this);

        GUI.SetScreen(display.x, d.y);
        GUI.Initialize();

        Camera.SetDisplay(display.x, d.y);
        Camera.AddBackground(40);
        
        ClearScene();
    }

    public void SeedForGamePlay(){
    	// Dummy-controlled ships
    	SSS.AddNewShip(Generic.startupShip, 
    			Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(361), 
    			new Color(), true, true); //false, true); момент пока закостылен
    	
    	// Player-controlled
    	SSS.AddNewShip(Generic.startupShip, 0, 0, -90f, new Color(), true, false);
    }
    
    public void AddNewShip(Model newType, Color shipColor, boolean isDummy, boolean isGUILinked)
    {
        SSS.AddNewShip(newType, 0, 0, -90f, shipColor, isGUILinked, isDummy);        
    }

    public void ClearScene()
    {
    	//timer.cancel();
    	
        SSS.Reset();
        Particles.Clear();
        GUI.Initialize();
        Camera.Init();
    }
    
    public void SetState(boolean active, boolean shown){
    	if(!continueUpdating && active)
            timer.start();
    	
    	continueUpdating = active;
    	continueRedrawing = shown;
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
			GUI.HandleTouchEvent(event);
		return true;
	}		

    private void Update()//object sender, System.Timers.ElapsedEventArgs e)
    {
        this.timeUpdate = SystemClock.elapsedRealtime();
        
        SSS.Update(FRAMETIME);
        Particles.Update(FRAMETIME);

        SSS.CheckCollisions(Particles.GetShellObjects());
        SSS.UpdateWithRelMatrix();
        
        this.timeUpdate = SystemClock.elapsedRealtime() - timeUpdate;
    }

    public void Draw(Canvas canvas)
    {    
        float lineY = Generic.paintText.getTextSize() + 5;
        float lineX = GUI.GetCellSize() + 5;
        canvas.drawText(String.format("%s %d %d", GUI.GetControlMode() ? "F" : "N", timeUpdate, timeDraw), lineX, lineY, Generic.paintText);


        if (SSS.controlledShip != null)
            SSS.controlledShip.stateContainer.PrintState(canvas, lineX, lineY);

        this.timeDraw = SystemClock.elapsedRealtime();

        GUI.Draw(canvas);
        canvas.translate(-Camera.viewX , -Camera.viewY );
        
        Camera.DrawBackground(canvas);
        SSS.Draw(canvas);
        Particles.Draw(canvas); //Prts после SS - это важно UPD.Наверное UPD уже не важно
        Camera.DrawNavArrows(canvas);

        this.timeDraw = SystemClock.elapsedRealtime() - timeDraw;
    }
    
    private class SSTimer extends CountDownTimer
    {
    	GScene ownerScene;
        public SSTimer(long a, GScene owner)
        {
        	super(a,a*2);
            this.ownerScene = owner;
        }

		@Override
		public void onFinish() {
            if (ownerScene.continueUpdating){
                this.start();
                try{
	            ownerScene.Update();
                }
                catch(NullPointerException e){
                	// ололо исключение, в котором разобраться не позволяет кривой отладчик эклипс
                }
                
	            if (ownerScene.continueRedrawing)
	            	ownerScene.sceneView.invalidate();
            }			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			
		}
    }
}
