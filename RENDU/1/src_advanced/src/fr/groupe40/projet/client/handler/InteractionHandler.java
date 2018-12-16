package fr.groupe40.projet.client.handler;


import fr.groupe40.projet.file.DataSerializer;
import fr.groupe40.projet.model.board.Galaxy;
import javafx.scene.Scene;

public class InteractionHandler {
	
	//private KeyboardListener keyboard;
	
	/**
	 *  will manage the mouse event
	 */
	private MouseListener mouse;

	//private Squad selected = null;
	
	/**
	 *  constructor for the user input
	 * @param galaxy
	 */
	public InteractionHandler(Galaxy galaxy, Scene scene, DataSerializer saver) {
		//this.setKeyboard(new KeyboardListener(galaxy, scene, saver));
		this.mouse = new MouseListener(galaxy, scene);
		
	}
	
	/**
	 *  launch the mouse & keyboard handler
	 */
	public void exec() {
		mouse.launch();
		//keyboard.launch();	
		
	}

	/*
	public KeyboardListener getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(KeyboardListener keyboard) {
		this.keyboard = keyboard;
	}
	*/
}