package example.simulator.exceptions;

public abstract class InvalidException extends Exception {
	
	private static final long serialVersionUID = -3031941252913610428L;
	
	public InvalidException(String message) {
		super(message);
	}

}
