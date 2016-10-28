package tianma.ss.spider.main;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import tianma.ss.spider.ShadowSocksSpider;
import tianma.ss.spider.util.TLog;

/**
 * The main entrance
 * @author Tianma
 *
 */
public class MainStarter {

	private static final int HOURS = 6;
	private static final int ONE_HOUR = 60 * 60 * 1000;

	private static final String MODE_SINGLE = "single";
	private static final String MODE_CIRCLE = "circle";

	public static void main(String[] args) {
		TLog.init();
		String mode;
		if (args.length <= 0) {
			System.out.println("用法： [mode]");
			System.out.println("其中mode包括:");
			System.out.println("single 只执行一次");
			System.out.println("circle 循环执行(每6小时执行一次)");
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException ignore) {
			}
			System.exit(0);
		}
		mode = args[0];
		if (MODE_CIRCLE.equals(mode)) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					perform();
				}
			}, new Date(), HOURS * ONE_HOUR);

		} else if (MODE_SINGLE.equals(mode)) {
			perform();
		}
	}

	private static void perform() {
		ShadowSocksSpider spider = ShadowSocksSpider.getInstance();
		spider.start();
	}

}
