package tianma.ss.spider.craw;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;

/**
 * Default Account Crawler.
 * @author Tianma
 *
 */
abstract public class DefaultAccountCrawler implements AccountCrawler{

	/**
	 * Provide a method of getting the instance of ShadowSocks Proxy.
	 * @return
	 */
	protected RequestConfig getShadowSocksProxy() {
		HttpHost proxy = new HttpHost("127.0.0.1", 1080, "http");
		return RequestConfig.custom().setProxy(proxy).build();
	}
	
	/**
	 * Default proxyNeeded is false.
	 */
	@Override
	public boolean proxyNeeded() {
		return false;
	}
	
}
