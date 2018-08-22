package model_json;

public class formatDataInfoUser {
	private String _id;
	//private String role_id;
	private String phone;
	private String fullname;
	private String email;
	private String token;
	private String token_expire;
	private String status_leader;
	private String role_code;
	public String getStatus_leader() {
		return status_leader;
	}
	public void setStatus_leader(String status_leader) {
		this.status_leader = status_leader;
	}
	public String getRole_code() {
		return role_code;
	}
	public void setRole_code(String role_code) {
		this.role_code = role_code;
	}
	public String getToken_expire() {
		return token_expire;
	}
	public void setToken_expire(String token_expire) {
		this.token_expire = token_expire;
	}
	private String photo;
	private String face_id;
	private String google_id;
	private String created_time;
	private String total_distance_run_one_month;
	public String getTotal_distance_run_one_month() {
		return total_distance_run_one_month;
	}
	public void setTotal_distance_run_one_month(String total_distance_run_one_month) {
		this.total_distance_run_one_month = total_distance_run_one_month;
	}
	// more info
	private String car_id;
	private String type;
	private String campaign_id;
	private String car_color;
	private String license_plate;
	private String car_manufacturer;
	private String birthday;
	private String sex;
	
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getCar_manufacturer() {
		return car_manufacturer;
	}
	public void setCar_manufacturer(String car_manufacturer) {
		this.car_manufacturer = car_manufacturer;
	}
	//private String driver_id;
	private String group_id;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getFace_id() {
		return face_id;
	}
	public void setFace_id(String face_id) {
		this.face_id = face_id;
	}
	public String getGoogle_id() {
		return google_id;
	}
	public void setGoogle_id(String google_id) {
		this.google_id = google_id;
	}
	public String getCreated_time() {
		return created_time;
	}
	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}
	public String getCar_id() {
		return car_id;
	}
	public void setCar_id(String car_id) {
		this.car_id = car_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
//	public String getDriver_id() {
//		return driver_id;
//	}
//	public void setDriver_id(String driver_id) {
//		this.driver_id = driver_id;
//	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
}
