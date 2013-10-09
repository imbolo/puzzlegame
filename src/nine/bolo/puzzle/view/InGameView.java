package nine.bolo.puzzle.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nine.bolo.puzzle.MainActivity;
import nine.bolo.puzzle.R;

import nine.bolo.puzzle.data.Contant;
import nine.bolo.puzzle.graphics.Sprite;
import nine.bolo.puzzle.util.ImagePiece;
import nine.bolo.puzzle.util.ImageSpliter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class InGameView extends GameView {


	public InGameView(Context context, int NPerPow) {
		super(context);
//		Contant.Level = NPerPow;
		Contant.GameStart = true;

		setupTitleAndButton();
		
		initial();
	}

	public void destroy() {
		timeThread.isRun = false;
		
	}
	
	

	private void initial() {
		
		dividePicture();
		
		timeThread = new TimeCaculateThread();

		setupMap(Contant.Level);
		
	}
	


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			moveSelected(x, y);
			onClickButtons(x, y);
			break;
		}
		judge();
		invalidate();
		return true;
	}

	static public boolean arrayEqual(int[][] a, int b[][]) {

		for (int i = 0; i < a.length; i++) {

			if (!Arrays.equals(a[i], b[i])) {
				return false;
			}
		}

		return true;
	}
	@Override
	protected void judge() {
		if (InGameView.arrayEqual(map, tempMap) == true && gameFlag == false) {
			gameFlag = true;
			
			Button share = new Button(this.getContext());
			share.setText(R.string.share);
			share.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(Intent.ACTION_SEND);   
	                intent.setType("image/*");   
	                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");   
	                intent.putExtra(Intent.EXTRA_TEXT, Contant.TxtShare1+numTimeUsed+" "+Contant.TxtShare2);   
	                
	                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
	                ((MainActivity)InGameView.this.getContext()).startActivity(Intent.createChooser(intent, Contant.TxtAppName));   
				}
			});
			
			DialogInterface.OnClickListener clickListner = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					InGameView.this.nextMap();
					InGameView.this.reset(Contant.Level);
				}
			};
			
			DialogInterface.OnClickListener listenNeu = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setWallPaper();
				}
			};
			this.timeThread.isRun = false;
			new AlertDialog.Builder(this.getContext())
					.setTitle(R.string.congratulations)
					.setMessage(Contant.TxtTimeused + numTimeUsed/10+ " s\n"
							+getResources().getString(R.string.moveused) + numMoved)
					.setView(share)
					.setPositiveButton(R.string.replay, null)
					.setNeutralButton(R.string.wallpaper, listenNeu)
					.setNegativeButton(R.string.goon, clickListner).show();
			
		}
	}


}
