package tests;

import org.junit.jupiter.api.Test;

import Controller.Controller;
import Model.Song;
import Model.SongList;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import Server.Server;

public class Tests {
	@Test
	public void testClient1()  {
		System.out.println("ran");
	Song s1=new Song();
		s1.name="Ali";
		SongList songlist=new SongList();
		songlist.addSong(s1);
		System.out.println("_");
		assertEquals(songlist.getMetadata().name,"Ali");
		
	//	assertNotEquals(null, server.basicAuth);
		
	}
	@Test
	public void testClient2()  {
		System.out.println("ran");
	Song s1=new Song();
		s1.name="Ali";
		s1.duration=11;
		SongList songlist=new SongList();
		songlist.addSong(s1);
		assertEquals(songlist.getMetadata().name,"Ali");
		
		Controller controller=new Controller("");
		controller.addPlayList(songlist);
		System.out.print("__"+controller.getSongPlaying());
		assertNotEquals(controller.getSongPlaying().getDuration(),11);
//		controller.addPlayList(null);
		
	//	assertNotEquals(null, server.basicAuth);
	}
}