package View;

import Controller.Controller;
import Model.Song;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 * Search Scene of the App. 
 * 
 * The class contains the Search scene of the app and displays a list view of songs from the server.
 * The view saves all the songs that the users asks to save from home pages and the search pages.
 * 
 * @author namanpandey
 *
 */
public class SearchView {
	
	static Controller controller;
	
	ListView scrollView;
	
	/**
	 * Intializes the class with the controller.
	 * 
	 * @param controller
	 */
	public SearchView(Controller controller) {
		this.controller = controller;
	}
	
	/**
	 * Returns a Scene back to the PlayerView.java
	 * Contains a title, list view and nav bar.
	 * @param navBar
	 * @return Scene - search scene
	 */
	public Scene getScene(HBox navBar) {
		Label titleBar = setPageTitle("Search");
		
		 scrollView = setListView(this.controller.getSearchPage(controller.getCurrentSearchString()));
		
		HBox navigationBar 	= navBar;
		HBox searchBar   	= searchBar();
		
		VBox box = new VBox();
		box.getChildren().addAll(titleBar, searchBar, scrollView, navigationBar);
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
		listView.refresh();
		
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
		        	System.out.println(new_val.toString());
		        	try {
						controller.get_Mp3(new_val);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		 });
		listView.refresh();
		return listView;
	}
	
	/**
	 * ListViewCell class which creates a cell for the List view object
	 * 
	 */
	static class ListViewCell extends ListCell<Song> {
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
            
            Button libraryButton = null;
            for (Song libSong: controller.getLibrary()) {
    			if (libSong == null) continue;
    			if (libSong.id.equals(item.id)) {
    				libraryButton = removeFromLibraryButton(item);
    				break;
    			}
    		}
            if (libraryButton == null) libraryButton = addToLibraryButton(item);
            
            HBox ret = new HBox(imageView, labelBox, libraryButton);
            
            ret.setPadding(new Insets(20));
            ret.setAlignment(Pos.CENTER_LEFT);
            if (item != null) {
                setGraphic(ret);
            }
        }
    }
	
	/**
	 * Creates a search bar for the scene for the user to search a song
	 * 
	 * The search bar on enter gives the controller a string of the search term to search
	 * 
	 * The view is upadted and the list view shows the songs.
	 * 
	 * @return
	 */
	private HBox searchBar() {
		TextField searchBar = new TextField (controller.getCurrentSearchString());
		searchBar.setPadding(new Insets(12));
		searchBar.setFont(new Font("AppleSystemUIFont", 24));
		
		searchBar.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent event) {
		        if(event.getCode().equals(KeyCode.ENTER)) {
		             controller.setCurrentSearchString(searchBar.getText());
		        }
		    }
		});
	
		HBox searchBox = new HBox();
		HBox.setHgrow(searchBar, Priority.ALWAYS);
		searchBox.getChildren().addAll(searchBar);
		searchBox.setSpacing(10);
		
		return searchBox;
	}
	
	/**
	 * Add to library button, creates a button to add song to the library
	 * 
	 * The button calls the controller on event click to update the library
	 * @param song
	 * @return Button
	 */
	public static Button addToLibraryButton(Song song) {
		Button addLibraryButton = new Button("Add to Library");
        addLibraryButton.setOnAction(e -> {
        	controller.addToLibrary(song, "seach_string_updated");
        });
        return addLibraryButton;
	}
	
	 /**
	  * remove from library button, creates a button to add song to the library
	  * The button calls the controller on event click to update the libraryz
	  * The button calls the controller on event click to update the library
	  * 
	  *  @param song
	  *  @return Button
	  */
	public static Button removeFromLibraryButton(Song song) {
		Button addLibraryButton = new Button("Remove from Library");
        addLibraryButton.setOnAction(e -> {
        	controller.removeFromLibrary(song);
        });
        return addLibraryButton;
	}
	
	

}
