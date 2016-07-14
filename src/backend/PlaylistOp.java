package backend;

import java.util.ArrayList;


import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class PlaylistOp extends Fragment {
	private ArrayList<Song> songList=new ArrayList<Song>();
	public ArrayList<Song> getSongList() {
		return songList;
	}

	private static ArrayList<PlayList> playList=new ArrayList<PlayList>();
	public static ArrayList<PlayList> getPlayList() {
		return playList;
	}

	private static Context ctx;
	
	@Override
    public void onCreate(
            Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	ctx=getActivity();
    	playList.clear();
    	if(playList.size()==0){
    		Log.d("Artists","Starting the background task");
    		new MyAsyncTask().execute(" ");
    	}
    }
    
	private class MyAsyncTask extends AsyncTask<String, Void,Void>{
		
		
		@Override
		protected Void doInBackground(String... params) {
			ContentResolver resolver=ctx.getContentResolver();
			String[] proj = 
				{   
				     MediaStore.Audio.Playlists.NAME,
				     MediaStore.Audio.Playlists._ID
				};
				
			Uri uri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			
			Cursor cursor = resolver.query(
			                uri,proj, null, null, null);
			
			if (!cursor.moveToFirst()) {
			    // no media on the device
			} 
			else {
				int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Playlists._ID);
			    int nameColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Playlists.NAME);
			    

			    do {
			       long thisId = cursor.getLong(idColumn);
			       String thisName = cursor.getString(nameColumn);
			       
			      
			       if(!playList.contains((new PlayList(thisId,thisName)))){
			    	   playList.add(new PlayList(thisId,thisName));
			    	   Log.d("PlayList","Added "+thisName);
			       }
			       
			       //Log.d("TAG","Added "+thisTitle);
			    } while (cursor.moveToNext());
			}
			cursor.close();
			
			return null;
			
		}
		
		@Override 
		protected void onPostExecute(Void result){
			createPlaylist(ctx.getContentResolver(),"right one");
			//Log.d("PlayList","Done executing the background thread");
			//Log.d("PlayList","Songs COUNT="+songList.size());
		}
		
	}
	
	public  void readPlayList(long playId){
		songList.clear();
		ContentResolver resolver=ctx.getContentResolver();
		//String whereVal[] = {String.valueOf(playId)};

		String[] proj = 
		{   
		     MediaStore.Audio.Playlists.Members.AUDIO_ID,
		     MediaStore.Audio.Playlists.Members.ARTIST,
		     MediaStore.Audio.Playlists.Members.TITLE,
		     MediaStore.Audio.Playlists.Members.ALBUM,
		     MediaStore.Audio.Playlists.Members.DURATION
		};

		Cursor cursor = resolver.query(
		                MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(String.valueOf(playId))), 
		                proj, null, null, null);
		if (!cursor.moveToFirst()) {
		    // no media on the device
		} 
		else {
			int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Playlists.Members.AUDIO_ID);
		    int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Playlists.Members.TITLE);
		    int artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Playlists.Members.ARTIST);
		    int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Playlists.Members.ALBUM);
		    int durColumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION);

		    do {
		       long thisId = cursor.getLong(idColumn);
		       String thisTitle = cursor.getString(titleColumn);
		       String thisArtist= cursor.getString(artistColumn);
		       String thisAlbum= cursor.getString(albumColumn);
		       int thisDur= cursor.getInt(durColumn);
		      
		       if(!songList.contains((new Song(thisId,thisTitle,thisArtist,thisAlbum,thisDur)))){
		    	   songList.add(new Song(thisId,thisTitle,thisArtist,thisAlbum,thisDur));
		    	   Log.d("Playlist","Read "+thisTitle);
		       }
		       
		       //Log.d("TAG","Added "+thisTitle);
		    } while (cursor.moveToNext());
		}
		cursor.close();
	}
	
	public void addToPlaylist(long audioId, long playId) {
		readPlayList(playId);
		for(int i=0;i<songList.size();i++){
			if(songList.get(i).getId()==audioId) return;
			else continue;
		}
		ContentResolver resolver=ctx.getContentResolver();
        String[] cols = new String[] {
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playId);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Long.valueOf(base + audioId));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
        Log.d("Playlist ","Added a song");
    }

   public static void removeFromPlaylist(ContentResolver resolver, int audioId, int playId) {
       Log.v("made it to add",""+audioId);
        String[] cols = new String[] {
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playId);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();

        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID +" = "+audioId, null);
    }
	
	public static void createPlaylist(ContentResolver resolver, String pName) { 
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI; 
		ContentValues values = new ContentValues(); 
		values.put(MediaStore.Audio.Playlists.NAME, pName); 
		Uri newPlaylistUri = resolver.insert(uri, values); 
		Log.d("Playlist - Created", "newPlaylistUri:" + newPlaylistUri);
		//long playlistId = getNewPlayListId(newPlaylistUri); addToPlaylist(playlistId);
	} 
}
