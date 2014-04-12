package carpool.HttpServer.exception.location;

import carpool.HttpServer.exception.PseudoException;

public class LocationNotFoundException extends PseudoException {
	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "LocationNotFound";

	public LocationNotFoundException(){
        super("对不起，您要找的地理位置不存在");
    }
	
	public LocationNotFoundException(String exceptionText){
		super(exceptionText);
	}
	@Override
	public int getCode() {
        return 16;
    }
}
