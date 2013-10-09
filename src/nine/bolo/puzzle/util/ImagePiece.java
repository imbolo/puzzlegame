package nine.bolo.puzzle.util;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class ImagePiece {
	public int index = 0;
	
	private Bitmap bitmap = null;
	
//	public Rect rect;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
//		this.rect = new Rect(left, top, right, bottom)
	}
	

}
