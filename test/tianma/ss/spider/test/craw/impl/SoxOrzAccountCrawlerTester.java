package tianma.ss.spider.test.craw.impl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tianma.ss.spider.craw.impl.SoxOrzAccountCrawler;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.util.TLog;

public class SoxOrzAccountCrawlerTester {

	private SoxOrzAccountCrawler crawler;
	
	@Before
	public void init() {
		TLog.init();
		crawler = new SoxOrzAccountCrawler();
	}
	
	@Test
	public void test() {
		List<Config> configs = crawler.crawAccounts();
		System.out.println(configs);
	}
	
}
