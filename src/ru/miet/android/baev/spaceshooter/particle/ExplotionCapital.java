package ru.miet.android.baev.spaceshooter.particle;

import ru.miet.android.baev.spaceshooter.provider.Camera;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Sounds;
import ru.miet.android.baev.spaceshooter.provider.Sprites;
import ru.miet.android.baev.spaceshooter.provider.Sounds.Effect;
import android.graphics.Canvas;
import android.graphics.PointF;

public class ExplotionCapital extends Explotion {
	
	private static float TTL = 80;
    private static float FADEStart = 70;

    private int frame;
    private float initR;
    private float radius;
    
    private float SCALE;

    private float iX;
    private float iY;

    public ExplotionCapital(float x, float y, float r)
    {
        initR = r;
        radius = r;
        iX = x;
        iY = y;

        frame = 0;

        isAlife = true;
        PointF soundInfo = Camera.calcSoundInfo(x, y, 0, 0);
        Sounds.playEffect(Effect.ExplCapital, soundInfo.x, soundInfo.y);
    }

    public void Update(float dTime)
    {
        if (isAlife)
        {
            if (frame >= TTL)
                isAlife = false;
            else
            {
                float s = (radius + initR * (TTL - frame) / TTL) / radius;
                radius *= s;

                SCALE = radius/initR/textureScale;

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
