package frontend;

import java.util.ArrayList;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;
import android.widget.TextView;
import backend.Artists;
import backend.GridAdapter;
import backend.MusicController;
import backend.MusicService;
import backend.MusicService.MusicBinder;
import backend.PlaylistOp;
import backend.SongAdapter;

import com.example.samplemusic.R;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

public class Serve extends Service implements MediaPlayerControl{
	private static boolean IS_ART_DONE=false;
	private static int DP_OF_BUTTONS_2=105;
	private static int SEEKBAR_STATE=-1;//-1 means not started even once
										//0 means just closed
										//1 means opened and touched
										//2 means opened but not touched
	private static Context ctx;
	private static WindowManager windowManager;
	private static RelativeLayout back;
	private RelativeLayout backView;
	private RelativeLayout body;
	private Body bodyTopLeft;
	private RelativeLayout bodyTopRight;
	private static SeekArcBody bar;
	private static Options op1;
	private static boolean optionOpened=false;
	private static CircleButton opBut;
	private static CircleButton opQueue;
	private static CircleButton opPlaylist;
	private static CircleButton opDelete;
	
	private RelativeLayout frameMain;
	
	private static ImageView artHead;
	private static LinearLayout artHeadContainer;
	private static TextView curSong;
	private static CircleButton playPause;
	private static CircleButton prev;
	private static CircleButton next;
	private static CircleButton shuffle;
	private static CircleButton repeat;
	private static TextView bodProgress;
	private static boolean seekColorSet=false;
	private static ImageView settings;
	
	private static CustomViewPager pager;
	private MyPagerAdapter adapter;
	private boolean overshot=false;
	private boolean removedBod=false;
	private static ListView listView;
	
	private WindowManager.LayoutParams bodParams;
	private WindowManager.LayoutParams butParams;
	private WindowManager.LayoutParams barParams;
	private WindowManager.LayoutParams backParams;
	private static WindowManager.LayoutParams opParams;
	
	private final BaseSpringSystem springSystem=SpringSystem.create();
	private final ExampleSpringListener springListener=new ExampleSpringListener();
	private Spring bodSpring;
	private Spring butSpring;
	private static Spring seekSpring;
	private static Spring seekArtSpring;
	private static Spring pagerSpring;
	private static Spring opSpring;
	//private ArrayList<Spring> spring=new ArrayList<Spring>;
	
	public static int screenWidth;
	public static int screenHeight;
	
	//static ArrayList<String> album=new ArrayList<String>();
	//static ArrayList<Bitmap> bit=new ArrayList<Bitmap>();
	private static MusicService musicSrv=null;
	private Intent playIntent;
	private boolean musicBound=false;
	private MusicController controller;
	private static ListView lv;
	private static GridView gv;
	private static SongAdapter songAdapter;
	public GridAdapter gridAdapter;
	static boolean artistClicked=false;
    static boolean albumClicked=false;
    static boolean playlistClicked=false;
    
   
	private Handler seekHandler=new Handler();
    private static int min;
    private static int sec;
    private static SeekArc seekArc;
    private ImageView seekArt;
    private static int songDuration;
    private static TextView barProgress;
	static int p=0;
	
	@Override
	public void onCreate(){
		super.onCreate();
		seekHandler=new Handler();
        
		if(playIntent==null){
			Log.d("SERVE","PlayIntent is null");
	        playIntent = new Intent(this, MusicService.class);
	        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
	        //startService(playIntent);
	      }
		else Log.d("SERVE","PlayIntent is NOT null");
		
		windowManager=ChatHeadService.getWindow();		
		ctx=this;
		/*final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        normal=ImageUtils.drawableToBitmap(wallpaperDrawable);*/
        screenHeight= getResources().getDisplayMetrics().heightPixels;
		screenWidth= getResources().getDisplayMetrics().widthPixels; 
		
		/*back =  (RelativeLayout) LayoutInflater.from(this).
				inflate(R.layout.background, null);
		backView=(RelativeLayout)back.findViewById(R.id.background);
		
		back.setVisibility(View.INVISIBLE);
		
		back.setOnTouchListener(new BackTouchListener(){});*/
		
		/*body=(RelativeLayout) LayoutInflater.from(this).
				inflate(R.layout.activity_main, null);*/
		body=new Body(this);
		
		pager=(CustomViewPager)body.findViewById(R.id.pager);
		adapter =new MyPagerAdapter();
		pager.setAdapter(adapter);
		pager.setCurrentItem(ChatHeadService.getPagePos());
		Log.d("SERVE","Pager code has started");
		pager.setOffscreenPageLimit(3);
		
		frameMain=(RelativeLayout)body.findViewById(R.id.main_frame);
		frameMain.setVisibility(View.VISIBLE);
		
		
		
		/*bodyTopRight=(RelativeLayout) LayoutInflater.from(this).
				inflate(R.layout.body_top_right, null);
		
		bodyTopLeft=new Body(this);
		bodyTopLeft.setVisibility(View.INVISIBLE);*/
		
		/*bar=(RelativeLayout) LayoutInflater.from(this).
				inflate(R.layout.seek_bar, null);*/
		
		
		artHeadContainer=(LinearLayout)body.findViewById(R.id.head_art_container);
		artHead=(ImageView)body.findViewById(R.id.head_art);
		artHeadContainer.setOnTouchListener(new BackTouchListener(){});
		artHead.setVisibility(View.INVISIBLE);
		artHead.setAlpha(0f);
		
		curSong=(TextView)body.findViewById(R.id.current_song);
		curSong.setOnTouchListener(new BackTouchListener(){});
		curSong.setVisibility(View.INVISIBLE);
		curSong.setAlpha(0f);
		
		
		bodProgress=(TextView)body.findViewById(R.id.seek);
		bodProgress.setTextColor(Color.parseColor("#FFFFFF"));
		
		playPause=(frontend.CircleButton)body.findViewById(R.id.play_button);
		prev=(frontend.CircleButton)body.findViewById(R.id.previous);
		next=(frontend.CircleButton)body.findViewById(R.id.next);
		shuffle=(frontend.CircleButton)body.findViewById(R.id.shuffle);
		repeat=(frontend.CircleButton)body.findViewById(R.id.repeat);
		initialiseButtons();
		
		playPause.setScaleX(0.7f);
		playPause.setScaleY(0.7f);
		
		listView=(ListView)body.findViewById(R.id.list);
		
		
		
		//pager.getChildAt(pager.getCurrentItem()).);
		
		/*backParams=new WindowManager.LayoutParams(
				(int)screenWidth,(int)(screenHeight*1.1),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				PixelFormat.TRANSLUCENT);
		backParams.gravity=Gravity.LEFT|Gravity.TOP;
		backParams.x=0;
		backParams.y=-Utils.dpToPixels(30, getResources());
		windowManager.addView(back, backParams);*/
		
		bodParams=new WindowManager.LayoutParams(
				(int)screenWidth,(int)(screenHeight*1.01),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				PixelFormat.TRANSLUCENT);
		bodParams.gravity=Gravity.LEFT|Gravity.TOP;
		bodParams.x=0;
		bodParams.y=-Utils.dpToPixels(30, getResources());;
		frameMain.setTranslationY(bodParams.height);
		
		
		/*param2=new WindowManager.LayoutParams(
				Utils.dpToPixels(DP_OF_BUTTONS_2, getResources()),
				Utils.dpToPixels(DP_OF_BUTTONS_2, getResources()),
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				PixelFormat.TRANSLUCENT);
		param2.gravity=Gravity.LEFT|Gravity.TOP;
		param2.x=screenWidth/2-Utils.dpToPixels(52, getResources());
		param2.y=screenHeight;
		windowManager.addView(bodyTop, param2);*/
		
		windowManager.addView(body, bodParams);
		
		/*butParams=new WindowManager.LayoutParams(
				Utils.dpToPixels(DP_OF_BUTTONS_2, getResources()),
				Utils.dpToPixels(DP_OF_BUTTONS_2, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				PixelFormat.TRANSLUCENT);
		butParams.gravity=Gravity.LEFT|Gravity.TOP;
		butParams.x=screenWidth;
		butParams.y=(int)(screenHeight*0.115);
		windowManager.addView(bodyTopRight, butParams);
		
		butParams.x=-butParams.width;
		windowManager.addView(bodyTopLeft, butParams);*/
		
        bodSpring=springSystem.createSpring().setSpringConfig(new MySpringConfig(250,24,1 ));
		bodSpring.addListener(springListener);
		
		butSpring=springSystem.createSpring().setSpringConfig(new MySpringConfig(500,22,2));
		butSpring.addListener(springListener);
		
		pagerSpring=springSystem.createSpring().setSpringConfig(new MySpringConfig(300,26,5));
		pagerSpring.addListener(springListener);
		
		opSpring=springSystem.createSpring().setSpringConfig(new MySpringConfig(250,21,6));
		opSpring.addListener(springListener);
		
	}
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId){
		
		getSystemService(Context.NOTIFICATION_SERVICE);
		if (intent!=null&&intent.getBooleanExtra("stop_service", false)){
			// If it's a call from the notification, stop the service.
			//mNotificationManager.cancel(86);
			stopForeground(true);
			stopSelf();
			ChatHeadService.comeBack();
			
		}
		else{
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.itunes_platinum_lowest)
		        .setContentTitle("Music Head")
		        .setContentText("Tap to close the music head.");
		Intent notificationIntent = new Intent(this, ChatHeadService.class);
		notificationIntent.putExtra("stop_service", true);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);
		mBuilder.setContentIntent(pendingIntent);
		final Notification notification = mBuilder.build();
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) notification.priority=Notification.PRIORITY_MIN;
		//mBuilder.setOngoing(true);
		// mId allows you to update the notification later on.
		//mNotificationManager.notify(86, mBuilder.build());
		startForeground(96,notification);
		}
		
		
		
		/*Animation anim = null;
        anim = AnimationUtils.loadAnimation(ctx, R.anim.alpha_for_back);
        backView.setAnimation(anim);*/
		Runnable bod = new Runnable() {
		    @Override
		    public void run(){
		    	beginAnimation();
		    	//ChatHeadService.goUp();
		    }
		};
		
		
		/*Runnable but=new Runnable() {
			@Override
			public void run() {
				bodSpring.setCurrentValue(0);
				
				curSong.setVisibility(View.VISIBLE);
				artHead.setVisibility(View.VISIBLE);
				Animation animation = null;
		        animation = AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.wave_scale);
				playPause.startAnimation(animation);
				playPause.setScaleX(1f);
				playPause.setScaleY(1f);
				
				Animation anim = null;
		        anim = AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.alpha_for_but);
				prev.startAnimation(anim);
				next.startAnimation(anim);
				repeat.startAnimation(anim);
				shuffle.startAnimation(anim);
				anim=AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.alpha_for_head);
				curSong.startAnimation(anim);
				curSong.setAlpha(1f);
				artHead.startAnimation(anim);
				artHead.setAlpha(1f);
				
				butSpring.setCurrentValue(0.01);
				butSpring.setEndValue(1);
				
				//setController();
				
			}
		};*/
		Handler h = new Handler();
		h.postDelayed(bod, 100);
		//h.postDelayed(bac, 100);
		//h.postDelayed(but, 1000);
		
		
		//temp.setVisibility(View.VISIBLE);
		//stopSelf();
		return Service.START_STICKY;
	}
	
	private void showButtons(){
		//bodSpring.setCurrentValue(0);
		
		curSong.setVisibility(View.VISIBLE);
		artHead.setVisibility(View.VISIBLE);
		Animation animation = null;
        animation = AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.wave_scale);
		playPause.startAnimation(animation);
		playPause.setScaleX(1f);
		playPause.setScaleY(1f);
		
		Animation anim = null;
        anim = AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.alpha_for_but);
		prev.startAnimation(anim);
		next.startAnimation(anim);
		repeat.startAnimation(anim);
		shuffle.startAnimation(anim);
		anim=AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.alpha_for_head);
		curSong.startAnimation(anim);
		curSong.setAlpha(1f);
		artHead.startAnimation(anim);
		artHead.setAlpha(1f);
		
		butSpring.setCurrentValue(0.01);
		butSpring.setEndValue(1);
	}
	
	//connect to the service
		private ServiceConnection musicConnection = new ServiceConnection(){
		
		  @Override
		  public void onServiceConnected(ComponentName name, IBinder service) {
		    MusicBinder binder = (MusicBinder)service;
		    //get service
		    musicSrv = binder.getService();
		    //pass list
		    // musicSrv.setList(Artists.songList);
		   // musicSrv.initialise();
		    musicBound = true;
		    Log.d("CDA","Music Service has been instantiated");
		    
			if(isPlaying()){
				playPause.setImageResource(R.drawable.av_pause);
			}
			updateSeekText();
			updateHead();
			
		  }
		  
		  @Override
		  public void onServiceDisconnected(ComponentName name) {
		    musicBound = false;
		  }
		};
		
	
	@Override
	public void onDestroy(){
		seekHandler.removeCallbacksAndMessages(null);
		if(SEEKBAR_STATE==2) removeBar(-1);
		ChatHeadService.isItInflated=false;
		ChatHeadService.setPagePos(pager.getCurrentItem());
		if(pager.getCurrentItem()!=3){
			GridView g=new GridView(this);
			g=(GridView) pager.findViewWithTag(pager.getCurrentItem());
			ChatHeadService.setScrollPos(g.getFirstVisiblePosition());
		}
		else {
			ListView l=new ListView(this);
			l=(ListView) pager.findViewWithTag(pager.getCurrentItem());
			ChatHeadService.setScrollPos(l.getFirstVisiblePosition());
		}
		musicSrv=null;
		this.unbindService(musicConnection);
		/**/
		/*try{
				butSpring.setAtRest();
			
		}catch (Exception e) {}    */
		
		endingAnimation();
		//windowManager.removeView(back);
		artHead.setVisibility(View.INVISIBLE);
		Runnable r = new Runnable() {
		    @Override
		    public void run(){
       
				
				try {
					//spring1.setAtRest();
					windowManager.removeView(body);	
					removedBod=true;
				} catch (Exception e) {}    
				
		    }
		};
	
		Handler h = new Handler();
		h.postDelayed(r, 300);
		
		/*if(playIntent!=null){
			stopService(playIntent);
		}*/
		//if(!pager.getPagingEnabled()) removeOptions();
		super.onDestroy();
		
	}
	
	private void endingAnimation(){
		
	    bodSpring.setCurrentValue(0);
	    bodSpring.setEndValue(1);
	    
	    /*Animation anim = null;
        anim = AnimationUtils.loadAnimation(ctx, R.anim.small_scale);
        anim.setInterpolator(new ReverseInterpolator());
		backView.setAnimation(anim);*/
		/*Animation animation = null;
		animation = AnimationUtils.loadAnimation(ctx, R.anim.exit_anim);
		frameMain.setAnimation(animation);*/
	}

	private void beginAnimation(){
		
		//springY.setCurrentValue(getResources().getDisplayMetrics().heightPixels);
		//springY.setEndValue(getResources().getDisplayMetrics().heightPixels*0.5);
		/*springX.setCurrentValue(0.01);
		springX.setEndValue(1);*/
		/*Animation animation = null;
        animation = AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.entry_anim);
		body.setVisibility(View.VISIBLE);
        frameMain.startAnimation(animation);*/
		
		/*Animation animation = null;
		animation = AnimationUtils.loadAnimation(ctx, R.anim.alpha_for_but);
		animation.setDuration(300);
		frameMain.startAnimation(animation);*/
      
		/*ObjectAnimator trans =ObjectAnimator.ofFloat(frameMain, "y",1280f, 0f);
		trans.setDuration(350);
		trans.start();*/
		bodSpring.setCurrentValue(1.0);
	    bodSpring.setEndValue(0);
	    overshot=false;
	}

	private void updateSeekText(){
		Runnable sk=new Runnable(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateSeekText();					
		        
			}
	    	
	    };
		if(SEEKBAR_STATE!=-1){
			Log.d("Serve seekBar","State is "+SEEKBAR_STATE);
			//Test for the state of the seek bar
		    if(SEEKBAR_STATE==0){
		    	if(seekArc.getProgress()/1000!=p/1000){
		    		seekTo(seekArc.getProgress());
		    		Log.d("Serve seekBar",""+p);
		    		Log.d("Serve seekBar",""+seekArc.getProgress());
		    	}
		    	SEEKBAR_STATE=-1;
		    }
		    else if(SEEKBAR_STATE==1){
		    	if(seekArc.getProgress()/1000!=p/1000){
		    		seekTo(seekArc.getProgress());
		    		Log.d("Serve seekBar",""+p);
		    		Log.d("Serve seekBar",""+seekArc.getProgress());
		    	}
		    	p= seekArc.getProgress();
		    	/*seekHandler.removeCallbacks(sk);
			    updateSeekText();
		    	return;*/
		    }
		    else if(SEEKBAR_STATE==2){
		    	seekArc.setProgress(getCurrentPosition());
		    }
		    
		}
		
		/*if(!isPlaying()){
	    	//Log.d("Serve songDur",String.valueOf(MusicService.getSongDur()));
	    	min=getCurrentPosition()/1000;
	        sec=min%60;
	        min=min/60;
	        if(sec/10==0) bodProgress.setText(String.valueOf(min)+":0"+String.valueOf(sec));
	        else bodProgress.setText(String.valueOf(min)+":"+String.valueOf(sec));
		}*/
		//else {
			//Log.d("Serve getPosn",String.valueOf(getCurrentPosition()));
			min=getCurrentPosition()/1000;
	        sec=min%60;
	        min=min/60;
	        if(sec/10==0) bodProgress.setText(String.valueOf(min)+":0"+String.valueOf(sec));
	        else bodProgress.setText(String.valueOf(min)+":"+String.valueOf(sec));
		//}
	    seekHandler.removeCallbacks(sk);
	    seekHandler.postDelayed(sk,500);
	    
	}
	
	public static void updateSeekArc(int x){
		//Setting the text
		int m=x/1000;
		int s=m%60;
		m=m/60;
		if(s/10==0) barProgress.setText(String.valueOf(m)+":0"+String.valueOf(s));
        else barProgress.setText(String.valueOf(m)+":"+String.valueOf(s));
		Log.d("Serve makeSeekBar",(String) bodProgress.getText());
		barProgress.append("/");
		m=songDuration/1000;
		s=m%60;
		m=m/60;
		if(s/10==0) barProgress.append(String.valueOf(m)+":0"+String.valueOf(s));
        else barProgress.append(String.valueOf(m)+":"+String.valueOf(s));
	}
	
	public static int getSEEKBAR_STATE() {
		return SEEKBAR_STATE;
	}
	public static void setSEEKBAR_STATE(int x) {
		SEEKBAR_STATE=x;
	}
	private void makeSeekBar(){
		SEEKBAR_STATE=2;
		bar=new SeekArcBody(this);
		
		seekArc= (SeekArc) bar.findViewById(R.id.seekArc);
		barProgress = (TextView) bar.findViewById(R.id.seekArcProgress);
		seekArt= (ImageView) bar.findViewById(R.id.seekArt);
		seekArc.setVisibility(View.INVISIBLE);
		seekArt.setVisibility(View.INVISIBLE);
		//Params initialisation
		barParams=new WindowManager.LayoutParams(
				(int)(screenWidth),(int)(screenHeight*0.73),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				PixelFormat.TRANSLUCENT);
		barParams.gravity=Gravity.LEFT|Gravity.TOP;
		barParams.x=0;
		barParams.y=(int)(screenHeight*0.26);
		windowManager.addView(bar, barParams);
		//Setting the text
		barProgress.setText(bodProgress.getText());
		Log.d("Serve makeSeekBar",(String) bodProgress.getText());
		barProgress.append("/");
		songDuration=getDuration();
		int m=songDuration/1000;
		int s=m%60;
		m=m/60;
		if(s/10==0) barProgress.append(String.valueOf(m)+":0"+String.valueOf(s));
        else barProgress.append(String.valueOf(m)+":"+String.valueOf(s));
		seekArc.setMax(getDuration());
		seekArc.setProgress(getCurrentPosition());
		//Log.d("Serve makeSeekBar",(String) songDur.getText());
		
		//Animations start here
		seekArc.setVisibility(View.VISIBLE);
		seekArt.setVisibility(View.VISIBLE);
		seekArt.setImageBitmap(Bitmap.createScaledBitmap(ChatHeadService.getBm()
				,Utils.dpToPixels(80,ctx.getResources())
				,Utils.dpToPixels(80,ctx.getResources()),
				false));
		//Springs needed for the animations
		seekSpring=springSystem.createSpring().setSpringConfig(new MySpringConfig(220,20,3));
		seekSpring.addListener(springListener);
		seekArtSpring=springSystem.createSpring().setSpringConfig(new MySpringConfig(220,20,4));
		seekArtSpring.addListener(springListener);
				
		seekSpring.setCurrentValue(1);
		seekSpring.setEndValue(0);
		Runnable r=new Runnable(){

			@Override
			public void run() {
				seekArtSpring.setCurrentValue(1.2);
				seekArtSpring.setEndValue(0);
				barProgress.setVisibility(View.VISIBLE);
			}
			
			};
    	Handler k=new Handler();
    	k.postDelayed(r, 80);
    	bodProgress.setVisibility(View.INVISIBLE);
		
	}

	public static void removeBar(int state){
		SEEKBAR_STATE=state;
		bodProgress.setVisibility(View.VISIBLE);
		seekSpring.setCurrentValue(0);
		seekSpring.setEndValue(2);
		barProgress.setVisibility(View.INVISIBLE);
		Runnable m=new Runnable(){
	
			@Override
			public void run() {
				seekArtSpring.setCurrentValue(0);
				seekArtSpring.setEndValue(2);
			}
			
			};
		Runnable r=new Runnable(){
	
			@Override
			public void run() {
				windowManager.removeView(bar);
				seekSpring.destroy();
				seekArtSpring.destroy();
			}
			
			};
		Handler k=new Handler();
		k.postDelayed(m, 200);
		k.postDelayed(r, 300);
	}
	
	/*private static void nonRunningUpdateSeekText(){
		bodProgress.setVisibility(View.VISIBLE);
		min=seekArc.getProgress()/1000;
        sec=min%60;
        min=min/60;
        if(sec/10==0) bodProgress.setText(String.valueOf(min)+":0"+String.valueOf(sec));
        else bodProgress.setText(String.valueOf(min)+":"+String.valueOf(sec));
	}*/
	
	private class ExampleSpringListener extends SimpleSpringListener{
		private int large=65;
		private int small=45;
		@Override
		public void onSpringUpdate(Spring s){
			MySpringConfig cfg = (MySpringConfig) s.getSpringConfig();
			
			if(cfg.index==1 && !removedBod){
				//bodyTop.setVisibility(View.VISIBLE);
				frameMain.setTranslationY((float) s.getCurrentValue()*bodParams.height);
				//bodParams.y=(int)(screenHeight-screenHeight*0.84*s.getCurrentValue());
				windowManager.updateViewLayout(body, bodParams);
				if(s.isOvershooting() && !overshot){
					Runnable but=new Runnable() {
						@Override
						public void run() {
							showButtons();
						}
					};
					Handler h = new Handler();
					h.postDelayed(but, 250);
					overshot=true;
					//showButtons();
				}
				
			}
			
			else if(cfg.index==2){
				/*param1.y=(int) (screenHeight-s.getCurrentValue()*screenHeight*0.7);
				windowManager.updateViewLayout(body, param1);*/
				//param3.x=(int) (screenWidth-(screenWidth/2-
				//Utils.dpToPixels(40, getResources()))*s.getCurrentValue());
				/*butParams.x=(int) (screenWidth/2-Utils.dpToPixels(sub, getResources())
						+Utils.dpToPixels(add, getResources())*s.getCurrentValue());
				windowManager.updateViewLayout(bodyTopRight, butParams);
				
				butParams.x=(int) (screenWidth/2-Utils.dpToPixels(DP_OF_BUTTONS_2-sub, getResources())
						-Utils.dpToPixels(add, getResources())*s.getCurrentValue());
				windowManager.updateViewLayout(bodyTopLeft, butParams);*/
				next.setTranslationX((float) (s.getCurrentValue()*
						Utils.dpToPixels(large, getResources())));
				repeat.setTranslationX((float) (s.getCurrentValue()*
						Utils.dpToPixels(large+small, getResources())));
				prev.setTranslationX((float) (-s.getCurrentValue()*
						Utils.dpToPixels(large, getResources())));
				shuffle.setTranslationX((float) (-s.getCurrentValue()*
						Utils.dpToPixels(large+small, getResources())));
				
				
			}
			else if(cfg.index==3){
				//spring3.setSpringConfig(new MySpringConfig(220,20,3));
				seekArc.setTranslationY((float) s.getCurrentValue()*(barParams.height/2));
				//spring3.setSpringConfig(new MySpringConfig(220,30,3));
				
			}
			else if(cfg.index==4){
				seekArt.setTranslationY((float) s.getCurrentValue()*(barParams.height/2));
			}
			else if(cfg.index==5){
					pager.setTranslationY((float) s.getCurrentValue()*pager.getHeight());
			}
			else if(cfg.index==6){
				if(s.getCurrentValue()<0){
					opParams.y=(int) (screenHeight-(Math.abs(s.getCurrentValue()+1)
							*screenHeight*0.2));
					windowManager.updateViewLayout(op1, opParams);
					
				}
				else{
				opDelete.setTranslationY((float) (-(s.getCurrentValue()-1)*
						Utils.dpToPixels(90, getResources())));
				
				opPlaylist.setTranslationX((float) (-(s.getCurrentValue()-1)*
						Utils.dpToPixels(100, getResources())));
				opQueue.setTranslationX((float) ((s.getCurrentValue()-1)*
						Utils.dpToPixels(100, getResources())));
				opBut.setRotation((float) (-(s.getCurrentValue()-1)*180));
				}
			}
		}
	}

	public static void makeOptions(){
		if(!pager.getPagingEnabled()) return;
		pager.setPagingEnabled(false);
		opParams=new WindowManager.LayoutParams(
				Utils.dpToPixels(75,ctx.getResources()),Utils.dpToPixels(75,ctx.getResources()),
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				PixelFormat.TRANSLUCENT);
		opParams.gravity=Gravity.LEFT|Gravity.TOP;
		opParams.x=screenWidth/2-Utils.dpToPixels(37,ctx.getResources());
		opParams.y=(int)(screenHeight*0.80);
		
		op1=new Options(ctx);
		windowManager.addView(op1, opParams);
		opBut=(frontend.CircleButton)op1.findViewById(R.id.main);
		opPlaylist=(frontend.CircleButton)op1.findViewById(R.id.playlist);
		opQueue=(frontend.CircleButton)op1.findViewById(R.id.queue);
		opDelete=(frontend.CircleButton)op1.findViewById(R.id.delete);
		opBut.setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View v) {
				Log.d("Serve","Main options");
				if(!optionOpened){
					opSpring.setCurrentValue(-2);
					openOptions();
					optionOpened=true;
					opBut.setImageResource(R.drawable.options_cancel);
				}
				else {
					closeOptions(false);
				}
			}
        });	
		opQueue.setOnClickListener(new OnClickListener(){
		        	
					@Override
					public void onClick(View v) {
						Log.d("Serve","Queue option clicked");
					}
		        });	
		opPlaylist.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Log.d("Serve","Playlist option clicked");
			}
		});	
		opDelete.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Log.d("Serve","Delete option clicked");
				
			}
		});	
		opSpring.setCurrentValue(-1);
		opSpring.setEndValue(-2);
		Log.d("Serve","Making options");
	}
	
	private static void openOptions(){
		
		//opParams.x=0;
		Log.d("Serve","Opening options");
		windowManager.removeView(op1);
		opParams.width=screenWidth;
		opParams.x=0;
		opParams.height=Utils.dpToPixels(200,ctx.getResources());
		opParams.y=(int)(screenHeight*0.92-Utils.dpToPixels(200,ctx.getResources()));
		
		windowManager.addView(op1, opParams);
		
		opSpring.setCurrentValue(1);
		opSpring.setEndValue(2);
		//opParams.width=(int) (screenWidth);
		/*opBut.setX(screenWidth/2-Utils.dpToPixels(33, ctx.getResources()));
		opBut.setY((float) (screenHeight*0.25-Utils.dpToPixels(65, ctx.getResources())));
		*/
		
		
	}
	
	private static void closeOptions(boolean b){
		Log.d("Serve","Closing options");
		opBut.setImageResource(R.drawable.options_overflow);
		opSpring.setCurrentValue(2);
		opSpring.setEndValue(1);
		optionOpened=false;
		Runnable r=new Runnable(){
				
				@Override
				public void run() {
					windowManager.removeView(op1);
					opParams.width=Utils.dpToPixels(75,ctx.getResources());
					opParams.height=Utils.dpToPixels(75,ctx.getResources());
					opParams.x=screenWidth/2-Utils.dpToPixels(38,ctx.getResources());
					opParams.y=(int)(screenHeight*0.795);
					windowManager.addView(op1, opParams);
				}
				
				};
		Handler k=new Handler();
		k.postDelayed(r, 170);
		
	}
	
	public static void removeOptions(ArrayList<Integer> s){
		pager.setPagingEnabled(true);
		opSpring.setAtRest();
		windowManager.removeView(op1);
		if(pager.getCurrentItem()==3 ){
			ListView l=new ListView(ctx);
			l=(ListView) pager.findViewWithTag(pager.getCurrentItem());
			for(int i=0;i<=l.getLastVisiblePosition()-l.getFirstVisiblePosition();i++){
				l.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
				}
		}
		else if(getArtistClicked() || getAlbumClicked()){
			for(int i=0;i<=listView.getLastVisiblePosition()-listView.getFirstVisiblePosition();i++){
				listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
			}
		}
		else{
			GridView g=new GridView(ctx);
			g=(GridView) pager.findViewWithTag(pager.getCurrentItem());
			for(int i=0;i<=g.getLastVisiblePosition()-g.getFirstVisiblePosition();i++){
				g.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}
	
	private class MyPagerAdapter extends PagerAdapter {
		private View temp;
	    public MyPagerAdapter() {
	    }
	
	    @Override
	    public int getCount() {
	        return 4;
	        
	    }
	    /*public void get(ViewParent viewParent){
	    	temp=(View) viewParent;
	    }*/
	    @Override
	    public int getItemPosition(Object object) {
	    	if(object.equals(temp)){
	    		return POSITION_NONE;
	        		
	    	}
	        else {
	        	Log.d("Serve","The gridview is not to be removed");
	        	return POSITION_UNCHANGED;
	        }
	    }
	    
	    public View instantiateItem(View collection, int position) {
	        LayoutInflater inflater = (LayoutInflater) collection.getContext()
	                				  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        
	        View view=null;
	       
	        switch (position) {
	        case 0:
	        	
	        	view = inflater.inflate(R.layout.grid, null);
                gv=(GridView)view.findViewById(R.id.grid_view);
                gridAdapter=new GridAdapter(Serve.this,0,PlaylistOp.getPlayList());	    
                gv.setAdapter(gridAdapter);
                gv.setTag(position);
               /* gv.setOnItemClickListener(new OnItemClickListener(){
                	@Override
					public void onItemClick(AdapterView<?> v, View arg1,
							int pos, long arg3) {
						get(v.getParent());
						setArtist(pos);
						notifyDataSetChanged();
                		artistClicked=true;
                		makeList(pos,1);
						
					}
                });*/
                if(ChatHeadService.getPagePos()==1){
    	        	gv.setSelection(ChatHeadService.getScrollPos());
    	        	
    	        }
        	 break;
        	 
	        case 1:
	        	
	        		view = inflater.inflate(R.layout.grid, null);
	                gv=(GridView)view.findViewById(R.id.grid_view);
	                gridAdapter=new GridAdapter(Serve.this,Artists.songList,1);	    
	                gv.setAdapter(gridAdapter);
	                gv.setTag(position);
	               /* gv.setOnItemClickListener(new OnItemClickListener(){
	                	@Override
						public void onItemClick(AdapterView<?> v, View arg1,
								int pos, long arg3) {
							get(v.getParent());
							setArtist(pos);
							notifyDataSetChanged();
	                		artistClicked=true;
	                		makeList(pos,1);
							
						}
	                });*/
	                if(ChatHeadService.getPagePos()==1){
	    	        	gv.setSelection(ChatHeadService.getScrollPos());
	    	        	
	    	        }
	        	 break;
	        	 
	        case 2:
	        	
	        		view = inflater.inflate(R.layout.grid, null);
	                gv=(GridView)view.findViewById(R.id.grid_view);
	                gridAdapter=new GridAdapter(Serve.this,Artists.songList,2);	    
	                gv.setAdapter(gridAdapter);	 
	                gv.setTag(position);
	                /*gv.setOnItemClickListener(new OnItemClickListener(){
	                	
						@Override
						public void onItemClick(AdapterView<?> v, View arg1,
								int pos, long arg3) {
							get(v.getParent());
							setAlbum(pos);
							notifyDataSetChanged();
							albumClicked=true;
							makeList(pos,0);
							
						}
	                });*/
	                if(ChatHeadService.getPagePos()==2){
	                	gv.setSelection(ChatHeadService.getScrollPos());
	    	        }
	            break;
	            
	        case 3:
	        	view = inflater.inflate(R.layout.activity_title, null);
	            lv=(ListView)view.findViewById(R.id.music_list);
	    		//lv.setFadingEdgeLength(0);
	    		songAdapter=new SongAdapter(Serve.this,Artists.songList,3,-1);
	    	    /*SwingRightInAnimationAdapter swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(songAdapter);
	
	    	    // Assign the ListView to the AnimationAdapter and vice versa
	    	    swingRightInAnimationAdapter.setAbsListView(lv);
	    	    lv.setAdapter(swingRightInAnimationAdapter);*/
	    		lv.setAdapter(songAdapter);
	    		lv.setTag(position);
	    		lv.setFastScrollEnabled(true);
	            /*lv.setOnItemClickListener(new OnItemClickListener(){
	    			@Override
	    			public void onItemClick(AdapterView<?> parent, View view,
	    					int position, long id) {
	    				musicSrv.setList(Artists.songList);
	    				songPicked(position);
	    				//LastSongRun.setSong(position);
	    				ChatHeadService.updateHead();
	    				updateHead();
	    			}
	      	    	  
	      	      });*/
	            if(ChatHeadService.getPagePos()==3){
    	        	lv.setSelection(ChatHeadService.getScrollPos());
    	        }
	            break;
	        /*case 3:
	        	view = inflater.inflate(R.layout.activity_title, null);
	            lv=(ListView)view.findViewById(R.id.music_list);
	    		lv.setFadingEdgeLength(0);
	            songAdapter=new SongAdapter(Serve.this,Artists.songList,3);
	            
	            break;
	        case 4:
	        	view = inflater.inflate(R.layout.activity_title, null);
	            lv=(ListView)view.findViewById(R.id.music_list);
	    		lv.setFadingEdgeLength(0);
	            songAdapter=new SongAdapter(Serve.this,Artists.songList,1);
	            break;*/
	        }
	        Log.d("SERVE","The view is about to be initialised");
	        /*View view = inflater.inflate(resId, null);
	        final ArrayList<String> list = new ArrayList<String>();
		    for (int i = 0; i < names.length; ++i) {
		      list.add(names[i]+i+1);
		    }*/
	        //lv.setAdapter(songAdapter);
	        
	        ((ViewPager) collection).addView(view, 0);
	        return view;
	    }
	    @Override
	    public CharSequence getPageTitle(int pos) {
	    	if(pos==0) return "Playlists";
	    	else if(pos==1) return "Artists";
	    	else if(pos==2) return "Albums";
	    	else return "Songs";
	    }
	
	    @Override
	    public void destroyItem(View collection, int position, Object view) {
	    	((ViewPager) collection).removeView((View) view);
	    }
	    
	    
	    @Override
	    public boolean isViewFromObject(View view, Object object) {
	        return view == object;
	    }
	
	    @Override
	    public Parcelable saveState() {
	    	return null;
	    }
	
	    @Override
	    public void restoreState(Parcelable arg0, ClassLoader arg1) {
	    	//pager.onRestoreInstanceState(arg0);
	    }
	
	    @Override
	    public void startUpdate(View arg0) {
	    }
	
	    @Override
	    public void finishUpdate(View arg0) {
	    }
	}

    public static void  makeList(int pos,final int page){
    	songAdapter=new SongAdapter(ctx,Artists.songList,page,pos);
	    /*SwingRightInAnimationAdapter swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(songAdapter);

	    // Assign the ListView to the AnimationAdapter and vice versa
	    swingRightInAnimationAdapter.setAbsListView(lv);
	    lv.setAdapter(swingRightInAnimationAdapter);*/
		listView.setVisibility(View.VISIBLE);
		Runnable p = new Runnable() {
		    @Override
		    public void run(){
		    	pagerSpring.setCurrentValue(0);
				pagerSpring.setEndValue(1);
		    }
		};
		
		Animation animation = null;
        animation = AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.list_opening);
        listView.setAnimation(animation);
    	listView.setAdapter(songAdapter);
    	
    	Runnable r = new Runnable() {
		    @Override
		    public void run(){
		    	listView.bringToFront();
				listView.invalidate();
		    	//ChatHeadService.goUp();
		    }
		};
		Handler h=new Handler();
		h.postDelayed(p, 30);
		h.postDelayed(r, 300);
		
        /*listView.setOnItemClickListener(new OnItemClickListener(){
        	@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
        		if(page==0) musicSrv.setList(SongAdapter.getAlbumSongs());
            	else if(page==1) musicSrv.setList(SongAdapter.getArtistSongs());
				songPicked(position);		
				ChatHeadService.updateHead();
				updateHead();
				
        	}
        });*/
    }
    
    public static void bringBackPager(){
    	setAlbumClicked(false);
		setArtistClicked(false);
		setPlaylistClicked(false);
		Animation animation = null;
        animation = AnimationUtils.loadAnimation(ChatHeadService.getContext(), R.anim.list_closing);
        listView.setAnimation(animation);
    	pager.bringToFront();
    	pager.invalidate();
    	pagerSpring.setCurrentValue(1);
		pagerSpring.setEndValue(0);
    }
    
	private void initialiseButtons(){
		 playPause.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View v) {
					if(isPlaying()) {
						pause();
						playPause.setImageResource(R.drawable.av_play);
					}
					else {
						start();
						//songPicked(LastSongRun.getPos());
						playPause.setImageResource(R.drawable.av_pause);
					}
					
				}
	        });	
		 prev.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View v) {
					playPrev();
					playPause.setImageResource(R.drawable.av_pause);
					ChatHeadService.updateHead();
					updateHead();
				}
		 });
		 next.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View v) {
					playNext();
					playPause.setImageResource(R.drawable.av_pause);
					ChatHeadService.updateHead();
					updateHead();
				}
		 });
		 shuffle.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View v) {
					boolean set=musicSrv.setShuffle();
					if(set) shuffle.setImageResource(R.drawable.av_shuffle_set);
					else shuffle.setImageResource(R.drawable.av_shuffle);
				}
		 });
		 repeat.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View v) {
					switch(MusicService.getRepeat()){
					 case 0:{
						 repeat.setImageResource(R.drawable.av_repeat_set_once);
						 MusicService.setRepeat(1);
						 break;
					 }
					 case 1:{
						 repeat.setImageResource(R.drawable.av_repeat_set_twice);
						 MusicService.setRepeat(2);
						 break;
					 }
					 case 2:{
						 repeat.setImageResource(R.drawable.av_repeat_unset);
						 MusicService.setRepeat(0);
						 break;
					 }
					 }
				}
		 });
		 bodProgress.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View v) {
					makeSeekBar();
				}
		 });
		 
		 if(MusicService.isShuffle()) shuffle.setImageResource(R.drawable.av_shuffle_set);
		 else shuffle.setImageResource(R.drawable.av_shuffle);
		 
		 if(MusicService.getRepeat()==0) repeat.setImageResource(R.drawable.av_repeat_unset);
		 else if(MusicService.getRepeat()==1) repeat.setImageResource(R.drawable.av_repeat_set_once);
		 else repeat.setImageResource(R.drawable.av_repeat_set_twice);
	}

	public static void setAlbumClicked(boolean x){
    	albumClicked=x;
    }
    
    public static void setArtistClicked(boolean x){
    	artistClicked=x;
    }
   
    public static boolean getArtistClicked() {
		return artistClicked;
	}

	public static boolean getAlbumClicked() {
		return albumClicked;
	}
	
	public static boolean isPlaylistClicked() {
		return playlistClicked;
	}

	public static void setPlaylistClicked(boolean playlistClicked) {
		Serve.playlistClicked = playlistClicked;
	}

	
	public static ViewPager getPager(){
		return pager;
	}
	
	//play next
   private void playNext(){
     musicSrv.playNext();
     //controller.show(0);
   }
    
   //play previous
   private void playPrev(){
     musicSrv.playPrev();
     //controller.show(0);
   }
   
   private void setController(){
   	 //set the controller up
   	controller = new MusicController(this);
   	controller.setPrevNextListeners(new View.OnClickListener() {
   		  @Override
   		  public void onClick(View v) {
   		    playNext();
   		  }
   		}, new View.OnClickListener() {
   		  @Override
   		  public void onClick(View v) {
   		    playPrev();
   		  }
   		});
   	controller.setMediaPlayer(this);
   	controller.setAnchorView(body.findViewById(R.id.lin));
   	controller.setEnabled(true);
   }
   @Override
   public void pause() {
     musicSrv.pausePlayer();
   }
    
   public static void songPicked(int pos){
	   //musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
		musicSrv.setSong(pos);
		musicSrv.playSong();
		playPause.setImageResource(R.drawable.av_pause);
		//ChatHeadService.updateHead();
	}

   @Override
   public void seekTo(int pos) {
     musicSrv.seek(pos);
   }
    
   @Override
   public void start() {
	   
     musicSrv.go();
   }

	@Override
	public int getDuration() {
		if(musicSrv!=null && musicBound )
		return musicSrv.getDur();
		  else return 0;
	}

	@Override
	public int getCurrentPosition() {
		if(musicSrv!=null && musicBound)
		    return musicSrv.getPosn();
		  else return 0;
	}
	@Override
	public boolean isPlaying() {
		if(musicSrv!=null && musicBound){
			return musicSrv.isPng();
		}
		else return false;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}
	public static void setFlagForArt(){
		IS_ART_DONE=true;
		ChatHeadService.updateHead();
		//updateHead();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	class MySpringConfig extends SpringConfig {
	    
	    int index;
	    public MySpringConfig(double tension, double friction,int ind) {
	        super(tension, friction);
	        this.index = ind;
	    }
	}

	private class BackTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getActionMasked();
	
			switch (action) {
			
			case MotionEvent.ACTION_DOWN:
				// Filter and redirect the events to dragTray()
				stopSelf();
				ChatHeadService.comeBack();
				break;
			default:
				return false;
			}
			return true;
	
		}
	}
	
	/*private class MyAsyncTask extends AsyncTask<String, Void,Void>{
		
		
		@Override
		protected Void doInBackground(String... params) {

			while(!IS_ART_DONE){
				continue;
			}
			Log.d("Serve","The Album Art has finished");
			
			return null;
		}
		@Override 
		protected void onPostExecute(Void result){

			pager=(ViewPager)body.findViewById(R.id.pager);
			adapter =new MyPagerAdapter();
			pager.setAdapter(adapter);
			pager.setCurrentItem(1);
			pager.setOffscreenPageLimit(3);
			
			bodSpring.setCurrentValue(0.5);
		    bodSpring.setEndValue(1);
			//setController();
			//lin=(LinearLayout)playPause.getParent();
	        playPause.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View v) {
					if(isPlaying()) {
						pause();
						playPause.setImageResource(R.drawable.av_play);
					}
					else {
						start();
						//songPicked(LastSongRun.getPos());
						playPause.setImageResource(R.drawable.av_pause);
					}
					
				}
		});	
		
	}
 }*/
	public static void updateHead(){
		artHead.setImageBitmap(ChatHeadService.getBm());
		updateHeadText();
	}
	public static void updateHeadText(){
		if(musicSrv.getList().size()!=0){
			curSong.setText(musicSrv.getList().get(MusicService.getSongNum()).getTitle());
		}
	}
}
