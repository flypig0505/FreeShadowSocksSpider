package tianma.ss.spider.model;

/**
 * SoxOrz账户
 * 
 * @author Tianma
 *
 */
public class SoxOrzAccount {

	private String email;
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "SoxOrzAccount [email=" + email + ", password=" + password + "]";
	}

}
