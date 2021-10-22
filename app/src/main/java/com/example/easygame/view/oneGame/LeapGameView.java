package com.example.easygame.view.oneGame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.easygame.view.oneGame.bean.GameSetup;
import com.example.easygame.view.oneGame.bean.ObstacleBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;

public class LeapGameView extends View {

    private float timeSelect = 3000;

    private RectF rectF;
    private RectF rectFCenter;
    private Paint mPaint;
    private Paint mPaintText;
    private Paint mPaintRED;
    private int id;


    private RectF rightRectF;
    private RectF backRectF;
    private Random random;

    private List<ObstacleBean> topObs;

    private int width;
    private int height;
    //是否开始游戏
    private boolean isStar = false;

    private boolean isThreadStar = false;


    private CallBack callBack;

    public LeapGameView(Context context) {
        this(context, null);
    }

    public LeapGameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeapGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LeapGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setTextSize(20);

        mPaintRED = new Paint();
        mPaintRED.setColor(Color.RED);
        mPaintRED.setStyle(Paint.Style.FILL);
        mPaintRED.setStrokeWidth(5);
        mPaintRED.setTextSize(20);

        mPaintText = new Paint();
        mPaintText.setColor(Color.BLUE);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTextSize(50);


        random = new Random();

//        generateObs();

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (true) {

                    try {
                        sleep(GameSetup.updateInterval);
                        if(isThreadStar){
                            return;
                        }
                        generateObs();
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

    public boolean setRectF(int id, RectF rectF) {
        this.rectF = new RectF(
                rectF.centerX() - GameSetup.meWidth / 2f, rectF.centerY() - GameSetup.meHeight / 2f,
                rectF.centerX() + GameSetup.meWidth / 2f, rectF.centerY() + GameSetup.meHeight / 2f

        );
        this.id = id;
        if(!isThreadStar){
            postInvalidate();
        }


//        if (rectF != null && rectFCenter != null) {
//            return rectFCenter.contains(rectF);
//        }
        return false;
    }


    int widthRan;
    int heightRan;
    int obsSpacingRan;

    //生成障碍物
    private void generateObs() {

        if (widthRan == 0 || heightRan == 0 || obsSpacingRan == 0) {

            widthRan = random.nextInt(GameSetup.maxObsWidth - GameSetup.minObsWidth) + GameSetup.minObsWidth;
            heightRan = random.nextInt(GameSetup.maxObsHeight - GameSetup.minObsHeight) + GameSetup.minObsHeight;
            obsSpacingRan = random.nextInt(GameSetup.maxObsSpacing - GameSetup.minObsSpacing) + GameSetup.minObsSpacing;

        }

        if (topObs == null) {
            topObs = new ArrayList<>();
        }

        Log.e("numnum", topObs.size() + "   ");


        if (topObs.size() != 0) {
//            topObs.add(new ObstacleBean(new RectF()));

            ObstacleBean ob = null;

            for (int i = 0; i < topObs.size(); i++) {
                ob = topObs.get(i);
                ob.move(GameSetup.distance);
                if (ob.getTopRectF().right < 0) {
                    topObs.remove(ob);
                    i--;
                }
            }
            if (ob != null) {

                if (this.width >= ob.getTopRectF().right + obsSpacingRan) {
                    topObs.add(new ObstacleBean(
                            new RectF(ob.getTopRectF().right + obsSpacingRan,
                                    0, ob.getTopRectF().right + obsSpacingRan + widthRan
                                    , heightRan),
                            new RectF(ob.getTopRectF().right + obsSpacingRan,
                                    heightRan + obsSpacingRan,
                                    ob.getTopRectF().right + obsSpacingRan + widthRan, this.height)

                    ));
                    widthRan = 0;
                    heightRan = 0;
                    obsSpacingRan = 0;
                }
            }


        } else {

            int width = random.nextInt(GameSetup.maxObsWidth - GameSetup.minObsWidth) + GameSetup.minObsWidth;
            int height = random.nextInt(GameSetup.maxObsHeight - GameSetup.minObsHeight) + GameSetup.minObsHeight;
            int obsSpacing = random.nextInt(GameSetup.maxObsSpacing - GameSetup.minObsSpacing) + GameSetup.minObsSpacing;

            topObs.add(new ObstacleBean(
                    new RectF(this.width - width, 0, this.width, height),
                    new RectF(this.width - width, height + obsSpacing, this.width, this.height)

            ));
        }
        postInvalidate();
    }


    private boolean lifeAndDeath() {

        if (topObs == null || topObs.size() == 0 || this.rectF == null) {
            return false;
        }

        for (int i = 0; i < topObs.size(); i++) {
            RectF topRect = topObs.get(i).getTopRectF();
            RectF bottomRectF = topObs.get(i).getBottomRectF();

            if (isIntersection(topRect, this.rectF) || isIntersection(bottomRectF, this.rectF)) {
                return true;
            }

        }


        return false;
    }

    //判断是否相交
    private boolean isIntersection(RectF rectF1, RectF rectF2) {

        return isPortInRectf(rectF2.left, rectF2.top, rectF1)
                || isPortInRectf(rectF2.right, rectF2.top, rectF1)
                || isPortInRectf(rectF2.right, rectF2.bottom, rectF1)
                || isPortInRectf(rectF2.left, rectF2.bottom, rectF1);


    }

    long sweepTime;

    private int getSelectAngle() {

        if (sweepTime == 0) {
            sweepTime = System.currentTimeMillis();
        }

        return (int) ((System.currentTimeMillis() - sweepTime) / timeSelect * 360);

    }

    private boolean isPortInRectf(float x, float y, RectF rectF) {

        Log.e("sdfsdf", (x >= rectF.left) + "   " + (x <= rectF.right) + "   " + (y >= rectF.top) + "  " + (y <= rectF.bottom));
        return x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //给予一个默认值
        if(rectF == null){
            rectF = new RectF(width/2-GameSetup.meWidth/2,height/2-GameSetup.meHeight/2,
                    width/2+GameSetup.meWidth/2,height/2+GameSetup.meHeight/2
                    );
        }
        if (!isStar) {


            if (topObs != null && topObs.size() != 0) {
                for (int i = 0; i < topObs.size(); i++) {
                    canvas.drawRect(topObs.get(i).getTopRectF(), mPaintRED);
                    canvas.drawRect(topObs.get(i).getBottomRectF(), mPaintRED);

                }

            }


        } else {
            canvas.drawColor(Color.WHITE);
            canvas.drawText("游戏结束是否继续？", width / 2 - mPaintText.measureText("游戏结束是否继续？") / 2, height / 2 - 50, mPaintText);


            canvas.drawText("继续", width / 2 - 200, height / 2 + 100, mPaintText);
            canvas.drawText("退出", width / 2 + 200, height / 2 + 100, mPaintText);


            if (isPortInRectf(rectF.centerX(), rectF.centerY(), rightRectF)) {
//                canvas.drawCircle(rightRectF.centerX(), rightRectF.centerY(), 60, mPaint);
                int angle = getSelectAngle();

                if (angle < 360) {
                    canvas.drawArc(rightRectF, -90, angle, false, mPaint);
                } else {
                    onResume();
                }
            } else if (isPortInRectf(rectF.centerX(), rectF.centerY(), backRectF)) {

                int angle = getSelectAngle();
                if (angle < 360) {
                    canvas.drawArc(backRectF, -90, getSelectAngle(), false, mPaint);
                } else {

                    if (callBack != null) {
                        callBack.exit();
                    }

                }

            } else {
                sweepTime = 0;
            }

        }

        if (lifeAndDeath()) {
            isStar = true;
            rightRectF = new RectF(width / 2f - 200, height / 2f + 50, width / 2f - 200 + mPaintText.measureText("继续"), height / 2f + 100);
            backRectF = new RectF(width / 2f + 200, height / 2f + 50, width / 2f + 200 + mPaintText.measureText("继续"), height / 2f + 100);

        }


        if (rectF != null) {
            canvas.drawCircle(rectF.centerX(), rectF.centerY(), 20, mPaint);
        }


//        if(rectFCenter!=null){
//            canvas.drawRect(rectFCenter,mPaintRED);
//        }
    }


    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    private void onResume() {
        topObs.clear();
        isStar = false;
        sweepTime = 0;
    }

    public interface CallBack {
        void exit();
    }


    public void release() {
        isThreadStar = true;
    }
}
