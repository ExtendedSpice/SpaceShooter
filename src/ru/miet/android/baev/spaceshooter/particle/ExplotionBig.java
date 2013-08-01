package ru.miet.android.baev.spaceshooter.particle;

import ru.miet.android.baev.spaceshooter.provider.Camera;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Sounds;
import ru.miet.android.baev.spaceshooter.provider.Sprites;
import ru.miet.android.baev.spaceshooter.provider.Sounds.Effect;
import android.graphics.Canvas;
import android.graphics.PointF;

public class ExplotionBig extends Explotion{
	
	private enum State { Big, Small, Main }
    private static float TTL = 60;
    private static float FADEStart = 50;
    private static int STATELENGTH = 3;

    private State state;

    private int stateFrame;
    private int frame;
    private float initR;
    private float radius;
    
    private float SCALE;

    private float iX;
    private float iY;

    public ExplotionBig(float x, float y, float r)
    {
        initR = r;
        radius = initR;
        iX = x;
        iY = y;

        frame = 0;
        stateFrame = 0;
        state = State.Big;

        isAlife = true;
        
        PointF soundInfo = Camera.calcSoundInfo(x, y, 0, 0);
        Sounds.playEffect(Effect.ExplPlasma, soundInfo.x, soundInfo.y);
    }

    public void Update(float dTime)
    {
        if (isAlife)
        {
            if (frame >= TTL)
                isAlife = false;
            else
            {
                float s;
                switch (state)
                {
                    case Big:
                        s = 1;
                        if (stateFrame > STATELENGTH)
                        {
                            state = State.Small;
                            stateFrame = 0;
                        }
                        break;
                    case Small:
                        s = 0.6f;
                        if (stateFrame > STATELENGTH)
                        {
                            state = State.Main;
                            stateFrame = 0;
                        }
                        break;
                    default:
                        s = (radius + initR * (TTL - frame) / TTL / 2) / radius;
                        break;
                }
                radius *= s;
                
                SCALE = radius/initR/textureScale;

                stateFrame++;
                frame++;
            }
        }
    }

    public void Draw(Canvas canvas)
    {
        if (isAlife)
        {
            float alpha = 255;
            if (frame > FADEStart)
            {
                alpha = 255 * (TTL - frame) / (TTL - FADEStart);
            }

            Generic.paintContour.setAlpha((int)alpha);
            Sprites.Explotion.DrawScale(canvas, iX, iY, SCALE);
        }
    }
}
