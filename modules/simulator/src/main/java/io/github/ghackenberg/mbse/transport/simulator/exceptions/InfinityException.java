package example.simulator.exceptions;

public class InfinityException extends InvalidException {
	
	private static final long serialVersionUID = -2791501699211699362L;

	public InfinityException() {
		super("Infinity reached!");
	}

}
