package backend;

import frontend.Utils;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;
import backend.MusicService.MusicBinder;

import com.example.samplemusic.R;

public class BodyService extends Service implements MediaPlayerControl{
	static ArrayList<String> album=new ArrayList<String>();
	static ArrayList<Bitmap> bit=new ArrayList<Bitmap>();
	private static MusicService musicSrv=null;
	private Intent playIntent;
	private boolean musicBound=false;
	private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    private ViewPager viewPager;
    
    private static Context context;
    private RelativeLayout main;
    private LinearLayout lin;
    private Bitmap normal=null;
    private ImageView mBlurredImageHeader;
    
    private int screenWidth ;
    //This is where the initialisation of media control objects starts
    private MusicController controller;
	private WindowManager windowManager;
	private RelativeLayout body;
	private RelativeLayout temp;
	private ListView list;
	private static String[] names={"Song","Song","Song","Song","Song","Song","Song","Song","Song"};
	private WindowManager.LayoutParams params;
    private static ImageButton playPause;
	
    @Override 
	public void onCreate(){
		Log.d("TAG","Entered the BodyService class!!");
		super.onCreate();
		windowManager=(WindowManager)getSystemService(WINDOW_SERVICE);
		body=(RelativeLayout) LayoutInflater.from(this).
			inflate(R.layout.service_head, null);
		/*temp=(RelativeLayout) LayoutInflater.from(this).
				inflate(R.layout.temp, null);
		list=(ListView)temp.findViewById(R.id.temp_list);
		list.setAdapter(new ArrayAdapter<String>(this,R.id.temp_list,names));*/
		/*params=new WindowManager.LayoutParams(
				Utils.dpToPixels(100, getResources()),
				Utils.dpToPixels(100, getResources()),
				WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
				params.gravity=Gravity.LEFT|Gravity.TOP;
				params.x=0;
				params.y=0;*/
		WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
		wlp.gravity = Gravity.BOTTOM;
		wlp.height = Utils.dpToPixels(100, getResources());
		wlp.width = Utils.dpToPixels(100, getResources());
		wlp.type=WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		wlp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		wlp.format = PixelFormat.TRANSLUCENT;
				windowManager.addView(body, wlp);
				body.setVisibility(View.VISIBLE);
				final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
		        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
		        
		        /*int sdk = android.os.Build.VERSION.SDK_INT;
		        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
		            temp.setBackgroundDrawable(wallpaperDrawable);
		        } else {
		        	 temp.setBackground(wallpaperDrawable);
		        }*/
		        /*viewPager = (ViewPager) body.findViewById(R.id.pager);
		       
		        //mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getFragmentManager());
		        
		        viewPager.setOffscreenPageLimit(3);
		        viewPager.setAdapter(mDemoCollectionPagerAdapter);
		        viewPager.setCurrentItem(1);
		        
		        setController();
		        
				playPause=(ImageButton)body.findViewById(R.id.play_pause);
		        lin=(LinearLayout)playPause.getParent();
		        playPause.setOnClickListener(new OnClickListener(){
		        	
					@Override
					public void onClick(View v) {
						if(isPlaying()) {
							pause();
							playPause.setBackgroundResource(R.drawable.av_play);
						}
						else {
							start();
							playPause.setBackgroundResource(R.drawable.av_pause);
						}
						
					}});*/
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
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
    	new MyAsyncTask().execute(" ");
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
    
    public static Context getContext(){
		return context;
	}

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class DemoCollectionPagerAdapter extends FragmentPagerAdapter {

        public DemoCollectionPagerAdapter(android.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        
        @Override
        public Fragment getItem(int i) {
        	switch(i){
    			case 0:
    				Albums albums=new Albums();
    				Log.d("FRAGMENT","Making the albums fragment");
	            	return albums;			       
	            
    			case 1:
    				Artists artists=new Artists();
    				Log.d("FRAGMENT","Making the artists fragment");
	            	return artists;			       
	         		
			    case 2:
        			Titles song = new Titles();
			     	//fragment.change();
		           	Log.d("FRAGMENT","Making the songs fragment");
	            	return song;
	            	
			    case 3:
			    	Genre genre=new Genre();
			    	Log.d("FRAGMENT","Making the genrefragment");
	            	return genre;			       
	            	
			}
			return null;
        }
        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int pos) {
        	if(pos==0) return "Albums";
        	else if(pos==1) return "Artists";
        	else if(pos==2) return "Songs";
        	else return "Genre";
        }
    }
    @Override 
    public int onStartCommand(Intent intent, int flags, int startId){
    	if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
          }
    	return Service.START_NOT_STICKY;
    	
    }

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
    	controller.setAnchorView(body.findViewById(R.id.lin));
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
	private class MyAsyncTask extends AsyncTask<String, Void,Void>{
		
		
		@Override
		protected Void doInBackground(String... params) {

			Log.d("ALBUMartTAG", "About to make the  content resolver");
			ContentResolver contentResolver=getContext().getContentResolver();
			Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
			Log.d("ALBUMartTAG", "In the background task"+uri);
			
			final String[] projection = new String[]{MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ALBUM_ID};
			Cursor cursor = contentResolver.query(uri,projection, where, null,null);
			
			for( int i = 0; i < cursor.getColumnCount(); i++) {
			    Log.d("Column: ",cursor.getColumnName(i));
			}
			Log.d("ALBUMartTAG", "After ");
			
			
			if (!cursor.moveToFirst()) {
			    // no media on the device
			} 
			else {
				
			    int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			    Long prevId=(long) 0;
			    Bitmap bitmap=null;
			    bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                           R.drawable.ic_launcher);
			    do {
			       String thisAlbum= cursor.getString(albumColumn);
			       Long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
			       if(albumId==prevId) continue;
			       prevId=albumId;
			       Log.d("ALBUMID",String.valueOf(albumId));
			       // ...process entry...
			       Uri sArtworkUri = Uri
	                       .parse("content://media/external/audio/albumart");
			       
			    	   Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
			    	   Log.d("TAG",String.valueOf(albumId));
		               Log.d("TAG",albumArtUri.getPath());
		               bitmap=MusicUtils.getArtworkQuick(getContext(), albumId, 200, 200);
		               //bitmap=MusicUtils.getBitmapFromUri(albumArtUri);
				          
		               /*try {
		            	   ParcelFileDescriptor pfd = CollectionDemoActivity.getContext().getContentResolver()
			                       .openFileDescriptor(albumArtUri, "r");
		            	   
		                       FileDescriptor fd = pfd.getFileDescriptor();
		                       bitmap = BitmapFactory.decodeFileDescriptor(fd);
		                    
		                  

		               } catch (FileNotFoundException exception) {
		                   exception.printStackTrace();
		                   bitmap = BitmapFactory.decodeResource(CollectionDemoActivity.getContext().getResources(),
		                           R.drawable.ic_launcher);
		               } catch (IOException e) {

		                   e.printStackTrace();
		               }
			       
		               try {
		                    bitmap = MediaStore.Images.Media.getBitmap(
		                    		CollectionDemoActivity.getContext().getContentResolver(), albumArtUri);
		                    bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

		                } catch (FileNotFoundException exception) {
		                    exception.printStackTrace();
		                    bitmap = BitmapFactory.decodeResource(CollectionDemoActivity.getContext().getResources(),
		                            R.drawable.ic_launcher);
		                } catch (IOException e) {

		                    e.printStackTrace();
		                }

			       bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
*/
	               /*int x=0;
			       for(x =0;x<album.size();x++) {
			    	   if(album.get(x).getName()!=thisAlbum) continue;
	            	   
	               }
			       if(x==(album.size()-1)){
			    	   album.add(new AlbumArt(thisAlbum,bitmap));
				       Log.d("AlbumADD","Added "+thisAlbum);
			       }*/
			       if(!album.contains(thisAlbum)){
		    	   album.add(thisAlbum);
		    	   Log.d("AlbumADD","Added "+thisAlbum);
		    	   bit.add(bitmap);
			       }
			    } while (cursor.moveToNext());
			} 
			
		
			
			return null;
		}
		@Override 
		protected void onPostExecute(Void result){
			for(int x =0;x<album.size();x++){
				Log.d("ALL ALBUM",album.get(x));
					
			}
		}
		
	}
}
