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
import android.view.View;

public class MenuScene {
	private View sceneView;
    private Point display;

    private boolean isActive = false;

    //private System.Timers.Timer timerU;
    private SSTimer timer;
    private float FRAMETIME = 16;

    //private long timeTouch = 0;
    private long timeDraw = 0;
    private long timeUpdate = 0;

    public MenuScene(View contentView, Point d)
    {
        display = d;
        sceneView = contentView;

        timer = new SSTimer((long)FRAMETIME, this);
        //timerU = new System.Timers.Timer(FRAMETIME);
        //timerU.Elapsed += Update;

        GUI.SetScreen(display.x, d.y);
        GUI.Initialize();

        Camera.SetDisplay(display.x, d.y);
        Camera.AddBackground(40);
        ClearScene();
    }

    public void AddNewShip(Model newType, Color shipColor)
    {
        SSS.RemoveOldController();
        SSS.AddNewShip(newType, display.x / 2f, display.y / 2f, -90f, shipColor, true, true);
    }

    public void ClearScene()
    {
        GUI.Initialize();
        Camera.Init();
        //if(Generic.clearScene){
            SSS.Reset();
            Particles.Clear();
        //}
    }
    
    public void Start()
    {
        if (!isActive)
        {
            isActive = true;
            timer.start();
            //timerU.Start();
        }
    }
    
    public void Close(){
    	isActive = false;
    }
    
    private void Update()//object sender, System.Timers.ElapsedEventArgs e)
    {
        this.timeUpdate = SystemClock.elapsedRealtime();
        
        SSS.Update(FRAMETIME);
        Particles.Update(FRAMETIME);
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
        SSS.Draw(canvas);
        Particles.Draw(canvas); //Prts после SS - это важно UPD.Ќаверное UPD уже не важно
        Camera.DrawBackground(canvas);

        this.timeDraw = SystemClock.elapsedRealtime() - timeDraw;
    }
    
    private class SSTimer extends CountDownTimer
    {
    	MenuScene ownerScene;
        public SSTimer(long a, MenuScene owner)
        {
        	super(a,a*2);
            this.ownerScene = owner;
        }

		@Override
		public void onFinish() {

            if (ownerScene.isActive){
                this.start();
	            ownerScene.Update();
	            ownerScene.sceneView.invalidate();
            }			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			
		}
    }
}
