package ru.miet.android.baev.spaceshooter.provider;

import ru.miet.android.baev.spaceshooter.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Sprites {
	public static class Sprite{
		private static Matrix mx = new Matrix();
		private Bitmap pic;
		private float centerX;
		private float centerY;
		
		public Sprite(Bitmap src, float x, float y){
			pic = src;
			centerX = x;
			centerY = y;
		}
		
		public void Draw(Canvas c, float x, float y, float a){
			mx.setTranslate(-centerX, -centerY);
			mx.postRotate(a);
			mx.postTranslate(x, y);
			
			c.drawBitmap(pic, mx, null);
		}
		public void Draw(Canvas c, float x, float y){
			c.drawBitmap(pic, x-centerX, y-centerY, null);
		}
		public void Draw(Canvas c, float x, float y, Paint paint){
			c.drawBitmap(pic, x-centerX, y-centerY, paint);
		}
		public void DrawScale(Canvas c, float x, float y, float s){
			mx.setTranslate(-centerX, -centerY);
			mx.postScale(s,s);
			mx.postTranslate(x, y);
			
			c.drawBitmap(pic, mx, Generic.paintContour);
		}
	}
	
	public static Sprite Star;
	public static Sprite Arrow;
	public static Sprite SF_sprite;
	public static Sprite DH_sprite;
	public static Sprite SM_sprite;
	public static Sprite Explotion;
	
	public static void Init(Context c){
		Star = new Sprite(BitmapFactory.decodeStream(c.getResources().openRawResource(R.drawable.ball)), 1, 1);
		SF_sprite = new Sprite(BitmapFactory.decodeStream(c.getResources().openRawResource(R.drawable.sf_ship)), 19, 16);
		DH_sprite = new Sprite(BitmapFactory.decodeStream(c.getResources().openRawResource(R.drawable.dh_ship)), 18, 45);
		SM_sprite = new Sprite(BitmapFactory.decodeStream(c.getResources().openRawResource(R.drawable.sm_ship)), 34, 34);
		Arrow = new Sprite(BitmapFactory.decodeStream(c.getResources().openRawResource(R.drawable.arrow)), 3, 3);
		Explotion = new Sprite(BitmapFactory.decodeStream(c.getResources().openRawResource(R.drawable.expl)), 134, 134);
	}
}
