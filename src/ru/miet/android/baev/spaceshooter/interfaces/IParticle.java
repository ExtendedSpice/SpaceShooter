package ru.miet.android.baev.spaceshooter.interfaces;

import android.graphics.Canvas;

public interface IParticle {
    boolean IsAlive();
    void Draw(Canvas canvas);
    void Update(float dTime);
    void Recycle();
}
