package com.example.simplebasketballscore.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Chronometer;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint(
		{ "ViewConstructor", "SimpleDateFormat" })
public class MyCountDownClock extends Chronometer
{

	SimpleDateFormat mTimeFormat;

	public MyCountDownClock(Context context, AttributeSet attrs){
		super(context, attrs);
		//指定样式、设置Chronometer Tick监听器
		mTimeFormat = new SimpleDateFormat("mm:ss");
		this.setOnChronometerTickListener(listener);
	}

	public static long mNextTime;
	private OnTimeCompleteListener mListener;

	public MyCountDownClock(Context context){
		super(context);
	}
	/**
	 * OnChronometerTickListener listener
	 */
	private OnChronometerTickListener listener = new OnChronometerTickListener(){
		@Override
		public void onChronometerTick(Chronometer chronometer)
		{
			//当一节走完之后即mNextTime为0时，暂停clock，并触发OnTimeCompleteListener监听器的回调函数；
			//此时mNextTime复位，但clock状态为stop();需要调用clock.start()重新开始倒计时
			if (mNextTime == 0){
				MyCountDownClock.this.stop();
				if (null != mListener){
					mListener.onTimeComplete();
				}
				updateTimeText();
			}else{
				mNextTime--;
				updateTimeText();
			}
		}
	};

	/**
	 * 继续计时
	 */
	public void onResume(){
		this.start();
	}

	/**
	 * 暂停计时
	 */
	public void onPause(){
		this.stop();
	}

	/**
	 * 设置时间格式
	 *
	 * @param pattern
	 *            计时格式
	 */
	public void setTimeFormat(String pattern){
		mTimeFormat = new SimpleDateFormat(pattern);
	}

	public void setOnTimeCompleteListener(OnTimeCompleteListener l){
		mListener = l;
	}

	/**
	 * 初始化时间
	 * @param _time_s
	 */
	public void initTime(long _time_s){
		mNextTime = _time_s;
		updateTimeText();
	}

	/**
	 * 更新显示，在TextView上以指定format显示
	 * 可以看出 Chronometer 实际是一个 TextView
	 */
	private void updateTimeText(){
		this.setText(mTimeFormat.format(new Date(mNextTime * 1000)));
	}

	/**
	 * 定义倒计时结束的Listener接口
	 * @author gh
	 */
	public interface OnTimeCompleteListener{
		void onTimeComplete();
	}

}
