package tianma.ss.spider.test.craw.impl;

import tianma.ss.spider.craw.impl.FreeVPNSSAccountCrawler;

public class FreeVPNSSAccountCrawlerTester extends AccountCrawlerTester<FreeVPNSSAccountCrawler>{

	@Override
	protected FreeVPNSSAccountCrawler createAccountCrawler() {
		return new FreeVPNSSAccountCrawler();
	}

}
