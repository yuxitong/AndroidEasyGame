package com.example.easygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.TextureView;

import com.example.easygame.R;
import com.example.easygame.ai.BodyPart;
import com.example.easygame.ai.Constants;
import com.example.easygame.ai.Device;
import com.example.easygame.ai.Person;
import com.example.easygame.ai.Posenet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AITestActivity extends AppCompatActivity implements Camera.PreviewCallback {

    List<Pair<BodyPart,BodyPart>> bodyJoints = new ArrayList<Pair<BodyPart,BodyPart>>(){
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

    //识别概率
    private float minConfidence = 0.5f;
    //绘制关键点的半径
    private float circleRadius = 8.0f;

    private Paint paint = new Paint();

    private Posenet posenet;

    //预览分辨率
    private int PREVIEW_WIDTH = 640;
    private int PREVIEW_HEIGHT = 480;


    private String filename = "posenet_model.tflite";
    private Device device = Device.GPU;




    private Camera camera;

    private TextureView tv;

    Matrix matrix1 = new Matrix();

    /** Set the paint color and size.    */
    private void setPaint() {
        paint.setColor(Color.RED);
        paint.setTextSize(80);
        paint.setStrokeWidth(8);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_test);

        posenet = new Posenet(this,filename,device);

        tv = findViewById(R.id.tv);


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
                    camera.setPreviewCallback(AITestActivity.this);
                    camera.startPreview();

//                    camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
//                        @Override
//                        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
//                            if (faces != null && faces.length != 0) {
//
//                                RectF rect = new RectF();
//
//                                matrix1.mapRect(rect, new RectF(faces[0].rect));
//
//
//
//                                Log.e("ksksk",  " "+rect.top+"   "+rect.left);
//                                if (leapGameView.setRectF(faces[0].id, rect)) {
////                                    tiaozhuan();
//                                }
//
//                            } else {
//                            }
//
//                        }
//                    });
//                    camera.startFaceDetection();
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

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        YuvImage image = new YuvImage(data, ImageFormat.NV21, PREVIEW_HEIGHT,PREVIEW_WIDTH, null);
        if(image!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, PREVIEW_HEIGHT,PREVIEW_WIDTH), 80, stream);

            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            processImage(bmp);
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processImage(Bitmap bitmap){
        Bitmap croppedBitmap = cropBitmap(bitmap);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, Constants.MODEL_WIDTH, Constants.MODEL_HEIGHT, true);
        Person person = posenet.estimateSinglePose(scaledBitmap);
        Log.e("shibiejieguo",person.toString()+"   ");
    }


    //处理图片横纵比
    private Bitmap cropBitmap(Bitmap bitmap){
        float bitmapRatio =((float) bitmap.getHeight())/ bitmap.getWidth();
        float modelInputRatio = ((float)Constants.MODEL_HEIGHT) / Constants.MODEL_WIDTH;
        Bitmap croppedBitmap = bitmap;

        // Acceptable difference between the modelInputRatio and bitmapRatio to skip cropping.
        double maxDifference = 1e-5;

        // Checks if the bitmap has similar aspect ratio as the required model input.
        if(Math.abs(modelInputRatio - bitmapRatio) < maxDifference){
            return croppedBitmap;
        }else if(modelInputRatio < bitmapRatio){
            float cropHeight = bitmap.getHeight() - (((float)bitmap.getWidth())/modelInputRatio);
            croppedBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    (int)cropHeight/2,
                    bitmap.getWidth(),
                    (int)(bitmap.getHeight()-cropHeight));
        }else {
            float cropWidth = bitmap.getWidth() - (((float)bitmap.getHeight())/modelInputRatio);
            croppedBitmap = Bitmap.createBitmap(
                    bitmap,
                    (int)cropWidth/2,
                    0,
                    (int)(bitmap.getWidth()-cropWidth),
                    bitmap.getHeight()
            );

        }
        return croppedBitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        posenet.close();
    }
}