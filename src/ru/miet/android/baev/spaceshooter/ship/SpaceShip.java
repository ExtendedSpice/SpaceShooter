package ru.miet.android.baev.spaceshooter.ship;

import java.util.ArrayList;
import java.util.Iterator;

import ru.miet.android.baev.spaceshooter.components.BeamGun;
import ru.miet.android.baev.spaceshooter.components.PlasmaGun;
import ru.miet.android.baev.spaceshooter.components.Railgun;
import ru.miet.android.baev.spaceshooter.components.Thruster;
import ru.miet.android.baev.spaceshooter.interfaces.IComponent;
import ru.miet.android.baev.spaceshooter.particle.ShotBeam;
import ru.miet.android.baev.spaceshooter.provider.DummyAI;
import ru.miet.android.baev.spaceshooter.provider.GUI;
import ru.miet.android.baev.spaceshooter.provider.GUI.ButtonType;
import ru.miet.android.baev.spaceshooter.provider.Sprites;
import ru.miet.android.baev.spaceshooter.provider.Sprites.Sprite;
import ru.miet.android.baev.spaceshooter.ship.StateContainer.State;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class SpaceShip {
	private static Region clip = new Region(new Rect(-50000,-50000,50000,50000));
	public enum Model {Sharpflyer,Dalekhead,Supermeatship}
	
	public StateContainer stateContainer;
	
	// задание компонентов View
	private Sprite shipSprite;
    private Path ContourOrigin;
    private Path ContourRelative;
    private float scaleFactor;
    private Region ShipRegion = new Region();
    private Matrix relativeDrawMatrix;
    private ArrayList<IComponent> components = new ArrayList<IComponent>();
    private DummyAI dAI = null;

    public SpaceShip(Model type, Color shipColor)
    {
        // Конфигурирование балансной Model-части
        stateContainer = new StateContainer(type, shipColor);

        // Конфигурирование хард Model-части
        ContourOrigin = new Path();
        switch (type)
        {
            case Sharpflyer:
                {
                    ContourOrigin.moveTo(8, 0);
                    ContourOrigin.lineTo(-4, -4);
                    ContourOrigin.lineTo(-4, 4);
                    ContourOrigin.close();

                    scaleFactor = 3.6f;

                    Thruster engine1 = new Thruster();
                    ControlUnit scu1 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu1);
                    engine1.SetControlUnit(scu1, ButtonType.RightColumn);
                    engine1.SetModel(-4, 1, 180, 2, this);
                    engine1.SetContour(1, 1, 0.6f, 1);
                    components.add(engine1);

                    Thruster engine2 = new Thruster();
                    ControlUnit scu2 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu2);
                    engine2.SetControlUnit(scu2, ButtonType.LeftColumn);
                    engine2.SetModel(-4, -1, 180, 2, this);
                    engine2.SetContour(1, 1, 0.6f, 1);
                    components.add(engine2);

                    components.add(new Railgun(1.5f, 0, 0, 0.4f, 3, this));
                    shipSprite = Sprites.SF_sprite;
                }
                break;

            case Dalekhead:
                {
                    ContourOrigin.moveTo(-0.5f, 3.99f);
                    ContourOrigin.lineTo(-1f, 3);
                    ContourOrigin.lineTo(-1f, -3);
                    ContourOrigin.lineTo(-0.5f, -3.99f);
                    ContourOrigin.addArc(new RectF(-8.5f, -5f, 1.5f, 5f), -53, 106);

                    scaleFactor = 11f;

                    Thruster engine1 = new Thruster();
                    ControlUnit scu1 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu1);
                    engine1.SetControlUnit(scu1, ButtonType.LeftColumn);
                    engine1.SetModel(0.5f, -3, -37, 2, this);
                    engine1.SetContour(1, 1, 0.5f, 0.5f);
                    components.add(engine1);

                    Thruster engine2 = new Thruster();
                    ControlUnit scu2 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu2);
                    engine2.SetControlUnit(scu2, ButtonType.RightColumn);
                    engine2.SetModel(0.5f, 3, 37, 2, this);
                    engine2.SetContour(1, 1, 0.5f, 0.5f);
                    components.add(engine2);

                    Thruster engine3 = new Thruster();
                    ControlUnit scu3 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu3);
                    engine3.SetControlUnit(scu3, ButtonType.BothColumn);
                    engine3.SetModel(-1, 0, 180, 4, this);
                    engine3.SetContour(1, 0.5f, 0.6f, 1);
                    components.add(engine3);

                    components.add(new BeamGun(0f, 0, 0, 0.3f, ShotBeam.TTL*6, this));
                    shipSprite = Sprites.DH_sprite;
                }
                break;

            case Supermeatship:
                {
                    ContourOrigin.addRoundRect(new RectF(-2, -2, 2, 2), 0.5f, 0.5f, Path.Direction.CW);

                    scaleFactor = 14;

                    Thruster engine1 = new Thruster();
                    ControlUnit scu1 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu1);
                    engine1.SetControlUnit(scu1, ButtonType.LeftColumn);
                    engine1.SetModel(0, -2, -90, 3, this);
                    engine1.SetContour(1, 0.6f, 0.6f, 0.5f);
                    components.add(engine1);

                    Thruster engine2 = new Thruster();
                    ControlUnit scu2 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu2);
                    engine2.SetControlUnit(scu2, ButtonType.RightColumn);
                    engine2.SetModel(0, 2, 90, 3, this);
                    engine2.SetContour(1, 0.6f, 0.6f, 0.5f);
                    components.add(engine2);

                    Thruster engine3 = new Thruster();
                    ControlUnit scu3 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu3);
                    engine3.SetControlUnit(scu3, ButtonType.LeftColumn);
                    engine3.SetModel(-2, -1, 180, 3, this);
                    engine3.SetContour(1, 0.6f, 0.6f, 0.5f);
                    components.add(engine3);

                    Thruster engine4 = new Thruster();
                    ControlUnit scu4 = new ControlUnit();
                    stateContainer.ControlBlock.add(scu4);
                    engine4.SetControlUnit(scu4, ButtonType.RightColumn);
                    engine4.SetModel(-2, 1, 180, 3, this);
                    engine4.SetContour(1, 0.6f, 0.6f, 0.5f);
                    components.add(engine4);

                    components.add(new PlasmaGun(0, 0, 0, 0.2f, 60, this));
                    shipSprite = Sprites.SM_sprite;
                }
                break;
        }
        ContourRelative = new Path(ContourOrigin);
        relativeDrawMatrix = new Matrix();
    }

    public void CreateGUI()
    {
        GUI.Initialize();
        for(Iterator<IComponent> i = components.iterator(); i.hasNext();)
        	i.next().CreateGUI();   
        GUI.FinalizeButtons();
    }
    
    public void AssignDummyPilot(){
        dAI = new DummyAI(stateContainer.ControlBlock);
    }

    public void PlaceAt(float x, float y, float a)
    {
        this.stateContainer.PhysPoint.vectorX.x = x;
        this.stateContainer.PhysPoint.vectorX.y = y;
        this.stateContainer.PhysPoint.angularX = a;

        stateContainer.PhysSize *= scaleFactor;

        relativeDrawMatrix.setScale(scaleFactor, scaleFactor);
        relativeDrawMatrix.postRotate(a);
        relativeDrawMatrix.postTranslate(x, y);

        ContourRelative.transform(relativeDrawMatrix);

        for(Iterator<IComponent> i = components.iterator(); i.hasNext();)
        	i.next().PlaceAt(relativeDrawMatrix);
        
    }

    public boolean Update(float dTime)
    {
    	if(dAI != null)
    		dAI.Update();
    	
    	for(Iterator<IComponent> i = components.iterator(); i.hasNext();)
        	i.next().Update();
        
        // TODO: запилить смещение корабля во время стрельбы

        stateContainer.PhysReactor.Restore();
        stateContainer.PhysPoint.EndForce(dTime, stateContainer.PhysMass, stateContainer.PhysInertion);
        
        if (stateContainer.GenState != State.Alive)
            return stateContainer.Explode();
        else
        	return false;
    }

    public void UpdateWithRelMatrix()
    {
        stateContainer.PhysPoint.UpdateRelativeMatrix(relativeDrawMatrix);
        ContourRelative.transform(relativeDrawMatrix);

        for(Iterator<IComponent> i = components.iterator(); i.hasNext();)
        	i.next().UpdateWithRelMatrix(relativeDrawMatrix);
    }

    public void Draw(Canvas canvas)
    {
       // ProviderGeneric.paintContour.SetStyle(Paint.Style.Fill);
       // canvas.DrawPath(ContourRelative, ProviderGeneric.paintContour);
      //  ProviderGeneric.paintContour.SetStyle(Paint.Style.Stroke);
    	shipSprite.Draw(canvas, stateContainer.PhysPoint.vectorX.x, stateContainer.PhysPoint.vectorX.y, stateContainer.PhysPoint.angularX);
    	/*Generic.paintContour.setColor(Color.BLACK);
    	Generic.paintContour.setStrokeWidth(1);
    	canvas.drawPath(ContourRelative, Generic.paintContour);
        for(Iterator<IComponent> i = components.iterator(); i.hasNext();)
        	i.next().Draw(canvas);
        */
    }

    public void Dispose()
    {
    	/*
        ContourOrigin.Dispose();
        ContourRelative.Dispose();
        relativeDrawMatrix.Dispose();
        */

    	for(Iterator<IComponent> i = components.iterator(); i.hasNext();)
        	i.next().Dispose();
        
        components.clear();
    }

    public float GetSize()
    {
        return stateContainer.PhysSize;
    }
    public Region GetRegion()
    {
        ShipRegion.setPath(ContourRelative, clip);
        return ShipRegion;
    }
}
