package tianma.ss.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tianma.ss.spider.craw.AccountCrawler;
import tianma.ss.spider.craw.impl.FreeShadowSocksAccountCrawler;
import tianma.ss.spider.craw.impl.FreeVPNSSAccountCrawler;
import tianma.ss.spider.craw.impl.GetShadowSocksAccountCrawler;
import tianma.ss.spider.craw.impl.IShadowSocksAccountCrawler;
import tianma.ss.spider.craw.impl.SoxOrzAccountCrawler;
import tianma.ss.spider.craw.impl.TianmaProAccountCrawler;
import tianma.ss.spider.model.Config;
import tianma.ss.spider.model.SSProgramConfig;
import tianma.ss.spider.model.ShadowSocksConfig;
import tianma.ss.spider.util.TLog;
import tianma.ss.spider.util.TextUtils;

/**
 * This is a web crawler which can grab the related shadowsocks's node
 * information.
 * 
 * @author Tianma
 * @Date: 2016年1月5日 上午12:34:30
 */
public class ShadowSocksSpider {

	private static String projectURL = "https://github.com/tianma8023/FreeShadowSocksSpider";

	private SSProgramConfig programConfig;

	@SuppressWarnings("serial")
	private static List<AccountCrawler> crawlers = new ArrayList<AccountCrawler>() {
		{
			this.add(new FreeVPNSSAccountCrawler());
			this.add(new IShadowSocksAccountCrawler());
			this.add(new GetShadowSocksAccountCrawler());
			this.add(new FreeShadowSocksAccountCrawler());
			this.add(new SoxOrzAccountCrawler());
			this.add(new TianmaProAccountCrawler());
		}
	};

	private ShadowSocksSpider() {
		init();
	}

	private void init() {
		// 加载配置文件信息
		try {
			File dir = new File(""); // 获取当前路径
			dir = new File(dir.getAbsolutePath());
			File[] propFiles = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (TextUtils.isEmpty(name))
						return false;
					if (name.startsWith("SSProgram") && name.endsWith(".properties"))
						return true;
					return false;
				}
			});
			for (File propFile : propFiles) {
				InputStream in = new FileInputStream(propFile);
				Properties prop = new Properties();
				programConfig = new SSProgramConfig();
				// 加载配置文件数据到Properties对象中去
				prop.load(in);
				programConfig.setShadowSocksDir(prop.getProperty("shadowSocksDir"));
				programConfig.setConfigName(prop.getProperty("configName", "gui-config.json"));
				programConfig.setExeName(prop.getProperty("exeName", "Shadowsocks.exe"));
				break;
			}
		} catch (IOException e) {
			TLog.e("初始化失败,请检查SoxOrzAccount*.properties配置文件");
			// 初始化失败,抛出异常
			throw new ExceptionInInitializerError(e);
		}
	}

	public static ShadowSocksSpider getInstance() {
		return ShadowSocksSpiderHolder.instance;
	}

	private static class ShadowSocksSpiderHolder {
		static ShadowSocksSpider instance = new ShadowSocksSpider();
	}

	/**
	 * 开始进行数据爬取
	 * 
	 * @throws Exception
	 */
	public void start() {
		TLog.i("---------------------------------Start---------------------------------");
		List<Config> configs = new ArrayList<Config>();
		for (AccountCrawler crawler : crawlers) {
			TLog.i(crawler.getUrl());
			List<Config> newConfigs = crawler.crawAccounts();
			if (newConfigs == null || newConfigs.isEmpty()) {
				TLog.w("Cannot parse any ShadowSocks accounts from this page!");
			} else {
				configs.addAll(newConfigs);
			}
			TLog.i("------------------------------------------------------------------");
		}
		if (configs.isEmpty()) {
			TLog.e("Sorry, all shadowsocks accounts are not availiable. Please try again later, or you can checkout the latest code from "
					+ projectURL);
			return;
		}
		// 判断当前Shadowsocks.exe是否正在运行
		List<Integer> pids = getShadowSocksPidList();
		boolean running = pids.size() > 0;
		if (running) {
			TLog.i("ShadowSocks is running");
			// 杀死当前系统中的ShadowSocks进程
			taskKill(pids);
			TLog.i("Kill shadowsocks processes successful");
			// 将节点写入到gui-config.json文件中去
			writeToGUIConfig(configs);
			TLog.i("Write node to config file successful");
			// 再次启动ShadowSocks进程
			startShadowSocks();
			TLog.i("Restart shadowsocks program successful");
		} else {
			TLog.i("ShadowSocks is not running");
			// 将节点写入到gui-config.json文件中去
			writeToGUIConfig(configs);
			TLog.i("Write node to config file successful");
		}
		TLog.i("----------------------------------End----------------------------------\n");
	}

	/**
	 * 写入gui-config.json文件
	 * 
	 * @return true if write success, false if not.
	 */
	private boolean writeToGUIConfig(List<Config> configs) {
		boolean result = false;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File configPath = new File(programConfig.getShadowSocksDir(), programConfig.getConfigName());
		try {
			ShadowSocksConfig ssConfig = gson.fromJson(new FileReader(configPath), ShadowSocksConfig.class);
			ssConfig.setConfigs(configs);
			// 可能的话需要修改index的值(对应ShadowSocks客户端选择那个ss节点)
			ssConfig.setIndex(Math.min(ssConfig.getIndex(), configs.size() - 1));
			String data = gson.toJson(ssConfig);
			FileUtils.write(configPath, data);
			result = true;
		} catch (IOException e) {
			TLog.e("Write to gui-config.json failed", e);
		}
		return result;
	}

	/**
	 * 获取当前Windows系统正在运行的ShadowSocks进程的PID列表,如果没有ShadowSocks在运行,则返回空集合
	 * 
	 * @return the list of running process id about shadowsocks; return empty
	 *         list if no shadowsocks process running.
	 */
	private List<Integer> getShadowSocksPidList() {
		List<Integer> pids = new ArrayList<Integer>();
		Process p = null;
		try {
			// 通过tasklist命令获取当前系统中正在运行的进程列表
			p = Runtime.getRuntime().exec("TASKLIST /V /FI \"STATUS eq running\" /FO CSV");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
			String line = null;
			// 筛选出ShadowSocks进程
			String exeNameLower = programConfig.getExeName().toLowerCase();
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\"", "");
				if (line.toLowerCase().contains(exeNameLower)) {
					TLog.i(line);
					String[] splits = line.split(",");
					try {
						int pid = Integer.parseInt(splits[1]);
						pids.add(pid);
					} catch (Exception e) {
						continue;
					}
				}
			}
		} catch (IOException e) {
			TLog.e("Get ShadowSocks.exe's process ID failed", e);
		} finally {
			if (p != null)
				p.destroy();
		}
		return pids;
	}

	/**
	 * Kill the processes according to the pid list
	 * 
	 * @param pids
	 *            the process id list
	 */
	private void taskKill(List<Integer> pids) {
		// cmd: TASKKILL /F /PID 1230 /PID 1241 /T
		// /T : 终止指定的进程和由他启动的子进程
		// /F : 指定强制终止进程
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append("TASKKILL /F");
		for (int pid : pids) {
			cmdBuilder.append(" /PID " + pid);
		}
		cmdBuilder.append(" /T");
		String cmd = cmdBuilder.toString();
		TLog.i(cmd);
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			// 睡眠,等待ShadowSocks进程关闭
			int waiting = 3;
			TLog.i("Waiting for " + waiting + " seconds to shut up ShadowSocks");
			TimeUnit.SECONDS.sleep(waiting);
		} catch (Exception e) {
			TLog.e(e);
		} finally {
			if (p != null)
				p.destroy();
		}
	}

	/**
	 * Start shadowsocks program on windows
	 */
	private void startShadowSocks() {
		try {
			File shadowSocks = new File(programConfig.getShadowSocksDir(), programConfig.getExeName());
			String cmd = shadowSocks.getAbsolutePath();
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			TLog.e(e);
		}
	}

}
