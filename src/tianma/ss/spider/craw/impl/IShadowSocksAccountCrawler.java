package tianma.ss.spider.craw.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tianma.ss.spider.craw.AccountCrawler;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.util.TextUtils;

/**
 * IShadowSocks ShadowSocks账户爬取
 * 
 * @author Tianma
 *
 */
public class IShadowSocksAccountCrawler implements AccountCrawler {

	private static String url = "http://www.ishadowsocks.net/";

	@Override
	public List<Config> crawAccounts() {
		List<Config> configs = new ArrayList<Config>();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {

			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "utf-8");
			Document doc = Jsoup.parse(html);
			Element freeEle = doc.getElementById("free");
			Elements accounts = freeEle.getElementsByClass("col-lg-4");
			for (Element account : accounts) {
				Elements eles = account.getElementsByTag("h4");
				int port = 0;
				try {
					port = Integer.parseInt(getString(eles.get(1).text()));
				} catch (Exception ignore) {
					continue;
				}
				String server = getString(eles.get(0).text());
				// int port = Integer.parseInt(getString(eles.get(1).text()));
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
		return configs;
	}

	private String getString(String str) {
		if (TextUtils.isEmpty(str))
			return null;
		int index = str.indexOf(':');
		return str.substring(index + 1);
	}

	@Override
	public String getUrl() {
		return url;
	}

}
