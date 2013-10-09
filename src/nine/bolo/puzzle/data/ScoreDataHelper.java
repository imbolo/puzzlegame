package nine.bolo.puzzle.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreDataHelper extends SQLiteOpenHelper {

	// 数据名称，
	private static final String DBNAME = "score.db";
	// 数据库版本
	private static final int version = 1;
	
	public ScoreDataHelper(Context context) {
		super(context, DBNAME, null, version);
	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table score(level varchar(4), mapid integer, name varchar(20), moved integer, timeused double);");
		db.execSQL("create table point(type integer, time date);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
