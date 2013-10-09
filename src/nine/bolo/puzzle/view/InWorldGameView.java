package nine.bolo.puzzle.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import nine.bolo.puzzle.MainActivity;
import nine.bolo.puzzle.R;


import nine.bolo.puzzle.data.Contant;
import nine.bolo.puzzle.data.ScoreDataHelper;
import nine.bolo.puzzle.data.VOScore;
import nine.bolo.puzzle.graphics.Sprite;
import nine.bolo.puzzle.util.ImagePiece;
import nine.bolo.puzzle.util.ImageSpliter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InWorldGameView extends GameView {
	private AlertDialog aDialog;
	
	
	public InWorldGameView(Context context, int NPerPow) {
		super(context);
//		Contant.Level = NPerPow;
		Contant.GameStart = true;

		
		
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
	

	int tmp;
	int i,j;
	
	@Override
	protected void setupMap(int level) {
		
		tempMap = new int[level][level];
		map = new int[level][level];
		// map = new int[Contant.Level][Contant.Level];
		for (int i = 0; i < level; i++) {
			for (int j = 0; j < level; j++) {
				tempMap[i][j] = i * level + j;
			}
		}
		tempMap[level-1][level-1] = -1;
		
		String[] mapStr = Contant.WorldMap.split("#");
		for(int i=0, l=mapStr.length; i<l; i++) {
			mapStr[i] = mapStr[i].substring(4, mapStr[i].length()-1);
		}
		String[] maparr;
		
		maparr = mapStr[level-3].split(",");
		
		for(int i=0; i<level; i++)
		{
			for(int j=0; j<level; j++) {
				map[i][j] = Integer.parseInt(maparr[i*level+j]);
			}
		}
		
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
	protected void saveScore(VOScore score) {
		//本地储存一下
		SharedPreferences sharedPref = ((MainActivity)getContext()).getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("username", score.name);
		editor.commit();
		
		new Thread(new ThreadSaving(score)).start();
	}


	
	class ThreadSaving implements Runnable {
		
		private VOScore score;
		public ThreadSaving(VOScore score) {
			this.score = score;
		}
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 0;
			msg.obj = getResources().getString(R.string.uploading);
			((MainActivity)InWorldGameView.this.getContext()).handler.sendMessage(msg);
			ArrayList nameValuePairs = new ArrayList();
			nameValuePairs.add(new BasicNameValuePair("level", score.level));
			nameValuePairs.add(new BasicNameValuePair("mapid", String.valueOf(score.mapid)));

			nameValuePairs.add(new BasicNameValuePair("name", score.name));
			nameValuePairs.add(new BasicNameValuePair("moved", String.valueOf(score.moved)));
			nameValuePairs.add(new BasicNameValuePair("timeused", String.valueOf(score.timeused/10)));
			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://"+ getResources().getString(R.string.serverurl) +"/pintu/insert.php");
				
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
				HttpResponse response= httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				
			} catch (Exception e) {
				Log.getStackTraceString(e);
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null) {
					sb.append(line+"\n");
				}
				line = sb.toString();
				is.close();
				if(line.equals("0\n")) {
					Message m = new Message();
					m.what = 2;
					
					((MainActivity)InWorldGameView.this.getContext()).handler.sendMessage(m);
				}
				else
				{
					Message m = new Message();
					m.what = 1;
					m.obj = getResources().getString(R.string.connectfailed);
					
					((MainActivity)InWorldGameView.this.getContext()).handler.sendMessage(m);
					Thread.sleep(800);
					m = new Message();
					m.what = 2;
				
					((MainActivity)InWorldGameView.this.getContext()).handler.sendMessage(m);
				}
				
			} catch (Exception e) {
				Log.getStackTraceString(e);
			}
			
		}
		
	}


}
