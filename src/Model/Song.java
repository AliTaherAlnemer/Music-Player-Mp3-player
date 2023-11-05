package Model;
import com.google.gson.*;
import com.google.gson.*;

/**
 * Contains information about a song such as name, path to the mp3 and other metadata
 * @author namanpandey
 *
 */
public class Song implements java.io.Serializable {
	public String id;
	public String name;
	public String artist;
	public String preview_url;
	public int 	  duration;
	public String image;
	
	public boolean isLocal = false;
	public String  localPath = "";
	
	public String toString() {
		return "Song: " + name + ", Artist: " + artist + ", duration(ms): " + duration;
	}

	@Override
    public boolean equals(Object o) {
		if (o == null) return false;
		return id.equals(((Song) o).id);
	}
}
