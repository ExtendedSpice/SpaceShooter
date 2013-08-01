package ru.miet.android.baev.spaceshooter.provider;

import java.util.ArrayList;
import java.util.Iterator;

import ru.miet.android.baev.spaceshooter.particle.BackStar;
import ru.miet.android.baev.spaceshooter.ship.PhysicalPoint;
import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;

public class Camera {
	private static Rect viewArea;
    private static Rect largeArea;

    public static float viewX;
    public static float viewY;

    private static float viewCenterX;
    private static float viewCenterY;
    
    private static float arrowRange;
    private static float arrowOffset;

    private static ArrayList<BackStar> background;
    private static PhysicalPoint control;

    public static void SetDisplay(int Width, int Height)
    {
        viewArea = new Rect(0, 0, Width, Height);

        viewCenterX = Width / 2;
        viewCenterY = Height / 2;

        largeArea = new Rect((int)(-viewCenterX), (int)(-viewCenterY), (int)(viewCenterX * 3), (int)(viewCenterY * 3));
        arrowRange = Height/2;
        arrowOffset = Height/5;
    }

    public static void Init()
    {
        viewX = 0;
        viewY = 0;
        control = null;
        GenerateBackground();
    }

    public static void CenterOnControl(SpaceShip ss)
    {
        control = ss.stateContainer.PhysPoint;
        viewX = (control.vectorX.x - viewCenterX);
        viewY = (control.vectorX.y - viewCenterY);

        GenerateBackground();
    }

    public static void FollowControl()
    {
        if (control != null)
        {
            viewX = (control.vectorX.x - viewCenterX);
            viewY = (control.vectorX.y - viewCenterY);
        }
    }

    public static void AddBackground(int p)
    {
        background = new ArrayList<BackStar>(p);
        for (int i = 0; i < p; i++)
        {
            BackStar star = new BackStar();
            GenerateStar(star);
            background.add(star);
        }
    }

    private static void GenerateBackground()
    {
    	for(Iterator<BackStar> i = background.iterator(); i.hasNext(); ) {
    		BackStar item = i.next();
            if (!item.IsAlive())
                GenerateStar(item);
        }
    }

    public static void RefreshBackground(BackStar spaceParticleBackgroundStar)
    {
        RegenerateStar(spaceParticleBackgroundStar);
    }

    private static void GenerateStar(BackStar s)
    {
        s.ReStar(Generic.RND.nextInt(largeArea.right -largeArea.left) + largeArea.left,
            Generic.RND.nextInt(largeArea.bottom - largeArea.top ) + largeArea.top);
    }

    private static void RegenerateStar(BackStar star)
    {
        float x = 0;
        float y = 0;

        if (Generic.RND.nextBoolean())
        {
            x = largeArea.left + Generic.RND.nextInt(largeArea.right - largeArea.left);
            y = largeArea.top + Generic.RND.nextInt(viewArea.top - largeArea.top);
            if (Generic.RND.nextBoolean())
                y = viewArea.bottom - y;
        }
        else
        {
            x = largeArea.left + Generic.RND.nextInt(viewArea.left - largeArea.left);
            y = largeArea.top + Generic.RND.nextInt(largeArea.bottom - largeArea.top);
            if (Generic.RND.nextBoolean())
                x = viewArea.right - x;
        }

        star.ReStar(x + viewX, y + viewY);
    }

    public static boolean IsVisible(float coordsX, float coordsY)
    {
        return largeArea.contains((int)(coordsX - viewX), (int)(coordsY - viewY));
    }

    public static void DrawBackground(Canvas canvas)
    {
        Generic.paintDot.setAlpha(255);
        for(Iterator<BackStar> i = background.iterator(); i.hasNext(); ) 
    		i.next().Draw(canvas);
    }

    public static void DrawNavArrows(Canvas c){
    	for(Iterator<SpaceShip> i = SSS.spaceShips.iterator(); i.hasNext(); ) {
			float dx;
			float dy;
			double da;
    		PhysicalPoint p = i.next().stateContainer.PhysPoint;
    		if(p != control){
    			dx = p.vectorX.x - viewX - viewCenterX;
    			dy = p.vectorX.y - viewY - viewCenterY;
    			if(dx*dx + dy*dy > arrowRange*arrowRange){
	    			da = Math.atan2(dy,dx);
	    			Sprites.Arrow.Draw(c, (float)(viewX+viewCenterX + Math.cos(da)*arrowOffset), 
	    					(float)(viewY+viewCenterY + Math.sin(da)*arrowOffset), (float)(da*180/Math.PI));
    			}
    		}
    	}
    }
    
    public static PointF calcSoundInfo(float X, float Y, float VX, float VY){
    	float dX = X-viewX-viewCenterX;
    	float dY = Y-viewY-viewCenterY;
    	
    	float dVX = -VX;
    	float dVY = -VY;
    	
    	if(control!=null){
    		dVX += control.vectorV.x;
    		dVY += control.vectorV.y;
    	}
    	return new PointF((float)Math.sqrt(dX*dX + dY*dY), dX*dVX + dY*dVY);
    }
}
