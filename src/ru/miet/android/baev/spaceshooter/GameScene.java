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

public class GameScene implements View.OnTouchListener{

	private View sceneView;
    private Point display;

    private boolean isShown = false;
    private boolean isActive = false;

    //private System.Timers.Timer timerU;
    private SSTimer timer;
    private float FRAMETIME = 16;

    //private long timeTouch = 0;
    private long timeDraw = 0;
    private long timeUpdate = 0;

    public GameScene(View contentView, Point d)
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
        
        //bots
        SSS.AddNewShip(Generic.startupShip, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(361), new Color(), true, true);
        //SSS.AddNewShip(Model.Sharpflyer, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(361), new Color(), true, true);
        //SSS.AddNewShip(Model.Dalekhead, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(361), new Color(), true, true);
        //SSS.AddNewShip(Model.Supermeatship, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(1001)-500, Generic.RND.nextInt(361), new Color(), true, true);
        

        AddNewShip(Generic.startupShip, new Color());
        //Start();
    }

    public void AddNewShip(Model newType, Color shipColor)
    {
        //if (!allowMultipleShips)
        //    SSS.RemoveOldController();

        SSS.AddNewShip(newType, 0, 0, -90f, shipColor, true, false);        
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
        isShown = true;
    }
    
    public void SetState(boolean active, boolean shown){
    	isActive = active;
    	isShown = shown;
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
        Particles.Draw(canvas); //Prts после SS - это важно UPD.Ќаверное UPD уже не важно
        Camera.DrawNavArrows(canvas);

        this.timeDraw = SystemClock.elapsedRealtime() - timeDraw;
    }
    
    private class SSTimer extends CountDownTimer
    {
    	GameScene ownerScene;
        public SSTimer(long a, GameScene owner)
        {
        	super(a,a*2);
            this.ownerScene = owner;
        }

		@Override
		public void onFinish() {

            if (ownerScene.isActive){
                this.start();
	            ownerScene.Update();
	            if (ownerScene.isShown)
	            	ownerScene.sceneView.invalidate();
            }			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			
		}
    }
}
