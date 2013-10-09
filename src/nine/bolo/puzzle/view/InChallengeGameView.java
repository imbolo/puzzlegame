package nine.bolo.puzzle.view;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InChallengeGameView extends GameView {

	public InChallengeGameView(Context context, int NPerPow) {
		super(context);

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
		// 本地储存一下
		SharedPreferences sharedPref = ((MainActivity) getContext())
				.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("username", score.name);
		editor.commit();

		ScoreDataHelper dbHelper = new ScoreDataHelper(this.getContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cursor = db.query("score", new String[] { "level", "mapid",
				"name", "moved", "timeused", }, "name=?",
				new String[] { score.name }, null, null, "timeused");
		int num = cursor.getCount();
		if (num > 1) {
			cursor.moveToLast();
			db.delete("score", "timeused=?",
					new String[] { String.valueOf(cursor.getDouble(cursor
							.getColumnIndex("timeused"))) });
		}
		cursor.close();
		ContentValues values = new ContentValues();
		values.put("level", score.level);
		values.put("mapid", score.mapid);
		values.put("name", score.name);
		values.put("moved", score.moved);
		values.put("timeused", score.timeused / 10);
		db.insert("score", null, values);
		// db.releaseReference();
		db.close();
	}

}
