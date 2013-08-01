package ru.miet.android.baev.spaceshooter.components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import ru.miet.android.baev.spaceshooter.interfaces.IComponent;
import ru.miet.android.baev.spaceshooter.particle.EngineDust;
import ru.miet.android.baev.spaceshooter.provider.Camera;
import ru.miet.android.baev.spaceshooter.provider.GUI;
import ru.miet.android.baev.spaceshooter.provider.Generic;
import ru.miet.android.baev.spaceshooter.provider.Particles;
import ru.miet.android.baev.spaceshooter.provider.Sounds;
import ru.miet.android.baev.spaceshooter.ship.ControlUnit;
import ru.miet.android.baev.spaceshooter.ship.PhysicalPoint;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public class Thruster implements IComponent {
	
	// MODEL
    public ControlUnit modelControlUnit;
    public GUI.ButtonType modelControlButtonType;

    // Зависимость использования реактора от тяги
    private static float thrustReactorFactor = 0.1f;
    // Зависимость создания частит от тяги
    private static float thrustParticleFactor = 0.1f;

    public float modelThrust;
    public float modelThrustAngular;


    private float modelOriginX;
    private float modelOriginY;

    public float modelOrientation;

    // VIEW
    private Path contourOrigin;
    private Path contourNozzleOrigin;

    private Path contourRelative;
    private Path contourNozzleRelative;

    private SpaceShip engineCarrier;

    private PathMeasure pathMeasure = new PathMeasure();
    private float[] coords = { 0, 0 };

    //private int firingFrameCount = 0;
    private float reactorConsumed = 0;
    
    private int soundStreamID = 0;

    public void SetControlUnit(ControlUnit cu, GUI.ButtonType bt)
    {
        modelControlUnit = cu;
        modelControlButtonType = bt;
    }
    public void SetModel(float originX, float originY, float originAngle, float thrust, SpaceShip carrier)
    {
        // Базовые заготовки
        this.modelThrust = thrust;
        this.reactorConsumed = 1 / thrustParticleFactor;

        this.modelOriginX = originX;
        this.modelOriginY = originY;
        this.modelOrientation = originAngle;

        engineCarrier = carrier;

        // Подсчет тяги
        {
            // Точка отсчета для направления струи
            float oX = originX;
            float oY = originY;

            float angleRad = originAngle / 180 * (float)Math.PI;
            float tX = (float) (oX + Math.cos(angleRad));
            float tY = (float) (oY + Math.sin(angleRad));

            // Ниже результаты страшных вычислений
            float a = (oX - tX) / (oY - tY);
            float b = (tX * oY - oX * tY) / (oY - tY);

            float y = -a * b / (a * a + 1);
            float x = a * y + b;

            float r = (float) Math.sqrt(x * x + y * y);

            modelThrustAngular = r * (((-b * a * oX) > 0) ? 1 : -1);
        }
    }

    public void Update()
    {
        if (this.modelControlUnit.GetState())
        {        	
            PhysicalPoint p = this.engineCarrier.stateContainer.PhysPoint;

            //e.FireParticle
            float powerReceived = engineCarrier.stateContainer.PhysReactor.Drain(modelThrust * thrustReactorFactor);
            reactorConsumed += powerReceived;

            float thrustRecieved = powerReceived / thrustReactorFactor;

            float nozzleAngle = (this.modelOrientation + p.angularX) / 180 * (float)Math.PI;
            float forwardThrustX = (float) (-thrustRecieved * Math.cos(nozzleAngle));
            float forwardThrustY = (float) (-thrustRecieved * Math.sin(nozzleAngle));
            float angularThrust = this.modelThrustAngular * thrustRecieved;
            p.ToForce(forwardThrustX, forwardThrustY, angularThrust);

            int fireNum = (int)(reactorConsumed * thrustParticleFactor);
            if (fireNum > 0)
            {
                reactorConsumed -= fireNum;

                for (int i = 0; i < fireNum; i++)
                {
                    // создаем новую частицу; процесс так себе
                    float startPoint = (float)Generic.RND.nextDouble();

                    pathMeasure.setPath(contourNozzleRelative, false);
                    pathMeasure.getPosTan(pathMeasure.getLength() * startPoint, coords, null);

                    //int lifetime = (int)((1f - Math.Abs(startPoint - 0.5f) / 3) * Particles.EngineDust.TTL);

                    EngineDust dust = new EngineDust(coords[0], coords[1],
                        modelOrientation + p.angularX, this.modelThrust * 1.5f, 
                        p.vectorV.x, p.vectorV.y, EngineDust.TTL);

                    Particles.AddDust(dust);
                }
            }
            
            if(powerReceived >0){
            	PointF soundInfo = Camera.calcSoundInfo(engineCarrier.stateContainer.PhysPoint.vectorX.x, 
            			engineCarrier.stateContainer.PhysPoint.vectorX.y, 
            			engineCarrier.stateContainer.PhysPoint.vectorV.x, 
            			engineCarrier.stateContainer.PhysPoint.vectorV.y);
            	if(soundStreamID == 0)
            		soundStreamID = Sounds.playLoop(soundInfo.x, soundInfo.y);
            	else
            		soundStreamID = Sounds.setLoop(soundStreamID, soundInfo.x, soundInfo.y);
            }
            else{
        		Sounds.closeLoop(soundStreamID);
        		soundStreamID = 0;
            }

            //firingFrameCount++;
        }
        else{
        	Sounds.closeLoop(soundStreamID);
    		soundStreamID = 0;        	
        }
    }
    public void SetContour(float radius, float length, float nozzle, float scale)
    {
        contourOrigin = new Path();
        contourOrigin.moveTo(0, radius);
        contourOrigin.lineTo(length, nozzle);
        contourOrigin.lineTo(length, -nozzle);
        contourOrigin.lineTo(0, -radius);
        //contour.AddArc(new RectF(-radius, radius, radius, -radius), 90, 180);
        contourOrigin.close();

        contourNozzleOrigin = new Path();
        contourNozzleOrigin.moveTo(length, nozzle);
        contourNozzleOrigin.lineTo(length, -nozzle);

        Matrix mx = new Matrix();
        mx.setScale(scale, scale);
        mx.postRotate(modelOrientation);
        mx.postTranslate(modelOriginX, modelOriginY);

        contourOrigin.transform(mx);
        contourNozzleOrigin.transform(mx);

        contourRelative = new Path(contourOrigin);
        contourNozzleRelative = new Path(contourNozzleOrigin);
    }

    public void CreateGUI()
    {
        GUI.AddButton(modelControlUnit, modelControlButtonType);
    }

    public void PlaceAt(Matrix initMatrix)
    {
        contourRelative.transform(initMatrix);
        contourNozzleRelative.transform(initMatrix);
    }

    public void UpdateWithRelMatrix(Matrix relativeDrawMatrix)
    {
        contourRelative.transform(relativeDrawMatrix);
        contourNozzleRelative.transform(relativeDrawMatrix);
    }

    public void Draw(Canvas canvas)
    {
        canvas.drawPath(contourRelative, Generic.paintContour);
    }

    public void Dispose()
    {
    	/* =\
        contourNozzleOrigin.Dispose();
        contourNozzleRelative.Dispose();
        contourOrigin.Dispose();
        contourRelative.Dispose();
        pathMeasure.Dispose();
        */
    }

}
