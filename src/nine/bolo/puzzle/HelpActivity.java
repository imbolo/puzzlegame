package nine.bolo.puzzle;

import nine.bolo.puzzle.data.Contant;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.Menu;
import android.view.View;

/**
 * help info activity
 * @author bolo
 *
 */
public class HelpActivity extends Activity {
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		helpView.destroy();
		super.onDestroy();
	}

	public Paint paint;
	public Matrix matrix;
	public HelpView helpView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		helpView = new HelpView(this);
		setContentView(helpView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_help, menu);
		return true;
	}
	
	
	class HelpView extends View {
		
		private Bitmap imgHelp; 
		@Override
		protected void onDraw(Canvas canvas) {
			
			super.onDraw(canvas);
			canvas.drawBitmap(imgHelp, matrix, paint);
		}
		
		public void destroy() {
			imgHelp.recycle();
		}

		public HelpView(Context context) {
			super(context);
			paint = new Paint(); 
			matrix = new Matrix();
			matrix.setValues(new float[]{
				1,0,0,
				0,1,0,
				0,0,1,
			});
			imgHelp = BitmapFactory.decodeResource(context.getResources(), R.drawable.help);
			if(imgHelp.getHeight() != Contant.ScreenHeight)
			{
				Matrix mat = new Matrix();
				mat.setScale((float)Contant.ScreenWidth/imgHelp.getWidth(),
						(float)Contant.ScreenHeight/imgHelp.getHeight());
				imgHelp = Bitmap.createBitmap(imgHelp, 0, 0, 
						imgHelp.getWidth(),
						imgHelp.getHeight(), mat, true);
			}
			
		}
		
	}

}
