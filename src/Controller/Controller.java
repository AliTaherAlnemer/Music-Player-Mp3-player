package Controller;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observer;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Model.Model;
import Model.PlayLists;
import Model.Song;
import Model.SongList;
import javafx.scene.media.Media;
/**
 * controller Communicate with model and server
 * 
 * This is the Controller it responsiple for communcating with the model for 
 * song way of orgniztion and it communcates with the server to get spotify api information
 * @author alitn
 *
 */

public class Controller extends Thread implements Runnable {
	 
	private Model model;
	
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Thread thread;
	private String Phase;
	private String save;
	private String request;
	private String lastUrl;
	private String lastSearch;
	volatile String  message;
	private Song[] songArray;
	
	public Controller(String s)  {
		
		model=new Model();
		lastSearch="";
		request="";
		lastUrl="";
	
	}
	/**
	 * Constructr  for contoller
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	
	public Controller() throws UnknownHostException, IOException {
		this.connection = new Socket("localhost", 4000);
		this.output = new ObjectOutputStream(this.connection.getOutputStream());
		this.input = new ObjectInputStream(this.connection.getInputStream());
		model=new Model();
		lastSearch="";
		request="";
		lastUrl="";
	
	}
	/** Add Observer 
	 * 
	 * This function adds the given observer to the set of observer 
	 * that take care of the all the observer need.
	 * 
	 * @param observer
	 */
	public void addObserver(Observer o) { 
		model.addObserver(o);
		
	}
	/**
	 * Get library  of song
	 * 
	 * This function returns the library of song (store songs info)from model
	 * 
	 * @return Song array of object of the library of song
	 */
	public Song[] getLibrary() {
		return this.model.getLibrary();
	}
	/**
	 * Get player of song 
	 * 
	 * This function returns object of SOngList the player of song (songs to be played)from model
	 * 
	 * @return Song array of object of the library of song
	 */
	
	public SongList getPlayer() {
		return this.model.getPlayer();
	}
	/**
	 * Add given song obj to library
	 * 
	 * Function will Add given song obj to library stotered in model
	 * 
	 * @param song to add to library
	 */
	public void addToLibrary(Song song, String msg) {
		for (Song libSong: this.model.getLibrary()) {
			if (libSong == null) continue;
			if (libSong.equals(song)) {
				return;
			}
		}
		this.model.addToLibrary(song, msg);
	}
	 
	public void removeFromLibrary(Song song) {
		this.model.removeFromLibrary(song);
	}
	
	/**
	 * Add given song obj to player
	 * 
	 * Function will Add given song obj to player stored in model
	 * 
	 * @param song to add to library
	 */
	public void addToPlayer(Song song) {
		this.model.addToPlayer(Phase, song);
	}
/**
 * Set current Search String
 * 
 * THis function takes parm and calls model set current sting
 * 
 * @param search search string
 */
	public void setCurrentSearchString(String s) {
		this.model.setCurrentString(s);
	}
	/**
	 * Get current Search String
	 * 
	 * THis function   calls model and return current search
	 * 
	 * @return current Search String
	 */
		
	public String getCurrentSearchString() {
		return this.model.getCurrentStirng();
	}
	
	/**
	 * Get songs[] from the given searched string
	 * 
	 * THis function will start thread to give the song to server 
	 * and return the returned  song []
	 * 
	 * @param song string  to search 
	 * @return Song array of song we get from server
	 */
	public Song[] getSearchPage(String song) {
		try {
			if(!song.equals("")) {
			request="getSong";
			lastSearch="getSong?="+song;
			System.out.println("_"+lastSearch);
			System.out.println("1_"+song+"_");
			
			thread = new Thread(this);
			thread.start(); 
			thread.join();
			System.out.println("____"+songArray[0].name);
			return songArray;
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return new Song[0];
	} 
	
	
	
	
	
	
	

	/**
	 * Get songs[] of random songs
	 * 
	 * THis function will start thread tonotify server we need random songs 
	 * with string  " " to server and wait
	 *  for the server return the returned  song []
	 *  
	 *  0 - default
	 *  1 - popular
	 *  2 - rock
	 *  3 - rap
	 * 
	 * @param song string  to search 
	 * @return Song array of song we get from server
	 */
	public Song[] getHomePage()  {
		try {
			if (model.getHomePageType() == 1) return getPouplar();
			if (model.getHomePageType() == 2) return getRock();
			if (model.getHomePageType() == 3) return getRap();
			Phase="write";
			lastSearch="getSong?="+" ";
			request="getSong";
			System.out.print("_"+lastSearch);
			
			thread = new Thread(this);
			thread.start();
			thread.join();
			System.out.println("____"+songArray[0].name);
	 
			return songArray;
		} catch (Exception e) {
			System.out.println(e);
		}
		return new Song[0];
	}
	/**
	 * Get songs[] of poupler songs
	 * 
	 * THis function will start thread tonotify server we need poupler songs 
	 * with string  " getGenre?=Pop" to server and wait
	 *  for the server return the returned  song []
	 * 
	 * @param song string  to search 
	 * @return Song array of song we get from server
	 */
	
	private Song[] getPouplar()  {
		try {
			Phase="write";
			lastSearch="getGenre?="+"Pop";
			request="getGenre";
			System.out.print("_"+lastSearch);
			
			thread = new Thread(this);
			thread.start();
			thread.join();
			System.out.println("____"+songArray[0].name);
	 
			return songArray;
		} catch (Exception e) {
			System.out.println(e);
		}
		return new Song[0];
	}
	/**
	 * Get songs[] of poupler rock songs
	 * 
	 * THis function will start thread tonotify server we need poupler rock songs 
	 * with string  " getGenre?=rock" to server and wait
	 *  for the server return the returned  song []
	 * 
	 * @param song string  to search 
	 * @return Song array of song we get from server
	 */
	private Song[] getRock()  {
		try {
			Phase="write";
			lastSearch="getGenre?="+"Rock";
			request="getGenre";
			
			
			thread = new Thread(this);
			thread.start();
			thread.join();
			
	 
			return songArray;
		} catch (Exception e) {
			System.out.println(e);
		}
		return new Song[0];
	}
	/**
	 * Get songs[] of poupler rap songs
	 * 
	 * THis function will start thread tonotify server we need poupler rap songs 
	 * with string  " getGenre?=rap" to server and wait
	 *  for the server return the returned  song []
	 * 
	 * @param song string  to search 
	 * @return Song array of song we get from server
	 */
	private Song[] getRap()  {
		try {
			Phase="write";
			lastSearch="getGenre?="+"Rap";
			request="getGenre";
			System.out.print("_"+lastSearch);
			
			thread = new Thread(this);
			thread.start();
			thread.join();
			System.out.println("____"+songArray[0].name);
	 
			return songArray;
		} catch (Exception e) {
			System.out.println(e);
		}
		return new Song[0];
	}
	/**
	 *Add the song with provied id to 
	 * 
	 * THis function will start thread to give the song id to server 
	 * and wait for url from server. After recived add it to the player 
	 * in the model
	 * 
	 * @param Song with id string  to get url for 
	 */
	public void get_Mp3(Song song) throws InterruptedException {
		Phase="write";
	//	lastSearch="getMP3?="+"4fouWK6XVHhzl78KzQ1UjL";
		lastSearch="getMP3?="+song.id;
		System.out.println("_"+lastSearch);
		request="getMP3";
		thread = new Thread(this);
		thread.start();
		thread.join();
		System.out.println("+++"+lastUrl);
		model.addToPlayer(lastUrl, song);
		model.setPlayerCurrent(song);

		   return;
	}  
	
	/**
	 *Add the song with provied id to 
	 * 
	 * THis function will start thread to give the song id to server 
	 * and wait for url from server. After recived add it to the player 
	 * in the model
	 * 
	 * @param Song with id string  to get url for 
	 */
	
	public void addSongsMP3(Song s) {
		model.playerClear();
		
		for(Song libSong:this.model.getLibrary()) {
			if(libSong!=null) {
				try {
					System.out.println(libSong.name);
					get_Mp3(libSong) ;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		model.setPlayerCurrent(s);
		
	
//		this.model.addToPlayer(Phase, song);
	}
	
	

	
	/**
	 * Set song to play
	 * this function changes model to set playing song
	 * @param Song to get to play
	 */
	public void setSongPlaying(Song s) {
		model.setSongPlaying(s);
		
	}
	/**
	 * 
	 * @return media of the song to play
	 */
	public Media getSongPlaying() {
		return model.getSongPlaying();
	}
	
	public void addPlayList(SongList list) {
		
		model.addPlayList(list);
		
	}
	
	public PlayLists getPlayLists() {
		return model.getPlayLists();
		
	}
	
	public void changePageType(int i ) {
		model.setHomePageType(i);
	}
	
	@Override
	public void run() {
		try {

				int number = 0;
				//message = 	lastSearch;
				String message1=(lastSearch);
				System.out.print(message1);
				output.writeObject(message1);
				
				if(!message1.equals("quit")) {
					if(request.equals("getSong")||request.equals("getGenre")) {
					//	Object x=input.readObject();
					//	System.out.println(x);
						songArray=(Song[])input.readObject();
						
						ArrayList<Song>list = new ArrayList<Song>();
						for(int i = 0; i < songArray.length; i++) {
							if(songArray[i]!=null) {
								list.add(songArray[i]);
							}
						}
						songArray = list.toArray(new Song[0]);
						
					}
					else {
						System.out.println("Mp3 send");
						lastUrl=(String)input.readObject();
					}
				}
				save=message1;
				System.out.println("The Message is:"+message1);
				System.out.println("2)The Message is:"+message1);
				message1 = "quit";
	
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	


	}

}
