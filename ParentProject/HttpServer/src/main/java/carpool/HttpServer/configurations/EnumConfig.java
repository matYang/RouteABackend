package carpool.HttpServer.configurations;

import java.util.Calendar;

import carpool.HttpServer.common.DateUtility;


public class EnumConfig {

    public static enum MessageType{
    	ask(0),help(1), both(2);
        public int code;
        MessageType(int code){
            this.code = code;
        }
        private final static MessageType[] map = MessageType.values();
        public static MessageType fromInt(int n){
            return map[n];
        }
    }
    public static enum Gender{
        male(0),female(1),both(2);
        public int code;
        Gender(int code){
            this.code = code;
        }
        private final static Gender[] map = Gender.values();
        public static Gender fromInt(int n){
            return map[n];
        }
    }

    public static enum PaymentMethod{
        offline(0),paypal(1),all(2);
        public int code;
        PaymentMethod(int code){
            this.code = code;
        }
        private final static PaymentMethod[] map = PaymentMethod.values();
        public static PaymentMethod fromInt(int n){
            return map[n];
        }
    }

    //notifications and transactions are more time-sensitive, their states are more related to time and user interactions, event states will be used for their states
    public static enum TransactionState{
        init(0), cancelled(1), aboutToStart(2), finished(3), underInvestigation(4), invalid(5);
        public int code;
        TransactionState(int code){
            this.code = code;
        }
        private final static TransactionState[] map = TransactionState.values();
        public static TransactionState fromInt(int n){
            return map[n];
        }
    }

    public static enum TransactionStateChangeAction{
    	init(0), cancel(1), report(2), evaluate(3);
        public int code;
        TransactionStateChangeAction(int code){
            this.code = code;
        }
        private final static TransactionStateChangeAction[] map = TransactionStateChangeAction.values();
        public static TransactionStateChangeAction fromInt(int n){
            return map[n];
        }
    }

    public static enum NotificationEvent{
        transactionInit(0), transactionCancelled(1), transactionAboutToStart(2),
        transactionEvaluated(3), tranasctionUnderInvestigation(4), transactionReleased(5), watched(6), newLetter(7);
        public int code;
        NotificationEvent(int code){
            this.code = code;
        }
        private final static NotificationEvent[] map = NotificationEvent.values();
        public static NotificationEvent fromInt(int n){
            return map[n];
        }
    }
    
    public static enum NotificationState{
        unread(0), read(1);
        public int code;
        NotificationState(int code){
            this.code = code;
        }
        private final static NotificationState[] map = NotificationState.values();
        public static NotificationState fromInt(int n){
            return map[n];
        }
    }
    
    public static enum NotificationStateChangeActon{
        check(0), delete(1);
        public int code;
        NotificationStateChangeActon(int code){
            this.code = code;
        }
        private final static NotificationStateChangeActon[] map = NotificationStateChangeActon.values();
        public static NotificationStateChangeActon fromInt(int n){
            return map[n];
        }
    }
    


    //states of the user account, more states reserved for future uses
    public static enum UserState{
    	normal(0);
        public int code;
        UserState(int code){
            this.code = code;
        }
        private final static UserState[] map = UserState.values();
        public static UserState fromInt(int n){
            return map[n];
        }
    }

    public static enum UserSearchState{
        universityAsk(0), universityHelp(1), regionAsk(2), regionHelp(3), universityGroupAsk(4), universityGroupHelp(5);
        public int code;
        UserSearchState(int code){
            this.code = code;
        }
        private final static UserSearchState[] map = UserSearchState.values();
        public static UserSearchState fromInt(int n){
            return map[n];
        }
    }
    //states of the message, more states reserved for future uses
    public static enum MessageState{
    	deleted(0),closed(1),open(2);
        public int code;
        MessageState(int code){
            this.code = code;
        }
        private final static MessageState[] map = MessageState.values();
        public static MessageState fromInt(int n){
            return map[n];
        }
    }
    
    public static enum TransactionType{
    	departure(0), arrival(1);
    	public int code;
    	TransactionType(int code){
            this.code = code;
        }
        private final static TransactionType[] map = TransactionType.values();
        public static TransactionType fromInt(int n){
            return map[n];
        }
    }
    
    public static enum DayTimeSlot{
    	n0(0), n1(1), n2(2), n3(3), n4(4), n5(5), n6(6), n7(7), n8(8), n9(9), n10(10), n11(11), n12(12), n13(13), n14(14), n15(15), n16(16), n17(17), n18(18), n19(19), n20(20), n21(21), n22(22), n23(23);
    	public int code;
    	DayTimeSlot(int code){
            this.code = code;
        }
        private final static DayTimeSlot[] map = DayTimeSlot.values();
        public static DayTimeSlot fromInt(int n){
            return map[n];
        }
        public boolean isHourAfter(Calendar cal){
        	long givenHourTime = (cal.getTimeInMillis() % DateUtility.milisecInDay)/DateUtility.milisecInHour;
        	return this.code > givenHourTime;
        }
    }
    
    public static enum LetterType{
    	user(0), system(1);
    	public int code;
    	LetterType(int code){
            this.code = code;
        }
        private final static LetterType[] map = LetterType.values();
        public static LetterType fromInt(int n){
            return map[n];
        }
    }
    
    public static enum LetterState{
    	unread(0), read(1), invalid(2);
    	public int code;
    	LetterState(int code){
    		this.code = code;
    	}
    	private final static LetterState[] map = LetterState.values();
    	public static LetterState fromInt(int n){
    		return map[n];
    	}
    }
    
    public static enum LetterDirection{
    	inbound(0), outbound(1), both(2);
    	public int code;
    	LetterDirection(int code){
    		this.code = code;
    	}
    	private final static LetterDirection[] map = LetterDirection.values();
    	public static LetterDirection fromInt(int n){
    		return map[n];
    	}
    }
    
    public static enum EmailEvent{
    	activeateAccount(0), forgotPassword(1), notification(2);
    	public int code;
    	EmailEvent(int code){
    		this.code = code;
    	}
    	private final static EmailEvent[] map = EmailEvent.values();
    	public static EmailEvent fromInt(int n){
    		return map[n];
    	}
    }
    
    public static enum LicenseType{
    	idCard(0), driverLisence_a(1), driverLisence_b(2), driverLisence_c(3);
    	public int code;
    	LicenseType(int code){
    		this.code = code;
    	}
    	private final static LicenseType[] map = LicenseType.values();
    	public static LicenseType fromInt(int n){
    		return map[n];
    	}
    }
    
    
    public static enum VerificationType{
    	driver(0), passenger(1);
    	public int code;
    	VerificationType(int code){
    		this.code = code;
    	}
    	private final static VerificationType[] map = VerificationType.values();
    	public static VerificationType fromInt(int n){
    		return map[n];
    	}
    }
    
    public static enum VerificationState{
    	pending(0), rejected(1), verified(2), expired(3);
    	public int code;
    	VerificationState(int code){
    		this.code = code;
    	}
    	private final static VerificationState[] map = VerificationState.values();
    	public static VerificationState fromInt(int n){
    		return map[n];
    	}
    }
    
    
    public static enum PassengerVerificationOrigin{
    	passenger(0), driver(1);
    	public int code;
    	PassengerVerificationOrigin(int code){
    		this.code = code;
    	}
    	private final static PassengerVerificationOrigin[] map = PassengerVerificationOrigin.values();
    	public static PassengerVerificationOrigin frontInt(int n){
    		return map[n];
    	}
    	
    }
    
    

    /** -------------------Administrator-------------------**/
    //the temporary admin access code, admin access will be checked against this code instead of user cookies
    public static final String access_admin = "4rkozalh48z1";
    
    public static enum AdminRoutineAction{
    	clearBothDatabase(0), messageClean(1), transactionMonitor(2), cleanAndMonitor(3), reloadLocation(4);
    	public int code;
    	AdminRoutineAction(int code){
            this.code = code;
        }
        private final static AdminRoutineAction[] map = AdminRoutineAction.values();
        public static AdminRoutineAction fromInt(int n){
            return map[n];
        }
    }

}
