package frontend;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import backend.AlbumArtGet;
import backend.Artists;
import backend.PlaylistOp;

public class MainActivity extends Activity {

	private static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		context=this;
	    if(savedInstanceState == null){
            // everything else that doesn't update UI
	    	Artists art=new Artists();
			AlbumArtGet get=new AlbumArtGet();
			PlaylistOp play=new PlaylistOp();
			FragmentManager frag=getFragmentManager();
			frag.beginTransaction().add(art, "Songs").add(play, "PlayList").add(get, "AlbumArt").commit();
			Intent intent = new Intent(MainActivity.this,ChatHeadService.class);
		    intent.setAction("foo");
			startService(intent);
			
        }
	    finish();
	}
	
	public static Context getContext(){
		return context;
	}
}
