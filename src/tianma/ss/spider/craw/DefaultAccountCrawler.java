package tianma.ss.spider.craw;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;

abstract public class DefaultAccountCrawler implements AccountCrawler{

	protected RequestConfig getShadowSocksProxy() {
		HttpHost proxy = new HttpHost("127.0.0.1", 1080, "http");
		return RequestConfig.custom().setProxy(proxy).build();
	}
	
	@Override
	public boolean proxyNeeded() {
		return false;
	}
	
}
