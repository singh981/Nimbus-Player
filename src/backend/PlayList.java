package backend;

public class PlayList {
	private Long id;
	private String name;
	
	public PlayList(Long thisId,String thisName){
		setId(thisId);
		setName(thisName);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
