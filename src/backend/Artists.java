package backend;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;
import frontend.ChatHeadService;

public class Artists extends Fragment{
	public static boolean DONE;
	public static ArrayList<Song> songList=new ArrayList<Song>();
	private ListView lv;
	private static SongAdapter adapter;
	private Context ctx;
	
    @Override
    public void onCreate(
            Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	ctx=getActivity();
    	DONE=false;
    	songList.clear();
    	if(songList.size()==0){
    		Log.d("Artists","Starting the background task");
    		new MyAsyncTask().execute(" ");
    	}
        /*View rootView = inflater.inflate(R.layout.activity_title, container, false);
        lv=(ListView)rootView.findViewById(R.id.music_list);
		
        lv.setFadingEdgeLength(0);
        
        
        adapter=new SongAdapter(getActivity().getApplicationContext(),Artists.songList,1);*/
        
    	
    	
    	//ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
       // SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(swingRightInAnimationAdapter);

        //scaleInAnimationAdapter.setAbsListView(lv);
        //lv.setAdapter(scaleInAnimationAdapter);
		
        
    }
    
   
    private class MyAsyncTask extends AsyncTask<String, Void,Void>{
		
		
		@Override
		protected Void doInBackground(String... params) {

			//Log.d("TAG", "About to make the content resolver");
			ContentResolver contentResolver=ctx.getContentResolver();
			Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			//Log.d("TAG", "In the background task"+uri);
			final String[] projection = new String[]{
					MediaStore.Audio.Media._ID,
	                MediaStore.Audio.Media.ARTIST,
	                MediaStore.Audio.Media.ALBUM,
	                MediaStore.Audio.Media.TITLE, 
	                MediaStore.Audio.Media.IS_MUSIC,
	                MediaStore.Audio.Media.DURATION};
			Cursor cursor = contentResolver.query(uri,projection, null, null,android.provider.MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");
			/*for( int i = 0; i < cursor.getColumnCount(); i++) {
			    Log.d("Column: ",cursor.getColumnName(i));
			}*/
			//Log.d("TAG", "After ");
			
			
			if (!cursor.moveToFirst()) {
			    // no media on the device
			} 
			else {
				int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			    int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			    int artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			    int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			    int durColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
			    int isMusicColumn = cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);

			    do {
			    	
			    		long thisId = cursor.getLong(idColumn);
					       String thisTitle = cursor.getString(titleColumn);
					       String thisArtist= cursor.getString(artistColumn);
					       String thisAlbum= cursor.getString(albumColumn);
					       int thisDur= cursor.getInt(durColumn);
					       int isMusic=cursor.getInt(isMusicColumn);
					       
					       if(!songList.contains((new Song(thisId,thisTitle,thisArtist,thisAlbum,thisDur)))&& isMusic!=0){
					    	   songList.add(new Song(thisId,thisTitle,thisArtist,thisAlbum,thisDur));
					       }
					       //Log.d("TAG","Added "+thisTitle);
			       
			    } while (cursor.moveToNext());
			}
			cursor.close();
			return null;
		}
		@Override 
		protected void onPostExecute(Void result){
			
			Log.d("Artists","Done executing the background thread");
			Log.d("Artists","Songs COUNT="+songList.size());
			DONE=true;
		}
		
	}
    
    /*public void refresh(){
	Log.d("TAG", "About to make the content resolver");
	ContentResolver contentResolver=getActivity().getContentResolver();
	Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	Log.d("TAG", "In the background task"+uri);
	final String[] projection = new String[]{MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION};
	Cursor cursor = contentResolver.query(uri,projection, null, null,android.provider.MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC");
	for( int i = 0; i < cursor.getColumnCount(); i++) {
	    Log.d("Column: ",cursor.getColumnName(i));
	}
	Log.d("TAG", "After ");
	
	
	if (!cursor.moveToFirst()) {
	    // no media on the device
	} 
	else {
		int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
	    int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
	    int artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
	    int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
	    int durColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

	    do {
	       long thisId = cursor.getLong(idColumn);
	       String thisTitle = cursor.getString(titleColumn);
	       String thisArtist= cursor.getString(artistColumn);
	       String thisAlbum= cursor.getString(albumColumn);
	       int thisDur= cursor.getInt(durColumn);
	      
	      
          
              
	       songList.add(new Song(thisId,thisTitle,thisArtist,thisAlbum,thisDur));
	       
	       //Log.d("TAG","Added "+thisTitle);
	    } while (cursor.moveToNext());
	} 
	
}		*/

}
