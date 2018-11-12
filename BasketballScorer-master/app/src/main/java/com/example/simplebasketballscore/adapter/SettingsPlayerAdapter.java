package com.example.simplebasketballscore.adapter;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.simplebasketballscore.R;
import com.example.simplebasketballscore.activity.MainActivity;
import com.example.simplebasketballscore.bean.Player;
import com.example.simplebasketballscore.view.MyImageView;

import java.util.ArrayList;
import java.util.List;

public class SettingsPlayerAdapter extends BaseAdapter {

    private List<Player> mData = new ArrayList<Player>();
    private Context context;

    public SettingsPlayerAdapter(List<Player> mData, Context context) {
        super();
        this.mData = mData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Player player = mData.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_player, parent, false);
            holder = new ViewHolder();
            holder.num = (TextView) convertView.findViewById(R.id.adPlayer_tv_num);
            holder.name = (TextView) convertView.findViewById(R.id.adPlayer_tv_name);
            holder.edit = (MyImageView) convertView.findViewById(R.id.adPlayer_iv_edit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.edit.setIndex(position);
        holder.num.setText("" + player.getNum());
        holder.name.setText(player.getName());

        holder.edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((MyImageView) v).getIndex();
                Player myPlayer = mData.get(position);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = myPlayer;
                MainActivity.handler.sendMessage(msg);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView num;
        TextView name;
        MyImageView edit;
    }

}
