package fr.groupe40.projet.model.board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.javafx.scene.traversal.Direction;

import fr.groupe40.projet.client.User;
import fr.groupe40.projet.model.planets.Planet;
import fr.groupe40.projet.model.ships.Ship;
import fr.groupe40.projet.model.ships.Squad;
import fr.groupe40.projet.util.constants.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * \brief 'Board' class. It contains the data of the game as ships, planets, ...
 * @author Jordane Masson
 * @author Sarah Portejoie
 *
 */
public class Galaxy implements Serializable{
	private static final long serialVersionUID = 3668540725184418675L;

	/**
	 * \brief Array with every planet of the board
	 */
	private ArrayList <Planet> planets;

	/**
	 * \brief Array with every squads of the board
	 */
	private ArrayList <Squad> squads;

	/**
	 * \brief board generator
	 */
	private GalaxyGenerator generator = new GalaxyGenerator();

	/**
	 * \brief background image
	 */
	private transient Image background;
	
	/**
	 * \brief Generate a game board with every parameters randomized
	 */
	public Galaxy() {
		this.squads = new ArrayList<Squad>();
		this.planets = generator.getPlanets();
		this.generator = null;
		
		setBackground(new Image(Constants.path_img_background, Constants.width, Constants.height, false, false, true));
		
	}

	/**
	 * \brief Generate a game board from another
	 * @param g The game board to copy from
	 */
	public Galaxy(Galaxy g) {
		this.planets = g.planets;
		this.squads = g.squads;
		this.generator = null;
		
		setBackground(new Image(Constants.path_img_background, Constants.width, Constants.height, false, false, true));
	}

	/**
	 * \brief Generate a game board from an array of squads and planets
	 * @param planets Planets that we want to have
	 * @param squads Squads already present
	 */
	public Galaxy(List<Planet> planets, List<Squad> squads) {
		this.squads = (ArrayList<Squad>) squads;
		this.planets = generator.getPlanets();
		this.generator = null;
		
		setBackground(new Image(Constants.path_img_background, Constants.width, Constants.height, false, false, true));
		
	}
	
	/* Render & Game Update */
	/**
	 * \briefMain rendering function
	 * @param gc
	 */
	public void render(GraphicsContext gc) {
		renderBackground(gc);
		renderPlanets(gc);
		for(Squad s : squads)
			s.render_ships(gc);	
		renderGarrison(gc);
		renderPercentageSelected(gc);
	}
	
	/**
	 * \brief Main update function, manage AI, squads and hasLost
	 */
	public void updateSquadPosition() {
		for(Squad s : squads) {
			if(s==null) {	squads.remove(s);	continue;}
			s.update_all_positions(planets);			
		}

		userHasLost(Constants.ai_user);
	}
	
	/*	AI	*/
	/**
	 * \brief Manage the AI to send fleets
	 */
	public void updateAI() {
		Planet source, destination;
		
		for(Planet p : planets) {
			User ruler = p.getRuler();
			
			if(ruler.getId() < 0) {	//0 = neutral, >0 human, <0 bot
				source = p;
				
				for(Planet p2 : planets) {	//Check again the planets list
					destination = p2;
					if(p2.getTroups() < source.getTroups()) {
						Squad s = ruler.sendFleetAI(source, destination);
						if(s != null)
							squads.add(s);
						break;
					}
				}
			}
		}

	}

	/**
	 * \brief update the garrison value of each planets
	 */
	public void updateGarrison() {
		for(Planet p : planets)
			p.updateGarrison();	
	}

	/**
	 * \brief send all a new wave if need for each squads
	 */
	public void updateWavesSending() {
		for(Squad s : squads) {
			s.sendFleet();
		}
		
	}
	
	/* Defeat handler */
	/**
	 * \brief Check if an user has lost
	 * @param u the user to check
	 * @return true if he has last, else false
	 */
	public boolean userHasLost(User u) {	//S il existe au moins une planete lui appartenant -> pas perdu
		if(u.isLost()) {	//if user already registered has loser
			return true;
		}
		
		int id = u.getId();
		
		for(Planet p : planets) {
			int ruler_id = p.getRuler().getId();
			if(ruler_id == id) {
				return false;
			}
		}
		
		for(Squad s : squads) {
			User ruler = s.getRuler();
			int ruler_id = 0;
			if(ruler != null)
				ruler_id = ruler.getId();
			else
				continue;
			
			if(ruler_id == id) {
				return false;
			}
		}
		
		u.setLost(false);
		return true;
	}
	
	
	/* Client handler */
	/**
	 * \brief handle the scrolls event to change the percent of a fleet to send
	 * @param action The scroll action (up or down case)
	 */
	public void clientScrollHandler(Direction direction) {
		User u = Constants.human_user;
		int percent = u.getPercent_of_troups_to_send();
		switch(direction) {
			case DOWN://lower
				u.setPercent_of_troups_to_send(percent - 5); break;
			case UP://greater
				u.setPercent_of_troups_to_send(percent + 5); break;
			default:
				break;
		}
	}
	
	/* Collisions Between Ships & Planets */
	/**
	 * \brief Manage the collisions between squads & intermediate planets
	 * @param s The squad to manage
	 * @param p The planet to test collision with
	 */
	public void collisionHandler(Ship s, Planet p) {
		double deltaY = 0, deltaX = 0;
		double x = s.getX(), y = s.getY();
		double width = s.width(), height = s.height();
		double speed = s.getSpeed();

		double xCenter = p.getX() + p.width() /2;
		double yCenter = p.getY() + p.height()/2;
		
		if(p.isInside(x-2*speed, y, width, height)) {	
			if(yCenter > y) {
				deltaY -= speed;		
			} else {
				deltaY += speed;
			}			
		} else if(p.isInside(x+2*speed, y, width, height)) {
			if(yCenter > y) {
				deltaY -= speed;		
			} else {
				deltaY += speed;
			}
		}
		
		if(p.isInside(x, y-2*speed, width, height)) {
			if(xCenter > x) {
				deltaX += speed;
			} else {
				deltaX -= speed;	
			}
		} else if(p.isInside(x, y+2*speed, width, height)) {
			if(xCenter > x) {	
				deltaX += speed;
			} else {
				deltaX -= speed;	
			}
		}
		
		if(p.isInside(x+deltaX,y+deltaY,width,height))
			return;
		s.setPosition(x+deltaX, y+deltaY);
			
	}

	/**
	 * \brief check if there s a collision between a squad and every planet on board
	 * @param s the squad to check
	 * @return true if there s a collision, else false
	 */
	public boolean isCollision(Ship s) {
		for(Planet p : planets) {
			if(p != s.getDestination() && p != s.getSource()) {
				collisionHandler(s, p);
				return true;
			}
		}
		s.update_position(planets);

		return false;
	}
	
	/* Rendering subfunction */

	/**
	 * \brief Render the background image
	 * @param gc
	 */
	public void renderBackground(GraphicsContext gc) {
		gc.drawImage(getBackground(), 0, 0);
	}
	
	/**
	 * \brief Render each planets image
	 * @param gc
	 */
	public void renderPlanets(GraphicsContext gc) {
		for(Planet p : planets) {
			if(p != null)
				p.render(gc);					
		}
	}
	
	/**
	 * \brief Render every squads on board
	 * @param gc
	 */
	public void renderSquads(GraphicsContext gc) {
		Iterator<Squad> it = squads.iterator();
		while (it.hasNext()) {
			Squad ss = it.next();		
			if(ss == null) {	continue;	}
			
			for (Ship ship : ss.getShips()) {
				if(ship.isReached()) {
					ss.getShips().remove(ship);
					continue;					
				}else {
					//isCollision(ss);
					ss.render_ships(gc);	
					
				}
			}
		}
	}
	
	/**
	 * \brief Render the garrison amount of each planets on board
	 * @param gc
	 */
	public void renderGarrison(GraphicsContext gc) {
		for (Planet p : planets) {
			String txt = Integer.toString(p.getTroups());
			gc.setTextAlign(TextAlignment.CENTER);	
			gc.setStroke(Color.BLACK);
			
			switch(p.getRuler().getFaction()) {
				case Constants.player:
					gc.setFill(Constants.color_player); break;
				case Constants.ai:
					gc.setFill(Constants.color_ai); break;
				case Constants.neutral:
					gc.setFill(Constants.color_neutral); break;
				default:
					gc.setFill(Constants.color_default); break;
			}
			gc.fillText(txt, p.getX() + (p.width()/2), p.getY() + (p.height()/2));
			gc.strokeText(txt, p.getX() + (p.width()/2), p.getY() + (p.height()/2));
		}
	}

	/**
	 * \brief Render the percentage of troups to send selected by the player
	 * @param gc
	 */
	public void renderPercentageSelected(GraphicsContext gc) {
		gc.setFill(Constants.color_default);
		gc.setStroke(Color.RED);
		gc.setTextAlign(TextAlignment.CENTER);	
		
		for(Planet p :planets) {
			User u = p.getRuler();
			if (u.getFaction() == Constants.player) {
				String txt = "Troupes: "+u.getPercent_of_troups_to_send()+"%";
				
				gc.fillText(txt, Constants.width/7, 25);
				gc.strokeText(txt, Constants.width/7, 25);
				
				return;				
			}
		}
		String txt = Constants.message_game_over;
		gc.fillText(txt, Constants.width/5, 25);
		gc.strokeText(txt, Constants.width/5, 25);
	}
	
	/**
	 * \brief render in case of defeat 
	 *	//TODO defeat rendering
	 * @param gc the Graphics Context
	 */
	public void renderDefeat(GraphicsContext gc) {
		gc.setFill(Constants.color_default);
		gc.setStroke(Color.RED);
		gc.setTextAlign(TextAlignment.CENTER);	

		String txt = Constants.message_game_over;
		gc.fillText(txt, Constants.width/2, 25);
		gc.strokeText(txt, Constants.width/2, 25);
		
	}
	
	/**
	 * \brief render a explosion when a ship reach his destination
	 * @param gc
	 */
	public void renderExplosion(GraphicsContext gc) {
		
	}
	
	/* init the font type */
	/**
	 * \brief Init the default font style of the graphics context
	 * @param gc
	 */
	public void initFont(GraphicsContext gc) {
		gc.setFont(Font.font("Helvetica", FontWeight.BOLD, 24));
		gc.setFill(Constants.color_default);
		gc.setStroke(Color.RED);
		gc.setLineWidth(1);		
	}
	
	/* Getter & Setter */

	/**
	 * \brief Set the list containing every squads by another one
	 * @param planets
	 */
	public ArrayList<Squad> getSquads() {
		return squads;
	}

	/**
	 * \brief Return the list containing every squads on board
	 * @return
	 */
	public void setSquads(ArrayList<Squad> squads) {
		this.squads = squads;
	}

	/**
	 * \brief Set the list containing every planet by another one
	 * @param planets
	 */
	public void setPlanets(ArrayList<Planet> planets) {
		this.planets = planets;
	}
	
	/**
	 * \brief Return the list containing every planets on board
	 * @return
	 */
	public ArrayList<Planet> getPlanets() {
		return planets;
	}
	
	/**
	 * \brief Return the background image
	 * @return Image 
	 */
	public Image getBackground() {
		return background;
	}

	/**
	 * \brief Set the background image
	 * @param background
	 */
	public void setBackground(Image background) {
		this.background = background;
	}

	
}
