package com.example.vidkrypt.player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vidkrypt.Adapter.Constant;
import com.example.vidkrypt.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoPlayerActivity extends AppCompatActivity {
    PlayerView playerView;
    SimpleExoPlayer player;
    int position;
    String videoTitle;
    TextView title;
    ConcatenatingMediaSource concatenatingMediaSource;
    ImageButton backBtn,playPauseBtn,prevBtn,nextBtn,repeatBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        }
        setTheme(R.style.playerActivityTheme);
        setContentView(R.layout.activity_video_player);
        WindowCompat.setDecorFitsSystemWindows(getWindow(),false);
        WindowInsetsControllerCompat controllerCompat= new WindowInsetsControllerCompat(getWindow(),findViewById(android.R.id.content).getRootView());
        controllerCompat.hide(WindowInsetsCompat.Type.systemBars());
        controllerCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        init();

        playerView=findViewById(R.id.exoplayer_view);
        position=getIntent().getIntExtra("position",0);
        videoTitle=getIntent().getStringExtra("video_title");

        title =findViewById(R.id.video_title);
        title.setText(videoTitle);
        title.isSelected();

        createMedia();
        clickButton();
    }
    private void init()
    {
        backBtn=findViewById(R.id.backBtn);
        playPauseBtn=findViewById(R.id.playPauseBtn);
        nextBtn=findViewById(R.id.nextBtn);
        prevBtn=findViewById(R.id.prevBtn);
        repeatBtn=findViewById(R.id.repeatBtn);
    }
   private void clickButton()
   {
       player.addListener(new Player.Listener() {
           @Override
           public void onPlaybackStateChanged(int playbackState) {
               Player.Listener.super.onPlaybackStateChanged(playbackState);
               if(playbackState==Player.STATE_ENDED)
               {

               }
           }
       });
       backBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(player.isPlaying())
               {
                   player.stop();
                   finish();
               }else
               {
                   finish();
               }
           }
       });
       playPauseBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                if(player.isPlaying())
                {
                    pauseVideos();
                }else
                {
                    playVideos();
                }
           }
       });
       nextBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               player.stop();
               if(Constant.allMediaList.size()-1==position)
               {
                   position=0;
               }else
               {
                   ++position;
               }
               createMedia();
           }
       });
       prevBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               player.stop();
               if(position==0)
               {
                   position=Constant.allMediaList.size()-1;
               }else
               {
                   --position;
               }
               createMedia();
           }
       });
   }
    private void createMedia() {
        String path= Constant.allMediaList.get(position).getPath();
        Uri uri =Uri.parse(path);
        player = new SimpleExoPlayer.Builder(this).build();

        //try
        playerView.setPlayer(player);
        MediaItem mediaItem=MediaItem.fromUri(path);
        player.setMediaItem(mediaItem);
        player.prepare();
        playVideos();

//       DefaultDataSourceFactory dataSourceFactory=new DefaultDataSourceFactory(
//               this,Util.getUserAgent(this, String.valueOf(R.string.app_name)));
//       concatenatingMediaSource=new ConcatenatingMediaSource();
//
//       for(int i=0;i<Constant.allMediaList.size();i++)
//       {
//           new File(String.valueOf(Constant.allMediaList.get(i)));
//          MediaSource mediaSource=new ProgressiveMediaSource.Factory(dataSourceFactory)
//                  .createMediaSource(MediaItem.fromUri(uri));
//          concatenatingMediaSource.addMediaSource(mediaSource);
//       }
//       playerView.setPlayer(player);
//       playerView.setKeepScreenOn(true);
//       player.prepare(concatenatingMediaSource);
//       player.seekTo(position,C.TIME_UNSET);
       playError();
    }

   private void playVideos()
   {

       playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
       player.play();
   }
    private void pauseVideos()
    {
        playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        player.pause();
    }
    private void playError() {
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                Toast.makeText(VideoPlayerActivity.this, "Video Playing Error", Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(player.isPlaying())
        {
            player.stop();
            finish();
        }else
        {
            finish();
        }
    }
}