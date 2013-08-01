package ru.miet.android.baev.spaceshooter.provider;

import ru.miet.android.baev.spaceshooter.R;
import ru.miet.android.baev.spaceshooter.ship.StateContainer;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Sounds {
	public enum Effect {ExplPlasma, ExplSmall, ExplCapital, ShotRail, ShotPlasma, ShotBeam, WeaponHit};
	private static boolean allowSounds = true;
	
	private static float DISTANCEFACTOR;
	//private static float PITCHFACTOR = 200;
	
	private static int toggleControl;
	private static int alarm;
	private static int plasmaShot;
	private static int plasmaExpl;
	private static int laser;
	private static int railgun;
	private static int weaponHit;
	private static int explComp;
	private static int explotion1;
	private static int explotion2;
	private static int thruster;
	
	private static SoundPool soundPool;
	/** Populate the SoundPool*/
	public static void initSounds(float height) {
		DISTANCEFACTOR = height*2;
	}
	public static void populateSounds(Context context){		

		if(soundPool != null)
			soundPool.release();
		
	    soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 100);
	    
	    toggleControl = soundPool.load(context, R.raw.button, 1);
	    alarm = soundPool.load(context, R.raw.alarm, 1);
	    plasmaShot = soundPool.load(context, R.raw.plasma_bolt, 1);
	    plasmaExpl = soundPool.load(context, R.raw.plsama_blast, 1);
	    laser = soundPool.load(context, R.raw.laser, 1);
	    railgun = soundPool.load(context, R.raw.railgun, 1);
	    weaponHit = soundPool.load(context, R.raw.weapon_hit, 1);
	    explComp = soundPool.load(context, R.raw.explosion_component, 1);
	    explotion1 = soundPool.load(context, R.raw.explosion1, 1);
	    explotion2 = soundPool.load(context, R.raw.explosion2, 1);
	    thruster = soundPool.load(context, R.raw.jet_loop, 1);
	    
	    //soundPool.play(voidSound, 0, 0, 1, -1, 1);
	}
	public static void release(){

		if(soundPool != null)
			soundPool.release();
		soundPool = null;
	}
	
	public static void setSounds(boolean set){
		allowSounds = set;
	}
	
	public static void playAlarm(StateContainer calledShip){
		if(allowSounds && SSS.controlledShip.stateContainer == calledShip && soundPool!=null){
					soundPool.play(alarm, 1, 1, 10, 2, 1);
		}
	}
	public static void playClick(){
		if(allowSounds && soundPool!=null){
			soundPool.play(toggleControl, 1, 1, 8, 0, 1);
		}
	}
	public static void playEffect(Effect sound, float distance, float pitch){
		if(allowSounds && soundPool!=null){
			distance = 1 - (float)Math.sqrt(distance/DISTANCEFACTOR);
			if(distance>0){
				pitch = 1;//(float)Math.pow(2, Math.sqrt(-pitch/PITCHFACTOR));
				int soundID;
				int priority = 0;
				
				switch(sound){
				case ShotRail:
					soundID = railgun;
					distance *= 0.2f;
					priority = 0;
					break;
				case ShotBeam:
					soundID = laser;
					distance *= 0.2f;
					priority = 10;
					break;
				case ShotPlasma:
					soundID = plasmaShot;
					distance *= 0.3f;
					priority = 10;
					break;
				case ExplPlasma:
					soundID = plasmaExpl;
					distance *= 0.3f;
					priority = 10;
					break;
				case ExplSmall:
					soundID = explComp;
					distance *= 2f;
					priority = 0;
					break;
				case ExplCapital:
					soundID = Generic.RND.nextBoolean()? explotion1:explotion2;
					priority = 10;
					break;
				case WeaponHit:
					soundID = weaponHit;
					distance *= 0.4f;
					priority = 0;
					break;				
				default:
					return;
				}
				soundPool.play(soundID, distance, distance, priority, 0, pitch);
			}
		}
	}
	public static int playLoop(float distance, float pitch){
		if(allowSounds && soundPool!=null){
			distance = 1 - (float)Math.sqrt(distance/DISTANCEFACTOR);
			if(distance>0){
				distance /=5;
				pitch = 1;//(float)Math.pow(2, Math.sqrt(-pitch/PITCHFACTOR));int s = 0;

				return soundPool.play(thruster, distance, distance, 15, -1, pitch);				
			}
		}
		return 0;		
	}
	public static int setLoop(int streamID, float distance, float pitch){
		if(allowSounds && soundPool!=null){
			distance = 1 - (float)Math.sqrt(distance/DISTANCEFACTOR);
			if(distance>0){
				distance /=5;
				pitch = 1;//(float)Math.pow(2, Math.sqrt(-pitch/PITCHFACTOR));
				soundPool.setVolume(streamID, distance, distance);
				return streamID;
			}
		}
		return 0;
	}
	public static void closeLoop(int streamID){
		if(allowSounds && soundPool!=null)
			soundPool.stop(streamID);
	}
}
