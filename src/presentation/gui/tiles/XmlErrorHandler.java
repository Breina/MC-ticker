package presentation.gui.tiles;

import logging.Log;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlErrorHandler implements ErrorHandler {
	
	public XmlErrorHandler() {}
	
	private String generateMessage(SAXParseException exception) {
		return exception.getSystemId() + ", line:" + exception.getLineNumber() + "; " + exception.getMessage();
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		Log.w(generateMessage(exception));
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		Log.w(generateMessage(exception));
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		Log.e(generateMessage(exception));
		throw exception;
	}

}
