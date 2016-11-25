package tianma.ss.spider.test.craw.impl;

import tianma.ss.spider.craw.impl.SoxOrzAccountCrawler;

public class SoxOrzAccountCrawlerTester extends AccountCrawlerTester<SoxOrzAccountCrawler>{

	@Override
	protected SoxOrzAccountCrawler createAccountCrawler() {
		return new SoxOrzAccountCrawler();
	}
	
}
