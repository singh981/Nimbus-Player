package backend;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.example.samplemusic.R;

import frontend.ChatHeadService;
import frontend.Serve;
import frontend.Utils;

public class AlbumArtGet extends Fragment{
	
		private static ArrayList<String> album=new ArrayList<String>();
		private static ArrayList<Bitmap> bit=new ArrayList<Bitmap>();
		static Context ctx;
		int measure;
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			new MyAsyncTask().execute(" ");
			ctx=getActivity();
			measure=Utils.dpToPixels(140, getResources());
		}
		
		public static ArrayList<String> getAlbum() {
			return album;
		}

		public static void setAlbum(ArrayList<String> album) {
			AlbumArtGet.album = album;
		}

		public static ArrayList<Bitmap> getBit() {
			return bit;
		}

		public static void setBit(ArrayList<Bitmap> bit) {
			AlbumArtGet.bit = bit;
		}
		
		public static Context getCtx() {
			return ctx;
		}

		
		private class MyAsyncTask extends AsyncTask<String, Void,Void>{
			
			
			@Override
			protected Void doInBackground(String... params) {

				Log.d("ALBUMartTAG", "About to make the  content resolver");
				ContentResolver contentResolver=ctx.getContentResolver();
				Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
				//Log.d("ALBUMartTAG", "In the background task"+uri);
				
				final String[] projection = new String[]{MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ALBUM_ID};
				Cursor cursor = contentResolver.query(uri,projection, where, null,null);
				
				/*for( int i = 0; i < cursor.getColumnCount(); i++) {
				    Log.d("Column: ",cursor.getColumnName(i));
				}*/
				//Log.d("ALBUMartTAG", "After ");
				
				
				if (!cursor.moveToFirst()) {
				    // no media on the device
				} 
				else {
					
				    int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
				    Long prevId=(long) 0;
				    Bitmap bitmap=null;
				    bitmap = BitmapFactory.decodeResource(ctx.getResources(),
	                           R.drawable.ic_launcher);
				    do {
				       String thisAlbum= cursor.getString(albumColumn);
				       Long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
				       if(albumId==prevId) continue;
				       prevId=albumId;
				  //     Log.d("ALBUMID",String.valueOf(albumId));
				       // ...process entry...
				       //Uri sArtworkUri = Uri
		               //        .parse("content://media/external/audio/albumart");
				       
				    	   //Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
				    //	   Log.d("TAG",String.valueOf(albumId));
			          //     Log.d("TAG",albumArtUri.getPath());
			               bitmap=MusicUtils.getArtworkQuick(ctx, albumId,measure,measure);
			               if(bitmap==null) continue;
			               //bitmap=MusicUtils.getBitmapFromUri(albumArtUri);
					          
			               /*try {
			            	   ParcelFileDescriptor pfd = CollectionDemoActivity.getContext().getContentResolver()
				                       .openFileDescriptor(albumArtUri, "r");
			            	   
			                       FileDescriptor fd = pfd.getFileDescriptor();
			                       bitmap = BitmapFactory.decodeFileDescriptor(fd);
			                    
			                  

			               } catch (FileNotFoundException exception) {
			                   exception.printStackTrace();
			                   bitmap = BitmapFactory.decodeResource(CollectionDemoActivity.getContext().getResources(),
			                           R.drawable.ic_launcher);
			               } catch (IOException e) {

			                   e.printStackTrace();
			               }
				       
			               try {
			                    bitmap = MediaStore.Images.Media.getBitmap(
			                    		CollectionDemoActivity.getContext().getContentResolver(), albumArtUri);
			                    bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

			                } catch (FileNotFoundException exception) {
			                    exception.printStackTrace();
			                    bitmap = BitmapFactory.decodeResource(CollectionDemoActivity.getContext().getResources(),
			                            R.drawable.ic_launcher);
			                } catch (IOException e) {

			                    e.printStackTrace();
			                }

				       bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
*/
		               /*int x=0;
				       for(x =0;x<album.size();x++) {
				    	   if(album.get(x).getName()!=thisAlbum) continue;
		            	   
		               }
				       if(x==(album.size()-1)){
				    	   album.add(new AlbumArt(thisAlbum,bitmap));
					       Log.d("AlbumADD","Added "+thisAlbum);
				       }*/
				       if(!getAlbum().contains(thisAlbum)){
			    	   getAlbum().add(thisAlbum);
			    	   Log.d("AlbumADD","Added "+thisAlbum);
			    	   getBit().add(bitmap);
				       }
				    } while (cursor.moveToNext());
				} 
				
			
				cursor.close();
				return null;
			}
			@Override 
			protected void onPostExecute(Void result){
				Log.d("AlbumArtGet","Done loading the art");
				Log.d("AlbumArtGet","Art COUNT = "+getBit().size());
				//for(int x =0;x<album.size();x++){
					//Log.d("ALL ALBUM",album.get(x));
					Serve.setFlagForArt();
				//}
				
			}
			
		}
	
}
