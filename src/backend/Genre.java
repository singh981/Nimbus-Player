package backend;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.samplemusic.R;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class Genre extends Fragment{
	public static final String ARG_NUMBER = "object";
	private ListView lv;
	private static SongAdapter adapter;
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_title, container, false);
        lv=(ListView)rootView.findViewById(R.id.music_list);
		//adapter=new SongAdapter(getActivity().getApplicationContext(),Artists.songList,3);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
		swingBottomInAnimationAdapter.setAbsListView(lv);
		lv.setAdapter(swingBottomInAnimationAdapter);
		
		
        return rootView;
    }
    
}
