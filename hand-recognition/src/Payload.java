
public class Payload {
	private Object key;
	private Object value;
	public Payload(Object key_,Object value_){
		setKey(key_);
		setValue(value_);
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	
}
