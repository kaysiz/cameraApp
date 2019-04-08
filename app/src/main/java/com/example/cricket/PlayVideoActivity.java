package com.example.cricket;

import androidx.appcompat.app.AppCompatActivity;
import ua.polohalo.zoomabletextureview.ZoomableTextureView;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class PlayVideoActivity extends AppCompatActivity  implements TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl, View.OnTouchListener {

//    private VideoView videoView;
//    private TextureView videoView;
//    private MediaPlayer mediaPlayer;
//    private String str_video_url;
//    private MediaController mediaController;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_play_video);
//
//        Date modifiedDate = new Date();
//        String path = Environment.getExternalStorageDirectory().toString()+"/Movies/" + getString(R.string.app_name);
//        Log.d("Files", "Path: " + path);
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//        Log.d("Files", "Size: "+ files.length);
//        for (int i = 0; i < files.length; i++)
//        {
//            long file_time = files[i].lastModified();
//            long current_time = System.currentTimeMillis();
//            Log.d("Files", "FileName:" + files[i].lastModified() + "current time is " + System.currentTimeMillis());
//        }
//        init();
//    }
//
//
//    private void init() {
//        videoView = findViewById(R.id.videoView);
//        str_video_url = getIntent().getStringExtra("video");
//        videoView.setVideoPath(str_video_url);
//        mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
//        mediaController.requestFocus();
//        videoView.start();
//    }

    private float downx = 0;
    private float downy = 0;
    private float upx = 0;
    private float upy = 0;
    private int framing = 0;

    // 4 frame points
    private float point1x = 0;
    private float point1y = 0;
    private float point2x = 0;
    private float point2y = 0;
    private float point3x = 0;
    private float point3y = 0;
    private float point4x = 0;
    private float point4y = 0;


    private MediaPlayer mediaPlayer;

//    private TextureView textureView;

    private String str_video_url;

    private ZoomableTextureView textureView;

//    private MediaController videoController;
    private Handler handler = new Handler();

    private Button back_button;

    private Button clr_button;

    private Canvas canvas;

    private Paint paint;

    private ImageView imageView;

    private Bitmap bitmap;

    static int i = 0;
    float x = 200;
    float y = 200;
    float x1 = 0;
    float y1 = 0;

    private boolean drawing = false;

    private boolean isLBW = false;

    private boolean isRunout = false;

    private int runout = 0;

//

    @Override
    public void onCreate(Bundle SavedInstanceState) {

        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_play_video);

        // Draw lines setup
        Point point = new Point();
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        currentDisplay.getSize(point);
        int width = point.x;
        int height = point.y;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        imageView = findViewById(R.id.drawlines);
        imageView.setImageBitmap(bitmap);
        imageView.setOnTouchListener(this);

        textureView = findViewById(R.id.videoView);
        back_button = findViewById(R.id.back_btn);
        textureView.setSurfaceTextureListener(this);

        mediaPlayer = new MediaPlayer();
        str_video_url = getIntent().getStringExtra("video");
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoPlayList();
            }
        });
        clr_button = findViewById(R.id.button4);
        clr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap.eraseColor(Color.TRANSPARENT);
                canvas.drawBitmap(bitmap,0,0,paint);
            }
        });
        setSpeedOptions();
        // Action button for different modes
        SpeedDialView speedDialView = findViewById(R.id.speedDial);
        speedDialView.inflate(R.menu.menu_speed_dial);
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.action_zoom:
                        imageView.setOnTouchListener(null);
                        drawing = false;
                        isLBW = false;
                        Toast.makeText(getApplicationContext(), "Zoom mode active, frame and draw mode deactivated", Toast.LENGTH_SHORT).show();
                        return false; // true to keep the Speed Dial open
                    case R.id.action_draw:
                        if (drawing) {
                            imageView.setOnTouchListener(null);
                            drawing = false;
                            // make the line transparent
                            Toast.makeText(getApplicationContext(), "Drawing mode deactivated!", Toast.LENGTH_SHORT).show();
                        } else {
                            imageView.setOnTouchListener(PlayVideoActivity.this::onTouch);
                            drawing = true;
                            isLBW = false;
                            framing = 0;
                            paint.setStrokeWidth(10);
                            Toast.makeText(getApplicationContext(), "Drawing mode activated, zoom and frame feature deactivated!", Toast.LENGTH_SHORT).show();
                        }
                        return false; // true to keep the Speed Dial open
                    case R.id.action_lbw:
                        if (isLBW) {
                            isLBW = false;
                            // make the line transparent
                            paint.setAlpha(0);
                            framing = 0;
                            Toast.makeText(getApplicationContext(), "LBW mode deactivated", Toast.LENGTH_SHORT).show();
                        } else {
                            imageView.setOnTouchListener(PlayVideoActivity.this::onTouch);
                            isLBW = true;

                            paint.setStrokeWidth(50);
                            drawing = false;
                            // make the line transparent
                            paint.setAlpha(160);
                            Toast.makeText(getApplicationContext(), "LBW mode activated, zoom, runout and draw mode deactivated", Toast.LENGTH_SHORT).show();
                        }
                        return false; // true to keep the Speed Dial open
                    case R.id.action_runout:
                        if (isRunout) {
                            isRunout = false;
                            // make the line transparent
                            runout = 0;
                            paint.setAlpha(0);
                            Toast.makeText(getApplicationContext(), "Runout mode deactivated", Toast.LENGTH_SHORT).show();
                        } else {
                            imageView.setOnTouchListener(PlayVideoActivity.this::onTouch);
                            isRunout = true;

                            paint.setStrokeWidth(30);
                            isLBW = false;
                            drawing = false;
                            // make the line transparent
                            paint.setAlpha(160);
                            Toast.makeText(getApplicationContext(), "Runout mode activated, zoom, LBW and draw mode deactivated", Toast.LENGTH_SHORT).show();
                        }
                        return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);

        try {
            mediaPlayer.setDataSource(str_video_url);

            mediaPlayer.setSurface(surface);
            mediaPlayer.prepare();
//            MediaController videoController = new MediaController(this);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaplayer) {
                    int vwidth = mediaPlayer.getVideoWidth();
                    int vheight = mediaPlayer.getVideoHeight();
                    mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    mediaplayer.start();
                    mMediaControllerEnable();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // write your code here
            Log.e("Back", "it works");
        }
        return true;    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(this,VideoPlayerPlaylist.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        startActivity(intent);
//        finish();
//        Toast.makeText(this, "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
    }

    private void mMediaControllerEnable(){
        MediaController videoController = new MediaController(this) {
            @Override
            public void hide() {
            }
        };
        videoController.setMediaPlayer(this);//activity which implemented MediaPlayerControl
        videoController.setAnchorView(textureView);
        videoController.setEnabled(true);
        videoController.requestFocus();
//        videoController.setEnabled(true);
        videoController.show();
        handler.post(new Runnable() {

            public void run() {
                videoController.setEnabled(true);
                videoController.show();
            }
        });
    }

    private void openVideoPlayList() {
        Intent intent = new Intent(this, VideoPlayerPlaylist.class);
        startActivity(intent);
    }

    // speed values displayed in the spinner
    private String[] getSpeedStrings() {
        return new String[]{"Speed","0.2", "0.4","0.6", "0.8", "1.0", "1.2", "1.4", "1.6", "1.8", "2.0"};
    }

    private void setSpeedOptions() {
        final Spinner speedOptions = findViewById(R.id.speedOptions);
        String[] speeds = getSpeedStrings();

        ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, speeds);
        speedOptions.setAdapter(arrayAdapter);

        // change player playback speed if a speed is selected
        speedOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mediaPlayer != null) {
                    if (i > 0) {
                        float selectedSpeed = Float.parseFloat(
                                speedOptions.getItemAtPosition(i).toString());

                        changeplayerSpeed(selectedSpeed);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void changeplayerSpeed(float speed) {
        // this checks on API 23 and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
            } else {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                mediaPlayer.pause();
            }
        }
    }

    public float scaler(float point, float scale) {
        return point*scale;
    }

    public float pusher(float point, float middle, float scale) {
        float lambda = 0;
        float portion = 0;

        portion = point - middle;
        lambda = portion * scale;

        return middle + lambda;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;
        float height = size.y;
        Log.e("Width", "" + width);
        Log.e("height", "" + height);

        // Perfect for Nexus 5 when using scaler
        //float scaleX = width/(width-50);
        //float scaleY = height/(height-200);

        // Perfect for Nexus S - when using scaler
        //float scaleX = width/(width-50);
        //float scaleY = height/(height-75);

        // Perfect for Pixel 2 when using scaler
        //float scaleX = width/(width-100);
        //float scaleY = height/(height-180);

        // Huawei P8 Lite 2017
        //float scaleX = width/(width-100);
        //float scaleY = height/(height-180);

        // Samsung S8
        float scaleX = width/(width-50);
        float scaleY = height/(height-75);


        switch (action){
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();

                if (isLBW) {
                    if (framing == 0) {
                        point1x = scaler(downx,scaleX);
                        //point1y = pusher(downy,height/2,scaleY);
                        point1y = scaler(downy,scaleY);
                        Log.d("Files", "PathX: " + downx + " ~ PathY: " + downy);
                        Log.d("Files", "PointX: " + point1x + " ~ PointY: " + point1y);
                    } else if (framing == 1) {
                        point2x = scaler(downx,scaleX);
                        //point2y = pusher(downy,height/2,scaleY);
                        point2y = scaler(downy,scaleY);
                        Log.d("Files", "PathX: " + downx + " ~ PathY: " + downy);
                        Log.d("Files", "PointX: " + point2x + " ~ PointY: " + point2y);
                    }else if (framing == 2) {
                        point3x = scaler(downx,scaleX);
                        //point3y = pusher(downy,height/2,scaleY);
                        point3y = scaler(downy,scaleY);
                        Log.d("Files", "PathX: " + downx + " ~ PathY: " + downy);
                        Log.d("Files", "PointX: " + point3x + " ~ PointY: " + point3y);
                    }else if (framing == 3) {
                        point4x = scaler(downx,scaleX);
                        //point4y = pusher(downy,height/2,scaleY);
                        point4y = scaler(downy,scaleY);
                        Log.d("Files", "PathX: " + downx + " ~ PathY: " + downy);
                        Log.d("Files", "PointX: " + point4x + " ~ PointY: " + point4y);

                        paint.setStrokeWidth(50);
                        paint.setStyle(Paint.Style.FILL);
                        Path path = new Path();

                        path.moveTo(point1x, point1y);
                        path.lineTo(point2x, point2y);
                        path.lineTo(point3x, point3y);
                        path.lineTo(point4x, point4y);
                        path.close();

                        // before draw clear the screen
                        bitmap.eraseColor(Color.TRANSPARENT);
                        canvas.drawBitmap(bitmap,0,0,paint);

                        framing = 0;
                        canvas.drawPath(path, paint);
                        // reset the points to zero after drawing the ponts
                        point1x = 0;
                        point1y = 0;
                        point2x = 0;
                        point2y = 0;
                        point3x = 0;
                        point3y = 0;
                        point4x = 0;
                        point4y = 0;

                        paint.setStyle(Paint.Style.STROKE);
                        imageView.invalidate();
                        break;
                    }
                    framing += 1;
                } else if (isRunout) {
                    if (runout == 1) {
                        canvas.drawLine(downx, downy, upx, upy, paint);
                        imageView.invalidate();
                        runout = 0;
                    } else {
                        runout++;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (drawing) {
                    upx = event.getX();
                    upy = event.getY();
                    canvas.drawLine(downx, downy, upx, upy, paint);
                    imageView.invalidate();
                    downx = upx;
                    downy = upy;
                }
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                if (drawing) {
                    paint.setStrokeWidth(10);
                    canvas.drawLine(downx, downy, upx, upy, paint);
                    imageView.invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

}
