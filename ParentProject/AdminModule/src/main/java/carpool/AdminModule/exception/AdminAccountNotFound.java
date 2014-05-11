package carpool.AdminModule.exception;

import carpool.HttpServer.exception.PseudoException;

@SuppressWarnings("serial")
public class AdminAccountNotFound extends PseudoException {

	protected String exceptionType = "IdentityVerificationNotFound";

	public AdminAccountNotFound(){
        super("对不起，您要找的用户认证不存在");
    }
	
	public AdminAccountNotFound(String exceptionText){
        super(exceptionText);
    }

	@Override
    public int getCode() {
        return -1;
    }
}
