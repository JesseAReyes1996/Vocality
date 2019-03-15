package edu.jreye039.vocality;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class LyricView extends android.support.v7.widget.AppCompatTextView {
    //LyricView size
    private float width;
    private float height;

    //paint to lyric
    private Paint currentPaint;
    private Paint notCurrentPaint;

    //text size of lyric
    private float textHeight = 65;
    private float textMaxSize = 50;
    private float textSize = 40;

    //index of lyric
    private int index = 0;

    private List<LyricInfo> mLrcList = new ArrayList<>();

    public void setmLrcList(List<LyricInfo> LrcList) {
        this.mLrcList = LrcList;
    }

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setFocusable(true);
        //高亮部分
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);	//设置抗锯齿，让文字美观饱满
        currentPaint.setTextAlign(Paint.Align.CENTER);//设置文本对齐方式
    }

   @Override
   protected void onDraw(Canvas canvas){
       super.onDraw(canvas);
       if(canvas == null) {
           return;
       }
       currentPaint.setColor(Color.argb(210, 0, 0, 255));
       currentPaint.setTextSize(30);
       currentPaint.setTypeface(Typeface.SERIF);


       try {
           setText("");
           for(int i =0; i<mLrcList.size();i++) {
               canvas.drawText(this.mLrcList.get(i).getLrcStr(), width / 2, 50+(50*i), currentPaint);
           }


       }catch (Exception e){
           setText(e.toString()+"\n"+e.getMessage());
           e.printStackTrace();
       }
   }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setLrcList(List<LyricInfo> mLrcList){
        this.mLrcList = mLrcList;
    }
}
