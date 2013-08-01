package ru.miet.android.baev.spaceshooter.ship;

import ru.miet.android.baev.spaceshooter.provider.Sounds;

public class Reactor {
	// Power to restore after overloading
	private static float restoreCap = 0.8f;
	private StateContainer owner;
    public enum ReactorState { Working, Overloaded };
    
    public ReactorState state = ReactorState.Working;
    public float capMax;
    public float cap;
    public float power;

    public Reactor(float batteryCapacity, float reactorOutput, StateContainer owner)
    {
    	this.owner = owner;
        capMax = batteryCapacity;
        cap = capMax;
        power = reactorOutput;
    }

    public float Drain(float drain)
    {
        float ret = 0;
        if (state == ReactorState.Working)
        {
            if (cap < drain)
            {
            	Sounds.playAlarm(owner);
                state = ReactorState.Overloaded;
                ret = cap;
                cap = 0;
            }
            else
            {
                cap -= drain;
                ret = drain;
            }
        }
        return ret;
    }

    public void Restore()
    {
        // Восстановление реактора
        if (cap < capMax)
        {
            cap += power;
            if ((state == ReactorState.Overloaded) && ((cap / capMax) > restoreCap))
                state = ReactorState.Working;
        }

        if (cap > capMax)
            cap = capMax;
    }
}
