package tianma.ss.spider.http;

import org.apache.http.client.CookieStore;

/**
 * This class used for manage the CookieStore
 * 
 * @author Tianma
 *
 */
public final class CookieStoreManager {
	private ThreadLocal<CookieStore> threadLocal;

	private CookieStoreManager() {
		threadLocal = new ThreadLocal<CookieStore>();
	}

	public synchronized CookieStore getCookieStore() {
		return threadLocal.get();
	}

	public synchronized void setCookieStore(CookieStore cookieStore) {
		if (cookieStore != null)
			threadLocal.set(cookieStore);
	}

	public static final CookieStoreManager getInstance() {
		return CookieStoreManagerHolder.sInstance;
	}

	private static class CookieStoreManagerHolder {
		public static CookieStoreManager sInstance = new CookieStoreManager();
	}
}
