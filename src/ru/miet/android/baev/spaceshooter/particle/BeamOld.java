package ru.miet.android.baev.spaceshooter.particle;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Region;
import ru.miet.android.baev.spaceshooter.interfaces.ICollidable;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class BeamOld implements ICollidable, IParticle{
	private enum BeamState { Charge, Fire, Cooldown, Idle };

    private static float chargePower = 2;
    private static float firingPower = 4;
    private static int DAMAGE = 8;

    private static float CHARGEFRAMES = 40;
    private static float FULLPOWER = 5;
    private static float OVERHEATFRAMES = 60;
    private static int SEGMENTSNUMBER = 20;

    private boolean nonDisposed;

    private BeamState state;
    private int frame;
    private boolean triggered;

    private float maxLength;
    private float maxChargeLength;

    private SpaceShip owner;

    private Path beamCharge1;
    private Path beamCharge2;
    private Path beamMain;

    private float scaleCharge;
    private float scaleFire;

    private Matrix scaleMatrix;
    private PathMeasure pathMeasure = new PathMeasure();
    private float[] coords = { 0, 0 };

    public boolean IsAlive()
    {
        return nonDisposed;
    }

    public BeamOld(Path mainBeam, Path supportBeams, float length, SpaceShip ss)
    {
        nonDisposed = true;

        owner = ss;
        maxLength = length;

        frame = 0;
        triggered = false;

        scaleCharge = 0;
        scaleFire = 0;
        scaleMatrix = new Matrix();

        state = BeamState.Idle;

        pathMeasure.setPath(mainBeam, false);
        pathMeasure.getPosTan(0, coords, null);
        PointF A = new PointF(coords[0], coords[1]);
        pathMeasure.getPosTan(pathMeasure.getLength(), coords, null);
        PointF B = new PointF(coords[0], coords[1]);

        pathMeasure.setPath(supportBeams, false);
        pathMeasure.getPosTan(0, coords, null);
        PointF C = new PointF(coords[0], coords[1]);
        pathMeasure.getPosTan(pathMeasure.getLength(), coords, null);
        PointF D = new PointF(coords[0], coords[1]);

        beamMain = new Path();
        beamMain.moveTo(A.x, A.y);
        beamMain.lineTo(B.x, B.y);

        beamCharge1 = new Path();
        beamCharge1.moveTo(C.x, C.y);
        beamCharge1.lineTo(B.x, B.y);

        beamCharge2 = new Path();
        beamCharge2.moveTo(D.x, D.y);
        beamCharge2.lineTo(B.x, B.y);

        pathMeasure.setPath(beamCharge1, false);
        maxChargeLength = pathMeasure.getLength();
    }

    public void SetFiringMode(boolean fire)
    {
        triggered = fire;
    }

    public void Update(float dTime)
    {
        if (nonDisposed)
        {
            boolean powered;
            switch (state)
            {
                case Idle:
                    if (triggered)
                    {
                        state = BeamState.Charge;
                        frame = 1;
                        scaleCharge = frame / CHARGEFRAMES;
                    }
                    break;

                case Charge:
                    powered = (owner.stateContainer.PhysReactor.Drain(chargePower) == chargePower);
                    if (frame > CHARGEFRAMES)
                    {
                        state = BeamState.Fire;
                        frame = 1;
                        scaleFire = frame / FULLPOWER;
                    }
                    else if (frame == 0)
                    {
                        state = BeamState.Idle;
                        frame = 0;
                    }
                    else
                    {
                        scaleCharge = frame / CHARGEFRAMES;
                        if (triggered && powered)
                        {
                            frame++;
                        }
                        else
                        {
                            frame--;
                        }
                    }
                    break;

                case Fire:
                    powered = (owner.stateContainer.PhysReactor.Drain(firingPower) == firingPower);
                    if (!(triggered && powered) || (frame > OVERHEATFRAMES))
                    {
                        state = BeamState.Cooldown;
                        scaleCharge = 1;
                        frame = 0;
                    }
                    else
                    {
                        if (frame <= FULLPOWER)
                            scaleFire = frame / FULLPOWER;
                        frame++;
                    }
                    break;

                case Cooldown:
                    if (frame == CHARGEFRAMES)
                    {
                        frame = 0;
                        state = BeamState.Idle;
                    }
                    else
                    {
                        scaleCharge = (CHARGEFRAMES - frame) / CHARGEFRAMES;
                        frame++;
                    }
                    break;
            }
        }
    }

    public void UpdateWithRelMatrix(Matrix relMatrix)
    {
        beamCharge1.transform(relMatrix);
        beamCharge2.transform(relMatrix);
        beamMain.transform(relMatrix);
    }
    public void Draw(Canvas canvas)
    {
        if (nonDisposed)
        {

            if (state != BeamState.Idle)
            {
                pathMeasure.setPath(beamCharge1, false);
                float s = maxChargeLength / pathMeasure.getLength() * scaleCharge;

                pathMeasure.getPosTan(0, coords, null);
                scaleMatrix.setScale(s, s, coords[0], coords[1]);
                beamCharge1.transform(scaleMatrix);

                pathMeasure.setPath(beamCharge2, false);
                pathMeasure.getPosTan(0, coords, null);
                scaleMatrix.setScale(s, s, coords[0], coords[1]);
                beamCharge2.transform(scaleMatrix);

                //canvas.DrawPath(beamPrime, SpaceShooterScene.PAINTCONTOUR);
                Generic.paintContour.setAlpha((int)(scaleCharge * 255));
                canvas.drawPath(beamCharge1, Generic.paintContour);
                canvas.drawPath(beamCharge2, Generic.paintContour);

                if (state == BeamState.Fire)
                {
                    pathMeasure.setPath(beamMain, false);
                    pathMeasure.getPosTan(0, coords, null);
                    scaleMatrix.setScale(maxLength / pathMeasure.getLength() * scaleFire, maxLength / pathMeasure.getLength() * scaleFire, coords[0], coords[1]);
                    beamMain.transform(scaleMatrix);

                    Generic.paintContour.setAlpha((int)(scaleFire * 255));
                    canvas.drawPath(beamMain, Generic.paintContour);
                }
            }
        }
    }

    public void Recycle()
    {
    	/*
        beamCharge1.Dispose();
        beamCharge2.Dispose();
        beamMain.Dispose();
        scaleMatrix.Dispose();
        pathMeasure.Dispose();
        */
        nonDisposed = false;
    }


    public boolean IsExplodable()
    {
        return nonDisposed && (state == BeamState.Fire) && (frame > 1);
    }

    public SpaceShip GetOwner()
    {
        return owner;
    }

    public void CollideWith(SpaceShip s, boolean vibrate)
    {
        float x0;
        float y0;
        float dx;
        float dy;
        float d;
        float A;
        float B;
        float C;

        //pathMeasure.SetPath(beamMain, false);
        pathMeasure.getPosTan(0, coords, null);
        x0 = coords[0];
        y0 = coords[1];
        pathMeasure.getPosTan(pathMeasure.getLength(), coords, null);
        dx = coords[0] - x0;
        dy = coords[1] - y0;
        d = (float) Math.sqrt(dx * dx + dy * dy);

        A = dy / d;
        B = -dx / d;
        C = -A * x0 - B * y0;

        d = Math.abs(s.stateContainer.PhysPoint.vectorX.x * A + B * s.stateContainer.PhysPoint.vectorX.y + C);
        if (d < s.GetSize())
        {
            float param = 0;
            Region r = s.GetRegion();

            for (int i = 0; i <= SEGMENTSNUMBER; i++)
            {
                param = (float)i / SEGMENTSNUMBER;
                A = x0 + param * dx;
                B = y0 + param * dy;

                if (r.contains((int)A, (int)B))
                {
                	ExplotionSmall e = new ExplotionSmall(A + Generic.RND.nextInt(11) - 5, 
                    		B + Generic.RND.nextInt(11) - 5,
                        .2f * Generic.paintWidth * (Generic.RND.nextInt(21)+90)/100f);
                    Particles.AddExplotion(e);

                    s.stateContainer.Damage(DAMAGE);

                    if (vibrate)
                    {
                        Generic.Vibrate(30);
                    }
                    break;
                }
            }
        }
    }

}
