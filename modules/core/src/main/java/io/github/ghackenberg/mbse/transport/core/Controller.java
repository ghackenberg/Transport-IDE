package io.github.ghackenberg.mbse.transport.core;

import io.github.ghackenberg.mbse.transport.core.entities.Demand;
import io.github.ghackenberg.mbse.transport.core.entities.Segment;
import io.github.ghackenberg.mbse.transport.core.entities.Station;
import io.github.ghackenberg.mbse.transport.core.entities.Vehicle;

public interface Controller {

	/**
	 * Decide whether to pickup the demand or not.
	 * 
	 * @param vehicle The vehicle for potential pickup.
	 * @param demand The demand to pickup.
	 * 
	 * @return True if pickup, false otherwise.
	 */
	public boolean selectDemand(Vehicle vehicle, Demand demand);
	
	/**
	 * Decide whether to charge battery at station or not.
	 * 
	 * @param vehicle The vehicle for potential battery charge.
	 * @param station The station where to charge.
	 * 
	 * @return True if charging, false otherwise.
	 */
	public boolean selectStation(Vehicle vehicle, Station station);

	/**
	 * Decide whether to stop charging battery at station or not.
	 * 
	 * @param vehicle The vehicle for potential battery charge stop.
	 * 
	 * @return True if charging, false otherwise.
	 */
	public boolean unselectStation(Vehicle vehicle);
	
	/**
	 * Select the upcoming vehicle speed.
	 * 
	 * @param vehicle The vehicle for speed update.
	 * 
	 * @return The new speed.
	 */
	public double selectSpeed(Vehicle vehicle);
	
	/**
	 * Select the maximum timeout for new speed decision.
	 * 
	 * @param vehicle The vehicle for which the timeout holds.
	 * 
	 * @return The timeout in milliseconds of simulation time.
	 */
	public double selectMaximumSpeedSelectionTimeout(Vehicle vehicle);
	
	/**
	 * Select the maximum timeout for new station decision.
	 * 
	 * @param vehicle The vehicle for which the timeout holds.
	 * 
	 * @return The timeout in milliseconds of simulation time.
	 */
	public double selectMaximumStationSelectionTimeout(Vehicle vehicle);
	
	/**
	 * Select the next segment after reaching the end of the previous segment.
	 * 
	 * @param vehicle The vehicle for which the routing decision should be taken.
	 * 
	 * @return The selected segment.
	 */
	public Segment selectSegment(Vehicle vehicle);
	
	public void reset();
	
}
