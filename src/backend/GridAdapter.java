package backend;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samplemusic.R;

import frontend.Serve;
import frontend.Utils;

public class GridAdapter extends BaseAdapter{
	private ArrayList<Song> song;
	private ArrayList<PlayList> playList;
	private ArrayList<String> art=AlbumArtGet.getAlbum();
	private ArrayList<Bitmap> bit=AlbumArtGet.getBit();
	private static ArrayList<Integer> selected=new ArrayList<Integer>();
	private static ArrayList<String> album=new ArrayList<String>();
	private static ArrayList<String> artist=new ArrayList<String>();
	private LruCache<String, Bitmap> mMemoryCache;

	private LayoutInflater songInf;
	private int tab;
	private static boolean longClick=false;
	
	public static ArrayList<Integer> getSelected() {
		return selected;
	}
	public static boolean isLongClick() {
		return longClick;
	}
	public static void setLongClick(boolean longC) {
		longClick = longC;
	}
	
	public static ArrayList<String> getAlbum() {
		return album;
	}

	public static ArrayList<String> getArtist() {
		return artist;
	}

	
	
	//constructor
	public GridAdapter(Context c, ArrayList<Song> theSongs,int tabSet){
		song=theSongs;
		songInf=LayoutInflater.from(c);
		tab=tabSet;
		put();
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}
	public GridAdapter(Context c,int tabSet,ArrayList<PlayList> theSongs){
		playList=theSongs;
		songInf=LayoutInflater.from(c);
		tab=tabSet;
	}
	
	private void put(){
		for(int i=0;i<song.size();i++){
    		
        	
        	if(!isIn(song.get(i).getAlbum(),album)) {
        		album.add(song.get(i).getAlbum());
        		//Log.d("ISIN", song.get(i).getAlbum());
        	}
        	
    		if(!isIn(song.get(i).getArtist(),artist)) artist.add(song.get(i).getArtist());
    		
    	}
		Collections.sort(album,new Comparator<String>(){

			@Override
			public int compare(String s1, String s2) {
				// TODO Auto-generated method stub
				return s1.compareTo(s2);
			}
    		
    	});
		Collections.sort(artist,new Comparator<String>(){

			@Override
			public int compare(String s1, String s2) {
				// TODO Auto-generated method stub
				return s1.compareTo(s2);
			}
    		
    	});
		
	}
	
	private boolean isIn(String query,ArrayList<String> string){
		return string.contains(query);
	}


	@Override
	public int getCount() {
		if(tab==0){
			return playList.size();
		}
		else if(tab==1) {
		//Log.d("Artists COUNT",String.valueOf(artist.size()));
		return artist.size();
		}
		else if(tab==2) {
			
			//Log.d("Albums COUNT",String.valueOf(album.size()));
			return album.size();	
		}
		else return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        if(convertView==null)
            vi = songInf.inflate(R.layout.list_column, null);
        Bitmap bm=null;
        int x=0;
        
        vi.setBackgroundColor(selected.contains(position) ? 
        		Color.argb(80,255, 255, 255) : Color.TRANSPARENT);
        
        vi.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				longClick=true;
				Serve.makeOptions();
				v.setBackgroundColor(Color.argb(80,255, 255, 255));
				selected.clear();
				selected.add(position);
				return true;
			}
        	
        });
        vi.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(longClick) {
					if(selected.contains(position)) {
						v.setBackgroundColor(Color.TRANSPARENT);
						selected.remove(selected.indexOf(position));
					}
					else {
						v.setBackgroundColor(Color.argb(80,255, 255, 255));
						selected.add(position);
					}
					return;
				}
				Serve.makeList(position,tab);
				if(tab==0) Serve.setPlaylistClicked(true);
				else if(tab==2)Serve.setAlbumClicked(true);
				else if(tab==1)Serve.setArtistClicked(true);
			}
        });
        
        TextView name = (TextView)vi.findViewById(R.id.item_title); // duration
        ImageView gridImage=(ImageView)vi.findViewById(R.id.grid_image); // thumb image
        
        Animation animation = null;
        animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.push_left_in);
        
        switch(tab){
        
        case 0:
        	name.setText(playList.get(position).getName());
        	gridImage.setImageResource(R.drawable.no_art);
        	//loadBitmap((position-1-2*position),gridImage);
        	//Log.d("SETARt", artist.get(position));
        	//title.setText(x.getArtist());
        	break;
        
        case 1:
        	/*if(position%2==0){
        		animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.grid_left);
        	}
        	else{
        		animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.grid_right);
        	}*/
        	/*animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.fade_in);
        	gridImage.setAnimation(animation);
        	animation=null;*/
        	name.setText(artist.get(position));
        	loadBitmap(position, gridImage);
        	if(position>3){
        		if(position%2==0){
            		animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.grid_left);
            	}
            	else{
            		animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.grid_right);
            	}
            	gridImage.setAnimation(animation);
            	animation=null;
            	
        	}
        	
        	//Log.d("SETARt", artist.get(position));
        	//title.setText(x.getArtist());
        	break;
        
		case 2:
			
            if(art.contains(album.get(position))){ 
            		x=art.indexOf(album.get(position));
            		//loadBitmap(bit.get(x),gridImage);
            		bm=bit.get(x); 
            		gridImage.setImageBitmap(bm);
            		//Log.d("Bitmap","art.get(x).getName()");
            }
            else{
            	/*Drawable draw=songInf.getContext().getResources().getDrawable(R.drawable.no_art);
            	bm=MusicUtils.drawableToBitmap(draw);
            	bm=MusicUtils.getCroppedBitmap(bm);*/
            	gridImage.setImageResource(R.drawable.no_art);
            }
        	
            name.setText(album.get(position));
            //if(position>3){
        		if(position%2==0){
            		animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.grid_left);
            	}
            	else{
            		animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.grid_right);
            	}
            	gridImage.setAnimation(animation);
            	animation=null;
            	
        	//}
        	
        	//Log.d("SET ALBUM", album.get(position));
        	/*for(x=0;x<song.size();x++){
        		if(song.get(x).getAlbum()==album.get(position)) break;
        	}*/
        	//sTitle.setText(song.get(x).getAlbum());
        	//title.setText(x.getAlbum());
            //sTitle.setText(x.getArtist());
        	break;
        }
        return vi;
        
	}
		@Override
		public boolean isEnabled(int position)
		{
		return true;
		}
		
		static class AsyncDrawable extends BitmapDrawable {
		    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		    public AsyncDrawable(Resources res, Bitmap bitmap,
		            BitmapWorkerTask bitmapWorkerTask) {
		        super(res, bitmap);
		        bitmapWorkerTaskReference =
		            new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		    }

		    public BitmapWorkerTask getBitmapWorkerTask() {
		        return bitmapWorkerTaskReference.get();
		    }
		}

		public void loadBitmap(int pos, ImageView imageView) {
			final String imageKey = String.valueOf(pos);

		    final Bitmap bitmap = getBitmapFromMemCache(imageKey);
		    if (bitmap != null) {
		        imageView.setImageBitmap(bitmap);
		    }
		    else if (cancelPotentialWork(pos, imageView)) {
		        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
		        
		        Drawable draw=songInf.getContext().getResources().getDrawable(R.drawable.no_art);
		        Bitmap b=MusicUtils.drawableToBitmap(draw);
	        	b=MusicUtils.getCroppedBitmap(b);
	        	
		        final AsyncDrawable asyncDrawable =
		                new AsyncDrawable(songInf.getContext().getResources(), b, task);
		        imageView.setImageDrawable(asyncDrawable);
		        task.execute(pos);
		    }
		}
		
		public static boolean cancelPotentialWork(int pos, ImageView imageView) {
		    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		    if (bitmapWorkerTask != null) {
		        final int bitmapData = bitmapWorkerTask.pos;
		        // If bitmapData is not yet set or it differs from the new data
		        if (bitmapData == 0 || bitmapData != pos) {
		            // Cancel previous task
		            bitmapWorkerTask.cancel(true);
		            Log.d("GA","Canceled a task");
		        } else {
		            // The same work is already in progress
		            return false;
		        }
		    }
		    // No task associated with the ImageView, or an existing task was cancelled
		    return true;
		}
		
		private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
			   if (imageView != null) {
			       final Drawable drawable = imageView.getDrawable();
			       if (drawable instanceof AsyncDrawable) {
			           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
			           return asyncDrawable.getBitmapWorkerTask();
			       }
			    }
			    return null;
			}
		
		class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		    private final WeakReference<ImageView> imageViewReference;
		    private int pos=0;
		    private Bitmap base;
		    private ArrayList<Integer> done=new ArrayList<Integer>();
		    
		    public BitmapWorkerTask(ImageView imageView) {
		        // Use a WeakReference to ensure the ImageView can be garbage collected
		        imageViewReference = new WeakReference<ImageView>(imageView);
		    }

		    // Decode image in background.
		    @Override
		    protected Bitmap doInBackground(Integer... params) {
		        pos = params[0];
		        done.clear();
		        int count =0;
		        base = Bitmap.createBitmap(Utils.dpToPixels(140, songInf.getContext().getResources())
		        		, Utils.dpToPixels(140, songInf.getContext().getResources()),
		        		Bitmap.Config.ARGB_8888); 
		        
            	String name=artist.get(pos);
		        for(int i=0;i<song.size();i++){
		        	if(song.get(i).getArtist().equals(name) && art.contains(song.get(i).getAlbum())
		        			&& !done.contains(art.indexOf(song.get(i).getAlbum()))){
		        		count++;
		        		done.add(art.indexOf(song.get(i).getAlbum()));		        		
		        		if(count==3) break;
		        	}
		        }
	        	
		        Canvas c=new Canvas(base);
        		/*Drawable draw=songInf.getContext().getResources().
        		 * getDrawable(R.drawable.itunes_gold_low);
 		        Bitmap b=MusicUtils.drawableToBitmap(draw);
 	        	b=MusicUtils.getCroppedBitmap(b);
 	        	b.setHeight(base.getHeight()/2);
 	        	b.setWidth(base.getWidth()/2);*/
 	        	/*b.setHeight(base.getHeight());
 	        	b.setWidth(base.getWidth());
 	        	c.drawBitmap(b
    					,0,0, null);
 	        	b.setHeight(base.getHeight()/2);
 	        	b.setWidth(base.getWidth()/2);*/
        		switch(count){
        		case 0: 
        			base=null;
        			return base;
        		case 1:
        			/*bit.get(done.get(0)).setHeight(Utils.dpToPixels(140,
        					songInf.getContext().getResources()));
        			bit.get(done.get(0)).setWidth(Utils.dpToPixels(140,
        					songInf.getContext().getResources()));*/
        			
        			c.drawBitmap(bit.get(done.get(0))
        					,0,0, null);
        			/*c.drawBitmap(bit.get(done.get(0))
        					,base.getWidth()/4,base.getHeight()/2, null);
        			c.drawBitmap(bit.get(done.get(0))
        					,base.getWidth()/2,base.getHeight()/4, null);
        			c.drawBitmap(bit.get(done.get(0))
        					,base.getWidth()/4,0, null);*/
        			break;
        		case 2:
        			/*bit.get(done.get(0)).setHeight(Utils.dpToPixels(140,
        					songInf.getContext().getResources()));
        			bit.get(done.get(0)).setWidth(Utils.dpToPixels(140,
        					songInf.getContext().getResources()));
        			
        			bit.get(done.get(1)).setHeight(Utils.dpToPixels(140,
        					songInf.getContext().getResources()));
        			bit.get(done.get(1)).setWidth(Utils.dpToPixels(140,
        					songInf.getContext().getResources()));
        			*/
        			c.drawBitmap(bit.get(done.get(0))
        					,0,0, null);
        			/*Bitmap b=Bitmap.createScaledBitmap(bit.get(done.get(1)),
        					Utils.dpToPixels(120,
        					songInf.getContext().getResources()), 
        					Utils.dpToPixels(120,
    	        					songInf.getContext().getResources()),false);*/
        			
        			c.drawBitmap(Bitmap.createScaledBitmap(bit.get(done.get(1)),
        					Utils.dpToPixels(120,
        					songInf.getContext().getResources()), 
        					Utils.dpToPixels(120,
    	        					songInf.getContext().getResources()),false)
        					,Utils.dpToPixels(10,
    	        					songInf.getContext().getResources()),Utils.dpToPixels(20,
    	    	        					songInf.getContext().getResources()), null);
        			/*c.drawBitmap(bit.get(done.get(1))
        					,base.getWidth()/2,base.getHeight()/2, null);
        			c.drawBitmap(bit.get(done.get(0))
        					,base.getWidth()/4,0, null);*/
        			break;
        		case 3:
        			c.drawBitmap(bit.get(done.get(2))
        					,0,0, null);
        			c.drawBitmap(Bitmap.createScaledBitmap(bit.get(done.get(1)),
        					Utils.dpToPixels(120,songInf.getContext().getResources()), 
        					Utils.dpToPixels(120,songInf.getContext().getResources()),false)
        					,
        					Utils.dpToPixels(10,songInf.getContext().getResources()),
        					Utils.dpToPixels(20,songInf.getContext().getResources()),
        					null);
        			c.drawBitmap(Bitmap.createScaledBitmap(bit.get(done.get(0)),
        					Utils.dpToPixels(100,songInf.getContext().getResources()), 
        					Utils.dpToPixels(100,songInf.getContext().getResources()),false)
        					,
        					Utils.dpToPixels(20,songInf.getContext().getResources()),
        					Utils.dpToPixels(40,songInf.getContext().getResources()),
        					null);
        			break;
        		}
        		base=MusicUtils.getCroppedBitmap(base);
        		addBitmapToMemoryCache(String.valueOf(pos),base);
		        return base;
		        
		    }

		    // Once complete, see if ImageView is still around and set bitmap.
		    @Override
		    protected void onPostExecute(Bitmap bitmap) {
		    	if (isCancelled()) {
		            bitmap = null;
		        }

		        if (imageViewReference != null && bitmap != null) {
		            final ImageView imageView = imageViewReference.get();
		            final BitmapWorkerTask bitmapWorkerTask =
		                    getBitmapWorkerTask(imageView);
		            if (this == bitmapWorkerTask && imageView != null) {
		            	//imageView.setBackgroundResource(R.drawable.no_art);
		                imageView.setImageBitmap(bitmap);
		            }
		        }
		        
		    }
		}
		
		public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		    if (getBitmapFromMemCache(key) == null) {
		        mMemoryCache.put(key, bitmap);
		    }
		}

		public Bitmap getBitmapFromMemCache(String key) {
			Log.d("GA",key);
		    return mMemoryCache.get(key);
		}
}

