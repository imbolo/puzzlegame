package nine.bolo.puzzle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

import nine.bolo.puzzle.MainActivity.ThreadConnectServer;
import nine.bolo.puzzle.data.Contant;
import nine.bolo.puzzle.data.ScoreDataHelper;
import nine.bolo.puzzle.data.VOScore;
import nine.bolo.puzzle.view.InChallengeGameView;
import nine.bolo.puzzle.view.InGameView;
import nine.bolo.puzzle.view.InWorldGameView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
/**
 * activity for showing rank list
 * @author bolo
 *
 */
public class RankActivity extends Activity  {

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		DialogInterface.OnClickListener lsOk = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ScoreDataHelper dbHelper = new ScoreDataHelper(
						RankActivity.this);
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.delete("score", null, null);
				sqliteQuery();
				listItemAdapter.notifyDataSetChanged();
				db.close();
			}
		};

	
			new AlertDialog.Builder(RankActivity.this)
					.setMessage(
							getResources().getString(R.string.warmingclear))
					.setNegativeButton(R.string.cancel, null)
					.setPositiveButton(R.string.ok, lsOk).show();
		
		return super.onMenuItemSelected(featureId, item);
	}

	public Handler handler = new Handler() {

		private ProgressDialog pDialog;

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 0:// 上传
				pDialog = new ProgressDialog(RankActivity.this);
				pDialog.setMessage((String) msg.obj);
				pDialog.show();

				break;
			case 1://
				pDialog.setMessage((String) msg.obj);
				break;
			case 2:// 结束
				if (pDialog != null) {
					pDialog.dismiss();
				}
				break;
			case 10:
				if (listItemAdapter != null) {
					listItemAdapter.notifyDataSetChanged();
				} else {
					setupList();
				}

				if (pDialog != null) {
					pDialog.dismiss();
				}

				break;
			}
			super.handleMessage(msg);
		}

	};

	private int gametype;

	List<VOScore> data = new ArrayList<VOScore>();

	ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();

	private SimpleAdapter listItemAdapter;

	private String[] level;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank_list);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle(R.string.title_activity_rank_list);

		gametype = Contant.GameType;
		if (Contant.Level == 3) {
			level = new String[] { "3X3" };
		} else if (Contant.Level == 4) {
			level = new String[] { "4X4" };
		} else if (Contant.Level == 5) {
			level = new String[] { "5X5" };
		}
		if (Contant.GameType == 1 || Contant.GameType == 0) {
			sqliteQuery();
			setupList();

		} else if (Contant.GameType == 2) {
			// new Thread(new ThreadQuery()).start();

		}
		//获取时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		strDate = formatter.format(curDate);
		
		setupSpinner();
//		setupButtons();
//		setupRadios();

	}

	
	class SpinnerOnItemSelectListenerLevel implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> AdapterView, View view,
				int position, long arg3) {

			String selected = AdapterView.getItemAtPosition(position)
					.toString();
			if (gametype == 1 || gametype == 0) {
				if (position == 0) {

					level = new String[] { "3X3" };
					sqliteQuery();
					listItemAdapter.notifyDataSetChanged();
				} else if (position == 1) {

					level = new String[] { "4X4" };
					sqliteQuery();
					listItemAdapter.notifyDataSetChanged();
				} else if (position == 2) {

					level = new String[] { "5X5" };
					sqliteQuery();
					listItemAdapter.notifyDataSetChanged();
				}

			} else if (gametype == 2) {
				if (position == 0) {
					level = new String[] { "3X3" };
				} else if (position == 1) {
					level = new String[] { "4X4" };
				} else if (position == 2) {
					level = new String[] { "5X5" };
				}

				new Thread(new ThreadQuery()).start();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			System.out.println("NothingSelected");
		}

	}
	
	class SpinnerOnItemSelectListenerWhere implements OnItemSelectedListener {
		private boolean isFirstFlag = false;
		@Override
		public void onItemSelected(AdapterView<?> AdapterView, View view,
				int position, long arg3) {
			
			if(isFirstFlag == true)
			{
				if (position == 0) {
					gametype = 1;
					sqliteQuery();
					listItemAdapter.notifyDataSetChanged();
				} else if (position == 1) {
					gametype = 2;
					new Thread(new ThreadQuery()).start();
				}
			}
			isFirstFlag = true;
		
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			System.out.println("NothingSelected");
		}

	}
	private String strDate = "";
	class SpinnerOnItemSelectListenerDate implements OnItemSelectedListener {
		private boolean isFirstFlag = false;
		@Override
		public void onItemSelected(AdapterView<?> AdapterView, View view,
				int position, long arg3) {
			
			if(gametype == 2)
			{
				if(isFirstFlag == true)
				{
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					curDate.setDate(curDate.getDate()-position);
					strDate = formatter.format(curDate);
					
					new Thread(new ThreadQuery()).start();
				}
			}
			
			
			
			isFirstFlag = true;
		
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			System.out.println("NothingSelected");
		}

	}


	private RadioGroup rlev;

	private ListView ranklist;


	
	private void setupSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.lvspin);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.lv_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(Contant.Level - 3);
		spinner.setOnItemSelectedListener(new SpinnerOnItemSelectListenerLevel());
		
		
		Spinner spinnerWhere = (Spinner) findViewById(R.id.whspin);
		ArrayAdapter<CharSequence> adapterWh = ArrayAdapter.createFromResource(
				this, R.array.wh_array, android.R.layout.simple_spinner_item);
		adapterWh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerWhere.setAdapter(adapterWh);
		spinnerWhere.setSelection(Contant.GameType - 1);
		spinnerWhere.setOnItemSelectedListener(new SpinnerOnItemSelectListenerWhere());
		
		Spinner spinnerDate = (Spinner) findViewById(R.id.spindate);
		ArrayAdapter<CharSequence> adapterDate = ArrayAdapter.createFromResource(
				this, R.array.arr_date, android.R.layout.simple_spinner_item);
		adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDate.setAdapter(adapterDate);
		spinnerDate.setSelection(0);
		spinnerDate.setOnItemSelectedListener(new SpinnerOnItemSelectListenerDate());
	}

	private void setupList() {
		ranklist = (ListView) findViewById(R.id.ranklist);

		listItemAdapter = new SimpleAdapter(
				this,
				listData,// 数据源
				R.layout.rankitem,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "username", "rank", "moved", "timeused" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.username, R.id.rank, R.id.moved, R.id.timeused });
		ranklist.setAdapter(listItemAdapter);

	}

	private void sqliteQuery() {
		ScoreDataHelper dbHelper = new ScoreDataHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cursor = db.query("score", new String[] { "level", "mapid",
				"name", "moved", "timeused", }, "level=?", level, null, null,
				"timeused");

		int rank = 0;
		listData.clear();
		VOScore score = new VOScore();
		
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("username", cursor.getString(cursor.getColumnIndex("name")));
			map.put("rank", "Rank:" + (++rank));
			map.put("moved", cursor.getString(cursor.getColumnIndex("moved")));
			map.put("timeused",
					cursor.getString(cursor.getColumnIndex("timeused")) + " s");
			listData.add(map);
		}

		cursor.close();
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 1, 1, getResources().getString(R.string.clearlocaldata));
		return true;
	}

	@Override
	protected void onDestroy() {
		// adView.destroy();
		super.onDestroy();
	}

	class ThreadQuery implements Runnable {

		@Override
		public void run() {
			Message m = new Message();
			m.what = 0;
			m.obj = getResources().getString(R.string.connecting);
			handler.sendMessage(m);
			listData.clear();
			ArrayList nameValuePairs = new ArrayList();
			nameValuePairs.add(new BasicNameValuePair("level", level[0]));
			nameValuePairs.add(new BasicNameValuePair("date", strDate));
			/* nameValuePairs.add(new BasicNameValuePair("mapid", "1")); */

			InputStream is = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpPost httppost = new HttpPost("http://"
						+ getResources().getString(R.string.serverurl)
						+ "/pintu/select2.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "utf8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					if (sb.length() < 6) {

						handler.sendEmptyMessage(10);
					} else {
						String result = sb.toString();
						result = result.substring(5);

						JSONArray ja = new JSONArray(result);

						int rank = 0;
						for (int i = 0, l = ja.length(); i < l; i++) {
							JSONObject jo1 = ja.getJSONObject(i);

							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("username", jo1.get("name"));
							map.put("rank", "Rank:" + (++rank));
							map.put("moved", jo1.get("moved"));
							map.put("timeused", jo1.get("timeused") + " s");
							listData.add(map);
						}

						handler.sendEmptyMessage(10);
					}

				} catch (Exception e) {
					m = new Message();
					m.what = 1;
					m.obj = "网络连接失败";
					handler.sendMessage(m);
					try {
						Thread.sleep(800);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					handler.sendEmptyMessage(2);

				}
			} catch (Exception e) {
				m = new Message();
				m.what = 1;
				m.obj = "网络连接失败";
				handler.sendMessage(m);
				try {
					Thread.sleep(800);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				handler.sendEmptyMessage(2);
			}

		}

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
