package fr.groupe40.projet.client.handler;


import fr.groupe40.projet.file.DataSerializer;
import fr.groupe40.projet.model.board.Galaxy;
import javafx.scene.Scene;

public class InteractionHandler {
<<<<<<< HEAD
	
=======
>>>>>>> masterrace
	/**
	 *  will manage the mouse event
	 */
	private MouseListener mouse;
<<<<<<< HEAD

=======
	
>>>>>>> masterrace
	/**
	 *  constructor for the user input
	 * @param galaxy
	 */
	public InteractionHandler(Galaxy galaxy, Scene scene, DataSerializer saver) {
		this.mouse = new MouseListener(galaxy, scene);
		
	}
	
	/**
	 *  launch the mouse & keyboard handler
	 */
	public void exec() {
<<<<<<< HEAD
		mouse.launch();
		
	}

=======
		mouse.launch();		
	}
>>>>>>> masterrace
}
