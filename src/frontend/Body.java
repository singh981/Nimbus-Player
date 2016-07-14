package frontend;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.samplemusic.R;


public class Body extends RelativeLayout{
	
	LayoutInflater mInflater;
	Context ctx;
	
	//Constructors begin here
	public Body(Context context) {
		super(context);
		ctx=context;
		mInflater = LayoutInflater.from(context);
        init();
	
	}
	public Body(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    ctx=context;
	    mInflater = LayoutInflater.from(context);
        if (!isInEditMode()) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
	    init();
	}

	public Body(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    ctx=context;
	    mInflater = LayoutInflater.from(context);
        if (!isInEditMode()) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
	    init();
	}
	
	public void init()
	   {
	       mInflater.inflate(R.layout.activity_main, this, true);
	       
	   }
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	        /*if (event.getAction() == KeyEvent.ACTION_DOWN
	                && event.getRepeatCount() == 0) {

	            // Tell the framework to start tracking this event.
	            getKeyDispatcherState().startTracking(event, this);
	            return true;

	        }
	        else if (event.getAction() == KeyEvent.ACTION_UP) {
	            getKeyDispatcherState().handleUpEvent(event);
	            if (event.isTracking() && !event.isCanceled()) {

	                // DO BACK ACTION HERE
	                return true;

	            }
	        }
	        return super.dispatchKeyEvent(event);*/
	    	if (event.getAction() == KeyEvent.ACTION_DOWN
	                && event.getRepeatCount() == 0) {

	            // Tell the framework to start tracking this event.
	            getKeyDispatcherState().startTracking(event, this);
	            return true;

	        }
	    	
	    	else if (event.getAction() == KeyEvent.ACTION_UP) {
	            getKeyDispatcherState().handleUpEvent(event);
	            if (event.isTracking() && !event.isCanceled()) {

	                // DO BACK ACTION HERE
            		if(Serve.getAlbumClicked()==true
	    	    			||Serve.getArtistClicked()==true
	    	    			||Serve.isPlaylistClicked()==true){
	    	    		Serve.bringBackPager();
	    	    		return true;
            		}
            		else{
            			((Service) ctx).stopSelf();
            	    	ChatHeadService.comeBack();
            	    	return true;
            		}
	            	
	            }
	        }
	    	Log.d("VI","Back button pressed");
	    	return super.dispatchKeyEvent(event);
	    } 
	    else if(event.getKeyCode() == KeyEvent.KEYCODE_HOME){
	    	((Service) ctx).stopSelf();
	    	Log.d("VI","Home button pressed");
	    	return true;
	    
	    }
	    else {
	        return super.dispatchKeyEvent(event);
	    }
	}
	
	
}
