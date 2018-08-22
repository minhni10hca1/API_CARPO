package model_json;

public class formatDataInfoUserDriverCar_CM {
	private String _id;
	private String type;
	private String total_km_yesterday;
	private String total_km_30_day_before;
	private String phone;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getTotal_km_yesterday() {
		return total_km_yesterday;
	}
	public void setTotal_km_yesterday(String total_km_yesterday) {
		this.total_km_yesterday = total_km_yesterday;
	}
	public String getTotal_km_30_day_before() {
		return total_km_30_day_before;
	}
	public void setTotal_km_30_day_before(String total_km_30_day_before) {
		this.total_km_30_day_before = total_km_30_day_before;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getCampaign_id() {
		return campaign_id;
	}
	public void setCampaign_id(String campaign_id) {
		this.campaign_id = campaign_id;
	}
	public String getCar_color() {
		return car_color;
	}
	public void setCar_color(String car_color) {
		this.car_color = car_color;
	}
	public String getLicense_plate() {
		return license_plate;
	}
	public void setLicense_plate(String license_plate) {
		this.license_plate = license_plate;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getCreated_time() {
		return created_time;
	}
	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}
	public String getDriver_id() {
		return driver_id;
	}
	public void setDriver_id(String driver_id) {
		this.driver_id = driver_id;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	private String user_id;
	private String name;
	private String status_user;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus_user() {
		return status_user;
	}
	public void setStatus_user(String status_user) {
		this.status_user = status_user;
	}
	private String campaign_id;
	private String car_color;
	private String license_plate;
	private String car_manufacturer;
	public String getCar_manufacturer() {
		return car_manufacturer;
	}
	public void setCar_manufacturer(String car_manufacturer) {
		this.car_manufacturer = car_manufacturer;
	}
	private String start_time;
	private String end_time;
	private String created_time;
	private String driver_id;
	private String group_id;

}
