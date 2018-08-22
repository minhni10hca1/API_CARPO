package model_json;

public class formatDataGroupMember {
	public double getTotal_km_month() {
		return total_km_month;
	}
	public void setTotal_km_month(double total_km_month) {
		this.total_km_month = total_km_month;
	}
	private double total_km_month;
	private String driver_id;
	private String user_id;
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	private String name;
	private String phone;
	public String getDriver_id() {
		return driver_id;
	}
	public void setDriver_id(String driver_id) {
		this.driver_id = driver_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
