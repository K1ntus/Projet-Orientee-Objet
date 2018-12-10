package fr.groupe40.projet;


import fr.groupe40.projet.client.Music;
import fr.groupe40.projet.client.handler.InteractionHandler;
import fr.groupe40.projet.events.Events;
import fr.groupe40.projet.file.DataSerializer;
import fr.groupe40.projet.model.board.Galaxy;
import fr.groupe40.projet.util.constants.Constants;
import fr.groupe40.projet.util.constants.Debugging;
import fr.groupe40.projet.util.constants.Generation;
import fr.groupe40.projet.util.constants.Players;
import fr.groupe40.projet.util.constants.Resources;
import fr.groupe40.projet.util.constants.Ticks;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * \brief Main class. Currently managing users interactions and display
 * @author Jordane Masson
 * @author Sarah Portejoie
 *
 */
public class Game extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * \brief Board object containing every sprites, etc
	 */
	private Galaxy galaxy;
	//private InteractionHandler interactionHandler;
	
	
	/**
	 * \brief game_tick counter for events, etc
	 */
	private long game_tick = 0;	//long because counter, had to prevent the overflow case
	
	private Events eventManager;	
	private Music soundHandler = new Music();
	
	private AudioClip mediaPlayer_ship_explosion;
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	private InteractionHandler interactionHandler;
	public void start(Stage stage) {
		/* 	---| OS check |---	
		 * Doing that because there s a little white
		 * margin under Windows (only tested under win 7 btw)
		 * So, if that's a windows OS, we re editing
		 * the window style to remove it
		 */
		if((OS.indexOf("win") >= 0)) {
			if(Debugging.DEBUG)
				System.out.println("OS type is windows");
			stage.initStyle(StageStyle.UTILITY);
		}else {
			if(Debugging.DEBUG)
				System.out.println("Non windows OS");
		}
		
		/* Initialize ships explosion sound */
		mediaPlayer_ship_explosion = soundHandler.generateAudioClip(Resources.path_sound_explosion, Resources.ship_explosion_volume);

		/* Window and game kernel creation */
		stage.setTitle("Nicolas Cage Space Simulator");
		stage.setResizable(false);

		Group root = new Group();
		Scene scene = new Scene(root);
		Canvas canvas = new Canvas(Generation.width, Generation.height);
		root.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		

		galaxy = new Galaxy(gc);
		galaxy.initFont(gc);

		DataSerializer saver = new DataSerializer(Constants.fileName_save, galaxy);
		
		if(Constants.events_enabled) 
			eventManager = new Events(galaxy, gc, true, true);		

		interactionHandler = new InteractionHandler(galaxy, scene, saver);
		interactionHandler.exec();
		
		
		stage.setScene(scene);
		stage.show();

		/**	KEYBOARD HANDLER	**/
		EventHandler<KeyEvent> keyboardHandler = new EventHandler<KeyEvent>() {
	
			@Override
			public void handle(KeyEvent e) {
					
				if (e.getCode() == KeyCode.F5) {
					System.out.println("Saving game ...");
					//OPEN POPUP ?
					saver.save_game();
				}
					
				if (e.getCode() == KeyCode.F6) {
					System.out.println("Loading game ...");
					galaxy = saver.load_game(gc);
					saver.reload_image_and_data(galaxy);

					interactionHandler = new InteractionHandler(galaxy, scene, saver);
					interactionHandler.exec();
				}
				
			}
		};
		
		scene.setOnKeyPressed(keyboardHandler);
        
		/*	Rendering */
		new AnimationTimer() {
			public void handle(long arg0) {	
				game_tick += 1;

				galaxy.render(gc);
				
				if(game_tick % Ticks.tick_per_squad_position_update == 0)
					galaxy.updateSquadPosition(mediaPlayer_ship_explosion);
				
				if(game_tick % Ticks.tick_per_produce == 0)
					galaxy.updateGarrison();
				
				if(game_tick % Ticks.tick_per_lift_off == 0)
					galaxy.updateWavesSending();
				
				if(game_tick % Ticks.tick_per_ai_attack == 0)
					galaxy.updateAI();

				
				if(game_tick % Ticks.tick_per_events == 0)
					if(Constants.events_enabled)
						eventManager.event_randomizer();
				
				if(game_tick % Ticks.tick_per_main_theme_check == 0)
					soundHandler.run();
								
				if(galaxy.userHasLost(Players.human_user)) {	//The user has lost
					System.out.println("Vous avez perdu");
					galaxy.render(gc);
					galaxy.renderDefeat(gc);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("Generating new board");
					galaxy = new Galaxy(gc);
					interactionHandler = new InteractionHandler(galaxy, scene, saver);
					interactionHandler.exec();
					//System.exit(0);
				}
				
			}
		}.start();
	}
	
	
}
