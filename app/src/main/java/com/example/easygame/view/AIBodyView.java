package com.example.easygame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.example.easygame.ai.BodyPart;
import com.example.easygame.ai.Constants;
import com.example.easygame.ai.KeyPoint;
import com.example.easygame.ai.Person;
import com.example.easygame.ai.Position;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class AIBodyView extends View {

    private Person person;

    private Paint mPaint;

    //绘制关键点的半径
    private float circleRadius = 16.0f;

    public AIBodyView(Context context) {
        this(context,null);
    }

    public AIBodyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AIBodyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public AIBodyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(80);
        mPaint.setStrokeWidth(8);
    }


    public void setPerson(Person person){
        this.person = person;
        postInvalidate();
    }
    private Bitmap bitmap;
    public void setPerson(Person person, Bitmap bitmap){
        this.person = person;
        this. bitmap = bitmap;
        postInvalidate();
    }


    int screenWidth = 0;
    int screenHeight = 0;
    int left = 0;
    int right = 0;
    int top = 0;
    int bottom = 0;
    float widthRatio;
    float heightRatio;

    int correctLeft = 0;

    int canvasExtrudeCorrect = 0;

    public void setBoundary(int width,int height){
        if (getHeight() > width) {
            screenWidth = width;
            screenHeight = width;
            left = 0;
            top = (height - getWidth()) / 2;
        } else {
            screenWidth = height;
            screenHeight = height;
            left = (width - height) / 2;
            top = 0;
        }
        right = left + screenWidth;
        bottom = top + screenHeight;
        Log.e("heiheihei",width+"   "+height+"    "+getWidth()+"   "+getHeight());

        widthRatio = ((float)width) / Constants.MODEL_WIDTH;
        heightRatio = ((float)height)/ Constants.MODEL_HEIGHT;


        canvasExtrudeCorrect = getWidth()/Constants.CAMERA_WIDTH;

        correctLeft =(getWidth()-width)/2;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);





        if(bitmap != null){
            canvas.drawBitmap(
                    bitmap,
                    new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                    new Rect(left, top, right, bottom),
                    mPaint
            );
        }




        if(person != null){
            for (KeyPoint keyPoint : person.getKeyPoints()) {
                Log.e("toptotoptotop", left+"  ");

                if (keyPoint.getScore() > Constants.minConfidence) {
                    Position position = keyPoint.getPosition();
                    float adjustedX = (position.getX() * widthRatio + left +correctLeft)*canvasExtrudeCorrect;
                    float adjustedY = position.getY() * heightRatio + top;
                    canvas.drawCircle(adjustedX, adjustedY, circleRadius, mPaint);
                }
            }


            for (Pair<BodyPart,BodyPart> line : Constants.bodyJoints) {
                if (
                        (person.getKeyPoints().get(line.first.ordinal()).getScore() > Constants.minConfidence) &&
                        (person.getKeyPoints().get(line.second.ordinal()).getScore() > Constants.minConfidence)
                ) {
                    canvas.drawLine(
                            person.getKeyPoints().get(line.first.ordinal()).getPosition().getX() * widthRatio + left+correctLeft,
                            person.getKeyPoints().get(line.first.ordinal()).getPosition().getY() * heightRatio + top,
                            person.getKeyPoints().get(line.second.ordinal()).getPosition().getX() * widthRatio + left+correctLeft,
                            person.getKeyPoints().get(line.second.ordinal()).getPosition().getY() * heightRatio + top,
                            mPaint
                    );
                }
            }



        }

    }
}
