package View;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.*;

import Controller.Controller;
import Model.Model;
import Model.Song;
import Model.SongList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.scene.media.Media;  
import javafx.scene.media.MediaPlayer;  
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * 
 * main view class that starts the Javafx application
 * Class class multiple views to make scenes.
 * 
 * @author namanpandey
 * 
 *
 */
public class PlayerView extends Application implements Observer {
	Controller 	     controller;
	ApplicationViews views;  // Stores the stage and all the scenes.
	
	HomeView 		 homeView;
	SearchView 		 searchView;
	LibraryView 	 libraryView;
	MediaPlayerView  playerView;
	
	Stage mainStage;
	
	
	public static void Init() {
		Application.launch(PlayerView.class);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		mainStage = stage;
		controller = new Controller();
		controller.addObserver(this);
		views 	   = new ApplicationViews(stage);
		
		refreshViews(false);
		mainStage.setScene(views.homeScene);
		mainStage.show();
	}
	
	private void refreshViews(Boolean notPlayer) {
		homeView    = new HomeView(this.controller);
		searchView  = new SearchView(this.controller);
		libraryView = new LibraryView(this.controller);
		if (notPlayer == false) {
			if (playerView != null) playerView.stopSong();
			playerView  = new MediaPlayerView(this.controller);
		}
		
		views.homeScene    = homeView.getScene(getNavBar());
		views.searchScene  = searchView.getScene(getNavBar());
		views.libraryScene = libraryView.getScene(getNavBar());
		views.playerScene  = playerView.getScene(getNavBar());
	}
	

	
	// HELPER FUNCTIONS
	
	private HBox getNavBar() {
		Button homeBtn = getButton("./Assets/homeBarIcon.png", 0);
		Button librBtn = getButton("./Assets/libraryBarIcon.png", 1);
		Button searBtn = getButton("./Assets/searchBarIcon.png", 2);
		Button playBtn = getButton("./Assets/playBarIcon.png", 3);
		
        HBox.setHgrow(homeBtn, Priority.ALWAYS);
        HBox.setHgrow(librBtn, Priority.ALWAYS);
        HBox.setHgrow(playBtn, Priority.ALWAYS);
		
		HBox navigationBar = new HBox(homeBtn, librBtn, searBtn, playBtn);
		
        HBox.setHgrow(homeBtn, Priority.ALWAYS);
        HBox.setHgrow(librBtn, Priority.ALWAYS);
        HBox.setHgrow(searBtn, Priority.ALWAYS);
        HBox.setHgrow(playBtn, Priority.ALWAYS);
        
		navigationBar.setPrefWidth(ViewConstants.SCENE_WID);
		navigationBar.setPrefHeight(ViewConstants.NAV_BAR_HEI);
		
		return navigationBar;
	}
	
	private Button getButton(String url, int i) {
		Button button = new Button();
		ImageView view = new ImageView(new Image(url));
		button.setGraphic(view);
		
		if (i == 0) button.setOnAction(e -> this.views.stage.setScene(views.homeScene));
		if (i == 1) button.setOnAction(e -> this.views.stage.setScene(views.libraryScene));
		if (i == 2) button.setOnAction(e -> this.views.stage.setScene(views.searchScene));
		if (i == 3) button.setOnAction(
			e -> this.views.stage.setScene(views.playerScene)
		);
		
		button.setMaxWidth(Double.MAX_VALUE);
		button.setMaxHeight(ViewConstants.NAV_BAR_HEI);
		button.setStyle("-fx-focus-color: transparent;");
		return button;
	}
 
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		System.out.println("updated " + arg);
		
		
		if ((String) arg == "library") {
			this.refreshViews(true); 
			mainStage.setScene(views.libraryScene);
		}
		else if((String) arg == "home") {
			this.refreshViews(true); 
			mainStage.setScene(views.homeScene);
		}
		else if ((String) arg == "player") {
			this.refreshViews(false); 
			mainStage.setScene(views.playerScene);
		} 
		else if ((String) arg == "seach_string_updated") {
			this.refreshViews(true); 
			System.out.println("Calling search Update");
			mainStage.setScene(views.searchScene);
		} 
		mainStage.show();
		
	}
	
	
	
}


































