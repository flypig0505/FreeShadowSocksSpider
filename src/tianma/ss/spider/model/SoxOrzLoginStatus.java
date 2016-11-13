package tianma.ss.spider.model;

import com.google.gson.annotations.SerializedName;

/**
 * SoxOrz Login Status.
 * @author Tianma
 *
 */
public class SoxOrzLoginStatus {

	// {"code":"1","ok":"1","msg":"\u6b22\u8fce\u56de\u6765"}

	/**
	 * ok的码值为okay
	 */
	public static final int OK_OKAY = 1;

	@SerializedName("ret")
	private int code;
	@SerializedName("msg")
	private String msg;
	
	public SoxOrzLoginStatus() {
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	@Override
	public String toString() {
		return "SoxOrzLoginStatus [code=" + code + ", msg=" + msg + "]";
	}
}
