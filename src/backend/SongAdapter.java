package backend;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samplemusic.R;

import frontend.ChatHeadService;
import frontend.Serve;

public class SongAdapter extends BaseAdapter{
	private PlaylistOp op=new PlaylistOp();
	private ArrayList<Song> song;
	private static ArrayList<Integer> selected=new ArrayList<Integer>();
	public static ArrayList<Integer> getSelected() {
		return selected;
	}

	private ArrayList<String> art=AlbumArtGet.getAlbum();
	private ArrayList<Bitmap> bit=AlbumArtGet.getBit();
	
	private static ArrayList<Song> albumSongs=new ArrayList<Song>();
	private static ArrayList<Song> artistSongs=new ArrayList<Song>();
	private static ArrayList<Song> playlistSongs=new ArrayList<Song>();
	
	public static ArrayList<Song> getAlbumSongs() {
		return albumSongs;
	}
	public static ArrayList<Song> getArtistSongs() {
		return artistSongs;
	}
	
	private LayoutInflater songInf;
	private static boolean longClick=false;
	public static boolean isLongClick() {
		return longClick;
	}
	public static void setLongClick(boolean longC) {
		longClick = longC;
	}

	private int tab;
	private int sec;
	private int min;
	private int pos;
	
	//constructor
	public SongAdapter(Context c, ArrayList<Song> theSongs,int tabSet,int position){
		song=theSongs;
		songInf=LayoutInflater.from(c);
		tab=tabSet;
		pos=position;
		//put();
		if(tab==0){
			op.readPlayList(op.getPlayList().get(pos).getId());
			playlistSongs=op.getSongList();
		}
		else if(tab==2){
			putInAlbum();
		}
		else if(tab==1){
			putInArtist();
		}
	}
	/*private void put(){
		for(int i=0;i<song.size();i++){
    		
        	
        	if(!isIn(song.get(i).getAlbum(),album)) {album.add(song.get(i).getAlbum());
        	//Log.d("ISIN", song.get(i).getAlbum());
        	}
    		
    		if(!isIn(song.get(i).getArtist(),artist)) artist.add(song.get(i).getArtist());
    		
    	}
	}*/
	private void putInAlbum(){
		albumSongs.clear();
		String name=GridAdapter.getAlbum().get(pos);
		int i=0;
		for(i=0;i<song.size();++i){
        	if(song.get(i).getAlbum().equals(name)){
        		albumSongs.add(song.get(i));
            	Log.d("ISIN", song.get(i).getTitle());
        	}
    	}
	}
	private void putInArtist(){
		artistSongs.clear();
		for(int i=0;i<song.size();i++){
			if(song.get(i).getArtist().equals(GridAdapter.getArtist().get(pos)))
        	{artistSongs.add(song.get(i));
        	//Log.d("ISIN", song.get(i).getAlbum());
        	}
    	}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(tab==0) return playlistSongs.size();
		
		else if(tab==1) {
			//Log.d("Artists COUNT",String.valueOf(artistSongs.size()));
			return artistSongs.size();
		}
		
		else if(tab==2) {
			//Log.d("Albums COUNT",String.valueOf(albumSongs.size()));
			return albumSongs.size();	
		}
		
		else if(tab==3){
			//Log.d("Songs COUNT",String.valueOf(song.size()));
			return song.size();
		}
		else return 1;
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        if(convertView==null)
            vi = songInf.inflate(R.layout.list_row, null);
        Bitmap bm=null;
        int x=0;
        vi.setBackgroundColor(selected.contains(position) ? 
        		Color.argb(80,255, 255, 255) : Color.TRANSPARENT);
        vi.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				longClick=true;
				Serve.makeOptions();
				v.setBackgroundColor(Color.argb(80,255, 255, 255));
				selected.clear();
				selected.add(position);
				Log.d("SA","Added position "+position);
				return true;
			}
        	
        });
        vi.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(longClick) {
					if(selected.contains(position)) {
						v.setBackgroundColor(Color.TRANSPARENT);
						selected.remove(selected.indexOf(position));
					}
					else {
						v.setBackgroundColor(Color.argb(80,255, 255, 255));
						selected.add(position);
					}
					return;
				}
				switch(tab){
				case 0:
					
					
					MusicService.setList(playlistSongs);
					break;
				case 1:{
					
					MusicService.setList(artistSongs);
					break;
				}
				case 2:{
					
					MusicService.setList(albumSongs);
					break;
				}case 3:{
					
					MusicService.setList(song);
					break;
				}
				}
				Serve.songPicked(position);
				ChatHeadService.updateHead();
				Serve.updateHead();
			}
        	
        });
        
        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView sTitle = (TextView)vi.findViewById(R.id.artist); // album name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
         
        //Log.d("NUM",String.valueOf(artist.size()));
        
        Animation animation = null;
        animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.push_left_in);
        //animation.setDuration(350); 
        
        switch(tab){
        case 0:
        	if(art.contains(playlistSongs.get(position).getAlbum())){ 
        		x=art.indexOf(playlistSongs.get(position).getAlbum());
        		bm=bit.get(x); 
        		//Log.d("Bitmap","art.get(x).getName()");
        	}
        	else{
            	Drawable draw=songInf.getContext().getResources().getDrawable(R.drawable.no_art);
            	bm=MusicUtils.drawableToBitmap(draw);
            }
    	title.setText(playlistSongs.get(position).getTitle());
    	thumb_image.setImageBitmap(bm);
    	thumb_image.setAnimation(animation);
    	
    	animation=null;
    	break;
        case 1:
        	//animation = AnimationUtils.loadAnimation(songInf.getContext(), R.anim.wave_scale);
        	if(art.contains(artistSongs.get(position).getAlbum())){ 
        		x=art.indexOf(artistSongs.get(position).getAlbum());
        		bm=bit.get(x); 
        		//Log.d("Bitmap","art.get(x).getName()");
        	}
        	else{
            	Drawable draw=songInf.getContext().getResources().getDrawable(R.drawable.no_art);
            	bm=MusicUtils.drawableToBitmap(draw);
            }
        	title.setText(artistSongs.get(position).getTitle());
        	thumb_image.setImageBitmap(bm);
        	//Log.d("SETARt", artist.get(position));
        	//title.setText(x.getArtist());
            sTitle.setText(" ");
            thumb_image.setAnimation(animation);
        	animation=null;
        	
        	break;
        
        
        case 2:
	        	if(art.contains(GridAdapter.getAlbum().get(pos))){ 
            		x=art.indexOf(GridAdapter.getAlbum().get(pos));
            		bm=bit.get(x); 
            		//Log.d("Bitmap","art.get(x).getName()");
            	}
            	else{
                	Drawable draw=songInf.getContext().getResources().getDrawable(R.drawable.no_art);
                	bm=MusicUtils.drawableToBitmap(draw);
                	bm=MusicUtils.getCroppedBitmap(bm);
                }
	        	title.setText(albumSongs.get(position).getTitle());
	        	thumb_image.setImageBitmap(bm);
	        	thumb_image.setAnimation(animation);
	        	animation=null;
            	
	        	//Log.d("SET ALBUM", album.get(position));
	        	/*for(x=0;x<song.size();x++){
	        		if(song.get(x).getAlbum()==album.get(position)) break;
	        	}
	        	sTitle.setText(song.get(x).getAlbum());*/
	        	//title.setText(x.getAlbum());
	            //sTitle.setText(x.getArtist());
	        	break;
        	
        case 3 :
        	
        	
        	Song so = song.get(position);
        if(art.contains(so.getAlbum())) {
        	x=art.indexOf(so.getAlbum());
    		bm=bit.get(x);
        }
        else{
        	Drawable draw=songInf.getContext().getResources().getDrawable(R.drawable.no_art);
        	bm=MusicUtils.drawableToBitmap(draw);
        	bm=MusicUtils.getCroppedBitmap(bm);
        }
        thumb_image.setImageBitmap(bm);
        title.setText(so.getTitle());
        //Log.d("SETTESXT", so.getTitle());
        sTitle.setText(so.getArtist());
        min=so.getDuration()/1000;
        sec=min%60;
        min=min/60;
        if(sec/10==0) duration.setText(String.valueOf(min)+":0"+String.valueOf(sec));
        else duration.setText(String.valueOf(min)+":"+String.valueOf(sec));
        if(position>=5){
    		thumb_image.setAnimation(animation);
    	}animation=null;
        break;
  
        case 4:
        	title.setText("Not made yet");
        	break;
        }	
        return vi;

	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean isEnabled(int position)
	{
	return true;
	}

}
