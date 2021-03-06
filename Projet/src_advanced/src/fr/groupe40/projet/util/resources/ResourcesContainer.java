package fr.groupe40.projet.util.resources;

import fr.groupe40.projet.util.constants.Constants;
import fr.groupe40.projet.util.constants.Resources;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

/**
 * Save multiple resources file for performance and speed of the application
 * @author Jordane Masson
 * @author Sarah Portejoie
 */
public class ResourcesContainer {
	/**
	 * contain the Image of the main menu background
	 */
	private static Image main_menu_background;
	
	/**
	 * contain the Image of the game background
	 */
	private static Image game_background;

	/**
	 * contain the Image of human ships saved
	 */
	private Image game_human_ships;
	
	/**
	 * contain the Image of ai ships saved
	 */
	private Image game_ai_ships;
	
	/**
	 * contain the Image of pirate ships saved
	 */
	private Image game_pirate_ships;

	/**
	 * Singleton to force the usage of only ONE resource container type
	 */
	private static ResourcesContainer instance = null;
	
	/**
	 * contain the sound of a ship collision with his destination
	 */
	private AudioClip sound_ship_explosion;
	
	/**
	 * contain the sound played when leaving the game
	 */
	private AudioClip quit_button_sound;
	
	/**
	 * contain the sound played when pressing the play button
	 */
	private AudioClip play_button_sound;
	
	/**
	 * contain the sound played when pressing the setting button
	 */
	private AudioClip settings_button_sound;

	/**
	 * Load and Save every stable pictures and sounds that could be used in the game
	 */
	private ResourcesContainer(){
		init_backgrounds();
		init_ships_image();
		init_sounds_fx();
		
		//init_gui_buttons();
	}

	private void init_backgrounds() {
		main_menu_background = ImageManager.getImageByPath_dynamic(ImageManager.getRessourcePathByName(Resources.path_img_menu_background), Constants.width);
		game_background = ImageManager.getImageByPath_dynamic(ImageManager.getRessourcePathByName(Resources.path_img_game_background), Constants.width);
	}
	
	private void init_ships_image() {
		game_human_ships = ImageManager.getImageByPath_dynamic(ImageManager.getRessourcePathByName(Resources.path_img_human_ships), Constants.size_squads);
		game_ai_ships = ImageManager.getImageByPath_dynamic(ImageManager.getRessourcePathByName(Resources.path_img_AI_ships), Constants.size_squads);
		game_pirate_ships = ImageManager.getImageByPath_dynamic(ImageManager.getRessourcePathByName(Resources.path_img_event_pirate_ships), Constants.size_squads);
	}
	
	private void init_sounds_fx() {
		play_button_sound = SoundManager.getAudioByPath_dynamic(Resources.path_sound_play, 0.5);
		quit_button_sound = SoundManager.getAudioByPath_dynamic(Resources.path_sound_quit, 0.5);
		settings_button_sound = SoundManager.getAudioByPath_dynamic(Resources.path_sound_settings, 0.5);
		sound_ship_explosion = SoundManager.getAudioByPath_dynamic(Resources.path_sound_explosion, Resources.ship_explosion_volume);
	}
	
	/**
	 * Get the instance of this class, or create a new one if there's none and return it
	 * @return the singleton ResourcesContainer
	 */
	public static ResourcesContainer getInstance() {
		if(instance == null)
			instance = new ResourcesContainer();
		return instance;
	}
	
	/**
	 * @return the game_background
	 */
	public Image getGame_background() {
		return game_background;
	}


	/**
	 * @param game_background the game_background to set
	 */
	public void setGame_background(Image game_background) {
		ResourcesContainer.game_background = game_background;
	}


	/**
	 * @return the main_menu_background
	 */
	public static Image getMain_menu_background() {
		return main_menu_background;
	}


	/**
	 * @param main_menu_background the main_menu_background to set
	 */
	public void setMain_menu_background(Image main_menu_background) {
		ResourcesContainer.main_menu_background = main_menu_background;
	}

	/**
	 * @return the game_human_ships
	 */
	public Image getGame_human_ships() {
		return game_human_ships;
	}

	/**
	 * @return the game_ai_ships
	 */
	public Image getGame_ai_ships() {
		return game_ai_ships;
	}

	/**
	 * @return the game_pirate_ships
	 */
	public Image getGame_pirate_ships() {
		return game_pirate_ships;
	}

	/**
	 * @param game_human_ships the game_human_ships to set
	 */
	public void setGame_human_ships(Image game_human_ships) {
		this.game_human_ships = game_human_ships;
	}

	/**
	 * @param game_ai_ships the game_ai_ships to set
	 */
	public void setGame_ai_ships(Image game_ai_ships) {
		this.game_ai_ships = game_ai_ships;
	}

	/**
	 * @param game_pirate_ships the game_pirate_ships to set
	 */
	public void setGame_pirate_ships(Image game_pirate_ships) {
		this.game_pirate_ships = game_pirate_ships;
	}

	/**
	 *  play sound when a ship of his squad reach his destination
	 * @param mediaPlayer_boom the audio clip to play
	 */
	public void renderCollisionSound() {
		
		if(sound_ship_explosion == null && !Resources.sounds_enabled) {
			return;
		}
		sound_ship_explosion.play();
	}

	/**
	 * @return the play_button_sound
	 */
	public AudioClip getPlay_button_sound() {
		return play_button_sound;
	}

	/**
	 * @param play_button_sound the play_button_sound to set
	 */
	public void setPlay_button_sound(AudioClip play_button_sound) {
		this.play_button_sound = play_button_sound;
	}

	/**
	 * @return the quit_button_sound
	 */
	public AudioClip getQuit_button_sound() {
		return quit_button_sound;
	}

	/**
	 * @param quit_button_sound the quit_button_sound to set
	 */
	public void setQuit_button_sound(AudioClip quit_button_sound) {
		this.quit_button_sound = quit_button_sound;
	}

	/**
	 * @return the settings_button_sound
	 */
	public AudioClip getSettings_button_sound() {
		return settings_button_sound;
	}

	/**
	 * @param settings_button_sound the settings_button_sound to set
	 */
	public void setSettings_button_sound(AudioClip settings_button_sound) {
		this.settings_button_sound = settings_button_sound;
	}

	/**
	 * @return the sound_ship_explosion
	 */
	public AudioClip getSound_ship_explosion() {
		return sound_ship_explosion;
	}

	/**
	 * @param sound_ship_explosion the sound_ship_explosion to set
	 */
	public void setSound_ship_explosion(AudioClip sound_ship_explosion) {
		this.sound_ship_explosion = sound_ship_explosion;
	}
}
