package fr.groupe40.projet.model.ships;

import static fr.groupe40.projet.util.constants.Direction.NO_COLLISION;

import java.io.Serializable;
import java.util.List;

import fr.groupe40.projet.client.User;
import fr.groupe40.projet.model.Sprite;
import fr.groupe40.projet.model.planets.Planet;
import fr.groupe40.projet.util.annot.TODO;
import fr.groupe40.projet.util.constants.Constants;
import fr.groupe40.projet.util.constants.Direction;
import fr.groupe40.projet.util.resources.ImageManager;


/**
 * This class is meant to define the object Ship into the game.</br>
 * A ship always has a source and a destination planet.</br>
 * </br>
 * It is also possible to know if this ship has reached or not his destination,
 * and which planet is on his way to the planet destination.</br>
 * In the class Ship you can find the path finding algorithm, allowing the
 * different troops to avoid collision with other planets.
 * 
 * @see Ship#calc_next_position(List)
 * 
 * @author Jordane Masson
 * @author Sarah Portejoie
 *
 */
public class Ship extends Sprite implements Serializable {
	private static final long serialVersionUID = -1872446628467348036L;

	/**
	 * ship type of this ship
	 */
	private ShipType ship_type = new ShipType();

	/**
	 * Source and Destination planets
	 */
	private Planet destination, source;

	/**
	 * If this ships has reacher or not his destination
	 */
	private boolean reached;

	/**
	 * The planet where there's a collision during his path in the board
	 */
	private Planet collision;

	/**
	 * constructor of the ship object
	 * 
	 * @param path        image path of this ship
	 * @param ruler       ruler of this ship
	 * @param destination his destination planet
	 * @param source      his source planet
	 * @param x_init      his initial x
	 * @param y_init      his initial y
	 * @param ship_type   ship_type (speed, power, ...)
	 */
	public Ship(String path, User ruler, Planet destination, Planet source, double x_init, double y_init,
			ShipType ship_type) {
		super(ImageManager.getRessourcePathByName(path), ruler, false);
		this.destination = destination;
		this.source = source;
		this.setX(x_init);
		this.setY(y_init);
		this.ship_type = ship_type;
		this.collision = source;
	}

	@Override
	public boolean isInside(Sprite s) {
		double x = this.getX(), y = this.getY();
		double width = this.width(), height = this.height();

		double x2 = s.getX(), y2 = s.getY();
		double width2 = s.width(), height2 = s.height();

		return ((x >= x2 && x <= x2 + width2) || (x2 >= x && x2 <= x + width))
				&& ((y >= y2 && y <= y2 + height2) || (y2 >= y && y2 <= y + height));
	}

	@Override
	public boolean isInside(double x, double y) {
		if (isInside(x, y, 1, 1)) {
			return true;
		}
		return false;

	}

	@Override
	public boolean isInside(double x, double y, double width, double height) {
		double x2 = this.getX(), y2 = this.getY(), width2 = this.width(), height2 = this.height();
		if (x > x2 + width2 || x + width < x2) {
			return false;
		}

		if (y > y2 + height2 || y + height < y2) {
			return false;
		}
		return true;
	}

	/**
	 * Check if the ship has reached his destination, and handle this case
	 */
	public boolean reached_destination() {

		if (reached)
			return true;

		if (destination.isInside(this)) { // Case if the squads reach the destination

			if (this.getRuler() != destination.getRuler()) { // If the faction are differents, then BOOM
				int difference = (int) (destination.getTroups() - ship_type.power);

				if (difference >= 1) { // Difference > 1 => kamikaze
					destination.setTroups(difference);
				} else { // Else, negative or 0 => new leader
					if (!destination.getRuler().equals(Constants.sun_user)) {

						destination.setRuler(this.getRuler());

						difference = Math.abs(difference);
						if (difference >= Constants.max_troups) { // Sum > 100, we lower the amount to stay at the
																	// limit
							destination.setTroups(Constants.max_troups);
						} else { // Else, reinforcement
							destination.setTroups(difference + 1);
						}
					}
				}
			} else if (this.getRuler().equals(destination.getRuler())) { // Same faction
				int sum = 1 + destination.getTroups(); // Sum of defense + squad
				if (sum >= Constants.max_troups) { // Sum > 100, we lower the amount to stay at the limit
					destination.setTroups(Constants.max_troups);
				} else { // Else, reinforcement
					destination.setTroups(sum);
				}
			}
			remove(); // Remove the squads of the galaxy
			return true;
		}
		return false;

	}

	/**
	 * Calculate the next position and provides collision handler
	 * 
	 * @param planets
	 */
	public void calc_next_position(List<Planet> planets) {
		double speed = ship_type.speed;

		double centre_x = destination.getX() + destination.width() / 2;
		double centre_y = destination.getY() + destination.height() / 2;
		double x = this.getX();
		double y = this.getY();
		switch (whereis_collision(x, y, speed, planets)) {
		case NO_COLLISION:
			no_collision_mover(x, y, centre_x, centre_y, speed);
			break;
		case TOP:
			top_collision_mover(x, y, centre_x, centre_y, speed);
			break;
		case BOTTOM:
			bottom_collision_mover(x, y, centre_x, centre_y, speed);
			break;
		case LEFT:
			left_collision_mover(x, y, centre_x, centre_y, speed);
			break;
		case RIGHT:
			right_collision_mover(x, y, centre_x, centre_y, speed);
			break;
		default:
			break;
		}
	}

	/**
	 * return where is the collision between a ship and every planets of a list
	 * 
	 * @param x       horizontal position of the ship
	 * @param y       vertical position of the ship
	 * @param speed   speed of the sheep
	 * @param planets array with every planets of the board on it
	 * @return a Direction enum type
	 */
	public Direction whereis_collision(double x, double y, double speed, List<Planet> planets) {
		double width = this.width();
		double height = this.height();
		Direction res = NO_COLLISION;

		for (Planet p : planets) {
			if (p.equals(destination)) {
				continue;
			}

			if (p.isInside(x - speed, y, width, height)) {
				collision = p;
				return Direction.RIGHT;
			} else if (p.isInside(x + speed, y, width, height)) {
				collision = p;
				return Direction.LEFT;

			} else if (p.isInside(x - speed, y - speed, width, height)
					|| p.isInside(x + speed, y - speed, width, height)) {
				collision = p;
				return Direction.BOTTOM;
			} else if (p.isInside(x - speed, y + speed, width, height)
					|| p.isInside(x + speed, y + speed, width, height)) {
				collision = p;
				return Direction.TOP;

			}
		}
		return res;

	}

	/**
	 * Next position calculation when there s a collision in the upper side
	 * 
	 * @param x        current x position
	 * @param y        current y position
	 * @param centre_x destination x
	 * @param centre_y destination y
	 * @param speed    speed of this ship
	 */
	public void top_collision_mover(double x, double y, double centre_x, double centre_y, double speed) {
		double deltaX = 0, deltaY = 0;

		double xCollision = collision.getX(), yCollision = collision.getY();
		double widthCollision = collision.width();

		if (destination.distance(xCollision, yCollision) > destination.distance(xCollision + widthCollision,
				yCollision))
			deltaX = +speed;
		else
			deltaX = -speed;

		if (destination.getY() < y)
			deltaY = -speed;

		this.setX(x + deltaX);
		this.setY(y + deltaY);
	}

	/**
	 * Next position calculation when there s a collision in the bottom side
	 * 
	 * @param x        current x position
	 * @param y        current y position
	 * @param centre_x destination x
	 * @param centre_y destination y
	 * @param speed    speed of this ship
	 */
	public void bottom_collision_mover(double x, double y, double centre_x, double centre_y, double speed) {
		double deltaX = 0, deltaY = 0;

		double xCollision = collision.getX(), yCollision = collision.getY();
		double widthCollision = collision.width();

		if (destination.distance(xCollision, yCollision) > destination.distance(xCollision + widthCollision,
				yCollision))
			deltaX = +speed;
		else
			deltaX = -speed;

		if (destination.getY() > y)
			deltaY = speed;

		this.setX(x + deltaX);
		this.setY(y + deltaY);
	}

	/**
	 * Next position calculation when there s a collision in the left side
	 * 
	 * @param x        current x position
	 * @param y        current y position
	 * @param centre_x destination x
	 * @param centre_y destination y
	 * @param speed    speed of this ship
	 */
	public void left_collision_mover(double x, double y, double centre_x, double centre_y, double speed) {
		double deltaX = 0, deltaY = 0;

		double xCollision = collision.getX(), yCollision = collision.getY();
		double heightCollision = collision.height();

		if (destination.distance(xCollision, yCollision) > destination.distance(xCollision,
				yCollision + heightCollision))
			deltaY = +speed;
		else
			deltaY = -speed;

		if (destination.getX() < x)
			deltaX = -speed;

		this.setX(x + deltaX);
		this.setY(y + deltaY);
	}

	/**
	 * Next position calculation when there s a collision in the right side
	 * 
	 * @param x        current x position
	 * @param y        current y position
	 * @param centre_x destination x
	 * @param centre_y destination y
	 * @param speed    speed of this ship
	 */
	public void right_collision_mover(double x, double y, double centre_x, double centre_y, double speed) {
		double deltaX = 0, deltaY = 0;

		double xCollision = collision.getX(), yCollision = collision.getY();
		double heightCollision = collision.height();

		if (destination.distance(xCollision, yCollision) > destination.distance(xCollision,
				yCollision + heightCollision))
			deltaY = +speed;
		else
			deltaY = -speed;

		if (destination.getX() > x)
			deltaX = speed;

		this.setX(x + deltaX);
		this.setY(y + deltaY);
	}

	/**
	 * calculate the angle between a ship and his destination
	 * 
	 * @param destination the destination planet
	 * @return the angle in degrees
	 */
	private double angle_to_follow(Planet destination) {
		double hyp = Sprite.distance(destination.getX() + destination.width() / 2,
				destination.getY() + destination.height() / 2, this.getX() + this.width() / 2,
				this.getY() + this.height() / 2);
		double adjacent_side = Math.abs((destination.getX() + destination.width() / 2) - this.getX());

		return Math.toDegrees(Math.cos(adjacent_side / hyp)) / 90;
	}

	/**
	 * case when there s no collision for a ship
	 * 
	 * @param x        current x position
	 * @param y        current y position
	 * @param centre_x destination x
	 * @param centre_y destination y
	 * @param speed    speed of this ship
	 */
	public void no_collision_mover(double x, double y, double centre_x, double centre_y, double speed) {
		double deltaX = 0, deltaY = 0;
		double angle = angle_to_follow(this.destination);

		if (x < centre_x) {
			if (x + speed > centre_x)
				deltaX = 0;
			else
				deltaX = speed;
		} else if (x > centre_x) {
			if (x - speed < centre_x)
				deltaX = 0;
			else
				deltaX = -speed;
		}

		if (y < centre_y) {
			if (y + speed >= centre_y)
				deltaY = 0;
			else
				deltaY = speed;
		} else if (y > centre_y) {
			if (y - speed <= centre_y)
				deltaY = 0;
			else
				deltaY = -speed;
		}

		this.setY(y + deltaY * (angle));
		this.setX(x + deltaX * (1 - angle));
	}

	/**
	 * Update the position of this ship
	 * 
	 * @param planets
	 */
	public void update_position(List<Planet> planets) {

		if (this.reached_destination()) { // Ships has reached his destination
			return;
		}
		calc_next_position(planets);
	}

	/**
	 * prepare his removal from the squad list
	 */
	@TODO(comment = "Render the ship explosion")
	public void remove() {
		this.setRuler(Constants.neutral_user);
		// this.setImage(Resources.path_gfx_ship_explosion);
		this.reached = true;
	}

	/**
	 * @return the ship_type
	 */
	public ShipType getShip_type() {
		return ship_type;
	}

	/**
	 * @param ship_type the ship_type to set
	 */
	public void setShip_type(ShipType ship_type) {
		this.ship_type = ship_type;
	}

	/**
	 * @return the destination
	 */
	public Planet getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Planet destination) {
		this.destination = destination;
	}

	/**
	 * @return the source
	 */
	public Planet getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Planet source) {
		this.source = source;
	}

	/**
	 * @return the reached
	 */
	public boolean isReached() {
		return reached;
	}

	/**
	 * @param reached the reached to set
	 */
	public void setReached(boolean reached) {
		this.reached = reached;
	}

	/**
	 * @return the collision
	 */
	public Planet getCollision() {
		return collision;
	}

	/**
	 * @param collision the collision to set
	 */
	public void setCollision(Planet collision) {
		this.collision = collision;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return ship_type.speed;
	}

	@Override
	public String toString() {
		return "Ships <" + this.getX() + ", " + this.getY() + "> - Ruled by id: " + this.getRuler().getId()
				+ "\nCaracteristics: " + this.getShip_type();
	}

}
