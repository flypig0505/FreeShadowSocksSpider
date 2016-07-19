package tianma.ss.spider.craw.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tianma.ss.spider.craw.AccountCrawler;
import tianma.ss.spider.http.CookieStoreManager;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.model.SoxOrzAccount;
import tianma.ss.spider.model.SoxOrzLoginStatus;
import tianma.ss.spider.util.CharacterUtil;
import tianma.ss.spider.util.TextUtils;

/**
 * SOXOrz ShadowSocks账户爬取
 * 
 * @author Tianma
 *
 */
public class SoxOrzAccountCrawler implements AccountCrawler {

	public static final String ROOT_URL = "http://www.vwp123.com/user/";
	public static final String HREF_PREFIX = "node_json.php";

	private CookieStoreManager cookieStoreManager;

	private List<SoxOrzAccount> accounts;

	public SoxOrzAccountCrawler() {
		init();
	}

	/**
	 * 登陆的Handler
	 */
	private ResponseHandler<SoxOrzLoginStatus> loginHandler = new ResponseHandler<SoxOrzLoginStatus>() {

		@Override
		public SoxOrzLoginStatus handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				if (entity == null)
					return null;
				String res = EntityUtils.toString(entity);
				Gson gson = new Gson();
				SoxOrzLoginStatus loginStatus = gson.fromJson(res, SoxOrzLoginStatus.class);
				return loginStatus;
			}
			return null;
		}
	};

	private void init() {
		cookieStoreManager = CookieStoreManager.getInstance();
		// 加载配置文件信息
		try {
			File dir = new File(""); // 获取当前路径
			dir = new File(dir.getAbsolutePath());
			File[] propFiles = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (TextUtils.isEmpty(name))
						return false;
					if (name.startsWith("SoxOrzCrawler") && name.endsWith(".properties"))
						return true;
					return false;
				}
			});
			accounts = new ArrayList<SoxOrzAccount>();
			for (File propFile : propFiles) {
				InputStream in = new FileInputStream(propFile);
				Properties prop = new Properties();
				SoxOrzAccount account = new SoxOrzAccount();
				// 加载配置文件数据到Properties对象中去
				prop.load(in);
				account.setEmail(prop.getProperty("email"));
				account.setPassword(prop.getProperty("password"));
				accounts.add(account);
			}
			// 随机乱序,让各个账号都有机会使用
			Collections.shuffle(accounts);
		} catch (IOException e) {
			System.out.println("初始化失败,请检查SoxOrzAccount*.properties配置文件");
			// 初始化失败,抛出异常
			throw new ExceptionInInitializerError(e);
		}
	}

	@Override
	public List<Config> crawAccounts() {
		System.out.println(ROOT_URL);
		boolean needSpiltLine = false;
		boolean needParse = false; // 是否有必要继续解析
		
		List<Config> configs = new ArrayList<Config>();
		
		for (SoxOrzAccount account : accounts) {
			if (needSpiltLine) {
				System.out.println("\n");
			} else {
				needSpiltLine = true;
			}
			System.out.println("Current email : " + account.getEmail());
			// 登陆
			boolean loginSuccess = login(account);
			if (!loginSuccess) {
				System.out.println("Login Failed");
				continue;
			}
			needParse = true;
			System.out.println("Login Success");
			// 签到
			checkin();
			
			if (needParse) {
				List<String> urls = parseAllNodeUrl();
				configs.addAll(parseAllConfigs(urls));
			}
			
		}

		System.out.println("-----------------------------");
		return configs;
	}

	/**
	 * Login SOXorz
	 * 
	 * @param account
	 *            SoxOrz账户
	 * 
	 * @return ture if login success, false if not
	 */
	public boolean login(SoxOrzAccount account) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CookieStore cookieStore = new BasicCookieStore();

			HttpClientContext localContext = HttpClientContext.create();
			localContext.setCookieStore(cookieStore);

			HttpPost httpPost = new HttpPost(ROOT_URL + "_login.php");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("email", account.getEmail()));
			nvps.add(new BasicNameValuePair("passwd", account.getPassword()));
			nvps.add(new BasicNameValuePair("remember_me", "false"));
			nvps.add(new BasicNameValuePair("verify", ""));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			SoxOrzLoginStatus loginStatus = httpClient.execute(httpPost, loginHandler, localContext);
			if (loginStatus.getOk() == SoxOrzLoginStatus.OK_OKAY) {
				cookieStoreManager.setCookieStore(cookieStore);
				return true;
			} else {
				System.out.println(loginStatus.getMsg());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 签到
	 * 
	 * @return true if check in, false if not.
	 */
	public boolean checkin() {
		boolean result = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CookieStore cookieStore = cookieStoreManager.getCookieStore();

			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);

			HttpGet httpGet = new HttpGet(ROOT_URL + "_checkin.php");
			HttpResponse response = httpClient.execute(httpGet, context);

			HttpEntity entity = response.getEntity();
			String checkinJson = EntityUtils.toString(entity);
			JsonObject jsonObject = new JsonParser().parse(checkinJson).getAsJsonObject();
			String msg = jsonObject.get("msg").getAsString();
			msg = CharacterUtil.gbDecoding(msg);
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(msg);
			boolean hasDigit = false; // 字符串中是否含有数字：(获得了128MB流量),含有数字表示签到成功,不含有数字表示已签到
			while (m.find()) {
				hasDigit = true;
				break;
			}
			if (hasDigit) {
				System.out.println("签到成功," + msg);
			} else {
				System.out.println("重复签到," + msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("签到失败");
		}
		return result;
	}

	/**
	 * 爬取网页，获取对应的节点数据
	 */
	private List<String> parseAllNodeUrl() {
		List<String> nodeUrls = new ArrayList<>();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CookieStore cookieStore = cookieStoreManager.getCookieStore();

			HttpClientContext localContext = HttpClientContext.create();
			localContext.setCookieStore(cookieStore);

			HttpGet httpGet = new HttpGet(ROOT_URL + "node.php");

			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity);

			Document doc = Jsoup.parse(html);
			Elements eleList = doc.getElementsByClass("dropdown-menu");
			for (Element ele : eleList) {
				Elements eleList2 = ele.getElementsByTag("a");
				for (Element element : eleList2) {
					String href = element.attr("href");
					if (!TextUtils.isBlank(href) && href.startsWith(HREF_PREFIX)) {
						String nodeUrl = ROOT_URL + href;
						nodeUrls.add(nodeUrl);
					}
				}
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
		return nodeUrls;
	}

	/**
	 * 解析所有的Url对应的Config对象,并以List的形式返回
	 */
	private List<Config> parseAllConfigs(List<String> nodeUrls) {
		List<Config> configs = new ArrayList<Config>();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CookieStore cookieStore = cookieStoreManager.getCookieStore();

			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);

			JsonParser parser = new JsonParser();
			for (String url : nodeUrls) {
				HttpGet httpGet = new HttpGet(url);
				HttpResponse response = httpClient.execute(httpGet, context);
				HttpEntity entity = response.getEntity();
				String json = EntityUtils.toString(entity);
				JsonElement ele = parser.parse(json);
				JsonObject jsonObject = ele.getAsJsonObject();
				String server = jsonObject.get("server").getAsString();
				if (TextUtils.isBlank(server)) {
					continue;
				}
				int server_port = jsonObject.get("server_port").getAsInt();
				String password = jsonObject.get("password").getAsString();
				String method = jsonObject.get("method").getAsString();
				configs.add(new Config(server, server_port, password, method, ""));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return configs;
	}
}
