package tianma.ss.spider.model;

public class SoxOrzLoginStatus {

	// {"code":"1","ok":"1","msg":"\u6b22\u8fce\u56de\u6765"}

	/**
	 * ok的码值为okay
	 */
	public static final int OK_OKAY = 1;

	private int code;
	private int ok;
	private String msg;

	public int getCode() {
		return code;
	}

	public int getOk() {
		return ok;
	}

	public String getMsg() {
		return msg;
	}

	public SoxOrzLoginStatus() {
	}

	@Override
	public String toString() {
		return "LoginStatus [code=" + code + ", ok=" + ok + ", msg=" + msg + "]";
	}

}
