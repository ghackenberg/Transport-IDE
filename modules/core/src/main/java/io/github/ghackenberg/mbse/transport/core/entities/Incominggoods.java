package io.github.ghackenberg.mbse.transport.core.entities;

// Import der Location-Klasse (Position auf einem Segment)
import io.github.ghackenberg.mbse.transport.core.structures.Location;

// JavaFX Properties (für automatische Updates im UI)
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Incominggoods {
	
	// 🔹 Innere Klasse für den SIMULATIONSZUSTAND
	// → getrennt vom statischen Modell (sehr wichtig!)
	public class State {
		
		// Referenz auf das Vehicle, das dieses Incominggoods gerade transportiert
		// → null = liegt noch irgendwo rum / wartet
		public Vehicle vehicle = null;
		
	}
	
	// 🔹 ThreadLocal = jeder Simulationsthread hat seinen eigenen Zustand
	// → wichtig für parallele Simulationen
	public final ThreadLocal<State> state = new ThreadLocal<>();

	// Properties
	
	// Geschwindigkeit des Incominggoods (z. B. Förderband / Verarbeitung)
	// JavaFX Property → UI kann automatisch darauf reagieren
	public final DoubleProperty speed = new SimpleDoubleProperty();
	

	// Structures
	
	// Position des Objekts im Netzwerk
	// enthält:
	// - Segment
	// - Distanz auf dem Segment
	// - Lane
	public final Location location = new Location();
	
	// Constructors
	
	public Incominggoods() {
		// Lane = -1 bedeutet:
		// → nicht auf einer bestimmten Spur
		// → Standardwert / "egal"
		location.lane.set(-1);
	}
	
	// Methods
	
	@Override
	public String toString() {
		// Gibt die Position als String zurück (für Debugging / Anzeige)
		return location.toString();
	}

}
