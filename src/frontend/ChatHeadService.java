package frontend;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import backend.AlbumArtGet;
import backend.Artists;
import backend.MusicService;
import backend.MusicService.MusicBinder;
import backend.MusicUtils;
import backend.Song;

import com.example.samplemusic.R;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;


public class ChatHeadService extends Service{
	
	//General essentials
	private static WindowManager windowManager;
	private WindowManager.LayoutParams headParams;
	private static ImageView chatHead;
	private static Bitmap bm;
	public static Bitmap getBm() {
		return bm;
	}
	private RelativeLayout rl;
	private static Context ctx;
	private RelativeLayout head;
	private static int pagePos=2;
	private static int scrollPos=0;
	
	
	//Music Service
	private static MusicService musicSrv=null;
	private Intent playIntent;
	private boolean musicBound=false;
	
	//Music Head
	private final BaseSpringSystem springSystem=SpringSystem.create();
	private final ExampleSpringListener springListener=new ExampleSpringListener();
	private static Spring springX;
	private static Spring springY;
	private static int mStartDragX;
	private static int mStartDragY;
	private int displaceX;
	private int displaceY;
	private int mPrevDragX;
	private int mPrevDragY;
	private static int screenHeight; 
	private static int screenWidth;
	private double deltaX;
	private double deltaY;
	float vx;
	float vy;
	double m=(double) 0.0;
	static int w;
	static int h;
	private boolean isHeld=false;
	private static boolean isRightBound=false;
	boolean moved=false;
	private VelocityTracker velTrack;
	public static boolean isItInflated=false;
	
	boolean hasMenuKey ;
	boolean hasBackKey ;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override 
	public void onCreate(){
		Log.d("TAG","Entered the chatHead class!!");
		super.onCreate();
		
		windowManager=(WindowManager)getSystemService(WINDOW_SERVICE);
		ctx=this;
		springX=springSystem.createSpring().setSpringConfig(new MySpringConfig(275,  21, true));
		springX.addListener(springListener);
		springY=springSystem.createSpring().setSpringConfig(new MySpringConfig(275,  21, false));
		springY.addListener(springListener);
		/*springX.setRestDisplacementThreshold(100);
		
		springY.setRestDisplacementThreshold(100);
		*/
		springX.setRestSpeedThreshold(Utils.dpToPixels(100,getResources()));
		springY.setRestSpeedThreshold(Utils.dpToPixels(100,getResources()));
		
		/*springO=springSystem.createSpring().setSpringConfig(new MySpringConfig(100,  14 , false));
		springO.addListener(springListener);*/
				 
		rl=(RelativeLayout) LayoutInflater.from(this).
			inflate(R.layout.service_head, null);
		
		//head=(RelativeLayout) rl.findViewById(R.id.root_layout);
		
		chatHead = new ImageView(this);
		
		chatHead.setOnTouchListener(new HeadTouchListener());
		
		chatHead.setImageResource(R.drawable.itunes_gold_low);
		
		bm=BitmapFactory.decodeResource(ctx.getResources(),R.drawable.itunes_gold_low);
		//updateHead();
		
		Log.d("TAG","All things have been iniatialised!!");
		
		
		
		headParams=new WindowManager.LayoutParams(
		Utils.dpToPixels(65, getResources()),
		Utils.dpToPixels(65, getResources()),
		WindowManager.LayoutParams.TYPE_PHONE,
		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
		|WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
		PixelFormat.TRANSLUCENT);
		headParams.gravity=Gravity.LEFT|Gravity.TOP;
		headParams.x=0;
		headParams.y=0;
		windowManager.addView(chatHead, headParams);
		
		/*final WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        p.gravity = Gravity.RIGHT | Gravity.TOP;
        p.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        p.width = 1;
        p.height = LayoutParams.MATCH_PARENT;
        p.format = PixelFormat.TRANSPARENT;
        helperWnd = new View(this); //View helperWnd;

        windowManager.addView(helperWnd, p);
        final ViewTreeObserver vto = helperWnd.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                if (helperWnd.getHeight()==screenHeight) {
                	Log.d("CHS","1");
                	//windowManager.addView(rl,params);
                } else {
                	Log.d("CHS","2");
                	//windowManager.removeView(rl);
                }
            }
        });*/
        
		w=headParams.width;
		h=headParams.height;
		Log.d("TAG","The view has been added");
		
		screenHeight= getResources().getDisplayMetrics().heightPixels;
		screenWidth= getResources().getDisplayMetrics().widthPixels;
		hasMenuKey = ViewConfiguration.get(getBaseContext()).hasPermanentMenuKey();
		hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
		springX.setCurrentValue(screenWidth/2);
		springX.setEndValue(0-w*0.2);
		springY.setCurrentValue(screenHeight-Utils.dpToPixels(30,getResources()));
		springY.setEndValue(screenHeight/2);
		}
	
	
    
	
	public static void updateHead(){
		//chatHead.setImageBitmap(LastSongRun.getAlbumArt());
		Log.d("CHS","Updating the head image");
		Song song=musicSrv.getList().get(MusicService.getSongNum());
		if(AlbumArtGet.getAlbum().contains(song.getAlbum())){
			bm=AlbumArtGet.getBit().get(AlbumArtGet.getAlbum().indexOf(song.getAlbum()));
			
			chatHead.setImageBitmap(bm);
		}
		else if(!AlbumArtGet.getAlbum().contains(song.getAlbum())){
			Log.d("CHS","bm is null");
			/*Drawable draw=ctx.getResources().getDrawable(R.drawable.itunes_gold_low);
        	bm=MusicUtils.drawableToBitmap(draw);
        	bm=MusicUtils.getCroppedBitmap(bm);
        	chatHead.setImageBitmap(bm);*/
        	chatHead.setImageResource(R.drawable.itunes_gold_low);
        	bm=BitmapFactory.decodeResource(ctx.getResources(),R.drawable.itunes_gold_low);
		}
		//SeekArc.setThumb(bm);
		//scaleAnimation(false);
	}
		
	// Listens to the touch events on the tray.
		private class HeadTouchListener implements OnTouchListener {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getActionMasked();

				switch (action) {
				case MotionEvent.ACTION_DOWN: 
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_OUTSIDE:
					// Filter and redirect the events to dragTray()
					dragTray(action, (int)event.getRawX(), (int)event.getRawY(),(MotionEvent)event);
					break;
				
					//stopSelf();
				default:
					return false;
				}
				return true;

			}
		}
	
	@SuppressLint("Recycle")
	private void dragTray(int action, final int x, final int y,MotionEvent event){
		int index=event.getActionIndex();
		int id=event.getPointerId(index);
		springY.setOvershootClampingEnabled(false);
		

		switch (action){
		case MotionEvent.ACTION_OUTSIDE:
			
			Log.d("OUT","x="+x);
			Log.d("OUT","y="+y);
		case MotionEvent.ACTION_DOWN:
			moved=false;
			isHeld=true;
			if(velTrack==null){
				velTrack=VelocityTracker.obtain();
			}
			else{
				velTrack.clear();
			}
			velTrack.addMovement(event);
			
			if(!springX.isAtRest()){
				springX.setAtRest();
				springY.setAtRest();
			}
			
			// Cancel any currently running animations/automatic tray movements.
			//if (mTrayTimerTask!=null){
				//mTrayTimerTask.cancel();
				//mTrayAnimationTimer.cancel();
			//}
			
			// Store the start points
			
			if(!isItInflated){
				mStartDragX = x;
				mStartDragY = y;
			}
			
			mPrevDragX = x;
			mPrevDragY = y;
			//springX.setCurrentValue(x);
			//springY.setCurrentValue(y);
			//Log.d("DISPLACE",""+rl.getWidth());
			break;
			
		case MotionEvent.ACTION_MOVE:
			
			
			/*if(isItInflated){
				stopService(new Intent(this,Serve.class));
				isItInflated=false;
			}*/
			if(x-mPrevDragX!=0&&y-mPrevDragY!=0){
				deltaX = x-mPrevDragX;
				deltaY = y-mPrevDragY;
			}
			 
			velTrack.addMovement(event);
			velTrack.computeCurrentVelocity(1000);
			vx=VelocityTrackerCompat.getXVelocity(velTrack, id);
			vy=VelocityTrackerCompat.getYVelocity(velTrack, id);
			/*int delay = 0; // delay for 0 sec. 
			int period = 5; // repeat every 50 m-sec. 
			Timer timer = new Timer(); 
			timer.scheduleAtFixedRate(new TimerTask() 
			    { 
			        public void run() 
			        { 
			        	mPrevDragX = x;
						mPrevDragY = y;  // display the data
			        } 
			    }, delay, period); */
			
			// Calculate position of the whole tray according to the drag, and update layout.
			
			 if(!moved){
					if((x)<headParams.x||x>(headParams.x+w) || (y)<headParams.y||(y)>(headParams.y+h)){
						/*springX.setEndValue(x);
						springY.setEndValue(y);*/
						headParams.x+=(x-mStartDragX);
						headParams.y+=(y-mStartDragY);
						displaceX=headParams.x-x;
						displaceY=headParams.y-y;
						windowManager.updateViewLayout(chatHead, headParams);
						moved=true;
						
					}
				}
				else {
						/*springX.setEndValue(x);
						springY.setEndValue(y);*/
					headParams.x=x+displaceX;
					headParams.y=y+displaceY;
					windowManager.updateViewLayout(chatHead, headParams);
				}
				
			 /*springX.setCurrentValue(x);
			 springY.setCurrentValue(y);*/
			/*Log.d("CHS","x="+x);
			Log.d("CHS","y="+y);
			Log.d("CHS","params.x="+headParams.x);
			Log.d("CHS","params.y="+headParams.y);*/
			
			 mPrevDragX = x;
			mPrevDragY = y; 
			/*springX.setEndValue(params.x+deltaX);
			springY.setEndValue(params.y+deltaY);*/
			/*chatHead.setX(x);
			chatHead.setY(y);
			chatHead.invalidate();*/
			/*params.x+=deltaX;
			params.y+=deltaY;
			windowManager.updateViewLayout(rl, params);*/
			//Log.d("TARGET","deltaX="+deltaX);
			//Log.d("TARGET","deltaY="+deltaY);
			//params.x += deltaX;
			//params.y += deltaY;
			//windowManager.updateViewLayout(rl, params);
			//animateButtons();
			//velTrack.addMovement(event);
			//Log.d("CHS","Moving ");
			//Log.d("Moving", "Moving");
			
			break;
			
		case MotionEvent.ACTION_UP:
			isHeld=false;
			int targetY=y;
			vy=vy*((float)screenWidth/(float)screenHeight);
			/*springX.setCurrentValue(params.x-params.width/2);
			springY.setCurrentValue(params.y-params.height/2);*/
			
			/*springX.setVelocity(deltaX*50);
			springY.setVelocity(deltaY*(screenWidth/screenHeight)*50);*/
			/*Log.d("VELOCITY","vx="+ vx);
			Log.d("VELOCITY","vy="+ vy);*/
			/*Log.d("ChatHeadService","mStartDragX="+mStartDragX);
			Log.d("ChatHeadService","x="+x);
			Log.d("ChatHeadService","mStartDragY="+mStartDragY);
			Log.d("ChatHeadService","y="+y);*/
			
			if(deltaX!=0.0) m=deltaY/deltaX;
			Log.d("ChatHeadService","m="+m);
			/*Log.d("CHS","x="+x);
			Log.d("CHS","y="+y);
			Log.d("CHS","params.x="+params.x);
			Log.d("CHS","params.y="+params.y);*/
			Log.d("ChatHeadService","daltaX="+deltaX);
			Log.d("ChatHeadService","deltaY="+deltaY);
			
			if(isItInflated){
				//springX.setEndValue(-Utils.dpToPixels(60,getResources()));
				//springY.setEndValue(mStartDragY);
				stopService(new Intent(this,Serve.class));
				comeBack();
				isItInflated=false;
				break;
			}
//			if(x<mStartDragX+params.width && x>mStartDragX && y>mStartDragY && y<mStartDragY-params.height){
				if(!moved&&Artists.DONE){
					Log.d("ChatHeadService","not moved");
					
				//springX.setEndValue(screenWidth);
				openView();
				isItInflated=true;
				//velTrack.recycle();
				break;
			}
				
				springX.setCurrentValue(headParams.x);
				springY.setCurrentValue(headParams.y+h);
			/*Log.d("TARGET","deltaX="+deltaX);
			Log.d("TARGET","deltaY="+deltaY);
			Log.d("START","mStartDragX="+mStartDragX);
			Log.d("START","mStartDragY="+mStartDragY);*/
			/*Log.d("TARGET","mPrevDragX="+mPrevDragX+"  x="+x);
			Log.d("TARGET","mPrevDragY="+mPrevDragY+"  y="+y);*/
	//This if statement is to assess velocity of drag when necessary 
			
			if(Math.abs(m)>=5.0 /*|| headParams.x<screenWidth*0.05 || headParams.x>screenWidth*0.95*/)	{
				if(deltaY<0){
					springY.setEndValue(headParams.y-Math.abs(vy/3));
					//springX.setEndValue(springX.getCurrentValue());
				}
				else {
					springY.setEndValue(headParams.y+Math.abs(vy/3));
					//springX.setEndValue(springX.getCurrentValue());
				}
				if(headParams.x>screenWidth*0.6) springX.setEndValue(screenWidth-headParams.width*0.8);
				else if(headParams.x<screenWidth*0.4)springX.setEndValue(0-headParams.width*0.2);
				Log.d("VELOCITY","vx="+ vx);
				Log.d("VELOCITY","vy="+ vy);
				break;
			}
				
			/*if(vx<25&&vx>-25||vy<25&&vy>-25){
				if(x>=screenWidth/2) springX.setEndValue((int)(screenWidth));
				else springX.setEndValue(0);
				//if(targetY>screenHeight&&!hasMenuKey&&!hasBackKey) targetY=screenHeight-Utils.dpToPixels(50,getResources());
				springY.setEndValue(targetY);
				//velTrack.recycle();
				break;
			}*/
			
			//if(deltaX>=-0.1&&deltaX<=0.1){
			if(Math.abs(vx)<30||Math.abs(vy)<30){
				if(x>screenWidth/2) {
					springX.setEndValue(screenWidth-headParams.width*0.8);
					isRightBound=true;
				}
				else {
					springX.setEndValue(0-headParams.width*0.2);
					isRightBound=false;
				}
				
				//if(targetY>screenHeight&&!hasMenuKey&&!hasBackKey) targetY=screenHeight-Utils.dpToPixels(50,getResources());
				springY.setEndValue(targetY);
				if(m==0.0) springY.setEndValue(screenHeight-springY.getEndValue());
				Log.d("VELOCITY","vx="+ vx);
				Log.d("VELOCITY","vy="+ vy);
				//velTrack.recycle();
				break;
			}
			
			
	//The rest of the below if statements assess the value of deltaX to predict outcome
			
			else if(deltaX>0.1){
				targetY=(int)(m*(screenWidth-x)+y);
				if(targetY>y){
					vy=Math.abs(vy);
					springY.setVelocity(vy*1.5);
					
				}
				else{
					vy=-Math.abs(vy);
					springY.setVelocity(vy*1.5);
					
				}
				springX.setVelocity(Math.abs(vx*1.5));
				Log.d("VELOCITY","vx="+ vx);
				Log.d("VELOCITY","vy="+ vy);
				/*if(vx<-700||vx>700){
					springX.setSpringConfig(new MySpringConfig(600,28,true));
					springY.setSpringConfig(new MySpringConfig(600,28,false));
					if(m>=1) targetY-=Utils.dpToPixels(100,getResources());
					else if(m<=-1) targetY+=Utils.dpToPixels(100,getResources());
					
				}*/
				//if(targetY>screenHeight&&!hasMenuKey&&!hasBackKey) targetY=screenHeight-Utils.dpToPixels(50,getResources());
				springX.setEndValue(screenWidth-headParams.width*0.8);
				springY.setEndValue(targetY);
				isRightBound=true;
				//springY.setEndValue(y);
			}
			else if(deltaX<-0.1){
				/*if(vx<-700||vx>700){
					springX.setSpringConfig(new MySpringConfig(600,28,true));
					springY.setSpringConfig(new MySpringConfig(600,28,false));
				}*/
				targetY=(int)(m*(0-x)+y);
				if(targetY>y){
					vy=Math.abs(vy);
					springY.setVelocity(vy*1.5);
					
				}
				else{
					vy=-Math.abs(vy);
					springY.setVelocity(vy*1.5);
					
				}
				springX.setVelocity(-Math.abs(vx*1.5));
				Log.d("VELOCITY","vx="+ vx);
				Log.d("VELOCITY","vy="+ vy);
				//if(targetY>screenHeight&&!hasMenuKey&&!hasBackKey) targetY=screenHeight-Utils.dpToPixels(50,getResources());
				
				springX.setEndValue(0-headParams.width*0.2);
				springY.setEndValue(targetY);
				isRightBound=false;
				//springY.setOvershootClampingEnabled(true);
				/*Log.d("TARGET","targetY="+targetY);
				Log.d("TARGET","y="+y);*/
				//springY.setEndValue(y);
			}
			//velTrack.recycle();
			/*final int ty=targetY;
			Runnable r = new Runnable() {
			    @Override
			    public void run(){
			    	if(ty>y){
						springY.setEndValue(ty+Math.abs(vy/10));
					}
					else{
						springY.setEndValue(ty-Math.abs(vy/10));
					}
			    }
			};
			Handler h = new Handler();
			h.postDelayed(r, 150);*/
			
			break;
			
		case MotionEvent.ACTION_CANCEL:
			Log.d("CHS","The movement just got canceled");
			// When the tray is released, bring it back to "open" or "closed" state.
			//if ((mIsTrayOpen && (x-mStartDragX)<=0) ||
				//(!mIsTrayOpen && (x-mStartDragX)>=0))
				//mIsTrayOpen = !mIsTrayOpen;
			
			/*mTrayTimerTask = new TrayAnimationTimerTask();
			mTrayAnimationTimer = new Timer();
			mTrayAnimationTimer.schedule(mTrayTimerTask, 0, ANIMATION_FRAME_RATE);
			break;*/
			/*spring.setEndValue(1);
			params.x=x+10;
			params.y=y+10;
			windowManager.updateViewLayout(rl, params);*/
			//spring.setEndValue(0);
			
			break;
		}
	}
	
	private class ExampleSpringListener extends SimpleSpringListener{
		@Override
		public void onSpringUpdate(Spring spring){
			 MySpringConfig cfg = (MySpringConfig) spring.getSpringConfig();
			 /*Log.d("SPRING LIS..","svx="+springX.getVelocity());
			 Log.d("SPRING LIS..","svy="+springY.getVelocity());*/
			 if(springY.isOvershooting() && Math.abs(m)<1.55){
				 springY.setOvershootClampingEnabled(true);
				 springY.setEndValue(springY.getEndValue()+vy/25);
			 }
			 if(headParams.y>screenHeight-Utils.dpToPixels(80,getResources())&&!isHeld) {
				 springY.setEndValue(screenHeight-Utils.dpToPixels(28,getResources()));
			 }
				else if(headParams.y<=Utils.dpToPixels(30,getResources())&&!isHeld) {
					 springY.setEndValue(+headParams.height);
				}
			if(cfg.horizontal==true){
				headParams.x=(int) (springX.getCurrentValue());
			}
				/*if(isRightBound) {
					headParams.x=(int) (springX.getCurrentValue()-headParams.width*0.8);
				}
				else {
					headParams.x=(int) (springX.getCurrentValue()-headParams.width*0.2);
				}*/
			else{
				headParams.y=(int) springY.getCurrentValue()-headParams.height;
			}
			windowManager.updateViewLayout(chatHead, headParams);
		}
	}
	
	private void openView(){
		isItInflated = true;
		//goUp();
		//rl.setVisibility(View.VISIBLE);
		Intent intent = new Intent(ChatHeadService.this,Serve.class);
		startService(intent);
		chatHead.setVisibility(View.INVISIBLE);
		/*springX.setSpringConfig(new MySpringConfig(200,22,true));
		springY.setSpringConfig(new MySpringConfig(200,22,false));*/
		
		
        /*intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);*/
		/*Runnable r = new Runnable() {
		    @Override
		    public void run(){
		    	
		    	goUp();
		       //goIn();//<-- put your code in here.
		    }
		};
		Handler h = new Handler();
		h.postDelayed(r, 1110);*/
        //startService(intent);
        
		/*Runnable next= new Runnable() {
		    @Override
		    public void run(){
		    	if(!musicBound) bindIt(); //<-- put your code in here.
		    }
		};*/

		
		//h.postDelayed(next,750);
		// <-- the "1000" is the delay time in miliseconds. 
        //startService(intent);
		//MainActivity.this.overridePendingTransition(R.anim.entry_anim,R.anim.exit_anim);
	}
	
	public static void goUp(){
		/*chatHead.setVisibility(View.INVISIBLE);
		springX.setCurrentValue(screenWidth/2);
		springX.setEndValue(screenWidth/2);
		springY.setCurrentValue(-screenHeight*0.1);
		chatHead.setVisibility(View.VISIBLE);
		springY.setEndValue(screenHeight*0.1);*/
		//chatHead.setVisibility(View.VISIBLE);
		if(isRightBound){
			springX.setCurrentValue(screenWidth);
		}
		else{
			springX.setCurrentValue(-w);
		}
		
	}
	
	public static void comeBack(){
		chatHead.setVisibility(View.VISIBLE);
		if(isRightBound){
			springX.setCurrentValue(screenWidth);
		}
		else{
			springX.setCurrentValue(-w);
		}
		Runnable r = new Runnable() {
		    @Override
		    public void run(){
				if(isRightBound) springX.setEndValue(screenWidth-w*0.8);
		    	else springX.setEndValue(-w*0.2);
				
				Animation anim = null;
		        anim = AnimationUtils.loadAnimation(ctx, R.anim.alpha_for_but);
		        chatHead.setAnimation(anim);
		    }
		};
		Handler h = new Handler();
		h.postDelayed(r, 350);
		/*springX.setCurrentValue(screenWidth/2);
		springY.setCurrentValue(screenHeight*0.12);
		springY.setEndValue(screenHeight);
		
		Runnable r = new Runnable() {
		    @Override
		    public void run(){
		    	if(isRightBound) springX.setEndValue(screenWidth-w*0.8);
		    	else springX.setEndValue(-w*0.2);
				springY.setEndValue(mStartDragY);
			}
		};
		Handler h = new Handler();
		h.postDelayed(r, 50);*/
		
		/*if(springX.getCurrentValue()<screenWidth/2) {
			springX.setEndValue(0);
			if(isItInflated) {
				Runnable r = new Runnable() {
				    @Override
				    public void run(){
				    	springX.setEndValue(-w);
				    }
				};
				Handler h = new Handler();
				h.postDelayed(r, 3000);
			}
			else springY.setEndValue(mStartDragY);
		}
		else {
			springX.setEndValue(screenWidth);
			if(isItInflated) {
				Runnable r = new Runnable() {
				    @Override
				    public void run(){
				    	springX.setEndValue(screenWidth+w);
				    }
				};
				Handler h = new Handler();
				h.postDelayed(r, 3000);
			}
			else springY.setEndValue(mStartDragY);
		}
		*/
	}
	
	public int getX(){
		return headParams.x;
	}
	public int getY(){
		return headParams.y;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (intent!=null&&intent.getBooleanExtra("stop_service", false)){
			// If it's a call from the notification, stop the service.
			mNotificationManager.cancel(86);
			//stopForeground(true);
			stopSelf();
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
		startForeground(86,notification);
		}
		
		bindIt();
		/*if (intent.getBooleanExtra("stop_service", false)){
			// If it's a call from the notification, stop the service.
			stopSelf();
		}else{
			// Make the service run in foreground so that the system does not shut it down.
			Intent notificationIntent = new Intent(this, ChatHeadService.class);
			notificationIntent.putExtra("stop_service", true);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);
			//int sdk = android.os.Build.VERSION.SDK_INT;
	        
	        	Notification notification = new Notification(
	        			R.drawable.wall, 
						"Music Head launched",
				        System.currentTimeMillis());
				notification.setLatestEventInfo(
						this,
						"Music Head",
				        "Tap to close the widget.", 
				        pendingIntent);
				startForeground(86, notification);*/
	     
					
	        /*else{
	        	Notification noti = new Notification.Builder(this)
		         .setContentTitle("Music Head")
		         .setContentText("Tap to close the widget.")
		         .setSmallIcon(R.drawable.av_play)
		         .setLargeIcon(ImageUtils.drawableToBitmap(chatHead.getBackground()))
		         .addAction(R.drawable.av_play, "Music Head", pendingIntent)
		         .build();
	   
				 noti.priority=Notification.PRIORITY_MIN;
				 startForeground(86, noti);
	        }*/
		
		return START_STICKY;
	}

	class MySpringConfig extends SpringConfig {
        
        boolean horizontal;
        double tension;
        public MySpringConfig(double tension, double friction, boolean horizontal) {
            super(tension, friction);
            this.horizontal = horizontal;
            this.tension=tension;
        }
    }
	
	public void bindIt(){
		if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //startService(playIntent);
          }
	}
	
	public static Context getContext(){
		return ctx;
	}
	public static WindowManager getWindow(){
		return windowManager;
	}
	
	private ServiceConnection musicConnection = new ServiceConnection(){
	     
	      @Override
	      public void onServiceConnected(ComponentName name, IBinder service) {
	        MusicBinder binder = (MusicBinder)service;
	        //get service
	        musicSrv = binder.getService();
	        //pass list
	        Log.d("CHS","Music Service has been instantiated");
	        
		    musicBound = true;
		    if(Artists.DONE) {
		    	Log.d("CHS MS","Setting the list on time");
		    	setList();
		    }
		    else {
		    	Runnable r=new Runnable(){

					@Override
					public void run() {
						setList();
						Log.d("CHS MS","Setting the list late");
					}
		    		
		    	};
		    	Handler h=new Handler();
		    	h.postDelayed(r, 1000);
		    }
		    //setList();
		    
	      }
	      
	      @Override
	      public void onServiceDisconnected(ComponentName name) {
	        musicBound = false;
	      }
	    };
	
	public static void setList() {
		//MusicService.setList(Artists.songList);
        musicSrv.initialise();
	}
	    
	public static int getPagePos() {
		return pagePos;
	}
	
	public static void setPagePos(int pagePos) {
		ChatHeadService.pagePos = pagePos;
	}

	public static int getScrollPos() {
		return scrollPos;
	}

	public static void setScrollPos(int scroll) {
		ChatHeadService.scrollPos = scroll;
		Log.d("CHS","Position="+scroll);
	}

	@Override
	public void onDestroy(){
		stopService(new Intent(this,Serve.class));
		if(musicBound){
			if(musicSrv.isOncePlayed()){
				MusicService.setSongDur(musicSrv.getPosn());
			}
			this.unbindService(musicConnection);
		}
		try {
			springX.destroy();
			springY.destroy();
			windowManager.removeView(chatHead);	
		} catch (Exception e) {}
		
		//LastSongRun.setPreference();
		
		
		//stopService(new Intent(this,MusicService.class));
		super.onDestroy();
		
	}
	
	/*private class LongTouchListener implements OnLongClickListener{
		@Override
		public boolean onLongClick(View arg0) {
			Log.d("SERVE","Long click registered");
			isLongPressed=true;
			makeSeekBar();
			//body.clearFocus();
			return true;
		}
	}
	
	private class LongItemListener implements OnItemLongClickListener{
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			Log.d("SERVE","Long click registered");
			isLongPressed=true;
			makeSeekBar();
			body.clearFocus();
			return true;
		}
	}
	
	private class HeadTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getActionMasked();

			switch (action) {
			case MotionEvent.ACTION_MOVE:
				if(!isLongPressed) {
					Log.d("SERVE","Long click not detected ");
					return false;
				}
				else{
					Log.d("SERVE","Long click being moved now");
					bar.dispatchTouchEvent(event);
					bar.invalidate();
					//.setPressed(false);
					//pager.clearFocus();
					//bar.dispatchTouchEvent(event);
					//bar.setPressed(true);
					//bar.requestFocus();
					
					param1.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
					windowManager.updateViewLayout(body, param1);
					return true;
				}
			case MotionEvent.ACTION_UP:
				if(isLongPressed){
					Log.d("SERVE","Long click being removed now");
					isLongPressed=false;
					windowManager.removeView(bar);
					return true;
				}
				else return false;
			case MotionEvent.ACTION_CANCEL:
				Log.d("SERVE","Long click being canceled");
				bar.dispatchTouchEvent(event);
				return false;
			default:
				return false;
			}
			//return true;

		}
	}*/
	

}