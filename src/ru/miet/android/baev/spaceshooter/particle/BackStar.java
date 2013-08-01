package ru.miet.android.baev.spaceshooter.particle;

import android.graphics.Canvas;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;
import ru.miet.android.baev.spaceshooter.provider.Camera;
import ru.miet.android.baev.spaceshooter.provider.Sprites;

public class BackStar implements IParticle{

	private float coordsX;
    private float coordsY;

    public BackStar()
    { }

    public void ReStar(float x, float y)
    {
        coordsX = x;
        coordsY = y;
    }

    public boolean IsAlive()
    {
        return Camera.IsVisible(coordsX, coordsY);
    }

    public void Draw(Canvas canvas)
    {
        if (Camera.IsVisible(coordsX, coordsY))
        {
            //canvas.DrawPoint(coordsX, coordsY, ProviderGeneric.paintDot);
            //canvas.drawBitmap(Generic.bitmapPoint, coordsX - 2, coordsY - 2, Generic.paintDot);
        	Sprites.Star.Draw(canvas, coordsX, coordsY);
        }
        else
        {
            Camera.RefreshBackground(this);
        }
    }

    public void Update(float dTime)
    { }

    public void Recycle()
    { }
}
