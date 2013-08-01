package ru.miet.android.baev.spaceshooter.ship;

import java.util.ArrayList;

import ru.miet.android.baev.spaceshooter.particle.ExplotionCapital;
import ru.miet.android.baev.spaceshooter.particle.ExplotionSmall;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.provider.SSS;

import android.graphics.Canvas;
import android.graphics.Color;

public class StateContainer {
	public enum State {Alive, Exploding, Dead}
	
	public State GenState;
	public String GenName;
	public Color GenColor;
	public int GenHitPoints;
	public float PhysSize;
	public float PhysMass;
	public float PhysInertion;
	public PhysicalPoint PhysPoint = new PhysicalPoint();
	
	public ArrayList<ControlUnit> ControlBlock = new ArrayList<ControlUnit>();

    public Reactor PhysReactor;

    private int explodingFrames;

    public StateContainer(SpaceShip.Model type, Color color)
    {
        GenState = State.Alive;
        GenColor = color;

        switch (type)
        {
            case Sharpflyer:
            	GenHitPoints = 400;
                PhysMass = 4000;
                PhysInertion = 900;
                PhysReactor = new Reactor(50, 0.5f, this);
                    PhysSize = 8;
                break;

            case Dalekhead:
            	GenHitPoints = 1000;
                PhysMass = 10000;
                PhysInertion = 2000;
                PhysReactor = new Reactor(150, 0.6f, this);
                PhysSize = 4;
                break;

            case Supermeatship:
            	GenHitPoints = 1500;
                PhysMass = 15000;
                PhysInertion = 1500;
                PhysReactor = new Reactor(150, 0.7f, this);
                PhysSize = 3;
                break;
        }
    }

    public void Damage(int dmg)
    {
    	GenHitPoints -= dmg;

        if ((GenHitPoints < 0) && (GenState == State.Alive))
        {
        	GenHitPoints = 0;
            GenState = State.Exploding;
            explodingFrames = Generic.RND.nextInt(80) + 120;
        }
    }

    public void PrintState(Canvas canvas, float lineX, float lineY)
    {
        lineY += Generic.paintText.getTextSize() * 1.1f;
        canvas.drawText(String.format("Internal: %d", GenHitPoints), lineX, lineY, Generic.paintText);
                
        switch (SSS.controlledShip.stateContainer.PhysReactor.state)
        {
            case Working:            	
                Generic.paintText.setColor(Color.CYAN);
                break;
            case Overloaded:
                Generic.paintText.setColor(Color.YELLOW);
                break;
        }
        lineY += Generic.paintText.getTextSize() * 1.1f;
        canvas.drawText(String.format("Reactor: %d%%", (int)(100f * PhysReactor.cap/PhysReactor.capMax)), lineX, lineY, Generic.paintText);
        Generic.paintText.setColor(Color.WHITE);
    }

    public boolean Explode()
    {
        if (explodingFrames == 0)
            return true;
        else
        {
            PhysReactor.cap = 0;
            PhysReactor.capMax = 0;
            PhysReactor.power = 0;
            if (GenState == State.Exploding)
            {
                if (explodingFrames > 30)
                {
                    if (Generic.RND.nextInt(101) > 80)
                    {
                        double angle = (float)(Generic.RND.nextDouble() * 2 * Math.PI);
                        float r = PhysSize * (float)Generic.RND.nextDouble();
                        float x = this.PhysPoint.vectorX.x + r * (float)Math.cos(angle);
                        float y = this.PhysPoint.vectorX.y + r * (float)Math.sin(angle);

                        Particles.AddExplotion(new ExplotionSmall(x, y, .4f * Generic.paintWidth));
                    }
                }
                else
                {
                    Particles.AddExplotion(new ExplotionCapital(this.PhysPoint.vectorX.x, this.PhysPoint.vectorX.y, 0.8f*Generic.paintWidth));
                    GenState = State.Dead;
                }
            }
            explodingFrames--;
        }
        return false;
    }
}
