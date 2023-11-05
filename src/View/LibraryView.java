package View;

import Controller.Controller;
import Model.Song;
import View.HomeView.ListViewCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 * Library Scene of the App. 
 * 
 * The class contains the library scene of the app and displays a list view of songs from the server.
 * The view saves all the songs that the users asks to save from home pages and the search pages.
 * 
 * @author namanpandey
 *
 */
public class LibraryView {

	Controller controller;
	
	/**
	 * Intializes the class with the controller.
	 * 
	 * @param controller
	 */
	public LibraryView(Controller controller) {
		this.controller = controller;
	}
	
	/**
	 * Returns a Scene back to the PlayerView.java
	 * Contains a title, list view and nav bar.
	 * @param navBar
	 * @return Scene - library scene
	 */
	public Scene getScene(HBox navBar) {
		ListView scrollView = setListView(this.controller.getLibrary());
		Label titleBar 		= setPageTitle("Library");
		HBox navigationBar  = navBar;
		
		VBox box = new VBox();
		box.getChildren().addAll(titleBar, scrollView, navigationBar);
		VBox.setVgrow(scrollView, Priority.ALWAYS);
		box.setPrefWidth(ViewConstants.SCENE_WID);
		box.setStyle("-fx-focus-color: transparent;");
		
		return new Scene(box, ViewConstants.SCENE_WID, ViewConstants.SCENE_HEI);
	}

	/**
	 * Private method to set the title label with a provided string.
	 * @param title
	 * @return Label - title label
	 */
	private Label setPageTitle(String title) {
		Label titleBar = new Label(title);
		titleBar.setMinWidth(50);
		titleBar.setMinHeight(50);
		titleBar.setFont(new Font("AppleSystemUIFont", 32));
		titleBar.setPadding(new Insets(20));
		return titleBar;
	}
	
	/**
	 * Displays the list style view for the home page songs. 
	 * 
	 * Takes in an array of Song objects 
	 * @param items
	 * @return ListView list view object
	 */
	private ListView setListView(Song[] items) {		
		ObservableList<Song> songs = FXCollections.observableArrayList(items);
		
		ListView<Song> listView = new ListView(songs);
		
		listView.setFixedCellSize(75);
		listView.setPrefHeight(ViewConstants.SCENE_HEI-200);
		listView.setCellFactory(new Callback<ListView<Song>, ListCell<Song>>() {
	                @Override 
	                public ListCell<Song> call(ListView<Song> list) {
	                    return new ListViewCell();
	                }
	            }
	        );
		listView.getSelectionModel().selectedItemProperty()
		.addListener(
		    new ChangeListener<Song>() {
		        public void changed(ObservableValue<? extends Song> ov, Song old_val, Song new_val) {
		        	controller.addSongsMP3(new_val);
		        	
					//get_Mp3(new_val);
					System.out.println("TESt");
		        }
		 });
		return listView;
	}
	
	/**
	 * ListViewCell class which creates a cell for the List view object
	 * 
	 */
	 class ListViewCell extends ListCell<Song> {
		Image albumCover = null;
		@Override
        public void updateItem(Song item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) return;
            
            Label songName = new Label(item.name);
            songName.setFont(new Font("AppleSystemUIFont", 14));
            Label artistName = new Label(item.artist);
            artistName.setFont(new Font("AppleSystemUIFont", 12));
            
           
            ImageView imageView = null;
            if (albumCover == null) imageView = new ImageView(new Image(item.image));
            
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            
            VBox labelBox = new VBox(songName, artistName);
            labelBox.setPadding(new Insets(10));
            Button libraryButton = removeFromLibraryButton(item);

            
            HBox ret = new HBox(imageView, labelBox, libraryButton);
            ret.setPadding(new Insets(20));
            ret.setAlignment(Pos.CENTER_LEFT);
            if (item != null) {
                setGraphic(ret);
            }
        }
		
    }
	
	 /**
	  * remove from library button, creates a button to add song to the library
	  * The button calls the controller on event click to update the libraryz
	  * The button calls the controller on event click to update the library
	  * 
	  *  @param song
	  *  @return Button
	  */
	public  Button removeFromLibraryButton(Song song) {
		Button addLibraryButton = new Button("Remove From Library");
        addLibraryButton.setOnAction(e -> {
        	controller.removeFromLibrary(song);
        });
        return addLibraryButton;
	}
} 
