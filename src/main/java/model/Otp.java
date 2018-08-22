package model;

public class Otp {
	private String _id;
	private String user_id;
	private String otp_number;
	private String expire_date;
	private String created_date;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getOtp_number() {
		return otp_number;
	}
	public void setOtp_number(String otp_number) {
		this.otp_number = otp_number;
	}
	public String getExpire_date() {
		return expire_date;
	}
	public void setExpire_date(String expire_date) {
		this.expire_date = expire_date;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	
	

}
