package com.example.simplebasketballscore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	public static final String CREATE_PLAYER = "create table Player ("
			+ "id integer primary key autoincrement, " 
			+ "num integer, "
			+ "name text, "
			+ "score integer," 
			+ "isTeamA integer)";
	
	public static final String CREATE_SCORE= "create table Scores ("
			+ "id integer primary key autoincrement, " 
			+ "num integer, "
			+ "name text, "
			+ "score integer," 
			+ "isTeamA integer,"
			+ "section integer)";
	
	public DBHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}

	//在onCreate中创建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		 * 建表
		 */
		db.execSQL(CREATE_PLAYER);
		db.execSQL(CREATE_SCORE);
	}

	//if need 在onUpgrade中更新数据库
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
