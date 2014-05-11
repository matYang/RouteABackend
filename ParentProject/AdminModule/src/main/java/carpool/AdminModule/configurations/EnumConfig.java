package carpool.AdminModule.configurations;

public class EnumConfig {

	public static enum AdminStatus{
		activated(0),deactivated(1),deleted(2);
		public int code;
		AdminStatus(int code){
			this.code = code;
		}
		private final static AdminStatus[] map = AdminStatus.values();
		public static AdminStatus fromInt(int n){
			return map[n];
		}
	}

	public static enum AdminPrivilege{
		first(0),business(1),economy(2);
		public int code;
		AdminPrivilege(int code){
			this.code = code;
		}
		private final static AdminPrivilege[] map = AdminPrivilege.values();
		public static AdminPrivilege fromInt(int n){
			return map[n];
		}
	}
}
