package nine.bolo.puzzle.view;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import nine.bolo.puzzle.MainActivity;
import nine.bolo.puzzle.R;
import nine.bolo.puzzle.data.Contant;
import nine.bolo.puzzle.data.ImageAdapter;
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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class GameView extends View {
	public GameView(Context context) {
		super(context);
		setupTitleAndButton();
		SharedPreferences sharedPref = ((MainActivity) getContext())
				.getPreferences(Context.MODE_PRIVATE);

		currentMapID = sharedPref.getInt("defaultmapid", 0);

	}

	public Handler hanlder = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				changeLevel();
				break;
			case 1:
				dividePicture();
				reset(Contant.Level);

				break;
			case 9:
				numTimeUsed++;
				invalidate();
				break;
			case 10:
				Toast.makeText(GameView.this.getContext(),
						String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
				break;
			}
			super.handleMessage(msg);
		}

	};

	public void destroy() {
		currentMap.recycle();
		this.adBar.recycle();
		title.destroy();
		freshBtn.destroy();
		lookBtn.destroy();
		rankBtn.destroy();
		openBtn.destroy();
		// backBtn.destroy();
		wallpaperBtn.destroy();
		settingBtn.destroy();
	}

	public void reset(int level) {
		setupMap(level);

		timeThread.isRun = false;
		timeThread = null;
		timeThread = new TimeCaculateThread();

		this.numMoved = 0;
		this.numTimeUsed = 0;
		totalChances = Contant.Level;

		gameFlag = false;
		isStart = false;
		invalidate();
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

	public void dividePicture() {

		Bitmap bitmapResource = null;

		if (Contant.isFreedom == false) {
			bitmapResource = BitmapFactory.decodeResource(this.getResources(),
					Contant.MAPS[currentMapID]);
		} else {
			try {
				bitmapResource = BitmapFactory.decodeFile(getResources()
						.getString(R.string.temppicname));
			} catch (Exception e) {
				bitmapResource = BitmapFactory.decodeResource(
						this.getResources(), Contant.MAPS[1]);
				Log.e(VIEW_LOG_TAG, "FreePic", e);
			}

		}
		if (bitmapResource == null) {
			bitmapResource = BitmapFactory.decodeResource(this.getResources(),
					Contant.MAPS[1]);
		}

		if (bitmapResource.getWidth() != Contant.ScreenWidth) {
			Matrix mat = new Matrix();
			mat.setScale(
					(float) Contant.ScreenWidth / bitmapResource.getWidth(),
					(float) Contant.ScreenWidth / bitmapResource.getWidth());
			bitmapResource = Bitmap.createBitmap(bitmapResource, 0, 0,
					bitmapResource.getWidth(), bitmapResource.getHeight(), mat,
					true);
		}

		bitmapResource = Bitmap.createBitmap(bitmapResource, 0,
				bitmapResource.getHeight() - bitmapResource.getWidth(),
				Contant.ScreenWidth, Contant.ScreenWidth);

		imageList = ImageSpliter.split(bitmapResource, Contant.Level,
				Contant.Level);
		imageRects = new ArrayList<Rect>();

		for (ImagePiece image : imageList) {
			int lx = (image.index % Contant.Level)
					* image.getBitmap().getWidth();
			// title.getHeight() + vgap + freshBtn.getHeight() + vgap
			// + ImageSpliter.HeightPer * i;
			int ly = title.getHeight() + vgap + freshBtn.getHeight() + vgap
					+ (int) (image.index / Contant.Level)
					* image.getBitmap().getHeight();
			imageRects.add(image.index, new Rect(lx, ly, lx
					+ image.getBitmap().getWidth(), ly
					+ image.getBitmap().getHeight()));
		}
	}

	public void changeLevel() {
		String[] levels = new String[] { "3 X 3", "4 X 4", "5 X 5" };
		aDialog = new AlertDialog.Builder(this.getContext())
				.setItems(levels, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Contant.Level = 3;
							dividePicture();
							reset(3);
							break;
						case 1:
							Contant.Level = 4;
							dividePicture();
							reset(4);
							break;
						case 2:
							Contant.Level = 5;
							dividePicture();
							reset(5);
							break;
						}
						SharedPreferences sharedPref = ((MainActivity) getContext())
								.getPreferences(Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putInt("gamelevel", Contant.Level);
						editor.commit();
					}
				}).setTitle(R.string.levelchoose).show();

	}

	public void addPoint(int point) {
		ScoreDataHelper dbHelper = new ScoreDataHelper(getContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);

		ContentValues values = new ContentValues();
		values.put("type", Contant.GameType);
		values.put("time", str);

		db.insert("point", null, values);
		db.close();

		this.invalidate();
	}

	public boolean hasFinishedToday() {
		ScoreDataHelper dbHelper = new ScoreDataHelper(getContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		Cursor cursor = db.rawQuery("select * from point where type="
				+ Contant.GameType + " and time='" + str + "';", null);

		boolean ret = false;
		if (cursor.getCount() == 0) {
			ret = false;
		} else {
			ret = true;
		}
		cursor.close();
		db.close();
		return ret;
	}

	protected void saveScore(VOScore score) {

	}

	protected void judge() {
		if (InGameView.arrayEqual(map, tempMap) == true && gameFlag == false) {
			gameFlag = true;

			LayoutInflater layoutInflater = LayoutInflater.from(this
					.getContext());
			View winView = layoutInflater.inflate(R.layout.winview, null);

			final EditText txtname = (EditText) winView
					.findViewById(R.id.savename);
			txtname.setHint(R.string.defaultname);
			SharedPreferences sharedPref = ((MainActivity) getContext())
					.getPreferences(Context.MODE_PRIVATE);
			txtname.setText(sharedPref.getString("username", ""));

			// Button btnRank = (Button) winView.findViewById(R.id.saverank);
			// btnRank.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// String name = txtname.getText().toString();
			// if (name != null || name != "") {
			// saveScore(new VOScore(Contant.Level + "X"
			// + Contant.Level, currentMapID, name, numMoved,
			// numTimeUsed));
			// }
			//
			// windialog.dismiss();
			//
			// ((MainActivity) GameView.this.getContext()).gotoRankList();
			//
			// }
			// });

			Button btnReplay = (Button) winView.findViewById(R.id.replayc);
			btnReplay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					windialog.dismiss();
				}
			});

			Button btnShare = (Button) winView.findViewById(R.id.share);
			btnShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("image/*");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
					intent.putExtra(Intent.EXTRA_TEXT, Contant.TxtShare1
							+ numTimeUsed/10 + " " + Contant.TxtShare2);

					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					((MainActivity) GameView.this.getContext())
							.startActivity(Intent.createChooser(intent,
									Contant.TxtAppName));
//					windialog.dismiss();
				}
			});

			OnClickListener liGoon = new OnClickListener() {

				@Override
				public void onClick(View v) {
					String name = txtname.getText().toString();
					if(name == null || name.equals(""))
					{
						new AlertDialog.Builder(getContext())
						.setMessage(R.string.nameisnull)
						.setPositiveButton(R.string.ok, null)
						.show();
					}
					else {
						saveScore(new VOScore(Contant.Level + "X"
								+ Contant.Level, currentMapID, name, numMoved,
								numTimeUsed));
						windialog.dismiss();
					}

					
				}
			};
			// 壁纸
			OnClickListener liWall = new OnClickListener() {
				@Override
				public void onClick(View v) {
					setWallPaper();

//					windialog.dismiss();
				}

			};

			View btnGoon = winView.findViewById(R.id.goon);
			btnGoon.setOnClickListener(liGoon);
			View btnWall = winView.findViewById(R.id.wallpaper);
			btnWall.setOnClickListener(liWall);

			this.timeThread.isRun = false;

			String strmessage;
			String title;
			
				title = getResources().getString(R.string.congratulations);
				strmessage = Contant.TxtTimeused + numTimeUsed / 10 + " s   "
						+ getResources().getString(R.string.moveused)
						+ numMoved;

				addPoint(Contant.GameType
						* Integer.parseInt(getResources().getString(
								R.string.localaward)));
			
			
			windialog = new AlertDialog.Builder(this.getContext())
					.setTitle(title).setMessage(strmessage).setView(winView)
					.show();

		}
	}

	class ThreadSetWallpaper implements Runnable {

		@Override
		public void run() {
			WallpaperManager w = WallpaperManager.getInstance(GameView.this
					.getContext());
			w.suggestDesiredDimensions(Contant.ScreenWidth,
					Contant.ScreenHeight);
			Bitmap wall = null;
			if (Contant.isFreedom == false) {
				wall = BitmapFactory.decodeResource(getResources(),
						Contant.MAPS[currentMapID]);

			} else {
				wall = BitmapFactory.decodeFile(getResources().getString(
						R.string.temppicname));
			}
			Matrix mat = new Matrix();
			mat.setScale((float) Contant.ScreenWidth / wall.getWidth(),
					(float) Contant.ScreenHeight / wall.getHeight());
			wall = Bitmap.createBitmap(wall, 0, 0, wall.getWidth(),
					wall.getHeight(), mat, true);
			try {

				w.setBitmap(wall);
				Message msg = new Message();
				msg.what = 10;
				msg.obj = (Object) getResources()
						.getString(R.string.setwallsuc);
				hanlder.sendMessage(msg);

			} catch (Exception e) {
				Message msg = new Message();
				msg.what = 10;
				msg.obj = (Object) getResources()
						.getString(R.string.setwallsuc);
				hanlder.sendMessage(msg);
				e.printStackTrace();
			}
		}

	}

	protected void setWallPaper() {
		new AlertDialog.Builder(getContext())
		.setMessage(R.string.confirmsetting)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Thread(new ThreadSetWallpaper()).start();
			}
		})
		.setNegativeButton(R.string.cancel, null)
		.show();
		
	}

	int tmp;

	protected void setupMap(int level) {

		tempMap = new int[level][level];
		map = new int[level][level];
		// map = new int[Contant.Level][Contant.Level];
		for (int i = 0; i < level; i++) {
			for (int j = 0; j < level; j++) {
				tempMap[i][j] = i * level + j;
				map[i][j] = i * level + j;
			}
		}
		tempMap[level - 1][level - 1] = -1;
		map[Contant.Level - 1][Contant.Level - 1] = -1;
		int t, k, o, p;
		int m = level * level - 1;

		for (int n = 0; n < 100; n++) {
			t = (int) (Math.random() * m);

			k = (int) (Math.random() * m);
			while (k == t) {
				k = (int) (Math.random() * m);
			}
			// o = (int) (Math.random() * m);
			// while (o == t || o == k) {
			// o = (int) (Math.random() * m);
			// }
			// p = (int) (Math.random() * m);
			// while (p == o || p == t || p == k) {
			// p = (int) (Math.random() * m);
			// }

			tmp = map[t / level][t % level];
			map[t / level][t % level] = map[k / level][k % level];
			map[k / level][k % level] = tmp;
			//
			// tmp = map[o / level][o % level];
			// map[o / level][o % level] = map[p / level][p % level];
			// map[p / level][p % level] = tmp;

		}

		// map = new int[][]
		// {
		// {-1,1,2,},
		// {3,4,5,},
		// {6,7,0}
		// };
		// map = new int[][] {
		// { 7, 5, 2 },
		// { 6, -1, 1 },
		// { 4, 3, 0 },
		// };
		//
		// double rd;
		// i = 1; j = 1;
		// for (int n = 0; n < 100; n++) {
		// rd = Math.random();
		// if (rd >= 0 && rd < 0.25) {
		// moveLeft();
		//
		// } else if (rd >= 0.25 && rd < 0.5) {
		// moveUp();
		// } else if (rd >= 0.5 && rd < 0.75) {
		// moveRight();
		//
		// } else if (rd >= 0.75 && rd < 1) {
		// moveDown();
		//
		// }
		// }
		// }
		//
		// if (level == 9) {
		// map = new int[][] {
		// { 0, 2, 3, 7 },
		// { 5, -1, 1, 13 },
		// { 10, 8, 4, 6 },
		// { 9, 12, 11, 14 }, };
		//
		// double rd;
		// i = 1; j = 1;
		//
		// for (int n = 0; n < 500; n++) {
		// rd = Math.random();
		// if (rd >= 0 && rd < 0.25) {
		// moveLeft();
		//
		// } else if (rd >= 0.25 && rd < 0.5) {
		// moveUp();
		// } else if (rd >= 0.5 && rd < 0.75) {
		// moveRight();
		//
		// } else if (rd >= 0.75 && rd < 1) {
		// moveDown();
		//
		// }
		// }
		// }
		//
		// if (level == 10) {
		// map = new int[][] {
		// { 0,10,19,3,16},
		// { 18,6,22,8,9 },
		// { 1,11,-1,23,20 },
		// { 15,4,17,5,2},
		// { 14,21,7,13,12},};
		//
		// double rd;
		// i = 2; j = 2;
		//
		// for (int n = 0; n < 500; n++) {
		// rd = Math.random();
		// if (rd >= 0 && rd < 0.25) {
		// moveLeft();
		//
		// } else if (rd >= 0.25 && rd < 0.5) {
		// moveUp();
		// } else if (rd >= 0.5 && rd < 0.75) {
		// moveRight();
		//
		// } else if (rd >= 0.75 && rd < 1) {
		// moveDown();
		//
		// }
		// }
		// }

	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawColor(Color.BLACK);// 设置画布背景颜色
		title.drawSelf(canvas, paint);

		paint.setTextSize(Contant.ScreenWidth / 25);
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
		paint.setARGB(255, 0, 0, 0);
		paint.setTextAlign(Align.LEFT);
		canvas.drawText(getResources().getString(R.string.levelstr),
				this.getWidth() * 1 / 32, title.getHeight() / 3, paint);
		canvas.drawText(Contant.Level + "X" + Contant.Level,
				this.getWidth() * 1 / 32, title.getHeight() * 2 / 3, paint);
		paint.setTextAlign(Align.RIGHT);
		canvas.drawText(Contant.TxtTime, this.getWidth() * 31 / 32,
				title.getHeight() / 3, paint);
		canvas.drawText(numTimeUsed / 10 + " s", this.getWidth() * 31 / 32,
				title.getHeight() * 2 / 3, paint);
		paint.setTextAlign(Align.CENTER);
		canvas.drawText(Contant.TxtMoved, this.getWidth() * 1 / 2,
				title.getHeight() / 3, paint);
		canvas.drawText(numMoved + "", this.getWidth() * 1 / 2,
				title.getHeight() * 2 / 3, paint);
		paint.setARGB(255, 0, 0, 0);

		freshBtn.drawSelf(canvas, paint);
		lookBtn.drawSelf(canvas, paint);
		rankBtn.drawSelf(canvas, paint);
		openBtn.drawSelf(canvas, paint);

		// backBtn.drawSelf(canvas, paint);
		canvas.drawBitmap(adBar, 0, Contant.ScreenHeight - adBar.getHeight(),
				paint);

		wallpaperBtn.drawSelf(canvas, paint);
		settingBtn.drawSelf(canvas, paint);

		for (int i = 0; i < Contant.Level; i++) {
			for (int j = 0; j < Contant.Level; j++) {

				lx = ImageSpliter.WidthPer * j;

				ly = title.getHeight() + vgap + freshBtn.getHeight() + vgap
						+ ImageSpliter.HeightPer * i;
				if (map[i][j] == -1) {
					canvas.drawRect(lx, ly, lx + ImageSpliter.WidthPer, ly
							+ ImageSpliter.HeightPer, paint);

				} else {

					ImagePiece image2 = imageList.get(map[i][j]);
					canvas.drawBitmap(image2.getBitmap(), lx, ly, paint);
					canvas.drawLine(lx, ly, lx + ImageSpliter.WidthPer, ly,
							paint);
					canvas.drawLine(lx, ly, lx, ly + ImageSpliter.HeightPer,
							paint);

					if (Contant.hasNum == true) {
						paint.setTextSize(ImageSpliter.WidthPer / 2);
						paint.setTypeface(Typeface
								.defaultFromStyle(Typeface.BOLD_ITALIC));
						paint.setARGB(230, 255, 255, 255);
						paint.setTextAlign(Align.CENTER);
						canvas.drawText(String.valueOf(map[i][j] + 1), lx
								+ ImageSpliter.WidthPer / 2, ly
								+ ImageSpliter.WidthPer / 2, paint);
						paint.setARGB(255, 0, 0, 0);
					}

				}

			}
		}

		super.onDraw(canvas);
	}

	protected void setupTitleAndButton() {
		title = new Sprite(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.title));
		title.setPosition(0, 0);

		freshBtn = new Sprite(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.fresh));
		adBar = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.adbar);

		vgap = Contant.ScreenHeight - title.getHeight() - adBar.getHeight()
				- freshBtn.getHeight() - Contant.ScreenWidth;
		vgap /= 3;

		int bottomMenusY = title.getHeight() + vgap;
		hgap = (Contant.ScreenWidth - freshBtn.getWidth() * 6) / 7;
		freshBtn.setPosition(hgap, bottomMenusY);
		lookBtn = new Sprite(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.menu));
		lookBtn.setPosition(hgap + hgap + freshBtn.getWidth(), bottomMenusY);

		rankBtn = new Sprite(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.rank));
		rankBtn.setPosition(hgap * 3 + freshBtn.getWidth() * 2, bottomMenusY);

		wallpaperBtn = new Sprite(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.pic));
		wallpaperBtn.setPosition(hgap * 4 + freshBtn.getWidth() * 3,
				bottomMenusY);

		// backBtn = new
		// Sprite(BitmapFactory.decodeResource(this.getResources(),
		// R.drawable.back));
		//
		// backBtn.setPosition(hgap * 5 + freshBtn.getWidth() * 4,
		// bottomMenusY);

		openBtn = new Sprite(BitmapFactory.decodeResource(this.getResources(),
				R.drawable.mopen));
		openBtn.setPosition(hgap * 5 + freshBtn.getWidth() * 4, bottomMenusY);

		settingBtn = new Sprite(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.menuopn));
		settingBtn
				.setPosition(hgap * 6 + freshBtn.getWidth() * 5, bottomMenusY);

	}

	protected void lastMap() {
		if (currentMapID == 0) {
			currentMapID = Contant.MAPS.length - 1;
		} else {
			currentMapID--;
		}
		Bitmap bitmapResource = BitmapFactory.decodeResource(
				this.getResources(), Contant.MAPS[currentMapID]);
		if (bitmapResource.getWidth() != Contant.ScreenWidth) {
			Matrix mat = new Matrix();
			mat.setScale(
					(float) Contant.ScreenWidth / bitmapResource.getWidth(),
					(float) Contant.ScreenWidth / bitmapResource.getWidth());
			bitmapResource = Bitmap.createBitmap(bitmapResource, 0, 0,
					bitmapResource.getWidth(), bitmapResource.getHeight(), mat,
					true);
		}

		bitmapResource = Bitmap.createBitmap(bitmapResource, 0,
				bitmapResource.getHeight() - bitmapResource.getWidth(),
				Contant.ScreenWidth, Contant.ScreenWidth);

		imageList = ImageSpliter.split(bitmapResource, Contant.Level,
				Contant.Level);
		imageRects = new ArrayList<Rect>();

		for (ImagePiece image : imageList) {
			int lx = (image.index % Contant.Level)
					* image.getBitmap().getWidth();
			int ly = title.getHeight() + freshBtn.getHeight()
					+ (int) (image.index / Contant.Level)
					* image.getBitmap().getHeight();
			imageRects.add(image.index, new Rect(lx, ly, lx
					+ image.getBitmap().getWidth(), ly
					+ image.getBitmap().getHeight()));
		}
		this.numMoved = 0;
		this.numTimeUsed = 0;
	}

	protected void nextMap() {
		if (currentMapID == Contant.MAPS.length - 1) {
			currentMapID = 0;
		} else {
			currentMapID++;
		}
		Bitmap bitmapResource = BitmapFactory.decodeResource(
				this.getResources(), Contant.MAPS[currentMapID]);

		if (bitmapResource.getWidth() != Contant.ScreenWidth) {
			Matrix mat = new Matrix();
			mat.setScale(
					(float) Contant.ScreenWidth / bitmapResource.getWidth(),
					(float) Contant.ScreenWidth / bitmapResource.getWidth());
			bitmapResource = Bitmap.createBitmap(bitmapResource, 0, 0,
					bitmapResource.getWidth(), bitmapResource.getHeight(), mat,
					true);
		}

		bitmapResource = Bitmap.createBitmap(bitmapResource, 0,
				bitmapResource.getHeight() - bitmapResource.getWidth(),
				Contant.ScreenWidth, Contant.ScreenWidth);

		imageList = ImageSpliter.split(bitmapResource, Contant.Level,
				Contant.Level);
		imageRects = new ArrayList<Rect>();

		for (ImagePiece image : imageList) {
			int lx = (image.index % Contant.Level)
					* image.getBitmap().getWidth();
			int ly = title.getHeight() + freshBtn.getHeight()
					+ (int) (image.index / Contant.Level)
					* image.getBitmap().getHeight();
			imageRects.add(image.index, new Rect(lx, ly, lx
					+ image.getBitmap().getWidth(), ly
					+ image.getBitmap().getHeight()));
		}
		this.numMoved = 0;
		this.numTimeUsed = 0;
	}

	protected void moveSelected(int x, int y) {

		for (int i = 0; i < imageRects.size(); i++) {
			if (imageRects.get(i).contains(x, y)) {

				int xx = i / Contant.Level;
				int yy = i % Contant.Level;

				try {
					if (map[xx + 1][yy] == -1) {

						map[xx + 1][yy] = map[xx][yy];
						map[xx][yy] = -1;
						this.invalidate();
						numMoved++;
					}
				} catch (Exception e) {

				}
				try {
					if (map[xx][yy + 1] == -1) {
						map[xx][yy + 1] = map[xx][yy];
						map[xx][yy] = -1;
						this.invalidate();
						numMoved++;
					}
				} catch (Exception e) {
				}
				try {
					if (map[xx - 1][yy] == -1) {
						map[xx - 1][yy] = map[xx][yy];
						map[xx][yy] = -1;
						this.invalidate();
						numMoved++;
					}
				} catch (Exception e) {
				}
				try {
					if (map[xx][yy - 1] == -1) {
						map[xx][yy - 1] = map[xx][yy];
						map[xx][yy] = -1;
						this.invalidate();
						numMoved++;
					}
				} catch (Exception e) {
				}
			}
		}
	}

	protected void onClickButtons(int x, int y) {

		if (freshBtn.isClicked(x, y)) {
			timeThread.isRun = false;
			reset(Contant.Level);
		} else if (openBtn.isClicked(x, y)) {
			Contant.isFreedom = false;
			Message m = new Message();
			m.obj = "Loading...";
			m.what = 4;
			((MainActivity) getContext()).handler.sendMessage(m);
			popupGallery();

		}
		// 自选图片模式
		else if (wallpaperBtn.isClicked(x, y)) {

			setWallPaper();

		}
		// else if (backBtn.isClicked(x, y)) {
		//
		// Contant.isFreedom = false;
		// lastMap();
		// reset(Contant.Level);
		// SharedPreferences sharedPref = ((MainActivity) getContext())
		// .getPreferences(Context.MODE_PRIVATE);
		// SharedPreferences.Editor editor = sharedPref.edit();
		// editor.putBoolean("isfreedom", false);
		// editor.putInt("defaultmapid", currentMapID);
		// editor.commit();
		// }
		else if (lookBtn.isClicked(x, y)) {
			if (totalChances > 0) {
				Bitmap b;
				if (Contant.isFreedom == false) {
					b = BitmapFactory.decodeResource(this.getResources(),
							Contant.MAPS[currentMapID]);
				} else {
					b = BitmapFactory.decodeFile(getResources().getString(
							R.string.temppicname));
				}

				Matrix mat = new Matrix();
				mat.setScale(Contant.ScreenWidth / ((float) 2 * b.getWidth()),
						Contant.ScreenWidth / ((float) 2 * b.getWidth()));
				b = Bitmap.createBitmap(b, 0, b.getHeight() - b.getWidth(),
						b.getWidth(), b.getWidth(), mat, true);
				ImageView imageView = new ImageView(this.getContext());
				imageView.setImageBitmap(b);
				new AlertDialog.Builder(this.getContext())
						.setView(imageView)
						.setMessage(
								getResources().getString(R.string.chancesleft)
										+ " " + totalChances).show();
				totalChances--;
			} else {
				new AlertDialog.Builder(this.getContext()).setMessage(
						R.string.nochances).show();
			}
		} else if (rankBtn.isClicked(x, y)) {
			((MainActivity) this.getContext()).gotoRankList();
		} else if (settingBtn.isClicked(x, y)) {
			((MainActivity) getContext()).handler.sendEmptyMessage(5);
		} else {
			if (isStart == false) {
				isStart = true;
				timeThread.start();
			}
		}

	}

	private void selectFromGallery() {
		SharedPreferences sharedPref = ((MainActivity) getContext())
				.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean("isfreedom", true);
		editor.putInt("defaultmapid", currentMapID);
		editor.commit();
		Contant.isFreedom = true;

		((MainActivity) getContext()).handler.sendEmptyMessage(3);
	}

	private GridView view;

	private void popupGallery() {
		LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
		view = (GridView) layoutInflater.inflate(R.layout.grid_2, null);

		view.setAdapter(new ImageAdapter(getContext()));
		// gridView.setBackground(R.drawable.)
		// 事件监听
		view.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(getContext(), "你选择了" + (position) + " 号图片",
						Toast.LENGTH_SHORT).show();
				currentMapID = position;
				dividePicture();
				reset(Contant.Level);
				// reset(Contant.Level);
				SharedPreferences sharedPref = ((MainActivity) getContext())
						.getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean("isfreedom", false);
				editor.putInt("defaultmapid", currentMapID);
				editor.commit();

				picDialog.dismiss();
			}
		});

		picDialog = new AlertDialog.Builder(getContext())
				.setView(view)
				.setPositiveButton(R.string.fromgallery,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								selectFromGallery();
							}
						}).show();
		((MainActivity) getContext()).handler.sendEmptyMessage(2);
	}

	// private class FreshPoint extends Thread {
	// public boolean isRun = false;
	// @Override
	// public void run() {
	// while (isRun == true) {
	// try {
	// // AdWall.
	// sleep(100);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// }
	// }

	protected class TimeCaculateThread extends Thread {
		public boolean isRun = false;

		public TimeCaculateThread() {
			isRun = true;
		}

		@Override
		public void run() {
			while (isRun == true) {
				try {
					hanlder.sendEmptyMessage(9);
					sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public Bitmap currentMap;

	protected Sprite title;
	protected Sprite freshBtn;
	protected Sprite lookBtn;
	protected Sprite rankBtn;
	protected Sprite openBtn;
	// protected Sprite backBtn;
	protected Sprite wallpaperBtn;
	protected Sprite settingBtn;
	protected Bitmap adBar;

	protected int numMoved = 0;
	protected double numTimeUsed = 0;
	protected int totalChances = 3;

	protected List<Rect> imageRects;
	protected List<ImagePiece> imageList;

	protected int[][] tempMap;
	protected int[][] map;
	protected int currentMapID = 0;
	protected boolean gameFlag; // 当前胜利状况
	protected boolean isStart;

	protected TimeCaculateThread timeThread;

	protected Paint paint = new Paint(); // 创建画笔

	protected float lx;
	protected float ly;

	protected AlertDialog windialog;
	protected AlertDialog aDialog;

	protected boolean isFreedomPic = false;

	private AlertDialog picDialog;

	private int vgap;

	private int hgap;

}
