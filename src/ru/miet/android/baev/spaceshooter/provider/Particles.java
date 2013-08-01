package ru.miet.android.baev.spaceshooter.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ru.miet.android.baev.spaceshooter.interfaces.IParticle;

public class Particles {
	
    private static ArrayList<IParticle> Dust = new ArrayList<IParticle>();
    private static ArrayList<IParticle> Shells = new ArrayList<IParticle>();
    private static ArrayList<IParticle> Explotions = new ArrayList<IParticle>();

    public static void AddDust(IParticle newItem)
    {
        AddItem(newItem, Dust);
    }
    public static void AddShell(IParticle newItem)
    {
        AddItem(newItem, Shells);
    }
    public static void AddExplotion(IParticle newItem)
    {
        AddItem(newItem, Explotions);
    }
    private static void AddItem(IParticle newItem, ArrayList<IParticle> list)
    {
    	for(ListIterator<IParticle> i = list.listIterator(); i.hasNext();)
        {
    		IParticle item = i.next();
            if (!item.IsAlive())
            {
                item.Recycle();
                i.set(newItem);
                return;
            }
        }
        list.add(newItem);
    }

    public static void Update(float dTime)
    {
    	for(Iterator<IParticle> i = Dust.iterator();i.hasNext();)
    		i.next().Update(dTime);
    	for(Iterator<IParticle> i = Shells.iterator();i.hasNext();)
    		i.next().Update(dTime);
    	for(Iterator<IParticle> i = Explotions.iterator();i.hasNext();)
    		i.next().Update(dTime);
    		/* :c
        foreach (var item in Dust)
            item.Update(dTime);

        foreach (var item in Shells)
            item.Update(dTime);

        foreach (var item in Explotions)
            item.Update(dTime);
            */
    }

    public static void Draw(Canvas canvas)
    {

    	for(Iterator<IParticle> i = Dust.iterator();i.hasNext();)
    		i.next().Draw(canvas);

        Generic.paintContour.setStyle(Paint.Style.FILL_AND_STROKE);
        Generic.paintContour.setStrokeWidth(Generic.paintWidth);
        Generic.paintContour.setColor(Color.WHITE);
        for(Iterator<IParticle> i = Explotions.iterator();i.hasNext();)
    		i.next().Draw(canvas);

        Generic.paintContour.setStyle(Paint.Style.STROKE);
        Generic.paintContour.setStrokeWidth(Generic.paintWidth * 0.8f);
        Generic.paintContour.setColor(Color.WHITE);
        for(Iterator<IParticle> i = Shells.iterator();i.hasNext();)
    		i.next().Draw(canvas);
    }

    public static void Clear()
    {
    	for(Iterator<IParticle> i = Dust.iterator();i.hasNext();)
    		i.next().Recycle();
    	for(Iterator<IParticle> i = Shells.iterator();i.hasNext();)
    		i.next().Recycle();
    	for(Iterator<IParticle> i = Explotions.iterator();i.hasNext();)
    		i.next().Recycle();

        Shells.clear();
        Dust.clear();
        Explotions.clear();
    }

    public static ArrayList<IParticle> GetShellObjects()
    {
        return Shells;
    }
}
