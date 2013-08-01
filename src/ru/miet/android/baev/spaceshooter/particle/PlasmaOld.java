package ru.miet.android.baev.spaceshooter.particle;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import ru.miet.android.baev.spaceshooter.interfaces.ICollidable;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class PlasmaOld implements IParticle,ICollidable
{
	public static int TTL = 30;
    private static float PLASMAVELOCITY = 1f;
    private static float PLASMAFRICTION = 0.99f;
    private static int DAMAGE = 300;

    private SpaceShip owner;

    private boolean alive;

    private Path contour;
    private Matrix relMatrix;

    private float velocityX;
    private float velocityY;

    private float X;
    private float Y;

    private int lifeTime; // 120 max

    public PlasmaOld(float x, float y, float directionAngle, float vX, float vY, int lifetime, SpaceShip ss)
    {
        contour = new Path();
        contour.moveTo(0, -1.5f);
        contour.lineTo(1.5f, 0);
        contour.lineTo(0, 1.5f);
        contour.lineTo(-5, 0);
        contour.close();

        owner = ss;

        X = x;
        Y = y;

        velocityX = vX + (float)Math.cos(directionAngle / 180 * Math.PI) * PLASMAVELOCITY;
        velocityY = vY + (float)Math.sin(directionAngle / 180 * Math.PI) * PLASMAVELOCITY;

        float speedScaling = (float) (Math.sqrt(velocityX * velocityX + velocityY * velocityY) / PLASMAVELOCITY);

        relMatrix = new Matrix();
        relMatrix.postScale(speedScaling * 2, 2);
        relMatrix.postRotate(directionAngle);
        relMatrix.postTranslate(x, y);
        contour.transform(relMatrix);


        lifeTime = lifetime;
        alive = true;
    }

    public void Update(float dTime)
    {
        if (alive)
        {
            velocityX *= PLASMAFRICTION;
            velocityY *= PLASMAFRICTION;

            X += velocityX * dTime;
            Y += velocityY * dTime;

            relMatrix.setTranslate(velocityX * dTime, velocityY * dTime);
            contour.transform(relMatrix);

            if (lifeTime < 0)
            {
                alive = false;
                ExplotionBig e = new ExplotionBig(X, Y, 2f * Generic.paintWidth);
                Particles.AddExplotion(e);
            }
            else
                lifeTime--;
        }
    }

    public void Draw(Canvas canvas)
    {
        if (alive)
        {
            Generic.paintContour.setAlpha(255);
            canvas.drawPath(contour, Generic.paintContour);
        }
    }
    public boolean IsAlive()
    {
        return this.alive;
    }
    public void Recycle()
    {
        //contour.Dispose();
        //relMatrix.Dispose();
    }

    public boolean IsExplodable()
    {
        return alive;
    }

    public SpaceShip GetOwner()
    {
        return owner;
    }

    public void CollideWith(SpaceShip s, boolean vibrate)
    {
        float x = s.stateContainer.PhysPoint.vectorX.x;
        float y = s.stateContainer.PhysPoint.vectorX.y;
        float d = (float) Math.sqrt((x - X) * (x - X) + (y - Y) * (y - Y));

        if (d < s.GetSize())
        {
            if (s.GetRegion().contains((int)X, (int)Y))
            {
                alive = false;
                ExplotionBig e = new ExplotionBig(X + Generic.RND.nextInt(11)-5, 
                		Y + Generic.RND.nextInt(11) - 5,
                    2f * Generic.paintWidth * (Generic.RND.nextInt(21) + 90)/100f);
                Particles.AddExplotion(e);

                s.stateContainer.Damage(DAMAGE);
                if (vibrate)
                    Generic.Vibrate(200);
            }
        }
    }
}
