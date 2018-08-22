package model;

public class Campaign_CM {
	private String _id;
	private String customer_id;
	private String name;
	private String total_distance;
	private String start_time;
	private String end_time;
	private String total_car;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTotal_distance() {
		return total_distance;
	}
	public void setTotal_distance(String total_distance) {
		this.total_distance = total_distance;
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
	public String getTotal_car() {
		return total_car;
	}
	public void setTotal_car(String total_car) {
		this.total_car = total_car;
	}
	public String getTotal_car_advertising() {
		return total_car_advertising;
	}
	public void setTotal_car_advertising(String total_car_advertising) {
		this.total_car_advertising = total_car_advertising;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getCar_wage() {
		return car_wage;
	}
	public void setCar_wage(String car_wage) {
		this.car_wage = car_wage;
	}
	private String total_car_advertising;
	private String area_code;
	private String area_name;
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}
	private String car_wage;

}
