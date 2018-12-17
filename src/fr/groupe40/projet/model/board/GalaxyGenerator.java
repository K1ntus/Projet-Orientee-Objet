package fr.groupe40.projet.model.board;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.groupe40.projet.client.User;
import fr.groupe40.projet.model.planets.Planet;
import fr.groupe40.projet.model.planets.SquarePlanet;
import fr.groupe40.projet.util.constants.Constants;
import fr.groupe40.projet.util.constants.Generation;
import fr.groupe40.projet.util.constants.Players;
import fr.groupe40.projet.util.constants.Resources;
import javafx.concurrent.Task;

/**
 *  Galaxy Planets Generator
 * @author Jordane Masson
 * @author Sarah Portejoie
 *
 */
public class GalaxyGenerator extends Task<ArrayList<Planet>> {
	/**
	 *  the results planets array that has been generated
	 */
	private ArrayList<Planet> planets = new ArrayList<Planet>();
	
	/**
	 *  Create and Generate the board
	 */
	public GalaxyGenerator() {
<<<<<<< HEAD
=======
		try {
			this.call();
		} catch (Exception e) {
			System.out.println("******************************");
			System.out.println("* Unable to generate a board *");
			System.out.println("******************************");
			e.printStackTrace();
		}
	}
	


	@Override
	protected ArrayList<Planet> call() throws Exception {
		if(Constants.sun_enabled)
			generateSun();
>>>>>>> masterrace
		generatePlanets();
		return planets;
	}
	
	/*	Sun generation	*/


	/* Planets Generation */	
	/**
	 *  return a randomly generated planet type (ie. round or square)
	 * @return the planet generated
	 */
	private Planet getRandomPlanet() {
		return new SquarePlanet(Resources.path_img_square_basic, new User(Players.neutral_user), (int) (Generation.left_margin_size + Generation.size_squads), 0);
	}
	
	/**
	 *  Generate the planets for the galaxy initialization
	 */
	public void generatePlanets() {
		double width = Math.random() * Generation.size_maximal_planets *0.25 + Generation.size_minimal_planets;
		double height = width;
		//Sprite(String path, double width, double height, double maxX, double maxY) 

		
		for(int i = 0; i < Generation.nb_planets_tentatives; i++) {
			double y = (Math.random() * (Generation.height - (height + Generation.bottom_margin_size)));
			Planet p = getRandomPlanet();

			p.setY(y);

			while(p.updatePlanetePosition() != -1) {
				if(isFarEnough(p, p.width()/2 + Generation.minimal_distance_between_planets)) {
					planets.add(p);	
					break;
				}
			}

		}
		
		if(planets.size() < Generation.min_numbers_of_planets) {	//si moins de 2 planetes
			System.out.println("Impossible de generer un terrain minimal");
			System.exit(-1);		//quitte le prgm
		}else {		//On attribue 2 planetes, une a l'ia, une au joueur
			planets.get(1).setRuler(Players.human_user);
			planets.get(1).setImg_path(Resources.path_img_planet_human);
			planets.get(1).updateImage();
			if(Constants.ai_enabled)
				planets.get(2).setRuler(Players.ai_user);
		}
		
		ArrayList<Planet> planet_ai = new ArrayList<Planet>();
		planet_ai.add(planets.get(2));
		normalize_beginning_troups(planets.get(1), planet_ai);
	}

	private void normalize_beginning_troups(Planet planet_human, List<Planet> planet_ai) {
		int max_troups_from_ai = -1;
		for(Planet p : planet_ai) {
			if(p.getTroups() > max_troups_from_ai)
				max_troups_from_ai = p.getTroups();
		}
		
		switch(Constants.difficulty) {
		case INITIE:
			planet_human.setTroups((int)(max_troups_from_ai*1.5));			
			break;
		case SPACE_MARINE:
			planet_human.setTroups((int)(max_troups_from_ai*1));			
			break;
		case PRAETOR:
			planet_human.setTroups((int)(max_troups_from_ai*0.75));			
			break;
		case PRIMARQUE:
			planet_human.setTroups((int)(max_troups_from_ai*0.5));			
			break;
		}
	}
	/**
	 *  Test the valid position of a planet compare to each others.
	 *  
	 *  Please, do not use this function anymore
	 *  Really high complexity for the job he's doing
	 * @param p The planet we are trying to generate
	 * @return false if not able to generate this planet, else true
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private boolean testPlacement(Planet p) {
		Iterator<Planet> it = planets.iterator();
		
		while (it.hasNext()) {
			if(p.getY() > Generation.height - Generation.bottom_margin_size - Generation.size_squads - p.height()) {
				return false;
			}
			
			Planet p_already_placed = it.next();
			
			if(p_already_placed.isInside(p) || p_already_placed.intersectCircle(p, p.width()/2 + Generation.minimal_distance_between_planets)) {
				if(p.updatePlanetePosition() == -1) {
					System.out.println("unable to generate this planet");
					return false;
				}
				
				it = planets.iterator();
			}
		}
		
		return true;
	}

	/**
	 * Check if the planet passed in parameters is
	 * far enough from every others planets
	 * (ie. distance < radius)
	 * @param p The planet to check
	 * @param radius the minimal distance between each planets
	 * @return true if its ok, else false
	 */
	private boolean isFarEnough(Planet p, double radius) {
		Iterator<Planet> it = planets.iterator();
		while (it.hasNext()) {
			Planet planet_already_placed = it.next();
			double distance = p.distance(planet_already_placed) - p.width()/2;
			
			if(distance < radius) {
				return false;
			}
			
		}
		
		return true;
	}
	
	/**
	 * @return the planets
	 */
	public ArrayList<Planet> getPlanets() {
		return planets;
	}

	/**
	 * @param planets the planets to set
	 */
	public void setPlanets(ArrayList<Planet> planets) {
		this.planets = planets;
	}
}
