package tianma.ss.spider.test.craw.impl;

import tianma.ss.spider.craw.impl.TianmaProAccountCrawler;

public class TianmaProAccountCrawlerTester extends AccountCrawlerTester<TianmaProAccountCrawler>{

	@Override
	protected TianmaProAccountCrawler createAccountCrawler() {
		return new TianmaProAccountCrawler();
	}

}
