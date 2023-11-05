package Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.media.Media;

public class PlayLists {
	private List<SongList> playList;
	private int currentSongIndex;
	private String title;
	public PlayLists() {
		
		playList = new ArrayList<SongList>();
		currentSongIndex = 0;
	}
  
	
	public SongList getCurrentSong() {
		return playList.get(currentSongIndex);
	}
	
	
	public SongList prevPlayList() {
		System.out.println(currentSongIndex + " " + playList.size());
		if (currentSongIndex == 0) {
			currentSongIndex = playList.size()-1;
			return  playList.get(currentSongIndex);
		}
		return playList.get(--currentSongIndex);
	}
	
	
	public SongList nextPlayList() {
		System.out.println(currentSongIndex + " " + playList.size());
		if (currentSongIndex == playList.size()-1) {
			currentSongIndex = 0;
			return  playList.get(0);
		}
		return playList.get(++currentSongIndex);
	}
	
	public int getListLength() { 
		return playList.size(); 
	}
	
	public int getCurrentPlayListIndex() {
		return this.currentSongIndex;
	}
	
	public boolean addPlayList(SongList list) {
		//if (path == "") return false;
		//try {
			SongList SingleList =  new SongList();
			playList.add(list);
			return true;
	/*	} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error: Could not add a song with path \"" + path + "\"");
		}
		return false;*/
	}
	
	
	
}
