package tianma.ss.spider.model;

/**
 * ShadowSocks gui-config.json中的ShadowSocks节点配置信息
 * 
 * @author Tianma
 *
 */
public class Config {

	private String server;
	private int server_port;
	private String password;
	private String method;
	private String remarks;

	public Config() {
	}

	public Config(String server, int server_port, String password, String method, String remarks) {
		super();
		this.server = server;
		this.server_port = server_port;
		this.password = password;
		this.method = method;
		this.remarks = remarks;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getServer_port() {
		return server_port;
	}

	public void setServer_port(int server_port) {
		this.server_port = server_port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "Config [server=" + server + ", server_port=" + server_port + ", password=" + password + ", method="
				+ method + ", remarks=" + remarks + "]";
	}

}
