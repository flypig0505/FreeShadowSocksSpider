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

/**
 * getshadowsocks ss账户爬取
 * @author Tianma
 *
 */
public class GetShadowSocksAccountCrawler implements AccountCrawler {

	private static String url = "http://getshadowsocks.com/";
	
	@Override
	public List<Config> crawAccounts() {
		System.out.println(url);
		List<Config> configs = new ArrayList<Config>();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {

			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity, "utf-8");
			Document doc = Jsoup.parse(html);
			Element panelEle = doc.getElementsByClass("panel").first();
			if (panelEle != null) {
				Elements eles = panelEle.getElementsByTag("input");
				String server = eles.get(0).attr("value");
				int port = Integer.parseInt(eles.get(1).attr("value"));
				String password = eles.get(2).attr("value");
				String method = eles.get(3).attr("value");
				configs.add(new Config(server, port, password, method, ""));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("-----------------------------");
		return configs;
	}

}
