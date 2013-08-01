package ru.miet.android.baev.spaceshooter.provider;

import java.util.ArrayList;

import ru.miet.android.baev.spaceshooter.ship.ControlUnit;

public class DummyAI {
	private ArrayList<ControlUnit> panel;
	
	public DummyAI(ArrayList<ControlUnit> arg){
		panel = arg;
	}
	
	public void Update(){
		if(Generic.RND.nextInt(91) == 90)
			panel.get(Generic.RND.nextInt(panel.size())).Switch();
	}
}
