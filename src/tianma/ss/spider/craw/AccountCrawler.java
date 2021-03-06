package tianma.ss.spider.craw;

import java.util.List;

import tianma.ss.spider.model.Config;

/**
 * ShadowSocks账户爬虫
 * 
 * @author Tianma
 *
 */
public interface AccountCrawler {

	public static final int HTTPS_PORT = 443;

	/**
	 * 爬取ShadowSocks账户
	 * 
	 * @return
	 */
	List<Config> crawAccounts();
	
	/**
	 * 获取ShadowSocks爬虫页面URL
	 * @return
	 */
	String getUrl();
	
	/**
	 * 在当前网站爬取ShadowSocks账户时,是否需要使用代理
	 * @return
	 */
	boolean proxyNeeded();

}
