package ru.miet.android.baev.spaceshooter.components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import ru.miet.android.baev.spaceshooter.interfaces.IComponent;
import ru.miet.android.baev.spaceshooter.particle.BeamOld;
import ru.miet.android.baev.spaceshooter.provider.GUI;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.provider.GUI.ButtonType;
import ru.miet.android.baev.spaceshooter.ship.ControlUnit;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class BeamerOld implements IComponent{
	private SpaceShip carrier;
    private ControlUnit modelControlUnit;

    private Path contourOrigin;
    private Path contourRelative;

    private Path contourMuzzle1Origin;
    private Path contourMuzzle1Relative;
    private Path contourMuzzle2Origin;
    private Path contourMuzzle2Relative;

    BeamOld beam;

    public BeamerOld(float x, float y, float a, float scale, SpaceShip ss)
    {
        carrier = ss;

        contourOrigin = new Path();
        contourMuzzle1Origin = new Path();
        contourMuzzle2Origin = new Path();

        {
            contourOrigin.addArc(new RectF(-2, -2.5f, 2, 2.5f), 28.615f, 302.77f);

            contourOrigin.moveTo(0, -1);
            contourOrigin.lineTo(3, -1);

            contourOrigin.moveTo(0, 1);
            contourOrigin.lineTo(3, 1);

            contourOrigin.addArc(new RectF(-1, -1, 1, 1), 90, 180);
            contourOrigin.addArc(new RectF(2.59f, -1.41f, 5.41f, 1.41f), 135, 90);
        }

        // стрелять из центра дуги до ее концов
        contourMuzzle1Origin.moveTo(2.59f, 0);
        contourMuzzle1Origin.lineTo(4, 0);

        contourMuzzle2Origin.moveTo(3, -1);
        contourMuzzle2Origin.lineTo(3, 1);


        Matrix mx = new Matrix();
        mx.setScale(scale, scale);
        mx.postRotate(a);
        mx.postTranslate(x, y);

        contourOrigin.transform(mx);
        contourMuzzle1Origin.transform(mx);
        contourMuzzle2Origin.transform(mx);

        //mx.Dispose();

        contourRelative = new Path(contourOrigin);
        contourMuzzle1Relative = new Path(contourMuzzle1Origin);
        contourMuzzle2Relative = new Path(contourMuzzle2Origin);
    }

    public void PlaceAt(Matrix initMatrix)
    {
        contourRelative.transform(initMatrix);
        contourMuzzle1Relative.transform(initMatrix);
        contourMuzzle2Relative.transform(initMatrix);

        beam = new BeamOld(contourMuzzle1Relative, contourMuzzle2Relative, 400, carrier);
        Particles.AddShell(beam);
    }
    public void CreateGUI()
    {
        modelControlUnit = new ControlUnit();
        carrier.stateContainer.ControlBlock.add(modelControlUnit);
        GUI.AddButton(modelControlUnit, ButtonType.DefaultButton);
    }

    public void Draw(Canvas canvas)
    {
        canvas.drawPath(contourRelative, Generic.paintContour);
    }

    public void UpdateWithRelMatrix(Matrix relMatrix)
    {
        contourRelative.transform(relMatrix);
        beam.UpdateWithRelMatrix(relMatrix);
    }

    public void Update()
    {
        beam.SetFiringMode(modelControlUnit.GetState());
    }

    public void Dispose()
    {
    	/*
        contourMuzzle1Origin.Dispose();
        contourMuzzle1Relative.Dispose();
        contourMuzzle2Origin.Dispose();
        contourMuzzle2Relative.Dispose();

        contourOrigin.Dispose();
        contourRelative.Dispose();
        */

        beam.Recycle();
    }
}
