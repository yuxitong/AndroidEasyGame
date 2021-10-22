package com.example.easygame.view.oneGame.bean;

import android.graphics.RectF;

public class ObstacleBean {


    private RectF topRectF;
    private RectF bottomRectF;

    public ObstacleBean(RectF topRectF, RectF bottomRectF) {
        this.topRectF = topRectF;
        this.bottomRectF = bottomRectF;
    }

    public RectF getTopRectF() {
        return topRectF;
    }

    public void setTopRectF(RectF topRectF) {
        this.topRectF = topRectF;
    }

    public RectF getBottomRectF() {
        return bottomRectF;
    }

    public void setBottomRectF(RectF bottomRectF) {
        this.bottomRectF = bottomRectF;
    }


    /**
     * 移动
     *
     * @param x 移动距离
     */
    public void move(int x) {

        if (topRectF != null) {
            topRectF.left = topRectF.left - x;
            topRectF.right = topRectF.right - x;
        }

        if (bottomRectF != null) {
            bottomRectF.left = bottomRectF.left -x;
            bottomRectF.right = bottomRectF.right - x;
        }
    }
}
