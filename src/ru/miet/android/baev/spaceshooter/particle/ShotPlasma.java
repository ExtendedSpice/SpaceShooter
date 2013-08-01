package ru.miet.android.baev.spaceshooter.particle;

import android.graphics.Canvas;
import ru.miet.android.baev.spaceshooter.interfaces.ICollidable;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class ShotPlasma implements IParticle,ICollidable
{
	public static int TTL = 30;
    private static float PLASMAVELOCITY = 1f;
    private static float PLASMAFRICTION = 0.99f;
    private static float PLASMASIZE = 2;
    private static int DAMAGE = 300;

    private SpaceShip owner;

    private boolean isAlive;

    private float coordsX;
    private float coordsY;

    private float coordsXold;
    private float coordsYold;

    private float velocityX;
    private float velocityY;

    private int lifeTime; // 120 max UPD. TTL max

    public ShotPlasma(float x, float y, float directionAngle, float vX, float vY, int lifetime, SpaceShip ss)
    {
        coordsX = x;
        coordsY = y;

        owner = ss;

        coordsXold = x;
        coordsYold = y;

        velocityX = vX + (float)Math.cos(directionAngle / 180 * Math.PI) * PLASMAVELOCITY;
        velocityY = vY + (float)Math.sin(directionAngle / 180 * Math.PI) * PLASMAVELOCITY;

        lifeTime = lifetime;

        isAlive = true;
    }

    public void Update(float dTime)
    {
        if (isAlive)
        {
            coordsXold = coordsX;
            coordsYold = coordsY;

            coordsX = coordsXold + velocityX * dTime;
            coordsY = coordsYold + velocityY * dTime;
            
            velocityX *= PLASMAFRICTION;
            velocityY *= PLASMAFRICTION;

            if (lifeTime < 0)
            {
                isAlive = false;
                ExplotionBig e = new ExplotionBig(coordsX, coordsY, 4f * Generic.paintWidth);
                Particles.AddExplotion(e);
            }
            else
                lifeTime--;
        }
    }

    public void Draw(Canvas canvas)
    {
        if (isAlive)
        {
        	Generic.paintContour.setStrokeWidth(Generic.paintWidth*PLASMASIZE);
            canvas.drawLine(coordsXold, coordsYold, coordsX, coordsY, Generic.paintContour);
        }
    }

    public boolean IsAlive()
    {
        return this.isAlive;
    }
    public void Recycle()
    { }


    public boolean IsExplodable()
    {
        return isAlive;
    }

    public SpaceShip GetOwner()
    {
        return owner;
    }

    public void CollideWith(SpaceShip s, boolean vibrate)
    {
        float x = s.stateContainer.PhysPoint.vectorX.x;
        float y = s.stateContainer.PhysPoint.vectorX.y;
        float d = (float) Math.sqrt((x - coordsX) * (x - coordsX) + (y - coordsY) * (y - coordsY));

        if (d < s.GetSize())
        {
            if (s.GetRegion().contains((int)coordsX, (int)coordsY))
            {
                isAlive = false;
                ExplotionBig e = new ExplotionBig(coordsX + Generic.RND.nextInt(11)-5, 
                		coordsY + Generic.RND.nextInt(11) - 5,
                    5f * Generic.paintWidth * (Generic.RND.nextInt(21) + 90)/100f);
                Particles.AddExplotion(e);
                s.stateContainer.Damage(DAMAGE);
                if (vibrate)
                    Generic.Vibrate(200);
            }
        }
    }
}
