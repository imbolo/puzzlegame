package nine.bolo.puzzle.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Sprite {


	private Bitmap bitmap;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Sprite(Bitmap bitmap) {
	
		this.bitmap = bitmap;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
	}
	public void destroy() 
	{
		bitmap.recycle();
	}
	
	public void drawSelf(Canvas canvas, Paint paint) {
		canvas.drawBitmap(bitmap, x, y, paint);
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean isClicked(int x, int y) {
		
		if(x>=this.x && x<=this.x+this.width &&
				y>=this.y && y<=this.y+this.height)
			return true;
		
		return false;
	}
	//
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
