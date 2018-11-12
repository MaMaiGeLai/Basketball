package com.example.simplebasketballscore.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.simplebasketballscore.bean.Player;
import com.example.simplebasketballscore.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoreDB {

	public static final String DB_NAME = "iScore";

	public static final int VERSION = 1;

	private static ScoreDB scoreDB;

	private SQLiteDatabase db;

	/**
	 * 	生成一个DBHelper对象
	 */
	private ScoreDB(Context context) {
		DBHelper dbHelper = new DBHelper(context,DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 同步地、获取一个static ScoreDB实例
	 * @param context
	 */
	public synchronized static ScoreDB getInstance(Context context) {
		if (scoreDB == null) {
			scoreDB = new ScoreDB(context);
		}
		return scoreDB;
	}

	/**
	 * 向Player表中插入队员
	 * @param player
	 */
	public void insertOnePlayer(Player player){
		if(player!=null){
			ContentValues values = new ContentValues();
			values.put("num", player.getNum());
			values.put("name", player.getName());
			values.put("score", player.getScore());
			values.put("isTeamA", player.getIsTeamA());
			db.insert("Player", null, values);
		}
	}

	/**
	 * 删除Player表中的莫个队员
	 * @param player
	 */
	public void deleteOnePlayer(Player player){
		if(player!=null){
			String name = player.getName();
			db.delete("Player", "num=? and name=?", new String[]{name});
		}
	}

	/**
	 * 查询Player表中的所用队员
	 * @return
	 */
	public List<Player> queryAllPlayers(){
		List<Player>list = new ArrayList<Player>();
		Cursor cursor = db.query("Player", null, null, null, null, null, null);
		while(cursor.moveToNext()){
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int score = cursor.getInt(cursor.getColumnIndex("score"));
			int isTeamA = cursor.getInt(cursor.getColumnIndex("isTeamA"));
			Player player = new Player();
			player.setNum(num);
			player.setName(name);
			player.setScore(score);
			player.setIsTeamA(isTeamA);
			list.add(player);
		}
		return list;
	}

	/**
	 * 修改Player表中的记录
	 * @param player
	 */
	public void modifyPlayerScore(Player player){
		if(player!=null){
			ContentValues values = new ContentValues();
			values.put("num", player.getNum());
			values.put("name", player.getName());
			values.put("score", player.getScore());
			values.put("isTeamA", player.getIsTeamA());
			db.update("Player", values, "num=? and name=?",new String[]{""+player.getNum(),player.getName()});
		}
	}

	/**
	 * 根据名称清空表中的记录
	 * @param table
	 */
	public void deleteTable(String table){
		if(table!=null){
			if(table.equals("Player")){
				db.execSQL("DELETE FROM Player");
			}else if(table.equals("Scores")){
				db.execSQL("DELETE FROM Scores");
			}
		}
	}

	/**
	 * 向Scores中插入新的记录
	 * @param player
	 * @param section
	 * @param score
	 */
	public void insertOneScore(Player player,int section,int score){
		if(player!=null && section<5 && section>0){
			ContentValues values = new ContentValues();
			values.put("num", player.getNum());
			values.put("name", player.getName());
			values.put("score", score);
			values.put("isTeamA", player.getIsTeamA());
			values.put("section",section);
			db.insert("Scores", null, values);
		}
	}

	/**
	 * 获取某支队伍某一节的得分
	 * @param isTeamA
	 * @return
	 */
	public int[] queryTeamPerSection(Boolean isTeamA){
		int isTeamACode = 0;
		if(isTeamA==true){
			isTeamACode=0;
		}else if(isTeamA==false){
			isTeamACode=1;
		}else {
			return new int[]{-1};
		}
		int []scores = new int[]{0,0,0,0};
		Cursor cursor = db.query("Scores", null, "isTeamA=?", new String[]{""+isTeamACode},null, null, null);
		while(cursor.moveToNext()){
			int score = cursor.getInt(cursor.getColumnIndex("score"));
			int section = cursor.getInt(cursor.getColumnIndex("section"));
			scores[section-1]+=score;
		}
		return scores;
	}

	/**
	 * 获取某支队伍全场得分
	 * @param isTeamA
	 * @return
	 */
	public int queryAllPlayersPerSection(Boolean isTeamA){
		int ScorePerSection[] = queryTeamPerSection(isTeamA);
		int totalScoreOneTeam = 0 ;
		for(int i=0;i<ScorePerSection.length;i++){
			totalScoreOneTeam +=  ScorePerSection[i];
		}
		return totalScoreOneTeam;
	}

	/**
	 * 获取某个队员所得总分
	 * @param player
	 * @return
	 */
	public int queryOnePlayerTotalScore(Player player){
		int totalScore = 0;
		if(player!=null){
			Cursor cursor = db.query("Scores", null, "num=? and name=?",
					new String[]{""+player.getNum(),player.getName()},null, null, null);
			while(cursor.moveToNext()){
				int score = cursor.getInt(cursor.getColumnIndex("score"));
				totalScore += score;
			}
		}
		return totalScore;
	}

}
