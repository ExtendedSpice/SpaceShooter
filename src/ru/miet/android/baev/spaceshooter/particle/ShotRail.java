package ru.miet.android.baev.spaceshooter.particle;

import android.graphics.Canvas;
import ru.miet.android.baev.spaceshooter.interfaces.ICollidable;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class ShotRail implements ICollidable,IParticle {
	
	public static int TTL = 40;//100
    private static int TTLfade = 3;
    private static float BVELOCITY = .4f;
    private static int DAMAGE = 50;

    private boolean isAlive;

    private SpaceShip owner;

    private float coordsX;
    private float coordsY;

    private float coordsXold;
    private float coordsYold;

    private float velocityX;
    private float velocityY;

    private int lifeTime; // 120 max UPD. TTL max

    public ShotRail(float x, float y, float directionAngle, float vX, float vY, int lifetime, SpaceShip ss)
    {
        coordsX = x;
        coordsY = y;

        owner = ss;

        coordsXold = x;
        coordsYold = y;

        velocityX = vX + (float)Math.cos(directionAngle / 180 * Math.PI) * BVELOCITY;
        velocityY = vY + (float)Math.sin(directionAngle / 180 * Math.PI) * BVELOCITY;

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

            if (lifeTime <= -TTLfade)
                isAlive = false;
            else
                lifeTime--;
        }
    }

    public void Draw(Canvas canvas)
    {
        if (isAlive)
        {
            int alpha = 255;
            if (lifeTime < 0)
                alpha = (TTLfade + lifeTime) * 255 / TTLfade;

            Generic.paintContour.setAlpha(alpha);
            Generic.paintContour.setStrokeWidth(Generic.paintWidth);
            canvas.drawLine(coordsXold, coordsYold, coordsX, coordsY, Generic.paintContour);

            Generic.paintContour.setAlpha(255);
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
                ExplotionSmall e = new ExplotionSmall(coordsX + Generic.RND.nextInt(11)-5, 
                		coordsY + Generic.RND.nextInt(11) - 5,
                		.1f * Generic.paintWidth * (Generic.RND.nextInt(21) + 90)/100f);
                Particles.AddExplotion(e);

                s.stateContainer.Damage(DAMAGE);
                if (vibrate)
                    Generic.Vibrate(30);
            }
        }
    }
}
