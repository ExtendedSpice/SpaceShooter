package ru.miet.android.baev.spaceshooter.components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import ru.miet.android.baev.spaceshooter.interfaces.IComponent;
import ru.miet.android.baev.spaceshooter.particle.ShotPlasma;
import ru.miet.android.baev.spaceshooter.provider.Camera;
import ru.miet.android.baev.spaceshooter.provider.GUI;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.provider.GUI.ButtonType;
import ru.miet.android.baev.spaceshooter.provider.Sounds;
import ru.miet.android.baev.spaceshooter.provider.Sounds.Effect;
import ru.miet.android.baev.spaceshooter.ship.ControlUnit;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class PlasmaGun implements IComponent {
	private static float powerConsumption = 30;

    private SpaceShip carrier;
    private ControlUnit modelControlUnit;

    private Path contourOrigin;
    private Path contourRelative;
    private Path contourMuzzleOrigin;
    private Path contourMuzzleRelative;

    private int rateOfFire;
    private int noFiringFrameCount;

    private PathMeasure pathMeasure = new PathMeasure();
    private float[] coords = { 0, 0 };

    public PlasmaGun(float x, float y, float a, float scale, int framesPerShot, SpaceShip ss)
    {
        carrier = ss;

        rateOfFire = framesPerShot;

        contourOrigin = new Path();
        contourMuzzleOrigin = new Path();

        {
            contourOrigin.moveTo(3, -2);
            contourOrigin.lineTo(3, -4);
            contourOrigin.lineTo(-1, -4);
            contourOrigin.lineTo(-3, -2);
            contourOrigin.lineTo(-3, 2);
            contourOrigin.lineTo(-1, 4);
            contourOrigin.lineTo(3, 4);
            contourOrigin.lineTo(3, 2f);

            contourOrigin.addRect(0, -2f, 5, 2f, Path.Direction.CW);
        }

        // стрелять из начала
        contourMuzzleOrigin.moveTo(5, 0);
        contourMuzzleOrigin.lineTo(0, 0);

        Matrix mx = new Matrix();
        mx.setScale(scale, scale);
        mx.postRotate(a);
        mx.postTranslate(x, y);

        contourOrigin.transform(mx);
        contourMuzzleOrigin.transform(mx);

        //mx.Dispose();

        contourRelative = new Path(contourOrigin);
        contourMuzzleRelative = new Path(contourMuzzleOrigin);
    }

    public void PlaceAt(Matrix initMatrix)
    {
        contourRelative.transform(initMatrix);
        contourMuzzleRelative.transform(initMatrix);
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
        contourMuzzleRelative.transform(relMatrix);
    }

    public void Update()
    {
        if (modelControlUnit.GetState() && (noFiringFrameCount > rateOfFire))
        {
            if (carrier.stateContainer.PhysReactor.Drain(powerConsumption) == powerConsumption)
            {
                pathMeasure.setPath(this.contourMuzzleRelative, false);
                pathMeasure.getPosTan(0, coords, null);

                ShotPlasma ball = new ShotPlasma(coords[0], coords[1],
                    carrier.stateContainer.PhysPoint.angularX, carrier.stateContainer.PhysPoint.vectorV.x,
                    carrier.stateContainer.PhysPoint.vectorV.y, ShotPlasma.TTL, carrier);
                //var ball = new SpaceParticleExplotionSmall(coords[0], coords[1], 1);
                Particles.AddShell(ball);
                
                PointF soundInfo = Camera.calcSoundInfo(carrier.stateContainer.PhysPoint.vectorX.x, 
                		carrier.stateContainer.PhysPoint.vectorX.y, 
                		carrier.stateContainer.PhysPoint.vectorV.x, 
                		carrier.stateContainer.PhysPoint.vectorV.y);
                Sounds.playEffect(Effect.ShotPlasma, soundInfo.x, soundInfo.y);
            }
            noFiringFrameCount = 0;
        }
        else
        {
            noFiringFrameCount++;
        }
    }

    public void Dispose()
    {
    	/*
        contourMuzzleOrigin.Dispose();
        contourMuzzleRelative.Dispose();
        contourOrigin.Dispose();
        contourRelative.Dispose();
        pathMeasure.Dispose();
        */
    }
}
