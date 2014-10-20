package presentation.exceptions;

import presentation.objects.Block;

public class UnhandledBlockDataException extends Exception {
	private static final long serialVersionUID = 4305693055737307775L;

	public UnhandledBlockDataException(String name, Block b) {
		super("Unhandled block data for " + name + ": " + b);
	}
}
