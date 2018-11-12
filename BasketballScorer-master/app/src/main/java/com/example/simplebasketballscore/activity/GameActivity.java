package com.example.simplebasketballscore.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplebasketballscore.R;
import com.example.simplebasketballscore.adapter.GamePlayerAdapter;
import com.example.simplebasketballscore.application.MyApplication;
import com.example.simplebasketballscore.bean.Player;
import com.example.simplebasketballscore.dao.ScoreDB;
import com.example.simplebasketballscore.view.MyCountDownClock;
import com.example.simplebasketballscore.view.MyCountDownClock.OnTimeCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameActivity extends Activity implements OnClickListener {

	/**
	 * 声明所需要的UI控件
	 */
	private TextView tv_teamA;
	private TextView tv_teamB;
	private TextView tv_scoreA;
	private TextView tv_scoreB;
	private TextView tv_section;
	private MyCountDownClock clock;
	private TextView tv_teamA_perSection;
	private TextView tv_teamB_perSection;
	private TextView tv_score_1A;
	private TextView tv_score_2A;
	private TextView tv_score_3A;
	private TextView tv_score_4A;
	private TextView tv_score_1B;
	private TextView tv_score_2B;
	private TextView tv_score_3B;
	private TextView tv_score_4B;
	private ListView listA;
	private ListView listB;
	private LinearLayout linear;

	/**
	 * 声明需要的数据类型，成员变量
	 */
	private List<Player> mDataA = new ArrayList<Player>();
	private List<Player> mDataB = new ArrayList<Player>();
	private GamePlayerAdapter adapterA;
	private GamePlayerAdapter adapterB;
	private ScoreDB scoreDB;
	private Boolean isFour = MyApplication.getIsTwoOrFour();
	public static Handler handler;
	private int CurrentSectionIndex = 1;
	public Boolean isPause = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_game);
		scoreDB = ScoreDB.getInstance(this);
		// 初始化UI
		init();
		// 初始化数据
		initData();
		// 注册监听器
		listA.setAdapter(adapterA);
		listB.setAdapter(adapterB);
		clock.setOnClickListener(this);

		/**
		 * clock倒计时完成的监听器 需判断比赛是两节还是四节，判断当前是第几节
		 * 节间跳转之后clock默认为stop状态，需要再点击一次才能继续计时
		 */
		clock.setOnTimeCompleteListener(new OnTimeCompleteListener() {
			@Override
			public void onTimeComplete() {
				if (isFour && CurrentSectionIndex < 4) {
					CurrentSectionIndex++;
					isPause = true;
					clock.initTime(60 * MyApplication.getPerMin());
					tv_section.setText("第" + CurrentSectionIndex + "节");
				} else if (isFour && CurrentSectionIndex == 4) {
					clock.stop();
					Toast.makeText(GameActivity.this, "Game Over",
							Toast.LENGTH_SHORT).show();
				} else if (isFour == false && CurrentSectionIndex < 2) {
					CurrentSectionIndex++;
					isPause = true;
					clock.initTime(60 * MyApplication.getPerMin());
					tv_section.setText("第" + CurrentSectionIndex + "节");
				} else {
					clock.stop();
					Toast.makeText(GameActivity.this, "Game Over",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		/**
		 * 处理adapter中按钮的点击事件
		 */
		handler = new Handler() {
			@SuppressLint("HandlerLeak")
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 2:
						// 利用msg.arg1传递ImageView的position属性值
						int position = msg.arg1;
						Player player = (Player) msg.obj;
						modifyScore(position, player);
						break;
				}
			}
		};
	}

	/**
	 * 初始化控件
	 */
	@SuppressLint("InflateParams")
	private void init() {
		tv_teamA = (TextView) this.findViewById(R.id.acGame_tv_teamA);
		tv_teamB = (TextView) this.findViewById(R.id.acGame_tv_teamB);
		tv_scoreA = (TextView) this.findViewById(R.id.acGame_tv_scoreA);
		tv_scoreB = (TextView) this.findViewById(R.id.acGame_tv_scoreB);
		tv_section = (TextView) this.findViewById(R.id.acGmae_tv_section);
		clock = (MyCountDownClock) this
				.findViewById(R.id.acGame_countDownClock);
		// 根据两节还是四节，动态添加布局的LinearLayout容器
		linear = (LinearLayout) this
				.findViewById(R.id.acGame_view_scorePerSection);
		if (isFour) {
			View view = LayoutInflater.from(this).inflate(
					R.layout.view_foursection, null);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			linear.addView(view, params);
			tv_score_1A = (TextView) view
					.findViewById(R.id.viewFourSection_tv_num1A);
			tv_score_2A = (TextView) view
					.findViewById(R.id.viewFourSection_tv_num2A);
			tv_score_1B = (TextView) view
					.findViewById(R.id.viewFourSection_tv_num1B);
			tv_score_2B = (TextView) view
					.findViewById(R.id.viewFourSection_tv_num2B);
			tv_score_3A = (TextView) view
					.findViewById(R.id.viewFourSection_tv_num3A);
			tv_score_4A = (TextView) view
					.findViewById(R.id.viewFourSection_tv_num4A);
			tv_score_3B = (TextView) view
					.findViewById(R.id.viewFourSection_tv_num3B);
			tv_score_4B = (TextView) view
					.findViewById(R.id.viewFourSection_tv_num4B);
			tv_teamA_perSection = (TextView) this
					.findViewById(R.id.viewFourSection_teamA);
			tv_teamB_perSection = (TextView) this
					.findViewById(R.id.viewFourSection_teamB);
		} else {
			View view = LayoutInflater.from(this).inflate(
					R.layout.view_twosection, null);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			linear.addView(view, params);
			tv_teamA_perSection = (TextView) view
					.findViewById(R.id.viewTwoSection_teamA);
			tv_teamB_perSection = (TextView) view
					.findViewById(R.id.viewTwoSection_teamB);
			tv_score_1A = (TextView) view
					.findViewById(R.id.viewTwoSection_tv_num1A);
			tv_score_2A = (TextView) view
					.findViewById(R.id.viewTwoSection_tv_num2A);
			tv_score_1B = (TextView) view
					.findViewById(R.id.viewTwoSection_tv_num1B);
			tv_score_2B = (TextView) view
					.findViewById(R.id.viewTwoSection_tv_num2B);
		}
		listA = (ListView) this.findViewById(R.id.acGame_list_playerA);
		listB = (ListView) this.findViewById(R.id.acGame_list_playerB);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		tv_teamA.setText(MyApplication.getTeamA());
		tv_teamB.setText(MyApplication.getTeamB());
		tv_teamA_perSection.setText(MyApplication.getTeamA());
		tv_teamB_perSection.setText(MyApplication.getTeamB());
		tv_scoreA.setText("0分");
		tv_scoreB.setText("0分");
		tv_section.setText("第1节");
		clock.initTime(60 * MyApplication.getPerMin());
		tv_score_1A.setText("0分");
		tv_score_2A.setText("0分");
		tv_score_1B.setText("0分");
		tv_score_2B.setText("0分");
		if (isFour) {
			tv_score_3A.setText("0分");
			tv_score_4A.setText("0分");
			tv_score_3B.setText("0分");
			tv_score_4B.setText("0分");
		}
		List<Player> list = new ArrayList<Player>();
		list = scoreDB.queryAllPlayers();
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				Player player = list.get(i);
				if (player.getIsTeamA() == 0) {
					mDataA.add(player);
				} else if (player.getIsTeamA() == 1) {
					mDataB.add(player);
				}
			}
		}
		adapterA = new GamePlayerAdapter(mDataA, GameActivity.this);
		adapterB = new GamePlayerAdapter(mDataB, GameActivity.this);
	}

	/**
	 * 活动的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.acGame_countDownClock:
				if (isPause == true) {
					clock.start();
					isPause = false;
				} else if (isPause == false) {
					clock.stop();
					isPause = true;
				}
				break;
		}
	}

	/**
	 * 处理每一次得分，更新每个人、相应节、总体的得分
	 *
	 * @param position
	 * @param player
	 */
	protected void modifyScore(final int position, final Player player) {
		View view  = LayoutInflater.from(GameActivity.this).inflate(R.layout.view_modifyscore,null,false);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(params);
		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setView(view);
		final AlertDialog dialog = builder.show();
		TextView tv_num = (TextView)view.findViewById(R.id.viewScore_tv_num);
		TextView tv_name = (TextView)view.findViewById(R.id.viewScore_tv_name);
		TextView tv_score = (TextView)view.findViewById(R.id.viewScore_tv_score);
		final EditText et_num_add  = (EditText)view.findViewById(R.id.viewScore_et_add);
		Button btn_confirm = (Button)view.findViewById(R.id.viewScore_btn_confirm);
		Button btn_cancel = (Button)view.findViewById(R.id.viewScore_btn_cancel);
		final Switch scoreSwitch =(Switch)view.findViewById(R.id.viewScore_et_scoreSwitch);

		tv_num.setText(player.getNum()+"");
		tv_name.setText(player.getName());
		tv_score.setText(player.getScore()+"分");
		/**
		 * 取消按钮
		 */
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//dialog有dismiss方法，AlertDialog.Builder没有该方法
				dialog.dismiss();
			}
		});
		/**
		 * 确认按钮
		 */
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String num_add = et_num_add.getText().toString();
				if(num_add!=null&&isNumber(num_add)==false){
					if(scoreSwitch.isChecked()==true){
						Toast.makeText(GameActivity.this, "输入错误，新增分数应该为正整数", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(GameActivity.this, "输入错误，减去分数应该为正整数", Toast.LENGTH_SHORT).show();
					}
				}else{
					/**
					 * 这样写可能有问题，由一个string 转成int的过程
					 */
					int num_et = Integer.parseInt(num_add);
					if(scoreSwitch.isChecked()==false){
						num_et = 0- num_et;
					}
					//插入到Score表中，并更新Player表
					scoreDB.insertOneScore(player, CurrentSectionIndex,num_et);
					Player player4update = new Player();
					player4update.setNum(player.getNum());
					player4update.setName(player.getName());
					player4update.setIsTeamA(player.getIsTeamA());
					player4update.setScore(player.getScore()+num_et);
					scoreDB.modifyPlayerScore(player4update);
					if(player.getIsTeamA()==0){
						//移除Player再在原来的位置上添加新player，从而保证队员间位置不变
						mDataA.get(position).setScore(player4update.getScore());
						adapterA.notifyDataSetChanged();
						//更新比分
						updateScore();
					}else if(player.getIsTeamA()==1){
						//移除Player再在原来的位置上添加新player，从而保证队员间位置不变
						mDataB.get(position).setScore(player4update.getScore());
						adapterB.notifyDataSetChanged();
						//更新比分
						updateScore();
					}
				}
				dialog.dismiss();
			}
		});
	}

	/**
	 * 更新两队每节、总体的得分
	 */
	protected void updateScore() {
		tv_scoreA.setText(scoreDB.queryAllPlayersPerSection(true) + "分");
		tv_scoreB.setText(scoreDB.queryAllPlayersPerSection(false) + "分");
		int perScoreA[] = new int[4];
		int perScoreB[] = new int[4];
		perScoreA = scoreDB.queryTeamPerSection(true);
		perScoreB = scoreDB.queryTeamPerSection(false);
		if (isFour) {
			tv_score_1A.setText("" + perScoreA[0] + "分");
			tv_score_2A.setText("" + perScoreA[1] + "分");
			tv_score_3A.setText("" + perScoreA[2] + "分");
			tv_score_4A.setText("" + perScoreA[3] + "分");
			tv_score_1B.setText("" + perScoreB[0] + "分");
			tv_score_2B.setText("" + perScoreB[1] + "分");
			tv_score_3B.setText("" + perScoreB[2] + "分");
			tv_score_4B.setText("" + perScoreB[3] + "分");
		} else if (isFour == false) {
			tv_score_1A.setText("" + perScoreA[0] + "分");
			tv_score_2A.setText("" + perScoreA[1] + "分");
			tv_score_1B.setText("" + perScoreB[0] + "分");
			tv_score_2B.setText("" + perScoreB[1] + "分");
		}
	}

	/**
	 * 判断string是否是正整数
	 *
	 * @param str
	 * @return
	 */
	public Boolean isNumber(String str) {
		if (str == null || "".equals(str.trim())) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str.trim());
		if (isNum.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 连续点击两次返回键退出应用
	 */
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(GameActivity.this, "再按一次退出应用",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			// return true 表示截断事件，使其不再传递
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 当页面销毁时调用
	 */
	@Override
	protected void onDestroy() {
		// 清空Scores表中的所有记录
		if (mDataA.size() > 0 || mDataB.size() > 0) {
			scoreDB.deleteTable("Scores");
		}
		super.onDestroy();
	}

}
