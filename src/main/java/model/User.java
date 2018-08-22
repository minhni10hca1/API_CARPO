package model;

public class User {
	private String _id;
	private String role_id;
	private String username;
	private String phone;
	private String password;
	private String fullname;
	private String email;
	private String token;
	private String token_expire;
	private String photo;
	private String birthday;
	private String sex;
	private String role_code;
	public String getRole_code() {
		return role_code;
	}
	public void setRole_code(String role_code) {
		this.role_code = role_code;
	}
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
	private String face_id;
	private String google_id;
	private String created_time;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public String getToken_expire() {
		return token_expire;
	}
	public void setToken_expire(String token_expire) {
		this.token_expire = token_expire;
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
	
	

}
