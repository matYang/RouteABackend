package carpool.HttpServer.exception.validation;

import carpool.HttpServer.exception.PseudoException;


public class EntityTooLargeException extends PseudoException {

	private static final long serialVersionUID = 1L;
	
	protected String exceptionType = "Entity too large";

	public EntityTooLargeException(){
        super("发布内容过大，请删减信息内容");
    }
	
	public EntityTooLargeException(String exceptionText){
		super(exceptionText);
	}
	
	@Override
    public int getCode() {
        return 15;
    }
	
}