package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import frontend.ChatHeadService;

public class MusicService extends Service implements
MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
MediaPlayer.OnCompletionListener {
		private static final String NAME="TITLE";
		private static final String DURATION="DURATION";
		private static final String REPEAT="REPEAT";
		private static final String SHUFFLE="SHUFFLE";
		private static final String PAGE="PAGE";
		private static final String POS="POS";
		
		
		//media player
		private MediaPlayer player;
		//song list
		private static ArrayList<Song> songs=new ArrayList<Song>();
		private ArrayList<Song> que;
		//current position
		private static int songPosn=0;
		private static int songDur=0;
		private static int page;
		private static int pos;
		private static boolean shuffle=false;
		private static int repeat=0;
		//0 means list will not repeat 
		//1 means list will repeat
		//2 means song will repeat
		
		private static boolean oncePlayed;
		public boolean isOncePlayed() {
			return oncePlayed;
		}

		private Random rand;
		static SharedPreferences set;
		static SharedPreferences.Editor editor;
		
		private final IBinder musicBind = new MusicBinder();
		
		
		public void onCreate(){
			  //create the service
			super.onCreate();
			
			//create player
			player = new MediaPlayer();
			rand=new Random();
			initMusicPlayer();
			//title=this.getSharedPreferences(NAME, 0);
			set=PreferenceManager.getDefaultSharedPreferences(this);
			editor=set.edit();
			//initialize position
			
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return musicBind;
		}
		
		@Override
		public boolean onUnbind(Intent intent){
			//oncePlayed=false;
		  player.stop();
		  player.reset();
		  player.release();
		  storeLastSong();
		  return false;
		}
		
		public void playSong(){
			if(oncePlayed){
				player.reset();
			}
			
			Song playSong=songs.get(songPosn);
			long currSong=playSong.getId();
			Log.d("MS",playSong.getTitle()+currSong);
			//set uri
			Uri trackUri = ContentUris.withAppendedId(
			  android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			  currSong);
			try{
				  player.setDataSource(ChatHeadService.getContext(), trackUri);
				}
				catch(Exception e){
				  Log.e("MUSIC SERVICE", "Error setting data source", e);
				}
			player.prepareAsync();
			player.setWakeMode(ChatHeadService.getContext(),
					PowerManager.PARTIAL_WAKE_LOCK);
			
		}
		
		public void initMusicPlayer(){
			oncePlayed=false;
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setOnPreparedListener(this);
			player.setOnCompletionListener(this);
			player.setOnErrorListener(this);
			//initialise();
		}
		
		public void initialise(){
			int x=set.getInt(POS,-1);
			if(x==-1) setList(Artists.songList);
			else {
				for(int i=0;i<x;i++){
					Long a=set.getLong(POS+i,0);
					for(int j=0;j<Artists.songList.size();j++){
						if(Artists.songList.get(j).getId().equals(a)){
							songs.add(Artists.songList.get(j));
							Log.d("MS","Last Song "+Artists.songList.get(j).getTitle()+" added");
						}
					}
				}
			}
			for(int i=0;i<songs.size();i++){
				if(songs.get(i).getTitle().equals(set.getString(NAME, NAME))) {
					songPosn=i;
					break;
				}
			}
			//songPosn=set.getInt(KEY, 0);
			songDur=set.getInt(DURATION, 0);
			repeat=set.getInt(REPEAT, 0);
			shuffle=set.getBoolean(SHUFFLE, false);
			Log.d("MS",set.getString(NAME, NAME));
			playSong();
		}
	
		private void storeLastSong(){
			if( songs.get(songPosn).getTitle()!=null){
				Log.d("MS",/*set.getString(KEY, KEY)*/String.valueOf(songPosn)+songs.get(songPosn).getTitle());
				editor.putString(NAME, songs.get(songPosn).getTitle());
				editor.putInt(DURATION, getSongDur());
				editor.putBoolean(SHUFFLE, shuffle);
				editor.putInt(REPEAT, repeat);
			}
			if(songs.size()!=Artists.songList.size()){
				editor.putInt(POS, songs.size());
				for(int i=0;i<songs.size();i++){
					editor.putLong(POS+i,songs.get(i).getId());
				}
			}
			else editor.putInt(POS, -1);
			//editor.putString(KEY, songs.get(songPosn).getTitle());
			editor.apply();
			
		}
		
		public static int getSongDur(){
			return songDur;
		}
		
		public static void setSongDur(int x){
			songDur=x;
		}
		
	
		public int getPosn(){
			songDur=player.getCurrentPosition();
		  return songDur;
		}
		 
		public int getDur(){
		  return player.getDuration();
		}
		 
		public boolean isPng(){
		  return player.isPlaying();
		}
		 
		public void pausePlayer(){
			player.pause();
		}
		 
		public void seek(int posn){
		  player.seekTo(posn);
		}
		 
		public void go(){
			if(oncePlayed){
				player.start();
			}
			else{
				Log.d("MS","Playing the song from last instance");
				playSong();
			}
		}
	
		public void playPrev(){
			  songPosn--;
			  if(songPosn==-1) songPosn=0;
			  playSong();
			}
		//skip to next
		public void playNext(){
			if(repeat==2){
				playSong();
				return;
			}
			if(shuffle){
				if(songs.size()==1) {
					playSong();
					return;
				}
			    int newSong = songPosn;
			    while(newSong==songPosn){
			      newSong=rand.nextInt(songs.size());
			    }
			    songPosn=newSong;
			}
			else if(repeat==1){
			    songPosn++;
			    if(songPosn==songs.size()) songPosn=0;
			}
			else if(repeat==0){
			    if(songPosn==songs.size()-1) return;
			    songPosn++;
			}
			/*songPosn++;
			if(songPosn==songs.size()) songPosn=0;*/
			  playSong();
			}
		
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if(oncePlayed) playNext();
	}
	
	public static void  setList(ArrayList<Song> theSongs){
		Log.d("MS SetList","The list has been changed");
		  songs=theSongs;
		  //que=songs;
		}
	
	public ArrayList<Song> getList(){
		return songs;
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if(what==-38){
			Log.d("MS ERROR","It's the fucking -38 error again");
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		//seek(songDur);
		//start playback
		
		if(!oncePlayed) {
			mp.seekTo(songDur);
		    oncePlayed=true;
		}
		else mp.start();
		Log.d("MS","The player is ready and seeked to "+ player.getCurrentPosition());
	}
	
	public void setSong(int songIndex){
		  songPosn=songIndex;
		}
	
	public static int getSongNum(){
		return songPosn;
	}
	
	public class CustomComparator implements Comparator<Song> {
	   

		@Override
		public int compare(Song lhs, Song rhs) {
			// TODO Auto-generated method stub
			
			return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
		}
	}
	
	public class MusicBinder extends Binder {
		  public MusicService getService() {
		    return MusicService.this;
		  }
		}
	
	public boolean setShuffle(){
		  if(shuffle) {
			  shuffle=false;
			  //Collections.sort(que,new CustomComparator());
			  Log.d("MS Shuffle matters","shuffle has been reset");
		  }
		  else {
			  //Collections.shuffle(que);
			  shuffle=true;
		  }
		  return shuffle;
		}
	public static boolean isShuffle() {
		return shuffle;
	}
	
	public static void setRepeat(int repeat) {
		MusicService.repeat = repeat;
	}
	public static int getRepeat() {
		return repeat;
	}

	
}
