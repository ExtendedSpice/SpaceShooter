package ru.miet.android.baev.spaceshooter.particle;

import android.graphics.Canvas;
import android.graphics.Region;
import ru.miet.android.baev.spaceshooter.interfaces.ICollidable;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class ShotBeam implements ICollidable,IParticle {
	
	public static int TTL = 10;
    private static float BVELOCITY = 4f;
    private static int DAMAGE = 20;
    private static int SEGMENTSNUMBER = 20;

    private boolean isAlive;

    private SpaceShip owner;

    private float coordsX;
    private float coordsY;

    private float coordsXold;
    private float coordsYold;

    private float velocityX;
    private float velocityY;

    private int lifeTime; // 120 max UPD. TTL max

    public ShotBeam(float x, float y, float directionAngle, SpaceShip ss)
    {
        coordsX = x;
        coordsY = y;

        owner = ss;

        coordsXold = x;
        coordsYold = y;

        velocityX = (float)Math.cos(directionAngle / 180 * Math.PI) * BVELOCITY;
        velocityY = (float)Math.sin(directionAngle / 180 * Math.PI) * BVELOCITY;

        lifeTime = TTL;

        isAlive = true;
    }

    public void Update(float dTime)
    {
        if (isAlive)
        {            
            if (lifeTime > 0){
            	coordsX +=velocityX * dTime;
            	coordsY +=velocityY * dTime;            	
            }
            else if(lifeTime > -TTL){
            	coordsXold +=velocityX * dTime;
            	coordsYold +=velocityY * dTime;            
            }
            else
                isAlive = false;
            lifeTime--;
        }
    }

    public void Draw(Canvas canvas)
    {
        if (isAlive)
        {
            Generic.paintContour.setStrokeWidth(Generic.paintWidth * (0.2f + (TTL - Math.abs(lifeTime))/(float)TTL));
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
    	float x0;
        float y0;
        float dx;
        float dy;
        float d;
        float A;
        float B;
        float C;

        x0 = coordsXold;
        y0 = coordsYold;
        dx = coordsX - x0;
        dy = coordsY - y0;
        d = (float) Math.sqrt(dx * dx + dy * dy);

        A = dy / d;
        B = -dx / d;
        C = -A * x0 - B * y0;

        d = Math.abs(s.stateContainer.PhysPoint.vectorX.x * A + B * s.stateContainer.PhysPoint.vectorX.y + C);
        if (d < s.GetSize())
        {
            float param = 0;
            Region r = s.GetRegion();

            for (int i = 0; i <= SEGMENTSNUMBER; i++)
            {
                param = (float)i / SEGMENTSNUMBER;
                A = x0 + param * dx;
                B = y0 + param * dy;

                if (r.contains((int)A, (int)B))
                {
                	ExplotionSmall e = new ExplotionSmall(A + Generic.RND.nextInt(11) - 5, 
                    		B + Generic.RND.nextInt(11) - 5,
                        .2f * Generic.paintWidth * (Generic.RND.nextInt(21)+90)/100f);
                    Particles.AddExplotion(e);

                    s.stateContainer.Damage(DAMAGE);

                    if (vibrate)
                    {
                        Generic.Vibrate(30);
                    }
                    break;
                }
            }
        }
    }
}
