package gui.exceptions;

import gui.objects.Block;

public class UnhandledBlockIdException extends Exception {
	private static final long serialVersionUID = 5391290880978962287L;

	public UnhandledBlockIdException(Block b) {
		super("Unhandled block: " + b);
	}
}
