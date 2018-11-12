package com.example.simplebasketballscore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {

    /**
     * 带pos信息的img
     */
	private int index = -1; 
	
    public int getIndex() {  
        return index;  
    }  
    
    public void setIndex(int index) {  
        this.index = index;  
    }  
    
    public MyImageView(Context context) {  
        super(context);  
    }  
    
    public MyImageView(Context context, AttributeSet attrs) {  
         super(context, attrs);  
    }  
    
    public MyImageView(Context context, AttributeSet attrs, int defStyle) {  
       super(context, attrs, defStyle);  
    }  
    

}	
