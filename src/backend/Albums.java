package backend;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.samplemusic.R;

public class Albums extends Fragment{
	
	private ListView lv;
	private static SongAdapter adapter;
	public static boolean flag=true;

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View rootView = inflater.inflate(R.layout.activity_title, container, false);
	        lv=(ListView)rootView.findViewById(R.id.music_list);
	       
	        //adapter=new SongAdapter(getActivity().getApplicationContext(),Artists.songList,0);
 	       // ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
 	       // SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(swingRightInAnimationAdapter);

 	        //scaleInAnimationAdapter.setAbsListView(lv);
 	        //lv.setAdapter(scaleInAnimationAdapter);
 	        lv.setAdapter(adapter);
	       
	        return rootView;
	    }
	 
	 	
}