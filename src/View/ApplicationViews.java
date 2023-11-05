package View;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Stores the stage and all four scenes in it.
 * 
 * The class is used to cleanly access all the four scenes and the stage objects
 * @author namanpandey
 *
 */
public class ApplicationViews {
	
	public final Stage stage;
	public Scene homeScene, libraryScene, searchScene, playerScene;
	
	/**
	 * Initializes the class with a stage, the scenes are public and are accessed as such.
	 * @param stage
	 */
	public ApplicationViews(Stage stage) {
		this.stage = stage;
		this.homeScene = null;
		this.libraryScene = null;
		this.searchScene = null;
		this.playerScene = null;
	}

}
