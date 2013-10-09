package nine.bolo.puzzle.view;

import nine.bolo.puzzle.HelpActivity;
import nine.bolo.puzzle.MainActivity;
import nine.bolo.puzzle.R;
import nine.bolo.puzzle.StartActivity;
import nine.bolo.puzzle.data.Contant;
import nine.bolo.puzzle.graphics.Sprite;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WelcomeView  extends View{

	private StartActivity activity;
	
	private Paint paint;
	private Matrix matrix;
	
	private Bitmap welcomeBorder;
	

	private int numButtons = 2;
	
//	private Sprite btnQuickstart;
	private Sprite btnLocalChallenge;
	private Sprite btnWorldChallenge;
//	private Sprite btnHelp;
	
	public WelcomeView(Context context) {
		super(context);
		Contant.GameStart = false;
		
		activity = (StartActivity)context;
		
		paint = new Paint(); 
		matrix = new Matrix();
		matrix.setValues(new float[]{
			1,0,0,
			0,1,0,
			0,0,1,
		});
		
		setupButtons();
		
		welcomeBorder = 
				BitmapFactory.decodeResource(context.getResources(), R.drawable.welcome);
		if(welcomeBorder.getHeight() != Contant.ScreenHeight)
		{
			Matrix mat = new Matrix();
			mat.setScale((float)Contant.ScreenWidth/welcomeBorder.getWidth(),
					(float)Contant.ScreenHeight/welcomeBorder.getHeight());
			welcomeBorder = Bitmap.createBitmap(welcomeBorder, 0, 0, 
					welcomeBorder.getWidth(),
					welcomeBorder.getHeight(), mat, true);
		}
		
		
	}

	private void setupButtons() {
//		btnQuickstart = new Sprite(BitmapFactory.decodeResource(activity.getResources(), R.drawable.quickgame));
		btnLocalChallenge = new Sprite(BitmapFactory.decodeResource(activity.getResources(), R.drawable.localchallenge));
		int gap = (Contant.ScreenWidth - btnLocalChallenge.getWidth()*numButtons)/(numButtons+1);
		btnLocalChallenge.setPosition(gap, Contant.ScreenHeight - btnLocalChallenge.getHeight()*7/5);
		
		btnWorldChallenge = new Sprite(BitmapFactory.decodeResource(activity.getResources(), R.drawable.worldchallenge));
		btnWorldChallenge.setPosition(gap*2+btnLocalChallenge.getWidth(), Contant.ScreenHeight - btnLocalChallenge.getHeight()*7/5);
		
		
//		btnWorldChallenge.setPosition(gap, Contant.ScreenHeight - btnQuickstart.getHeight()*6/5);
		
//		btnHelp = new Sprite(BitmapFactory.decodeResource(activity.getResources(), R.drawable.helpbtn));
//		btnHelp.setPosition(gap*2+btnLocalChallenge.getWidth(), Contant.ScreenHeight - btnQuickstart.getHeight()*6/5);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onClickButtons(x, y);
			break;
		}
		
		return true;
	}

	private void onClickButtons(int x, int y) {
//		if(btnQuickstart.isClicked(x, y))
//		{
//			Contant.GameType = 0;
//			StartActivity.startarg = 11;
//			Intent intent = new Intent();
//			intent.setClass(this.getContext(), MainActivity.class);
//			this.getContext().startActivity(intent);
////			activity.handler.sendEmptyMessage(11);
//		}
		
		if(btnLocalChallenge.isClicked(x, y))
		{
			Contant.GameType = 1;
			StartActivity.startarg = 12;
			Intent intent = new Intent();
			intent.setClass(this.getContext(), MainActivity.class);
			this.getContext().startActivity(intent);
//			activity.handler.sendEmptyMessage(12);
		}
		else if(btnWorldChallenge.isClicked(x, y))
		{
			Contant.GameType = 2;
			StartActivity.startarg = 13;
			Intent intent = new Intent();
			intent.setClass(this.getContext(), MainActivity.class);
			this.getContext().startActivity(intent);
//			activity.handler.sendEmptyMessage(13);
		}
//		else if(btnHelp.isClicked(x, y))
//		{
//			Intent intent = new Intent();
//			intent.setClass(this.getContext(), HelpActivity.class);
//			this.getContext().startActivity(intent);
//		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawBitmap(welcomeBorder, matrix, paint);
		
//		btnQuickstart.drawSelf(canvas, paint);
		btnLocalChallenge.drawSelf(canvas, paint);
		btnWorldChallenge.drawSelf(canvas, paint);
//		btnHelp.drawSelf(canvas, paint);
	}


}
