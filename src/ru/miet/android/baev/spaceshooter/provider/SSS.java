package ru.miet.android.baev.spaceshooter.provider;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Canvas;
import android.graphics.Color;

import ru.miet.android.baev.spaceshooter.interfaces.ICollidable;
import ru.miet.android.baev.spaceshooter.interfaces.IParticle;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip.Model;
import ru.miet.android.baev.spaceshooter.ship.StateContainer.State;

public class SSS {
	
	public static ArrayList<SpaceShip> spaceShips = new ArrayList<SpaceShip>();
    public static SpaceShip controlledShip;

    public static void RemoveOldController()
    {
        if (controlledShip != null)
        {
            //spaceShips.remove(controlledShip);
            //controlledShip.Dispose();
        	//controlledShip.stateContainer.Explode();
            controlledShip = null;
        }
    }

    public static void AddNewShip(Model newType, float x, float y, float a, Color shipColor, boolean isNewControlledShip, boolean dummyControlled)
    {
        SpaceShip s = new SpaceShip(newType, shipColor);
        s.PlaceAt(x, y, a);
        spaceShips.add(s);

        if (isNewControlledShip)
        {
            controlledShip = s;
            controlledShip.CreateGUI();
            Camera.CenterOnControl(controlledShip);
        }
        
        if(dummyControlled)
        	s.AssignDummyPilot();
    }

    public static void Reset()
    {
    	for(Iterator<SpaceShip> i = spaceShips.iterator(); i.hasNext(); ) 
    		i.next().Dispose();
    	
        spaceShips.clear();
        controlledShip = null;
    }

    public static void Update(float deltaT)
    {
        //foreach (var ship in spaceShips)
        for(int i = 0; i< spaceShips.size(); i++)
        {
        	SpaceShip item = spaceShips.get(i);
        	
            if (item.Update(deltaT))
            {
                if(item == controlledShip){
                    GUI.Initialize();
                    controlledShip = null;
                }
                item.Dispose();
                spaceShips.remove(i);
                i--;
            }
        }
        Camera.FollowControl();
    }

    public static void UpdateWithRelMatrix()
    {
    	for(Iterator<SpaceShip> i = spaceShips.iterator(); i.hasNext(); ) 
    		i.next().UpdateWithRelMatrix();
    }

    public static void Draw(Canvas canvas)
    {
        Generic.paintContour.setARGB(255,255,255,255);
        Generic.paintContour.setStrokeWidth(Generic.paintWidth);
        for(Iterator<SpaceShip> i = spaceShips.iterator(); i.hasNext(); ) 
    		i.next().Draw(canvas);
    }

    public static void CheckCollisions(ArrayList<IParticle> list)
    {
    	for(Iterator<SpaceShip> iss = spaceShips.iterator(); iss.hasNext(); ) 
        {
    		SpaceShip ss = iss.next();
    				
            if (ss.stateContainer.GenState == State.Alive)
            {
            	for(Iterator<IParticle> ibullet = list.iterator(); ibullet.hasNext(); ) 
                {
            		ICollidable b = (ICollidable)ibullet.next();
                    if (b.IsExplodable() && b.GetOwner() != ss)
                    {
                        b.CollideWith(ss, ss == controlledShip);
                    }
                }
            }
        }
    }
}
