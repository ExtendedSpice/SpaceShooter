package ru.miet.android.baev.spaceshooter.particle;

import android.graphics.Canvas;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Sprites;

public class EngineDust implements IParticle {
	
	public static int TTL = 10;
	private static float FRICTION = 0.9f;
	
    private boolean alive = true;

    private float coordsX;
    private float coordsY;

    private float velocityX;
    private float velocityY;

    private int lifeTime; // 120 max

    public EngineDust(float x, float y, float directionAngle, float startVelocity, float vX, float vY, int lifetime)
    {
        coordsX = x;
        coordsY = y;

        velocityX = vX + (float)Math.cos(directionAngle / 180 * Math.PI) * startVelocity;
        velocityY = vY + (float)Math.sin(directionAngle / 180 * Math.PI) * startVelocity;

        lifeTime = lifetime;

        alive = true;
    }

    public void Update(float dTime)
    {
        if (alive)
        {
            /* ћы и так работаем в производных, лолка :|
             
            float k = 1020 / (3060 - 9 * lifeTime) - 1 / 3;

            coordsX += deltaX * k;
            coordsY += deltaY * k;
             */
            velocityX *= FRICTION;
            velocityY *= FRICTION;

            coordsX += velocityX;
            coordsY += velocityY;

            if (lifeTime == 0)
                alive = false;
            else
                lifeTime--;
        }
    }

    public void Draw(Canvas canvas)
    {
        if (alive)
        {
            Generic.paintDot.setAlpha((int)(lifeTime * 255f / TTL));
            //canvas.DrawPoint(coordsX, coordsY, ProviderGeneric.paintDot);
            //canvas.drawBitmap(Generic.bitmapPoint, coordsX - 2, coordsY - 2, Generic.paintDot);
            Sprites.Star.Draw(canvas, coordsX, coordsY, Generic.paintDot);
        }
    }

    public boolean IsAlive()
    {
        return this.alive;
    }
    public void Recycle()
    { }

}
