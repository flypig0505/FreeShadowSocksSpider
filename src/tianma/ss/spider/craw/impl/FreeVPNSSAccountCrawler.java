package tianma.ss.spider.craw.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tianma.ss.spider.craw.AccountCrawler;
import tianma.ss.spider.http.HttpClientsProxy;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.util.TextUtils;

/**
 * freevpnss ShadowSocks账户爬取
 * 
 * @author Tianma
 *
 */
public class FreeVPNSSAccountCrawler implements AccountCrawler {

	// private static String url = "https://www.freevpnss.net/";
	private static String url = "http://i.freevpnss.com/";

	@Override
	public List<Config> crawAccounts() {
		System.out.println(url);
		List<Config> configs = new ArrayList<Config>();
		CloseableHttpClient httpClient = HttpClientsProxy.createSSLClientDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "utf-8");
			Document doc = Jsoup.parse(html);
			Elements accounts = doc.getElementsByClass("panel-body");
			for (Element account : accounts) {
				Elements eles = account.getElementsByTag("p");
				int port = 0;
				try {
					port = Integer.parseInt(getString(eles.get(1).text()));
				} catch (Exception ignore) {
					continue;
				}
				String server = getString(eles.get(0).text());
				String password = getString(eles.get(2).text());
				String method = getString(eles.get(3).text());
				configs.add(new Config(server, port, password, method, ""));
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("-----------------------------");
		return configs;
	}

	private String getString(String str) {
		if (TextUtils.isEmpty(str))
			return null;
		int index = str.indexOf('：');
		return str.substring(index + 1);
	}

}
