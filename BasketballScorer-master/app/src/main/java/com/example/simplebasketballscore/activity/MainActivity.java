package com.example.simplebasketballscore.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.simplebasketballscore.R;
import com.example.simplebasketballscore.adapter.SettingsPlayerAdapter;
import com.example.simplebasketballscore.application.MyApplication;
import com.example.simplebasketballscore.bean.Player;
import com.example.simplebasketballscore.dao.ScoreDB;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gh
 * https://github.com/zh1992
 */
public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
    /*
     * 定义控件
     */
    private EditText teamA_et;
    private EditText teamB_et;
    private EditText perMin_et;
    private ListView listTeamA;
    private ListView listTeamB;
    private ImageView finish;
    private ImageView addPlayerA;
    private ImageView addPlayerB;
    private CheckBox checkbox_two;
    private CheckBox checkbox_four;
    /*
     * 定义数据结构
     */
    private List<Player> playerTeamA = new ArrayList<Player>();
    private List<Player> playerTeamB = new ArrayList<Player>();
    private String teamA_name;
    private String teamB_name;
    private Boolean isFour;
    private int perMin;
    private SettingsPlayerAdapter adapterA;
    private SettingsPlayerAdapter adapterB;

    //ScoreDB 的实例
    private ScoreDB scoreDB;
    //处理adapter中的点击事件
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //初始化UI
        initUI();
        //初始化数据
        adapterA = new SettingsPlayerAdapter(playerTeamA, MainActivity.this);
        adapterB = new SettingsPlayerAdapter(playerTeamB, MainActivity.this);
        listTeamA.setAdapter(adapterA);
        listTeamB.setAdapter(adapterB);
        //声明监听器
        finish.setOnClickListener(this);
        addPlayerA.setOnClickListener(this);
        addPlayerB.setOnClickListener(this);
        checkbox_two.setOnCheckedChangeListener(this);
        checkbox_four.setOnCheckedChangeListener(this);

        //获取ScoreDB实例
        scoreDB = ScoreDB.getInstance(this);

        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Player player = (Player) msg.obj;
                        modifyPlayer(player);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 初始化控件
     */
    private void initUI() {
        teamA_et = (EditText) this.findViewById(R.id.acMain_et_teamA);
        teamB_et = (EditText) this.findViewById(R.id.acMain_et_teamB);
        checkbox_two = (CheckBox) this.findViewById(R.id.acMain_cb_two);
        checkbox_four = (CheckBox) this.findViewById(R.id.acMain_cb_four);
        perMin_et = (EditText) this.findViewById(R.id.acMain_et_perTime);
        addPlayerA = (ImageView) this.findViewById(R.id.acMain_iv_addPlayerA);
        addPlayerB = (ImageView) this.findViewById(R.id.acMain_iv_addPlayerB);
        finish = (ImageView) this.findViewById(R.id.acMain_iv_start);
        listTeamA = (ListView) this.findViewById(R.id.acMain_list_playerA);
        listTeamB = (ListView) this.findViewById(R.id.acMain_list_playerB);
    }

    /**
     * 活动的点击事件
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.acMain_iv_start:
                //判断设置的内容是否正确、完整，如果符合规定便启动GameActivity
                //一项填充内容符合要求就使计数器count_tag++，最后通过count_tag的值判断是否符合要求
                int count_tag = 0;
                //队名A
                teamA_name = teamA_et.getText().toString();
                if (teamA_name != null) {
                    MyApplication.setTeamA(teamA_name);
                    count_tag++;
                }
                //队名B
                teamB_name = teamB_et.getText().toString();
                if (teamB_name != null) {
                    MyApplication.setTeamB(teamB_name);
                    count_tag++;
                }
                //两节还是四节
                isFour = checkbox_four.isChecked();
                //isFour = twoOrFour_switch.isChecked();
                MyApplication.setIsTwoOrFour(isFour);
                count_tag++;
                //每节时间，要求是正整数
                String perMin_str = perMin_et.getText().toString();
                if (perMin_str != null && isNumber(perMin_str)) {
                    perMin = Integer.parseInt(perMin_str);
                    MyApplication.setPerMin(perMin);
                    count_tag++;
                } else if (perMin_str != null && !perMin_str.equals("")) {
                    Toast.makeText(MainActivity.this, "每节时间应为整数", Toast.LENGTH_SHORT).show();
                    perMin_et.setText("");
                }
                //判断是否符合要求，符合即跳转
                if (count_tag == 4) {
                    //启动比赛Activity
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(intent);
                    finish();
                } else if (perMin_str == null || perMin_str.equals("")) {
                    Toast.makeText(MainActivity.this, "未完成,请填充完整", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.acMain_iv_addPlayerA:
                //队伍A添加队员
                addPlayer("A");
                break;
            case R.id.acMain_iv_addPlayerB:
                //队伍B添加队员
                addPlayer("B");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.acMain_cb_two:
                if (isChecked == true) {
                    checkbox_four.setChecked(false);
                } else if (isChecked == false) {
                    checkbox_four.setChecked(true);
                }
                break;
            case R.id.acMain_cb_four:
                if (isChecked == true) {
                    checkbox_two.setChecked(false);
                } else if (isChecked == false) {
                    checkbox_two.setChecked(true);
                }
                break;
        }
    }

    /**
     * 向队伍string中添加队员，人数不限【可以重复点击、添加】
     *
     * @param string
     */
    @SuppressLint("InflateParams")
    private void addPlayer(String string) {
        final String AorB = string;
        //动态加载【添加队员】布局
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_addplayer, null, false);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        //new AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        //new dialog
        final AlertDialog dialog = builder.show();
        final EditText et_num = (EditText) view.findViewById(R.id.viewadd_et_num);
        final EditText et_name = (EditText) view.findViewById(R.id.viewadd_et_name);
        Button btn_confirm = (Button) view.findViewById(R.id.viewadd_btn_confirm);
        Button btn_cancel = (Button) view.findViewById(R.id.viewadd_btn_cancel);
        //取消按钮的点击事件
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //确认按钮的点击事件
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = et_num.getText().toString();
                if (num != null && isNumber(num) == false) {
                    Toast.makeText(MainActivity.this, "输入错误，号码应该为数字", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    String name = et_name.getText().toString();
                    Player player = new Player();
                    player.setNum(Integer.parseInt(num));
                    player.setName(name);
                    if ("A".equals(AorB)) {
                        //0代表TeamA
                        player.setIsTeamA(0);
                        //将player添加到list中，并更新数据库和UI
                        playerTeamA.add(player);
                        scoreDB.insertOnePlayer(player);
                        adapterA.notifyDataSetChanged();
                    } else if ("B".equals(AorB)) {
                        //1代表TeamB
                        player.setIsTeamA(1);
                        //将player添加到list中，并更新数据库和UI
                        playerTeamB.add(player);
                        scoreDB.insertOnePlayer(player);
                        adapterB.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });

    }

    /**
     * 修改运动员信息，可以修改或删除运动员
     *
     * @param player
     */
    @SuppressLint("InflateParams")
    protected void modifyPlayer(final Player player) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_modifyplayer, null, false);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.show();
        final EditText et_num = (EditText) view.findViewById(R.id.viewadd_et_num);
        final EditText et_name = (EditText) view.findViewById(R.id.viewadd_et_name);
        Button btn_confirm = (Button) view.findViewById(R.id.viewadd_btn_confirm);
        Button btn_cancel = (Button) view.findViewById(R.id.viewadd_btn_cancel);
        Button btn_delete = (Button) view.findViewById(R.id.viewadd_btn_delete);
        et_num.setText(player.getNum() + "");
        et_name.setText(player.getName());
        /**
         * 删除球员
         */
        btn_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreDB.deleteOnePlayer(player);
                if (player.getIsTeamA() == 0) {
                    playerTeamA.remove(player);
                    adapterA.notifyDataSetChanged();
                } else {
                    playerTeamB.remove(player);
                    adapterB.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        /**
         * 取消操作
         */
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /**
         * 确认操作
         */
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = et_num.getText().toString();
                if (num != null && isNumber(num) == false) {
                    Toast.makeText(MainActivity.this, "输入错误，号码应该为数字", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    String name = et_name.getText().toString();
                    Player player_update = new Player();
                    player_update.setNum(Integer.parseInt(num));
                    player_update.setName(name);
                    if (player.getIsTeamA() == 0) {
                        //0代表TeamA
                        player_update.setIsTeamA(0);
                        scoreDB.modifyPlayerScore(player);
                        playerTeamA.remove(player);
                        playerTeamA.add(player_update);
                        adapterA.notifyDataSetChanged();
                    } else if (player.getIsTeamA() == 1) {
                        //1代表TeamB
                        player_update.setIsTeamA(1);
                        scoreDB.modifyPlayerScore(player);
                        playerTeamB.remove(player);
                        playerTeamB.add(player_update);
                        adapterB.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 判断一个string是否是正整数
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
     * 当页面被销毁时调用
     */
    @Override
    protected void onDestroy() {
        //清空Player表中的所有记录
        if (playerTeamB.size() > 0 || playerTeamA.size() > 0) {
            scoreDB.deleteTable("Player");
        }
        super.onDestroy();
    }


}
