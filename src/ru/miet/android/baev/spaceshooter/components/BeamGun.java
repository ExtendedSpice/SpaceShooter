package ru.miet.android.baev.spaceshooter.components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import ru.miet.android.baev.spaceshooter.interfaces.IComponent;
import ru.miet.android.baev.spaceshooter.particle.ShotBeam;
import ru.miet.android.baev.spaceshooter.provider.Camera;
import ru.miet.android.baev.spaceshooter.provider.GUI;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.provider.Sounds;
import ru.miet.android.baev.spaceshooter.provider.GUI.ButtonType;
import ru.miet.android.baev.spaceshooter.provider.Sounds.Effect;
import ru.miet.android.baev.spaceshooter.ship.ControlUnit;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class BeamGun implements IComponent {
	private static float powerConsumption = 15;
	
	private SpaceShip carrier;
    private ControlUnit modelControlUnit;

    private Path contourOrigin;
    private Path contourRelative;

    private Path contourMuzzle1Origin;
    private Path contourMuzzle1Relative;

    private int rateOfFire;
    private int noFiringFrameCount;

    private PathMeasure pathMeasure = new PathMeasure();
    private float[] coords = { 0, 0 };

    public BeamGun(float x, float y, float a, float scale, int framesPerShot, SpaceShip ss)
    {
    	carrier = ss;

        rateOfFire = framesPerShot;
        contourOrigin = new Path();
        contourMuzzle1Origin = new Path();

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

        Matrix mx = new Matrix();
        mx.setScale(scale, scale);
        mx.postRotate(a);
        mx.postTranslate(x, y);

        contourOrigin.transform(mx);
        contourMuzzle1Origin.transform(mx);
        
        contourRelative = new Path(contourOrigin);
        contourMuzzle1Relative = new Path(contourMuzzle1Origin);
    }

    public void PlaceAt(Matrix initMatrix)
    {
        contourRelative.transform(initMatrix);
        contourMuzzle1Relative.transform(initMatrix);
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
        contourMuzzle1Relative.transform(relMatrix);
    }

    public void Update()
    {
        if (modelControlUnit.GetState() && (noFiringFrameCount > rateOfFire))
        {
            if (carrier.stateContainer.PhysReactor.Drain(powerConsumption) == powerConsumption)
            {
                pathMeasure.setPath(this.contourMuzzle1Relative, false);
                pathMeasure.getPosTan(0, coords, null);

                ShotBeam ball = new ShotBeam(coords[0], coords[1],
                    carrier.stateContainer.PhysPoint.angularX, carrier);
                //var ball = new SpaceParticleExplotionSmall(coords[0], coords[1], 1);
                Particles.AddShell(ball);
                
                PointF soundInfo = Camera.calcSoundInfo(carrier.stateContainer.PhysPoint.vectorX.x, 
                		carrier.stateContainer.PhysPoint.vectorX.y, 
                		carrier.stateContainer.PhysPoint.vectorV.x, 
                		carrier.stateContainer.PhysPoint.vectorV.y);
                Sounds.playEffect(Effect.ShotBeam, soundInfo.x, soundInfo.y);
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
