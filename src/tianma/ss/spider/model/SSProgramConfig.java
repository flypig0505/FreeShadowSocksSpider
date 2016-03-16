package tianma.ss.spider.model;

/**
 * 与ShadowSocks程序相关的配置文件
 * 
 * @author Tianma
 *
 */
public class SSProgramConfig {

	private String shadowSocksDir; // ShadowSocks的目录
	private String configName; // ShadowSocks的配置文件名
	private String exeName; // ShadowSocks的程序文件名

	public String getShadowSocksDir() {
		return shadowSocksDir;
	}

	public void setShadowSocksDir(String shadowSocksDir) {
		this.shadowSocksDir = shadowSocksDir;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getExeName() {
		return exeName;
	}

	public void setExeName(String exeName) {
		this.exeName = exeName;
	}

	@Override
	public String toString() {
		return "SSProgramConfig [shadowSocksDir=" + shadowSocksDir + ", configName=" + configName + ", exeName="
				+ exeName + "]";
	}

}
