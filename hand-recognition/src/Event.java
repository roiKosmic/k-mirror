import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.simple.*;

public class Event {
	
	JSONObject jsonEvent;
	
	public Event(String eventName) {
		jsonEvent = new JSONObject();
		jsonEvent.put("event", eventName);
		jsonEvent.put("time",System.currentTimeMillis());
	}

	public Event(String eventName,Object[][] data){
		this(eventName);
	
		List<Payload> data_ = new ArrayList<Payload> ();
		for (Object[] obj : data){
			Payload pl = new Payload(obj[0],obj[1]);
			data_.add(pl);
			
		}
		this.addData(data_);
	}
	public void print(){
		System.out.println(this.toString());
	}
	public void addData(List<Payload> data){
		JSONObject jsonData = new JSONObject();
		for(Payload pl_ : data){
			jsonData.put(pl_.getKey(), pl_.getValue());
		}
		
		jsonEvent.put("data",jsonData);
	}
	
	public String toString(){
		return jsonEvent.toJSONString();
	}
}
