package nine.bolo.puzzle;

import nine.bolo.puzzle.data.Contant;
import nine.bolo.puzzle.view.WelcomeView;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;

public class StartActivity extends Activity {

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			new AlertDialog.Builder(this)
			.setMessage(R.string.pleasereply)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			}).setNeutralButton(R.string.review, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW);  
						intent.setData(Uri.parse("market://details?id=nine.bolo.puzzle"));  
						startActivity(intent); 
					} catch (Exception e) {
						new AlertDialog.Builder(StartActivity.this)
						.setMessage(R.string.nomarket)
						.setPositiveButton(R.string.ok, null)
						.show();
					}
					 
				}
			}).show();
			
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	public static int startarg = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupResources();
		setContentView(new WelcomeView(this));
		
		
		/*AdTool.init(this, "ace3f9cb6217d08c", "91822d7382df9076");*/
//		AdTool.addPoints(null, 10);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}
	private void setupResources() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Contant.ScreenWidth = dm.widthPixels;
		Contant.ScreenHeight = dm.heightPixels;

		Contant.TxtAppName = getResources().getString(R.string.app_name);

		Contant.TxtMoved = getResources().getString(R.string.moved);
		Contant.TxtTime = getResources().getString(R.string.time);

		Contant.TxtTimeused = getResources().getString(R.string.timeused);

		Contant.TxtShare1 = getResources().getString(R.string.sharecontent1);
		Contant.TxtShare2 = getResources().getString(R.string.sharecontent2);

		Contant.TxtCurrentLevel = getResources().getString(R.string.levelstr);
		
		/*Contant.CurrentPoint = AdTool.queryPoint(this);*/
		
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}

}
