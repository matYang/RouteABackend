package carpool.HttpServer.exception.user;

import carpool.HttpServer.exception.PseudoException;

public class UserNotFoundException extends PseudoException {
	
	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "UserNotFound";

	public UserNotFoundException(){
        super("对不起，您要找的用户不存在");
    }
	
	public UserNotFoundException(String exceptionText){
        super(exceptionText);
    }

	@Override
    public int getCode() {
        return 1;
    }
	
}