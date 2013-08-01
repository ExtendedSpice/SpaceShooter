package ru.miet.android.baev.spaceshooter.particle;

import android.graphics.Canvas;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;

public abstract class Explotion implements IParticle{
	
	protected boolean isAlife;
	protected float textureScale = 50;

    public boolean IsAlive()
    {
        return isAlife;
    }

    public abstract void  Draw(Canvas canvas);

    public abstract void Update(float dTime);

    public void Recycle()
    {    }
}
