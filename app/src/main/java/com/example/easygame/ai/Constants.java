package com.example.easygame.ai;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final int REQUEST_CAMERA_PERMISSION = 1;
    public static final int MODEL_WIDTH = 257;
    public static final int MODEL_HEIGHT = 257;

    public static final int CAMERA_WIDTH = 1920;
    public static final int CAMERA_HEIGHT = 1080;

    //识别概率
    public static final float minConfidence = 0.5f;


    public static final List<Pair<BodyPart,BodyPart>> bodyJoints = new ArrayList<Pair<BodyPart,BodyPart>>(){
        {
            this.add(new Pair<>(BodyPart.LEFT_WRIST, BodyPart.LEFT_ELBOW));
            this.add(new Pair<>(BodyPart.LEFT_ELBOW, BodyPart.LEFT_SHOULDER));
            this.add(new Pair<>(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER));
            this.add(new Pair<>(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW));
            this.add(new Pair<>(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST));
            this.add(new Pair<>(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP));
            this.add(new Pair<>(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP));
            this.add(new Pair<>(BodyPart.RIGHT_HIP, BodyPart.RIGHT_SHOULDER));
            this.add(new Pair<>(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE));
            this.add(new Pair<>(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE));
            this.add(new Pair<>(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE));
            this.add(new Pair<>(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE));
        }
    };
}
