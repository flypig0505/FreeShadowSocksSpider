package tianma.ss.spider.test.craw.impl;

import tianma.ss.spider.craw.impl.IShadowSocksAccountCrawler;

public class IShadowSocksAccountCrawlerTester extends AccountCrawlerTester<IShadowSocksAccountCrawler>{

	@Override
	protected IShadowSocksAccountCrawler createAccountCrawler() {
		return new IShadowSocksAccountCrawler();
	}
}
