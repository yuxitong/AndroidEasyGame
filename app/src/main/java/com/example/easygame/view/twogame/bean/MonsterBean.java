package com.example.easygame.view.twogame.bean;

import android.graphics.RectF;

//怪物
public class MonsterBean {

    //怪物位置
    private RectF monsterRectF;
    //存在多少秒攻击一下玩家
    private int attackTime;
    //怪物移动速度
    private int speed;
    //怪物血量
    private int hp;
    //怪物满血血量
    private int maxHp;

    //创建时间
    private long createTime;

    //单次移动距离
    private int monsterMoveDistance;
    //单次
    private int monsterMoveOri;

    public MonsterBean(RectF monsterRectF, int attackTime, int speed, int hp,long createTime) {
        this.monsterRectF = monsterRectF;
        this.attackTime = attackTime;
        this.speed = speed;
        this.hp = hp;
        this.maxHp = hp;
        this.createTime = createTime;

    }

    public RectF getMonsterRectF() {
        return monsterRectF;
    }

    public void setMonsterRectF(RectF monsterRectF) {
        this.monsterRectF = monsterRectF;
    }

    public int getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(int attackTime) {
        this.attackTime = attackTime;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
