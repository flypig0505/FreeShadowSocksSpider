package tianma.ss.spider.craw.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tianma.ss.spider.craw.DefaultAccountCrawler;
import tianma.ss.spider.http.HttpClientsProxy;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.util.TLog;
import tianma.ss.spider.util.TextUtils;

/**
 * free shadowsocks 账户爬取
 * 
 * @author Tianma
 *
 */
public class FreeShadowSocksAccountCrawler extends DefaultAccountCrawler {

	// private static String url = "http://freeshadowsocks.cf/";
	// private static String url = "http://freessr.cf/";
	private static String url = "https://freessr.xyz/";
	
	// ss node state 
	private static final int NORMAL = 0;
	private static final int ABNORMAL = 1;
	

	@Override
	public List<Config> crawAccounts() {
		List<Config> configs = new ArrayList<Config>();
		CloseableHttpClient httpClient = HttpClientsProxy.createSSLClientDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			if (proxyNeeded()) {
				// Setting proxy
				httpGet.setConfig(getShadowSocksProxy());
			}
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "utf-8");
			Document doc = Jsoup.parse(html);
			Elements accounts = doc.getElementsByClass("col-md-6");
			for (Element account : accounts) {
				Elements eles = account.getElementsByTag("h4");
				if (eles == null || eles.size() < 4)
					continue;
				int port = 0;
				try {
					port = Integer.parseInt(getString(eles.get(1).text()));
					if (getState(getString(eles.get(4).text())) == ABNORMAL) // SS node abnormal
						continue;
				} catch (Exception e) {
					TLog.e(e);
					continue;
				}
				String server = getString(eles.get(0).text());
				String password = getString(eles.get(2).text());
				String method = getString(eles.get(3).text());
				configs.add(new Config(server, port, password, method, ""));
			}

		} catch (Exception e) {
			TLog.e("Craw ss accounts failed", e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				TLog.e("Close httpclient failed", e);
			}
		}
		return configs;
	}

	private String getString(String str) {
		if (TextUtils.isEmpty(str))
			return null;
		int index = str.indexOf(':');
		return str.substring(index + 1);
	}
	
	private int getState(String str) {
		String stateStr = getString(str);
		if ("正常".equals(stateStr))
			return NORMAL;
		return ABNORMAL;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
