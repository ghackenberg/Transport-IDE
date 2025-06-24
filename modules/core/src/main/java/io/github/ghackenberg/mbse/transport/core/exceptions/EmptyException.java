package io.github.ghackenberg.mbse.transport.core.exceptions;

public class EmptyException extends InvalidException {
	
	private static final long serialVersionUID = -2791501699211699362L;

	public EmptyException() {
		super("All batteries are empty!");
	}

}
