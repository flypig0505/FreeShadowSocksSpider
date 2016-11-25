package tianma.ss.spider.test.craw.impl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tianma.ss.spider.craw.AccountCrawler;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.util.TLog;

abstract public class AccountCrawlerTester<T extends AccountCrawler> {

	private T crawler;

	@Before
	public void init() {
		TLog.init();
		crawler = createAccountCrawler();
	}
	
	protected abstract T createAccountCrawler();

	@Test
	public void test() {
		List<Config> configs = crawler.crawAccounts();
		System.out.println(configs);
	}
	
}
