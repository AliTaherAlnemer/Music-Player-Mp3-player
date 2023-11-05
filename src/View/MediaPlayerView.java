package View;

import Controller.Controller;
import Model.Song;
import View.LibraryView.ListViewCell;
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
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer;

/**
 * Player Scene of the App. 
 * 
 * The class contains the Player scene of the app and displays a list view of songs from the server.
 * The view saves all the songs that the users asks to save from home pages and the search pages.
 * 
 * The class is responsible for playing the song when the model updates the view.
 * 
 * @author namanpandey
 *
 */
public class MediaPlayerView {
	Controller controller;
	MediaPlayer mediaPlayer;
	ImageView albumArt;

	Button playBtn;
	Boolean isPlay;
	
	private Label songName, artistName;
	private Slider progressBar;

	/**
	 * Intializes the class with the controller.
	 * 
	 * @param controller
	 */
	public MediaPlayerView(Controller controller) {
		this.controller = controller;
		songName   = new Label("No Current Song");
		artistName = new Label();
		mediaPlayer = null;
		if (controller.getPlayer().getListLength() > 0) {
			System.out.println("pausing the song?");
			if (mediaPlayer != null) mediaPlayer.stop();
			mediaPlayer = new MediaPlayer(controller.getPlayer().getCurrentSong());
			mediaPlayer.setAutoPlay(true);
		}
	}
	
	/**
	 * Called by PlayerView.java to stop a song before playing a next song. 
	 */
	public void stopSong() {
		if (mediaPlayer == null) return;
		mediaPlayer.stop();
	}

	/**
	 * Returns a Scene back to the PlayerView.java
	 * Contains a title, list view and nav bar.
	 * @param navBar
	 * @return Scene - search scene
	 */
	public Scene getScene(HBox navBar) {
		BorderPane pane = new BorderPane();
		Label titleBar = setPageTitle("Player");
		
		if (mediaPlayer == null) {
			albumArt = new ImageView(new Image("./Assets/no_song.jpg"));
			albumArt.setFitHeight(ViewConstants.PLAY_IMG);
			albumArt.setFitWidth(ViewConstants.PLAY_IMG);
		} else {
			albumArt = new ImageView(new Image(controller.getPlayer().getMetadata().image));
			albumArt.setFitHeight(ViewConstants.PLAY_IMG);
			albumArt.setFitWidth(ViewConstants.PLAY_IMG);
			songName.setText(controller.getPlayer().getMetadata().name);
			artistName.setText(controller.getPlayer().getMetadata().artist);
			progressBar = getSeekBar(mediaPlayer);
		}
		
		songName.setStyle("-fx-text-fill:GRAY; -fx-font-size: 40;");
		artistName.setStyle("-fx-text-fill:GRAY; -fx-font-size: 20;");

		VBox mainSongArea = new VBox(albumArt, this.songName, this.artistName);
		
		mainSongArea.setAlignment(Pos.CENTER);

		HBox navigationBar = navBar;
		progressBar = getSeekBar(mediaPlayer);

		VBox box = new VBox();

		box.getChildren().addAll(mainSongArea, progressBar, playerController());
		box.setPrefWidth(ViewConstants.SCENE_WID);
		box.setStyle("-fx-focus-color: transparent;");

		pane.setTop(titleBar);
		pane.setCenter(box);
		pane.setBottom(navigationBar);

		return new Scene(pane, ViewConstants.SCENE_WID, ViewConstants.SCENE_HEI);
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
	 * A seek bar for the song playing, the user can scrub through the song while it is playing.
	 * @param mediaPlayer
	 * @return returns a slider bar
	 */
	public Slider getSeekBar(MediaPlayer mediaPlayer) {
		progressBar = new Slider();
		if (mediaPlayer == null)
			return progressBar;

		mediaPlayer.setOnReady(new Runnable() {
			public void run() {
				progressBar.setMax(mediaPlayer.getTotalDuration().toSeconds());
			}
		});

		mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				progressBar.setValue(newValue.toSeconds());
			}
		});

		progressBar.setOnMouseClicked(e -> {
			mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
		});
		return progressBar;
	}

	/**
	 * Contains all the player buttons like play/pause, fastforwards/fastbackwards etc
	 *  
	 * @return returns a hbox with all the controlling buttons
	 */
	public HBox playerController() {
		playBtn = makeButton("./Assets/pause.png", true);
		isPlay = true;
		playBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (mediaPlayer == null) return;
				if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
					mediaPlayer.pause();
					playBtn.setGraphic(new ImageView("./Assets/play.png"));
				} else {
					mediaPlayer.play();
					playBtn.setGraphic(new ImageView("./Assets/pause.png"));
				}
				isPlay = !isPlay;
			}

		});

		Button fastBackward = makeButton("-10", false);
		fastBackward.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (mediaPlayer == null) return;
				new Thread(() -> mediaPlayer.seek(new Duration(mediaPlayer.getCurrentTime().toMillis() - 10000)))
						.start();
				mediaPlayer.play();
				playBtn.setGraphic(new ImageView("./Assets/pause.png"));
			}

		});
		Button fastForward = makeButton("+10", false);
		fastForward.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (mediaPlayer == null) return;
				new Thread(() -> mediaPlayer.seek(new Duration(mediaPlayer.getCurrentTime().toMillis() + 10000)))
						.start();
				mediaPlayer.play();
				playBtn.setGraphic(new ImageView("./Assets/pause.png"));
			}

		});

		Button prev = makeButton("./Assets/fastbackward.png", true);
		prev.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (mediaPlayer == null) return;
				mediaPlayer.stop();
				mediaPlayer = new MediaPlayer(controller.getPlayer().prevSong());
				if (controller.getPlayer().getMetadata() != null) {
					albumArt.setImage(new Image(controller.getPlayer().getMetadata().image));
				}
				mediaPlayer.play();
				playBtn.setGraphic(new ImageView("./Assets/pause.png"));
			}
		});

		Button next = makeButton("./Assets/fastforward.png", true);
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (mediaPlayer == null) return;
				mediaPlayer.stop();
				mediaPlayer = new MediaPlayer(controller.getPlayer().nextSong());
				if (controller.getPlayer().getMetadata() != null) {
					albumArt.setImage(new Image(controller.getPlayer().getMetadata().image));
					songName.setText(controller.getPlayer().getMetadata().name);
					artistName.setText(controller.getPlayer().getMetadata().artist);
					progressBar = getSeekBar(mediaPlayer);
				}
				mediaPlayer.play();
				playBtn.setGraphic(new ImageView("./Assets/pause.png"));
			}

		});
	
		HBox mainHBox = new HBox(10, prev, fastBackward, playBtn, fastForward, next);
		mainHBox.setAlignment(Pos.CENTER);

		return mainHBox;
	}

	/**
	 * Styles the control buttons in a set style.
	 * Takes in a boolean to determin if the string param is a button text or button image url
	 * 
	 * @param str
	 * @param isImg
	 * @return button with style
	 */
	private Button makeButton(String str, boolean isImg) {
		Button button;
		if (!isImg) {
			button = new Button(str);
		} else {
			button = new Button(
					);
			button.setGraphic(new ImageView(new Image(str)));
		}
		button.setStyle("-fx-background-radius: 5em; " + "-fx-min-width: 50px; " + "-fx-min-height: 50px; "
				+ "-fx-max-width: 50px; " + "-fx-max-height: 50px;");
		return button;
	}

}
