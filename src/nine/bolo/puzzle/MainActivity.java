package nine.bolo.puzzle;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import nine.bolo.puzzle.data.Contant;
import nine.bolo.puzzle.view.GameView;
import nine.bolo.puzzle.view.InChallengeGameView;
import nine.bolo.puzzle.view.InGameView;
import nine.bolo.puzzle.view.InWorldGameView;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;




public class MainActivity extends Activity  {

	private static int SELECT_PICTURE;

	private RelativeLayout layout;
	private GameView inGameView = null;
	
	public ImageView imageView;

	public ProgressDialog pDialog;

	private RelativeLayout.LayoutParams adParams;
	private RelativeLayout.LayoutParams lvParams;

	private File tempFile;

	private int worldLevel = 0;

	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 11:
				Contant.GameType = 0;
				/*if (welcomeView != null) {
					layout.removeView(welcomeView);
					welcomeView = null;
				}*/
				inGameView = new InGameView(MainActivity.this, Contant.Level);
				layout.addView((View) inGameView);


				break;
			case 12:
				/*Contant.GameType = 1;
				if (welcomeView != null) {
					layout.removeView(welcomeView);
					welcomeView = null;
				}*/
				inGameView = new InChallengeGameView(MainActivity.this, Contant.Level);

				layout.addView((View) inGameView);

				break;
			case 13:
				Contant.GameType = 2;
				new Thread(new ThreadConnectServer()).start();
				break;
			case 131:
				/*if (welcomeView != null) {
					layout.removeView(welcomeView);
					welcomeView = null;
				}*/
				inGameView = new InWorldGameView(MainActivity.this, Contant.Level);
				layout.addView((View) inGameView);
				handler.sendEmptyMessage(2);
				break;

			case 0:// 上传
				pDialog = new ProgressDialog(MainActivity.this);
				pDialog.setMessage((String) msg.obj);
				pDialog.show();

				break;
			case 1://
				pDialog.setMessage((String) msg.obj);
				break;
			case 2:// 结束
				pDialog.dismiss();
				if(inGameView == null)
				{
					finish();
				}
				break;
			case 3:// 选择照片
				changePic();
				break;
				//显示任意消息用
			case 4:
				if(pDialog == null)
				{
					pDialog = new ProgressDialog(MainActivity.this);
				}
				pDialog.setMessage((String) msg.obj);
				pDialog.show();
				break;
			case 5:
				MainActivity.this.openOptionsMenu();
				break;
			}
			super.handleMessage(msg);
		}
	};


	class ThreadConnectServer implements Runnable {
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 0;
			msg.obj = getResources().getString(R.string.connecting);
			handler.sendMessage(msg);

			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet("http://"
						+ getResources().getString(R.string.serverurl)
						+ "/pintu/rand.php");

				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

			} catch (Exception e) {
				Message m = new Message();
				m.what = 1;
				m.obj = getResources().getString(R.string.connectfailed);

				handler.sendMessage(m);
				try {
					Thread.sleep(800);
				} catch (InterruptedException e1) {
					m = new Message();
					m.what = 2;

					handler.sendMessage(m);
					Log.getStackTraceString(e);
					e1.printStackTrace();
				}
				m = new Message();
				m.what = 2;

				handler.sendMessage(m);
				Log.getStackTraceString(e);
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				line = sb.toString();
				Contant.WorldMap = line;
				is.close();
				if (line.startsWith("3X3")) {
					Message m = new Message();
					m.what = 131;
//					worldLevel = 3;
					handler.sendMessage(m);
				} else {
					Message m = new Message();
					m.what = 1;
					m.obj = getResources().getString(R.string.connectfailed);

					handler.sendMessage(m);
					Thread.sleep(800);
					m = new Message();
					m.what = 2;

					handler.sendMessage(m);
				}

			} catch (Exception e) {
				Log.getStackTraceString(e);
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		Contant.isFreedom = sharedPref.getBoolean("isfreedom", false);
		Contant.hasNum = sharedPref.getBoolean("hasnum", false);
		this.tempFile = new File(getResources().getString(R.string.temppicname));
		
		
		Contant.Level = sharedPref.getInt("gamelevel", 3);
		
		layout = new RelativeLayout(this);
		/*welcomeView = new WelcomeView(this);
		layout.addView(welcomeView);*/
		setContentView(layout);
		
		handler.sendEmptyMessage(StartActivity.startarg);
		
		
	}
	//转到排行
	public void gotoRankList() {
		Intent intent = new Intent();
		intent.setClass(this, RankActivity.class);
		startActivity(intent);
	}
	

	@SuppressWarnings("unused")
	private void setupLvBtn(int pixels, String strLv) {
		lvParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lvParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		Button btnLv = new Button(this);

		btnLv.setHeight(40);
		btnLv.setText(strLv);
		btnLv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				inGameView.hanlder.sendEmptyMessage(0);
			}
		});

		layout.addView(btnLv, lvParams);
	}



	private void changePic() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.putExtra("crop", "true");

		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		intent.putExtra("output", Uri.fromFile(tempFile));
		intent.putExtra("outputFormat", "JPEG");tempFile.exists();

		startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selectpic)),
				SELECT_PICTURE);
		
	}

	@Override
	protected void onDestroy() {
		if(inGameView != null)
		{
			inGameView.destroy();
		}
	
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				/*
				 * btnCam.setBackgroundDrawable(Drawable.createFromPath(tempFile
				 * .getAbsolutePath()));
				 */
				inGameView.hanlder.sendEmptyMessage(1);
				
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 1, 1, getResources().getString(R.string.menu_supportus));
//		menu.add(1, 1, 1, getResources().getString(R.string.menu_getgold));

		if(Contant.hasNum == false)
		{
			menu.add(2, 2, 1, getResources().getString(R.string.numon));
		}
		else
		{
			menu.add(2, 2, 1, getResources().getString(R.string.numoff));
		}
		
		menu.add(3, 3, 1, getResources().getString(R.string.menu_level));
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getGroupId())
		{
		case 0:
			new AlertDialog.Builder(this)
			.setMessage(R.string.thanks)
			.setPositiveButton(R.string.ok, null)
//			.setNegativeButton(R.string.cancel, null)
			.show();
			break;

		case 2:
			Contant.hasNum = !Contant.hasNum;
			SharedPreferences sharedPref = (this)
			.getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("hasnum", Contant.hasNum);
			editor.commit();
			if(Contant.hasNum == false)
			{
				item.setTitle(R.string.numon);
			}
			else 
			{
				item.setTitle(R.string.numoff);
			}
			inGameView.invalidate();
			break;
		case 3:
			if (inGameView != null) {
				inGameView.hanlder.sendEmptyMessage(0);
			}
			break;
		}
		
		

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		/*if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 这里重写返回键

			if (Contant.GameStart == true && inGameView != null) {
				inGameView.destroy();
				layout.removeAllViews();
				inGameView = null;
				adView = null;
				welcomeView = new WelcomeView(this);
				layout.addView(welcomeView);
			} else if (Contant.GameStart == false) {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (inGameView != null) {
				inGameView.hanlder.sendEmptyMessage(0);
			}

		}*/
		
		return super.onKeyDown(keyCode, event);
	}

}
