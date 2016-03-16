package tianma.ss.spider.util;

public final class TextUtils {

	/**
	 * 判断CharSequence是否为空(包含null和"")
	 * @param sequence
	 * @return
	 */
	public static boolean isEmpty(CharSequence sequence) {
		if (sequence == null || sequence.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断CharSequence是否为空白(包含null,"",和空白字符)
	 * @param sequence
	 * @return
	 */
	public static boolean isBlank(CharSequence sequence) {
		if (sequence == null || sequence.length() == 0
				|| sequence.toString().trim().equals("")) {
			return true;
		}
		return false;
	}

}
