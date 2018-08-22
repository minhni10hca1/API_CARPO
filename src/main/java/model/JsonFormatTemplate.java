package model;

public class JsonFormatTemplate {
	private int status;
	private Object data;
	private JsonFormatError error;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public JsonFormatError getError() {
		return error;
	}
	public void setError(JsonFormatError error) {
		this.error = error;
	}

}
