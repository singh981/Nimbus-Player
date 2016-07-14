package backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.samplemusic.R;

import frontend.ChatHeadService;

public class LastSongRun {
	private static final String KEY="TITLE";
	private static int pos;
	private static Bitmap albumArt;
	private static Song song;
	static SharedPreferences set;
	static SharedPreferences.Editor editor;
	public static Bitmap getAlbumArt() {
		if(albumArt==null){
			pos=check();
			setSong(pos);
		}
		if(albumArt==null){
			albumArt=BitmapFactory.decodeResource(ChatHeadService.getContext().getResources(),
                    R.drawable.itunes_gold);
		}
		return albumArt;
	}
	//check() is used to update position of the last song in case the user added more songs
	//to the device.
	public static int check(){
		//Log.d("LastSongRun",set.getString(KEY, Artists.songList.get(0).getTitle()));
		//Log.d("LastSongRun",Artists.songList.get(set.getInt(KEY, 0)).getTitle());
		if(set.getString(KEY, Artists.songList.get(0).getTitle())!=
				Artists.songList.get(set.getInt(KEY, 0)).getTitle()){
			for(int i=0;i<Artists.songList.size();i++){
				if(set.getString(KEY, Artists.songList.get(0).getTitle())==
						Artists.songList.get(i).getTitle()){
					pos=i;
				}
			}
		}
		else pos=0;
		return pos;
		
	}
	
	public static int getPos(){
		return pos;
	}
	
	private static void setAlbumArt(Bitmap art) {
		albumArt = art;
		ChatHeadService.updateHead();
	}

	public String  getTitle() {
		return set.getString(KEY, Artists.songList.get(0).getTitle());
	}
	public static void instantiate(Context ct){
		set=ct.getSharedPreferences(KEY,0);
		editor=set.edit();
	}
	public static void setPreference(){
		editor.putInt(KEY, pos);
		editor.putString(KEY, song.getTitle());
	}

	public static void setSong(int position) {
		pos=position;
		song = Artists.songList.get(pos);
		//Log.d("LastSongRun",song.getAlbum());
		//Log.d("LastSongRun",Artists.songList.get(0).getTitle());
		if(AlbumArtGet.getAlbum().contains(song.getAlbum())){
			//setAlbumArt(AlbumArtGet.getBit().get(AlbumArtGet.getAlbum().indexOf(song.getAlbum())));
		}
	}
}
