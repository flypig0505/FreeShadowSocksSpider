package tianma.ss.spider.craw.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tianma.ss.spider.craw.DefaultAccountCrawler;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.util.TLog;
import tianma.ss.spider.util.TextUtils;

/**
 * getshadowsocks ss账户爬取
 * 
 * @author Tianma
 *
 */
public class GetShadowSocksAccountCrawler extends DefaultAccountCrawler {

	private static String url = "http://getshadowsocks.com/";

	@Override
	public List<Config> crawAccounts() {
		List<Config> configs = new ArrayList<Config>();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {

			HttpGet httpGet = new HttpGet(url);
			if(proxyNeeded()) {
				// Setting proxy
				httpGet.setConfig(getShadowSocksProxy());
			}
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "utf-8");
			Document doc = Jsoup.parse(html);
			Elements sections = doc.getElementsByTag("section");
			for (Element section: sections) {
				Elements eles = section.getElementsByTag("p");
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

	@Override
	public String getUrl() {
		return url;
	}

}
