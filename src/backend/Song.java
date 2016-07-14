package backend;



public class Song  {
	private Long id;
	private String title;
	private String artist;
	private String album;
	private int duration;

	public Song(Long thisId,String songTitle, String songArtist,String songAlbum,int songDur){
		setId(thisId);
		title=songTitle;
		artist=songArtist;
		album=songAlbum;
		duration=songDur;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
}
