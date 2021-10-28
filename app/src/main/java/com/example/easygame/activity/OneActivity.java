package com.example.easygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import com.example.easygame.R;
import com.example.easygame.view.onegame.LeapGameView;

import java.io.IOException;
import java.util.List;

public class OneActivity extends AppCompatActivity {
    private Camera camera;

    private TextureView tv;

    Matrix matrix1 = new Matrix();

    LeapGameView leapGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        tv = findViewById(R.id.tv);

        leapGameView = findViewById(R.id.faceView);

        leapGameView.setCallBack(new LeapGameView.CallBack() {
            @Override
            public void exit() {
                leapGameView.release();
                OneActivity.this.finish();
            }
        });

        tv.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                camera = Camera.open(1);

                try {

                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode("off");
                    parameters.setPreviewFormat(ImageFormat.NV21);

                    List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();

//            Camera.Size size = getFitSize(parameters.getSupportedPictureSizes());
//            Log.e("faceCameraSize",size.width+"   "+size.height);
//            if (width > height)
//                parameters.setPictureSize(size.width, size.height);
//            else
//                parameters.setPictureSize(size.width, size.height);

//            size = getFitSize(parameters.getSupportedPreviewSizes());
//            if (width > height)
//                parameters.setPreviewSize(size.width, size.height);
//            else
//                parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(1920, 1080);
                    parameters.setPreviewSize(1920, 1080);

                    matrix1.setScale(-1, 1);
                    matrix1.postScale(1920 / 2000f, 1080 / 2000f);
                    matrix1.postTranslate(1920 / 2f, 1080 / 2f);


                    camera.setParameters(parameters);
                    camera.setPreviewTexture(surface);
//                    camera.setPreviewCallback(MainActivity.this);
                    camera.startPreview();

                    camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                        @Override
                        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                            if (faces != null && faces.length != 0) {

                                RectF rect = new RectF();

                                matrix1.mapRect(rect, new RectF(faces[0].rect));



                                Log.e("ksksk",  " "+rect.top+"   "+rect.left);
                                if (leapGameView.setRectF(faces[0].id, rect)) {
//                                    tiaozhuan();
                                }

                            } else {
                            }

                        }
                    });
                    camera.startFaceDetection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (camera != null) {
                    camera.stopFaceDetection();
                    camera.stopPreview();
                    camera.release();
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });


    }


}