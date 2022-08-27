package my.jdbc.demo.common;

public class Singer {

	private int id;
	private String name;
	private String bestSong;
	
	
	public Singer() {
	}


	public Singer(String name, String bestSong) {
		this.name = name;
		this.bestSong = bestSong;
	}

	public Singer(int id, String name, String bestSong) {
		super();
		this.id = id;
		this.name = name;
		this.bestSong = bestSong;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getBestSong() {
		return bestSong;
	}


	public void setBestSong(String bestSong) {
		this.bestSong = bestSong;
	}


	@Override
	public String toString() {
		return "Singer [id=" + id + ", name=" + name + ", bestSong=" + bestSong + "]";
	}
	
	
}
