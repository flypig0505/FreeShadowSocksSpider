package tianma.ss.spider.craw.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
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
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tianma.ss.spider.craw.DefaultAccountCrawler;
import tianma.ss.spider.http.CookieStoreManager;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.model.SoxOrzAccount;
import tianma.ss.spider.model.SoxOrzLoginStatus;
import tianma.ss.spider.util.CharacterUtil;
import tianma.ss.spider.util.TLog;
import tianma.ss.spider.util.TextUtils;

/**
 * SOXOrz ShadowSocks账户爬取
 * 
 * @author Tianma
 *
 */
public class SoxOrzAccountCrawler extends DefaultAccountCrawler {

	private static final String ROOT_URL = "http://www.vwp123.com";

	/** login action */
	private static final String ACTION_LOGIN = "/auth/login";
	/** check in action */
	private static final String ACTION_CHECK_IN = "/user/checkin";
	/** get node list action */
	private static final String ACTION_NODE_LIST = "/user/node";
	/** user home page */
	private static final String ACTION_USER_HOME = "/user";

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
			TLog.e("初始化失败,请检查SoxOrzAccount*.properties配置文件");
			// 初始化失败,抛出异常
			throw new ExceptionInInitializerError(e);
		}
	}

	@Override
	public List<Config> crawAccounts() {
		boolean needSpliLine = false;

		List<Config> configs = new ArrayList<Config>();

		for (SoxOrzAccount account : accounts) {
			if (needSpliLine) {
				TLog.i("");
			} else {
				needSpliLine = true;
			}
			TLog.i("Current email : " + account.getEmail());
			// 登陆
			boolean loginSuccess = login(account);
			if (!loginSuccess) {
				TLog.i("Login Failed");
				continue;
			}
			TLog.i("Login Success");
			// 签到
			checkin();
			String[] portAndPwd = parsePortAndPwd();
			if (portAndPwd != null) {
				try{
					configs = parseAllSSNodes(Integer.parseInt(portAndPwd[0]), portAndPwd[1]);
				}catch(Exception e) {
					TLog.e(e);
				}
			}
		}
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

			HttpPost httpPost = new HttpPost(ROOT_URL + ACTION_LOGIN);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("email", account.getEmail()));
			nvps.add(new BasicNameValuePair("passwd", account.getPassword()));
			nvps.add(new BasicNameValuePair("remember_me", "false"));
			nvps.add(new BasicNameValuePair("code", ""));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			if (proxyNeeded()) {
				// Setting proxy
				httpPost.setConfig(getShadowSocksProxy());
			}

			SoxOrzLoginStatus loginStatus = httpClient.execute(httpPost, loginHandler, localContext);
			if (loginStatus == null) {
				TLog.e("Access refused, login failed");
			} else if (loginStatus.getCode() == SoxOrzLoginStatus.OK_OKAY) {
				cookieStoreManager.setCookieStore(cookieStore);
				return true;
			} else {
				TLog.e(loginStatus.getMsg());
			}
		} catch (IOException e) {
			TLog.e(e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				TLog.e("Close httpclient failed", e);
			}
		}
		return false;
	}

	/**
	 * 签到
	 * 
	 * @return true if checked in, false if not.
	 */
	public boolean checkin() {
		boolean result = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CookieStore cookieStore = cookieStoreManager.getCookieStore();

			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);

			HttpPost httpPost = new HttpPost(ROOT_URL + ACTION_CHECK_IN);
			if (proxyNeeded()) {
				// Setting proxy
				httpPost.setConfig(getShadowSocksProxy());
			}
			HttpResponse response = httpClient.execute(httpPost, context);

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
				TLog.i("Check in success: " + msg);
			} else {
				TLog.w("Check in repeated: " + msg);
			}
			result = true;
		} catch (IOException e) {
			TLog.e("Check in failed", e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				TLog.e("Close httpclient failed", e);
			}
		}
		return result;
	}

	/**
	 * 解析出ss账户的端口和密码，以数组形式返回， s[0]: port, s[1]: password，解析失败则返回null
	 * 
	 * @return
	 */
	private String[] parsePortAndPwd() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CookieStore cookieStore = cookieStoreManager.getCookieStore();

			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);

			HttpGet httpGet = new HttpGet(ROOT_URL + ACTION_USER_HOME);
			if (proxyNeeded()) {
				// Setting proxy
				httpGet.setConfig(getShadowSocksProxy());
			}
			HttpResponse response = httpClient.execute(httpGet, context);
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity);

			Document doc = Jsoup.parse(html);
			Elements eles = doc.select("dl.dl-horizontal:contains(端口) > dd");
			if (eles.size() >= 2) {
				String[] result = new String[2];
				for (int i = 0; i < 2; i++) {
					result[i] = eles.get(i).text();
				}
				TLog.i("Parse port and password success");
				return result;
			}
		} catch (IOException e) {
			TLog.e(e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				TLog.e("Close httpclient failed", e);
			}
		}
		TLog.i("Parse port and password failed");
		return null;
	}
	
	/**
	 * 解析所有的Url对应的Config对象,并以List的形式返回
	 */
	private List<Config> parseAllSSNodes(int port, String password) {
		List<Config> configs = new ArrayList<Config>();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CookieStore cookieStore = cookieStoreManager.getCookieStore();

			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);
			
				HttpGet httpGet = new HttpGet(ROOT_URL + ACTION_NODE_LIST);
				if (proxyNeeded()) {
					// Setting proxy
					httpGet.setConfig(getShadowSocksProxy());
				}
				HttpResponse response = httpClient.execute(httpGet, context);
				HttpEntity entity = response.getEntity();
				String html = EntityUtils.toString(entity);
				Document doc = Jsoup.parse(html);
				
				Elements servers = doc.select("p:contains(地址：)");
				Elements methods = doc.select("p:contains(加密方式：)");
				
				if(servers == null) {
					TLog.e("Parse ss server list failed");
				} else if (methods == null) {
					TLog.e("Parse ss method list failed");
				} else if (servers.size() != methods.size()) {
					TLog.e("SS server list don't match method list");
				} else {
					for(int i = 0; i < servers.size(); i++) {
						String server = getString(servers.get(i).text());
						String method = getString(methods.get(i).text());
						if (server == null || method == null) {
							TLog.e("server or method is empty");
						} else {
							configs.add(new Config(server, port, password, method, ""));
						}
					}
				}
		} catch (IOException e) {
			TLog.e("Parse all ss configs failed", e);
		}
		return configs;
	}

	private String getString(String str) {
		if (TextUtils.isEmpty(str))
			return null;
		int index = str.indexOf('：');
		if (index != -1){
			return str.substring(index + 1).trim();
		}
		return str.trim();
	}
	
	@Override
	public String getUrl() {
		return ROOT_URL;
	}

	@Override
	public boolean proxyNeeded() {
		return true;
	}
}
