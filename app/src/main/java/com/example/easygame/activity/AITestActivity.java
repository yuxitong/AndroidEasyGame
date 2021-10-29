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
import android.widget.ImageView;

import com.example.easygame.R;
import com.example.easygame.ai.BodyPart;
import com.example.easygame.ai.Constants;
import com.example.easygame.ai.Device;
import com.example.easygame.ai.Person;
import com.example.easygame.ai.Posenet;
import com.example.easygame.view.AIBodyView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AITestActivity extends AppCompatActivity implements Camera.PreviewCallback {



    private Paint paint = new Paint();

    private Posenet posenet;

    private String filename = "posenet_model.tflite";
    private Device device = Device.GPU;




    private Camera camera;

    private TextureView tv;
    private AIBodyView aiBodyView;



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
        matrix1.postScale(-1, 1);

        tv = findViewById(R.id.tv);
        aiBodyView = findViewById(R.id.bodyView);
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
                    parameters.setPictureSize(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
                    parameters.setPreviewSize(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
//                    matrix1.setScale(-1, 1);
//                    matrix1.postScale(1920 / 2000f, 1080 / 2000f);
//                    matrix1.postTranslate(1920 / 2f, 1080 / 2f);


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
        YuvImage image = new YuvImage(data, ImageFormat.NV21, Constants.CAMERA_WIDTH,Constants.CAMERA_HEIGHT, null);
        if(image!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0,Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT), 80, stream);


            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            //处理镜像问题
            processImage(Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp
                    .getHeight(), matrix1, true));

//            processImage(bmp);
        }
    }

    private void processImage(Bitmap bitmap){

        Bitmap croppedBitmap = cropBitmap(bitmap);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, Constants.MODEL_WIDTH, Constants.MODEL_HEIGHT, true);
        Person person = posenet.estimateSinglePose(scaledBitmap);
        aiBodyView.setPerson(person);
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

        aiBodyView.setBoundary(croppedBitmap.getWidth(),croppedBitmap.getHeight());
        return croppedBitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        posenet.close();
    }
}