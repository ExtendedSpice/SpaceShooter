package ru.miet.android.baev.spaceshooter.interfaces;

import android.graphics.Canvas;
import android.graphics.Matrix;

public interface IComponent {
	void CreateGUI();
    void PlaceAt(Matrix initMatrix);
    void Update();
    void UpdateWithRelMatrix(Matrix relMatrix);
    void Draw(Canvas canvas);
    void Dispose();
}