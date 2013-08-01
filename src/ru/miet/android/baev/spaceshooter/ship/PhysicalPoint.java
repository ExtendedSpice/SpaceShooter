package ru.miet.android.baev.spaceshooter.ship;

import android.graphics.Matrix;
import android.graphics.PointF;

public class PhysicalPoint {
	public static float FRICTIONL = 0.96f;
    public static float FRICTIONA = 0.7f;


    public PointF vectorX = new PointF();
    public PointF vectorV = new PointF();
    public PointF vectorA = new PointF();

    public float angularX = 0;
    public float angularV = 0;
    public float angularA = 0;

    public float deltaX = 0;
    public float deltaY = 0;
    public float deltaA = 0;

    public void ToForce(float x, float y, float a)
    {
        // œÂÂ‰‡‚‡Ú¸ “ŒÀ‹ Œ —»À€!!!!
        vectorA.x += x;
        vectorA.y += y;
        angularA += a;
    }

    public void EndForce(float t, float mass, float inertion)
    {
        float linearInv = t / mass;
        float angularInv = t / inertion;

        vectorV.x = (vectorV.x + vectorA.x * linearInv) * FRICTIONL;
        vectorV.y = (vectorV.y + vectorA.y * linearInv) * FRICTIONL;

        deltaX = vectorV.x * t;
        deltaY = vectorV.y * t;

        vectorX.x += deltaX;
        vectorX.y += deltaY;

        angularV = (angularV + angularA * angularInv) * FRICTIONA;
        deltaA = angularV * t;
        angularX += deltaA;

        vectorA.x = 0;
        vectorA.y = 0;
        angularA = 0;
    }

    public void UpdateRelativeMatrix(Matrix oldOne)
    {
        // call this only from Draw-methods
        oldOne.setTranslate(deltaX, deltaY);
        oldOne.postRotate(deltaA, vectorX.x, vectorX.y);

        deltaX = 0;
        deltaY = 0;
        deltaA = 0;
    }
}
