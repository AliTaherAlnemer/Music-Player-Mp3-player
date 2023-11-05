
package Model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.media.Media;  


/**
 * Represents a list of songsMedia and contains methods to travel through the songsMedia. 
 * This class can be used to represent songsMedia in library or a playlist.
 * 
 * @author namanpandey
 *
 */
public class SongList {
	
	private ArrayList<Media> songsMedia;
	private ArrayList<Song> songsMetadata;
	private int currentSongIndex;
	
	
	public SongList() {
		songsMedia    = new ArrayList<Media>();
		songsMetadata = new ArrayList<Song>();
		currentSongIndex = 0;
	}

	public void setCurrentSong(Song x ){
		Media media =  new Media(new File("Media/" + x.id + ".mp3").toURI().toString());
		this.songsMetadata.set(currentSongIndex, x);
		this.songsMedia.set(currentSongIndex, media);
	}
	
	public Song getMetadata() {
		return this.songsMetadata.get(currentSongIndex);
	}
	
	public Media getCurrentSong() {
		return songsMedia.get(currentSongIndex);
	}
	
	
	public Media prevSong() {
		System.out.println(currentSongIndex + " " + songsMedia.size());
		if (currentSongIndex == 0) {
			currentSongIndex = songsMedia.size()-1;
			return  songsMedia.get(currentSongIndex);
		}
		return songsMedia.get(--currentSongIndex);
	}
	
	
	public Media nextSong() {
		
		if (currentSongIndex == songsMedia.size()-1) {
			currentSongIndex = 0;
			return  songsMedia.get(0);
		}
		return songsMedia.get(++currentSongIndex);
	}
	
	public int getListLength() { 
		return this.songsMedia.size(); 
	}
	
	public int getCurrentSongIndex() {
		return this.currentSongIndex;
	}
	public void setCurrent(Song s) {
		Song currentSong=songsMetadata.get(currentSongIndex);
		while(!currentSong.id.equals(s.id)) {
			nextSong();
			currentSong=songsMetadata.get(currentSongIndex);
		}
	
		
	}
	
	public boolean addSong(Song song) {
		this.songsMetadata.add(song);
		return true;
	}
	public void clear() {
		songsMedia.clear();
		songsMetadata.clear();
		currentSongIndex=0;
		
	}
	public String toString() {
		return songsMetadata.toString();
	}
	public boolean addSong(String path) {
		if (path == "") return false;
		try {
			Media media =  new Media(new File(path).toURI().toString());
			songsMedia.add(media);
			
	//	songsMetadata.add(null);
			return true;
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error: Could not add a song with path \"" + path + "\"");
		}
		return false;
	}
	public boolean addSong(String path,Song song) {
		if (path == "") return false;
		try {
		//	System.out.println("_+_+_+"+song.name);
			Media media =  new Media(new File(path).toURI().toString());
			songsMedia.add(media);
			songsMetadata.add(song);
			
			return true;
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error: Could not add a song with path \"" + path + "\"");
		}
		return false;
	}
}


