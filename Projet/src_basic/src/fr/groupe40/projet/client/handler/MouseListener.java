package fr.groupe40.projet.client.handler;

import fr.groupe40.projet.model.board.Galaxy;
import fr.groupe40.projet.model.planets.Planet;
import fr.groupe40.projet.model.ships.Squad;
import fr.groupe40.projet.util.constants.Constants;
import fr.groupe40.projet.util.constants.Direction;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MouseListener {
	private Galaxy galaxy;
	private Scene scene;

	/**
	 *  for the drag&drop, the next x&y position
	 */
	private double orgSceneX, orgSceneY;
	
	/**
	 *  the source and destination planets selected
	 */
	private Planet[] selected_planets = {null, null};
	
	/**
	 *  squads selected by the user
	 */
	private Squad selected_squad;
	
	
	/**
	 *  create the object with the minimal requirement
	 * @param galaxy to check the elements in the board
	 * @param scene linking the handler to this scene
	 */
	public MouseListener(Galaxy galaxy, Scene scene) {
		this.galaxy = galaxy;
		this.scene = scene;
	}

	
	/**
	 *  launch the mouse handler
	 */
	public void launch() {
		/**
		 *  Manage the initial mouse drag event
		 */
		EventHandler<MouseEvent> mousePressedEvent = new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent mouseEvent) {
		        mouseEvent.setDragDetect(true);
		        
	            orgSceneX = mouseEvent.getSceneX();
	            orgSceneY = mouseEvent.getSceneY();
	            
	            
				for(Squad s : galaxy.getSquads()) {
					if(s.squad_selected(orgSceneX, orgSceneY)) {
						if(s.getRuler().getFaction() == Constants.human_faction) {
							selected_squad = s;
						}
					}

				}
				
	            for(Planet p : galaxy.getPlanets()) {
		            if(p.isInside(orgSceneX, orgSceneY)) {
		            	selected_planets[0] = p;
						if(selected_planets[0] == null) {	break;	}
						if(p.getRuler().getFaction() == Constants.human_faction){
							selected_planets[0].getRuler().setSource(p);
							break;
						}else {
							if(Constants.DEBUG) {
								System.out.println("Vous n'etes pas le dirigeant de cette colonie");
							}
						}
					}
				}

	        }
	        
	    };

		/**
		 *  Manage the drop of the mouse
		 *  Shall be optimized, currently have a crazy complexity
		 */
		EventHandler<MouseEvent> mouseDraggedEvent = new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent mouseEvent){
	        	mouseEvent.setDragDetect(false);
	        	
	            double offsetX = mouseEvent.getSceneX();
	            double offsetY = mouseEvent.getSceneY();

	            //Move launched squads
	        	if(selected_squad != null) {	
	    			for(Planet p : galaxy.getPlanets()) {
	    				try {						
	    					if(p.isInside(offsetX, offsetY)) {
	    						selected_squad.update_destination(p);
	    						selected_squad = null;
	    						return;
	    					}

	    				} catch(NullPointerException e) {
	    				//Nothing
	    					selected_squad = null;
	    					return;
	    				}
	    			}
	    			return;
	        	}
	        	
	        	//Select destination planet
	        	if(selected_planets[0] == null) { return; }
				for(Planet p : galaxy.getPlanets()) {
					try {						
						if(p.isInside(offsetX, offsetY)) {
							if(!selected_planets[0].isInside(p)) {
								selected_planets[1] = p;
								selected_planets[0].getRuler().setDestination(p);
							}
							break;
						}

					} catch(NullPointerException e) {
						return;
					}
				}
	        	if(selected_planets[0] == null || selected_planets[1] == null || selected_planets[0].getRuler() != Constants.human_user) {	
	        		return;
	        	}else {
					Squad s = new Squad(Constants.human_user.getPercent_of_troups_to_send(), selected_planets[0], selected_planets[1]);
					//s.sendFleet(source, destination, Constants.human_user.getPercent_of_troups_to_send());
					galaxy.getSquads().add(s);

					selected_planets[0] = null;
					selected_planets[1] = null;
	        	}
	        	
	        }
		};
		
		/**
		 *  Manage the scroll event
		 */
		EventHandler<ScrollEvent> scrollEvent = new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
			    if (event.getDeltaY() < 0) {	//diminution
			        galaxy.clientScrollHandler(Direction.DOWN);
			    } else if (event.getDeltaY() > 0){//augmentation
			        galaxy.clientScrollHandler(Direction.UP);
			    } else {
			    	//Do nothing
			    }
				
			}
			
		};
		


		scene.setOnScroll(scrollEvent);
	    scene.setOnMousePressed(mousePressedEvent);
	    scene.setOnMouseDragged(mouseDraggedEvent);	
	}
}
