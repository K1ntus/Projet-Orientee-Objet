package fr.groupe40.projet.model.ships;

import java.io.Serializable;

import fr.groupe40.projet.util.constants.Constants;


/**
 *  Class containing the ships parameters as speed, ...
 * @author Jordane Masson
 * @author Sarah Portejoie
 *
 */
public class ShipType implements Serializable {
	private static final long serialVersionUID = 6833813366446523473L;
	
	/**
	 *  the speed of this ship
	 */
	protected double speed;
	
	/**
	 *  the power of this ship (theorically, when a ship reached a planet, his garrison decrement from this value)
	 */
	protected double power;
	
	/**
	 *  the number of ships produced per update for his planet
	 */
	protected double production_time;
	
	/**
	 *  Generate random ship parameters
	 */
	public ShipType() {
		generate_parameters();
	}

	/**
	 *  Generate power, speed and production time for a planet/squad
	 */
	private void generate_parameters() {
		this.power = (int) (Math.random() * (Constants.max_ship_power - Constants.min_ship_power)+1);
		this.speed =  (int) (Math.random() * (Constants.max_ship_speed - Constants.min_ship_speed)+1);
		this.production_time =  (int) (Math.random() * (Constants.max_ship_produce - Constants.min_ship_produce)+1);
	}
	


	@Override
	public String toString() {
		return "[Ships Parameters] - Speed="+this.speed+", power="+this.power+", production speed="+this.production_time;
	}
}
