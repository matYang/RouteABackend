package carpool.HttpServer.asyncTask;
import java.util.ArrayList;
import carpool.HttpServer.carpoolDAO.CarpoolDaoMessage;
import carpool.HttpServer.exception.location.LocationNotFoundException;
import carpool.HttpServer.exception.user.UserNotFoundException;
import carpool.HttpServer.interfaces.PseudoAsyncTask;
import carpool.HttpServer.model.Message;
import carpool.HttpServer.model.representation.SearchRepresentation;


public class MyTestTask implements PseudoAsyncTask{
	private SearchRepresentation SR;
	private ArrayList<Message> mlist = new ArrayList<Message>();
	private void printlist(ArrayList<Message>list, String which){
		for(int i=0; i<list.size(); i++){
			System.out.println(which+": "+list.get(i).getMessageId());
		}
	}
	private boolean ArrayListEquals(ArrayList<Message> list){
		for(int i=0; i<list.size(); i++){
			if(!list.get(i).equals(mlist.get(i))){
				return false;
			}
		}
		return true;
	}
	public MyTestTask(SearchRepresentation sr,ArrayList<Message> list){
		this.SR = sr;
		this.mlist = list;
	}

	@Override
	public boolean execute() {
		return MyRunTask();
	}

	public boolean MyRunTask(){		
		try {
			ArrayList<Message> testlist = CarpoolDaoMessage.searchMessage(SR);
			if(ArrayListEquals(testlist)){
				//Passed;
			}else{
				//printlist(mlist,"mlist");
				//printlist(testlist,"testlist");
				throw new RuntimeException("Message is not found");
			}
			
		} catch (UserNotFoundException | LocationNotFoundException e) {				
			e.printStackTrace();
			throw new RuntimeException("Thread 挂了");
		}
		return true;
	}
}
