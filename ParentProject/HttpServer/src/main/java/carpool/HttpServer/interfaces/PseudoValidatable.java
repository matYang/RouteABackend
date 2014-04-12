package carpool.HttpServer.interfaces;

import carpool.HttpServer.exception.validation.ValidationException;

public interface PseudoValidatable {
	
	public boolean validate() throws ValidationException;

}
