package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javafx.scene.media.Media;

public class Model extends Observable {
	private SongList  player;
	private PlayLists playLists;
	private ArrayList<Song> library;
	
	private Set<Observer>observers;
	
	private int homePageType;
	
	String currentSearchString;

	public Model() {
		observers=new HashSet<>();
		library = new ArrayList<Song>();
		player  = new SongList();
		currentSearchString = "";
		homePageType = 0;
	}
	/** Add Observer 
	 * 
	 * This function adds the given observer to the set of observer 
	 * that take care of the all the observer need.
	 * 
	 * @param observer
	 */
	public void addObserver(Observer o) { 
		observers.add(o);
		
	}
	public void addToLibrary(Song song, String msg) {
		this.library.add(song);
		for( Observer o:this.observers) {
			o.update(this, msg);
		}
 	}
	
	public void removeFromLibrary(Song song) {
		this.library.remove(song);
		for( Observer o:this.observers) {
			o.update(this, "library");
		}
 	}


	public void addToPlayer(String Url,Song song) {
		this.player.addSong(Url,song);
		for( Observer o:this.observers) {
			o.update(this, "player");
		}
	}
	public SongList getPlayer() {
		return this.player;
	}
	public void playerClear() {
		this.player.clear();
	}
	public void setPlayerCurrent(Song s) {
		this.player.setCurrent(s);
		for( Observer o:this.observers) {
			o.update(this, "player");
		}
		
	}
	public Media getSongPlaying() {
		return player.getCurrentSong();
	}
	
	public Song[] getLibrary() {
		Song[] ret = new Song[library.size()];
		for (int i=0; i< ret.length; i++) {
			ret[i] = (Song) library.get(i);
		}
		return ret;
	}
	
	public void setSongPlaying(Song s) {
		player.setCurrentSong(s);
	}
	
	public void addPlayList(SongList list) {
		playLists.addPlayList(list);
	}
	public PlayLists getPlayLists() {
		return playLists;
	}
	
	public void setCurrentString(String s) {
		s = s.replaceAll(" ", "+");
		System.out.println("Model " + s);
		currentSearchString = s;
		for( Observer o:this.observers) {
			o.update(this, "seach_string_updated");
		}
	}
	
	public String getCurrentStirng() {
		return currentSearchString;
	}
	
	public int getHomePageType() {
		return homePageType;
	}
	
	public void setHomePageType(int i) {
		homePageType = i;
		for( Observer o:this.observers) {
			o.update(this, "home");
		}
	}
}
	



















