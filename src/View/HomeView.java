package View;

import Controller.Controller;
import Model.Song;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 * Home Scene of the App. 
 * 
 * The class contains the home scene of the app and displays a list view of songs from the server.
 * The view also has a menu item to access different filters to get different home pages such as 
 * rock, rap, popular.
 * 
 * @author namanpandey
 *
 */
public class HomeView {
	
	static Controller controller;
	
	private VBox selector;
	
	/**
	 * Intializes the class with the controller.
	 * 
	 * @param controller
	 */
	public HomeView(Controller controller) {
		HomeView.controller = controller;
	}
	
	/**
	 * Returns a Scene back to the PlayerView.java
	 * Contains a title, list view and nav bar.
	 * @param navBar
	 * @return Scene - home scene
	 */
	public Scene getScene(HBox navBar) {
		//Scene 1
		Label titleBar = setPageTitle("Home");
		ListView<Song> scrollView = setListView(this.controller.getHomePage());
		
		HBox navigationBar =  navBar;
		selector = getGenereSelector();

		VBox box = new VBox();
		box.getChildren().addAll(titleBar, selector, scrollView, navigationBar);
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
	 * A menu bar below the title to select the genre to be displayed in the app. 
	 * 
	 * Contains events that ping the controller to change the genre and get a new api requested genre.
	 * 
	 * @return VBox - returns the menu bar.
	 */
	private VBox getGenereSelector() {
		Menu m = new Menu("Menu");
		  
        MenuItem def = new MenuItem("Default");
        MenuItem pop = new MenuItem("Popular");
        MenuItem rock = new MenuItem("Rock");
        MenuItem rap = new MenuItem("Rap");
  
        // add menu items to menu
        m.getItems().add(def);
        m.getItems().add(pop);
        m.getItems().add(rock);
        m.getItems().add(rap);
        
        // Event to change the genre of the page.
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println(((MenuItem)e.getSource()).getText() + " selected");
                if (((MenuItem)e.getSource()).getText().equals("Default")) {
                	controller.changePageType(0);
                } else if (((MenuItem)e.getSource()).getText().equals("Popular")) {
                	controller.changePageType(1);
                } else if (((MenuItem)e.getSource()).getText().equals("Rock")) {
                	controller.changePageType(2);
                }else if (((MenuItem)e.getSource()).getText().equals("Rap")) {
                	controller.changePageType(3);
                } else {
                	controller.changePageType(0);
                }
            }
        };
  
        // add event
        def.setOnAction(event);
        pop.setOnAction(event);
        rock.setOnAction(event);
        rap.setOnAction(event);
  
        MenuBar mb = new MenuBar();
        mb.getMenus().add(m);
  
        VBox genreVBox = new VBox(mb);
		return genreVBox;
	}
	
	/**
	 * Displays the list style view for the home page songs. 
	 * 
	 * Takes in an array of Song objects 
	 * @param items
	 * @return ListView list view object
	 */
	private ListView<Song> setListView(Song[] items) {		
		ObservableList<Song> songs = FXCollections.observableArrayList(items);
		
		ListView<Song> listView = new ListView<Song>(songs);
		
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
            ret.setAlignment(Pos.CENTER_LEFT);
            if (item != null) {
                setGraphic(ret);
            }
        }
		
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
        	controller.addToLibrary(song, "home");
        });
        return addLibraryButton;
	}
	
	/**
	 * remove from library button, creates a button to add song to the library
	 * 
	 * The button calls the controller on event click to update the library
	 * @param song
	 * @return Button
	 */
	public static Button removeFromLibraryButton(Song song) {
		Button remLibraryButton = new Button("Remove from Library");
		remLibraryButton.setOnAction(e -> {
        	controller.removeFromLibrary(song);
        });
        return remLibraryButton;
	}
	
}
