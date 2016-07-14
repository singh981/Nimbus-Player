package frontend;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import backend.Albums;
import backend.Artists;
import backend.Genre;
import backend.Titles;

import com.example.samplemusic.R;

public class MakeD extends DialogFragment{
	
	private Inflater inflate;
	
	private RelativeLayout main;
	private ImageButton playPause;
	private ViewPager viewPager;
	private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
	private View view;
	
	private boolean isModal = false;

    public static MakeD newInstance()
    {
    	MakeD frag = new MakeD();
        frag.isModal = true; // WHEN FRAGMENT IS CALLED AS A DIALOG SET FLAG
        return frag;
    }

    public MakeD()
    {
    } 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		if(isModal) // AVOID REQUEST FEATURE CRASH
        {
        return super.onCreateView(inflater, container, savedInstanceState);
        }
        else
        {
		//LayoutInflater in = getActivity().getLayoutInflater();
	    view=inflater.inflate(R.layout.activity_main, container,false);
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    
	    
	    
	    
	    final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
	    final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
	   // normal=ImageUtils.drawableToBitmap(wallpaperDrawable);
	           
	    //requestWindowFeature(Window.FEATURE_NO_TITLE);
	    main=(RelativeLayout)view.findViewById(R.id.rel);
	    
	    playPause=(ImageButton)view.findViewById(R.id.play_pause);
	    //lin=(LinearLayout)playPause.getParent();
	    playPause.setOnClickListener(new OnClickListener(){
	    	
			@Override
			public void onClick(View v) {
				if(inflate.isPlaying()) {
					inflate.pause();
					playPause.setBackgroundResource(R.drawable.av_play);
				}
				else {
					((Inflater) getActivity()).start();
					playPause.setBackgroundResource(R.drawable.av_pause);
				}
				
			}});
	    viewPager = (ViewPager) view.findViewById(R.id.pager);
	    viewPager.setId(R.id.pager);
	    int sdk = android.os.Build.VERSION.SDK_INT;
	    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
	        main.setBackgroundDrawable(wallpaperDrawable);
	    } else {
	    	 main.setBackground(wallpaperDrawable);
	    }
	    
	    mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getChildFragmentManager());
	    
	    
	    viewPager.setOffscreenPageLimit(3);
	    viewPager.setAdapter(mDemoCollectionPagerAdapter);
	    viewPager.setCurrentItem(1);
		return view;
        }
	}
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Dialog dialog = new Dialog(getActivity());
		View view = getActivity().getLayoutInflater().inflate(R.layout.activity_main, null);
	    dialog.setContentView(view);
	    
	    WindowManager.LayoutParams params = dialog.getWindow().getAttributes(); 
	    params.x = Utils.dpToPixels(0, getResources());
	    params.y = (int) (getResources().getDisplayMetrics().heightPixels*0.2);
	    params.width =  ViewGroup.LayoutParams.MATCH_PARENT;
	    params.height = (int) (getResources().getDisplayMetrics().heightPixels*0.8);
	    
	    dialog.getWindow().setAttributes(params);
	   
	    
	    //getWindow().setBackgroundDrawableResource(R.drawable.bkg);
	    
	   // mBlurredImageHeader=(ImageView)findViewById(R.id.blurred_image_header);
	    
	    /*File normalImage = new File(getFilesDir() + NORMAL_IMG_PATH);
	    if(normal==BitmapFactory.decodeFile(normalImage.getAbsolutePath())){
	    	Log.d("1st1st1st1st1st","Blurred Image is available");
	    	updateView(screenWidth);
	    }
	    else{
	    	Log.d("1st1st1st1st1st","Blurred Image is not available");
	    	ImageUtils.storeImage(normal, normalImage);
	    	getBlur();
	    }
	    */
	    
	    
	    // Create an adapter that when requested, will return a fragment representing an object in
	    // the collection.
	    // 
	    // ViewPager and its adapters use support library fragments, so we must use
	    // getSupportFragmentManager.
	    
	    // Set up the ViewPager, attaching the adapter.
	    
	    return dialog;
	}
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
}
