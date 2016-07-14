package frontend;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;
import backend.AlbumArtGet;
import backend.Artists;
import backend.MusicController;
import backend.MusicService;
import backend.MusicService.MusicBinder;

import com.example.samplemusic.R;

public class Inflater extends Activity implements MediaPlayerControl{
	
	private static Context context;
	private static ListView listView;
	private static ArrayList<String> list;
	private static AlertDialog builder;
	private static Window window;
	private static Resources res;
	private static final String BLURRED_IMG_PATH = "blurred_image.png";
	private static final String NORMAL_IMG_PATH = "normal_image.png";
	private static final int TOP_HEIGHT = 700;

	private static MusicService musicSrv=null;
	private Intent playIntent;
	private boolean musicBound=false;
	
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    //private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    private ViewPager viewPager;
    
    private RelativeLayout main;
    private LinearLayout lin;
    //private Bitmap normal=null;
    private ImageView mBlurredImageHeader;
    
    private int screenWidth ;
    //This is where the initialisation of media control objects starts
    private MusicController controller;
    private static ImageButton playPause;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        getAlbumArt();
        setController();
       showDialog();
        
    }
    public void showDialog(){
    	MakeD newFragment = new MakeD();
        newFragment.show(getFragmentManager(), "body");
    }
	
	/*public  void initialiseDialog(){
		    builder = new AlertDialog.Builder(getContext()).create();
		    LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
		    View view=inflater.inflate(R.layout.activity_main, null);
		    
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		    builder.setView(view);
		    //listView=(ListView) view.findViewById(R.id.list);
			
		    // Get the layout inflater
		    
		    list=new ArrayList<String>();
	        for(int i=0;i<30;i++){
	        	list.add("Song "+i);
	        }
	        ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,list);
	        listView.setAdapter(adapter);
		    // Add action buttons
		    WindowManager.LayoutParams params = getWindow().getAttributes(); 
		    params.x = Utils.dpToPixels(0, getResources());
		    params.y = (int) (getResources().getDisplayMetrics().heightPixels*0.2);
		    //params.width =  (int) (getResources().getDisplayMetrics().widthPixels);
		    params.height = (int) (getResources().getDisplayMetrics().heightPixels*0.8);

		    //this.getWindow().setAttributes(params);
		    //builder.show();
		    //builder.s
		
		}*/
	public static void show(){
		builder.show();
	}
	public void dismiss(){
		builder.dismiss();
	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        // do something on back.
	    	builder.cancel();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}*/
	/*public static Context getContext(){
		return context;
	}
	
	public static Window getWin(){
		return window;
	}
	
	private static Resources getRes(){
		return res;
	}
	*/
	
    @Override
    protected void onStart() {
      super.onStart();
      if(playIntent==null){
        playIntent = new Intent(this, MusicService.class);
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);
      }
    }
    
  //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){
     
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        MusicBinder binder = (MusicBinder)service;
        //get service
        musicSrv = binder.getService();
        //pass list
        Log.d("CDA","Music Service has been instantiated");
        musicSrv.setList(Artists.songList);
        musicBound = true;
      }
     
      @Override
      public void onServiceDisconnected(ComponentName name) {
        musicBound = false;
      }
    };
    
    public static void songPicked(int pos){
    	  //musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
    	musicSrv.setSong(pos);
    	musicSrv.playSong();
    	playPause.setBackgroundResource(R.drawable.av_pause);
    	}
    
    public void getAlbumArt(){
    	AlbumArtGet art=new AlbumArtGet();
    	android.app.FragmentManager fm=getFragmentManager();
    	fm.beginTransaction().add(art, "Tag").commit();
    }
    
   /* public void getBlur(){
    	final File blurredImage = new File(getFilesDir() + BLURRED_IMG_PATH);
		if (blurredImage.exists()) {

			// launch the progressbar in ActionBar
			setProgressBarIndeterminateVisibility(true);
			
			new Thread(new Runnable() {

				@Override
				public void run() {

					// No image found => let's generate it!
					//BitmapFactory.Options options = new BitmapFactory.Options();
					//options.inSampleSize = 2;
					
					
					Bitmap newImg = Blur.fastblur(getContext(), normal, 20);
					ImageUtils.storeImage(newImg, blurredImage);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							updateView(screenWidth);

							// And finally stop the progressbar
							setProgressBarIndeterminateVisibility(false);
						}
					});

				}
			}).start();

		}// else {

			// The image has been found. Let's update the view
			//updateView(screenWidth);

		//}

    }*/
    
    /*public static Context getContext(){
		return context;
	}*/

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
   

   /* private void updateView(final int screenWidth) {
		Bitmap bmpBlurred = BitmapFactory.decodeFile(getFilesDir() + BLURRED_IMG_PATH);
		//bmpBlurred = Bitmap.createScaledBitmap(bmpBlurred, screenWidth, (int) (bmpBlurred.getHeight()
			//	* ((float) screenWidth) / (float) bmpBlurred.getWidth()), true);
		
		//mBlurredImageHeader.setoriginalImage(bmpBlurred);
		
		bmpBlurred=Bitmap.createBitmap(bmpBlurred,0, 0,bmpBlurred.getWidth(), lin.getHeight());
		Drawable d = new BitmapDrawable(getResources(),bmpBlurred);
		int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            lin.setBackgroundDrawable(d);
        } else {
        	 lin.setBackground(d);
        }
       
    }*/

    
    //this is where the media control part of the activity starts
    
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
    	controller.setAnchorView(findViewById(R.id.lin));
    	controller.setEnabled(true);
    }
    
  //play next
    private void playNext(){
      musicSrv.playNext();
      controller.show(0);
    }
     
    //play previous
    private void playPrev(){
      musicSrv.playPrev();
      controller.show(0);
    }
    
    @Override
    public void pause() {
      musicSrv.pausePlayer();
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
		if(musicSrv!=null)
		return musicSrv.getDur();
		  else return 0;
	}

	@Override
	public int getCurrentPosition() {
		if(musicSrv!=null)
		    return musicSrv.getPosn();
		  else return 0;
	}
	@Override
	public boolean isPlaying() {
		if(musicSrv!=null )
		    return musicSrv.isPng();
		  return false;
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
	
}
