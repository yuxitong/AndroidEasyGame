package com.example.easygame.view.twogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.easygame.view.twogame.bean.GameSetup;
import com.example.easygame.view.twogame.bean.MonsterBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;

public class ShootingGameView extends View {

    private int width;
    private int height;

    private Paint monsterPaint;
    private boolean isStart = false;

    private long lastUpGenerateTime;

    //怪物集合
    private List<MonsterBean> monsterList;

    private Random random;

    public ShootingGameView(Context context) {
        this(context, null);
    }

    public ShootingGameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShootingGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ShootingGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        random = new Random();


        monsterPaint = new Paint();
        monsterPaint.setColor(Color.BLACK);
        monsterPaint.setStyle(Paint.Style.STROKE);
        monsterPaint.setStrokeWidth(5);
        monsterPaint.setTextSize(20);


        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        Thread.sleep(GameSetup.refresh);
                        if (isStart) {
                            return;
                        }
                        generateObs();
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        };

        thread.start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        Log.e("widthwidht", width + "   " + height);
//        rectFCenter = new RectF(width / 2 - 100, height / 2 - 100, width / 2 + 100, height / 2 + 100);

    }


    private void monsterMove(){
        if(monsterList == null||monsterList.size()==0){
            return;
        }
        for(int i = 0; i < monsterList.size(); i++){
            MonsterBean mb = monsterList.get(i);


        }
    }

    //生成怪物
    private void generateObs() {
        if (monsterList == null) {
            monsterList = new ArrayList<>();
        }

        if (lastUpGenerateTime == 0) {
            lastUpGenerateTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - lastUpGenerateTime < GameSetup.refreshMosterTime * 1000) {
            return;
        }


        if (this.width == 0 || this.height == 0) {
            return;
        }

        //刷新出多少只怪
        int density = random.nextInt(GameSetup.density - monsterList.size());


        for (int i = 0; i < density; i++) {
            int width = random.nextInt(GameSetup.monsterMaxWidth - GameSetup.monsterMinWidth) + GameSetup.monsterMinWidth;
            int height = random.nextInt(GameSetup.monsterMaxHeight - GameSetup.monsterMinHeight) + GameSetup.monsterMinHeight;


            int left = random.nextInt(this.width - width) + width / 2;
            int right = left + width / 2;
            int top = (random.nextInt(this.height / 2 - height) + height) / 2;
            int bottom = top + height / 2;

            int mosterAttackTime = random.nextInt(GameSetup.mosterAttackMaxTime - GameSetup.mosterAttackMinTime) + GameSetup.mosterAttackMinTime;
            int speed = random.nextInt(GameSetup.maxSpeed - GameSetup.minSpeed) + GameSetup.minSpeed;
            monsterList.add(new MonsterBean(new RectF(left, top, right, bottom), mosterAttackTime, speed, 3, System.currentTimeMillis()));
        }


//        MonsterBean mb = null;
//
//        for(int i = 0; i < monsterList.size(); i++){
//            mb = monsterList.get(i);
//        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (monsterList != null) {
            for (int i = 0; i < monsterList.size(); i++) {
                canvas.drawRect(monsterList.get(i).getMonsterRectF(), monsterPaint);


            }

        }
    }
}
