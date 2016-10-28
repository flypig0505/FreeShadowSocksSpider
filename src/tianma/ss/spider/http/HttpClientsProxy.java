package tianma.ss.spider.http;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import tianma.ss.spider.util.TLog;

/**
 * This is a proxy of  {@link HttpClients}
 * @author Tianma
 *
 */
public class HttpClientsProxy {

	private HttpClientsProxy() {

	}

	/**
	 * Creates builder object for construction of custom
	 * {@link CloseableHttpClient} instances.
	 */
	public static HttpClientBuilder custom() {
		return HttpClients.custom();
	}

	/**
	 * Creates {@link CloseableHttpClient} instance with default configuration.
	 */
	public static CloseableHttpClient createDefault() {
		return HttpClients.createDefault();
	}

	/**
	 * Creates {@link CloseableHttpClient} instance with default configuration
	 * based on ssytem properties.
	 */
	public static CloseableHttpClient createSystem() {
		return HttpClients.createSystem();
	}

	/**
	 * Creates {@link CloseableHttpClient} instance that implements the most
	 * basic HTTP protocol support.
	 */
	public static CloseableHttpClient createMinimal() {
		return HttpClients.createMinimal();
	}

	/**
	 * Creates {@link CloseableHttpClient} instance that implements the most
	 * basic HTTP protocol support.
	 */
	public static CloseableHttpClient createMinimal(final HttpClientConnectionManager connManager) {
		return HttpClients.createMinimal(connManager);
	}

	/**
	 * 创建支持Https请求的HttpClient
	 * 
	 * @return
	 */
	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			TLog.e(e);
		} catch (NoSuchAlgorithmException e) {
			TLog.e(e);
		} catch (KeyStoreException e) {
			TLog.e(e);
		}
		return HttpClients.createDefault();
	}

}
