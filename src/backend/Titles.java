package backend;

import com.example.samplemusic.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Titles extends Fragment{
	
	private ListView lv;
	private static SongAdapter adapter;
	
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        View rootView = inflater.inflate(R.layout.activity_title, container, false);
	        lv=(ListView)rootView.findViewById(R.id.music_list);
	        //adapter=new SongAdapter(getActivity().getApplicationContext(),Artists.songList,2);
	        //final ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
  	       // SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(swingRightInAnimationAdapter);

  	        //scaleInAnimationAdapter.setAbsListView(lv);
  	      lv.setAdapter(adapter);
	        /*new Handler().postDelayed(new Runnable() {
                public void run() {
                    
                    //async thread code to execute loading the list... 
                	if(lv.getAdapter()==null){
           	    	 	Log.d("CollectionDemo","Adapter is null");
                		lv.setAdapter(scaleInAnimationAdapter);
                	}
           	    	   else{
           	    		//scaleInAnimationAdapter.updateData(myNewData); //update your adapter's data
           	    		
           	    		scaleInAnimationAdapter.notifyDataSetChanged();
           	    	}
                	
                	
                }
            },500);*/
  	      
  	      lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Body.songPicked(position);
				
			}
  	    	  
  	      });
	        return rootView;
	    }
	
}
