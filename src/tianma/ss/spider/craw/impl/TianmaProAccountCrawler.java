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

/**
 * tianma.pro 账户爬取
 * 
 * @author Tianma
 *
 */
public class TianmaProAccountCrawler extends DefaultAccountCrawler {

	private static String url = "http://tianma.pro/post/402cab53/";

	@Override
	public List<Config> crawAccounts() {
		List<Config> configs = new ArrayList<Config>();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			if (proxyNeeded()) {
				// Setting Proxy
				httpGet.setConfig(getShadowSocksProxy());
			}

			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "utf-8");
			Document doc = Jsoup.parse(html);
			// Element tableEle = doc.getElementById("free-shadowsocks-account");
			// Elements trEles = tableEle.getElementsByTag("tr");
			Elements trEles = doc.select("#free-shadowsocks-account tr");
			for (Element trEle : trEles) {
				Elements eles = trEle.getElementsByTag("td");
				if (eles == null || eles.isEmpty()) // <tr></tr>
					continue;
				int port = 0;
				try {
					port = Integer.parseInt(eles.get(1).text());
				} catch (Exception e) {
					TLog.e(e);
					continue;
				}
				String server = eles.get(0).text();
				String password = eles.get(2).text();
				String method = eles.get(3).text();
				configs.add(new Config(server, port, password, method, ""));
			}

		} catch (Exception e) {
			TLog.e(e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				TLog.e("Close httpclient failed", e);
			}
		}
		return configs;
	}

	@Override
	public String getUrl() {
		return url;
	}

}
