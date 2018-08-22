package model;

public class GlobalErrorCode {
	
	/* code error from user */
	public static String error_code_user_login = "700";
	/* code error connect db */
	public static String error_code_connect_db = "701";
	public static String error_message_connect_db = "Lổi kết nối database";
	/* code error expired */
	public static String error_code_expire = "702";
	/* code error expired not mach */
	public static String error_code_expire_not_match = "710";
	/* code error find data */
	public static String error_code_find_data = "704";
	public static String error_message_find_data = "Không có dữ liệu";
	/* code error insert data */
	public static String error_code_insert_data = "705";
	public static String error_message_insert_data = "Thêm dữ liệu thất bại";
	/* code error delete data */
	public static String error_code_delete_data = "706";
	public static String error_message_delete_data = "Xóa dữ liệu thất bại";
	/* code error delete data */
	public static String error_code_update_data = "707";
	public static String error_message_update_data = "Cập nhật dữ liệu thất bại";
	/* code error data not match */
	public static String error_code_data_not_match = "708";
	/* loi truyen params khong chinh sat */
	public static String error_code_input_params = "709";
	public static String error_message_input_params = "Params truyền không hợp lệ.";
	/* loi truyen params header khong chinh sat */
	public static String error_code_input_params_header = "709";
	public static String error_message_input_params_header = "Params header truyền không hợp lệ.";
	/* loi may chu noi bo */
	public static String error_code_internal_server_error = "500";
	public static String error_message_internal_server_error = "Lổi máy chủ nội bộ";
	/* loi không được thực hiện */
	public static String error_code_not_implemented = "501";
	public static String error_message_not_implemented = "Phương thức yêu cầu HTTP không phải GET hoặc POST.";
	/* server quả tải */
	public static String error_code_service_unavailable = "503";
	public static String error_message_service_unavailable = "Số kết nối đến server quá lớn. server quá tải";
	/*không quyền truy cập app */
	public static String error_code_not_accept_app = "711";
	public static String error_message_not_accept_app = "Không có quyền try cập";
	
	
	
	
	
	
	
	

}
