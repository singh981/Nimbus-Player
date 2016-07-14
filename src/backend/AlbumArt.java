package backend;

import android.graphics.Bitmap;

public class AlbumArt {
		private String name;
		private Bitmap art;
		
		
		public AlbumArt(String album,Bitmap bitmap){
			setName(album);
			art=bitmap;
		}
		
		
		public Bitmap getArt() {
			return art;
		}
		public void setArt(Bitmap art) {
			this.art = art;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}
}
