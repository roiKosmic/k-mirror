import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class EventDispatcher {
	List<Event> eventList;
	HttpRequestThread thread;
	private static EventDispatcher instance;
	
	
	public EventDispatcher(){
		eventList = new ArrayList<Event> ();
		thread = new HttpRequestThread();
		thread.start();
		
	}
	public Event getLastEventByType(String type){
		Event last = null;
		synchronized(eventList){
			Iterator<Event> i = eventList.iterator();
			while(i.hasNext()){
				Event e = i.next();
				if(e.getName().equals(type)){
					e.print();
					last =e;
				}
			}
			
		}
		
		return last;
	}
	
	public void removeOldEvent(){
		long duration = Long.parseLong(Config.getConfig("EVENT_AGE_DELETE"));
		synchronized(eventList){
			Iterator<Event> i = eventList.iterator();
			while(i.hasNext()){
				Event e = i.next();
				if(e.isProcessed()){
					
					if(Math.abs(e.getTime()-System.currentTimeMillis())> duration){
						
						i.remove();
					}
				}
			}
		}
		
	}
	public void addEvent(Event e){
		if(Integer.parseInt(Config.getConfig(e.getName()))== 1){
			synchronized(eventList){
				if(e.getName().equals("HAND_CLICK")){
					Event previous = this.getLastEventByType("HAND_CLICK");
					if(previous !=null){
						if(Math.abs(previous.getTime()-e.getTime())>Integer.parseInt(Config.getConfig("CLICK_EVENT_LOCK_PERIOD"))){
							eventList.add(e);
						}else{
							System.out.println("Click event filtered - Lock period");
						}
					}else{
						eventList.add(e);
					}
				 }else{
					 eventList.add(e);
				 }
				Collections.sort(eventList,new Comparator<Event>(){
					public int compare(Event e1,Event e2){
						return Long.compare(e1.getTime(), e2.getTime());
					}
				});
			}
		}else{
			System.out.println("Event filtered");
		}
	}
	
	
	public static synchronized EventDispatcher getInstance(){
		if(instance == null){
			instance = new EventDispatcher();
		}
		return instance;
	}
	
	public class HttpRequestThread extends Thread{
		URL url;
		HttpURLConnection hurl;
		
		public HttpRequestThread(){
			super();
			try{
				
				url = new URL(Config.getConfig("API_URL"));
				
			
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		private void sendEventHttp(Event e){
			System.out.println("Sending HTTP event : ");
			System.out.println(e.toString());
			
			try{
				String jsonEvent = e.toString();
				HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
				hurl.setRequestMethod("POST");
				hurl.setDoOutput(true);
				hurl.setRequestProperty("Content-Type", "application/json");
				hurl.setRequestProperty("Accept", "application/json");
				OutputStreamWriter osw = new OutputStreamWriter(hurl.getOutputStream());
				osw.write(jsonEvent);
				osw.flush();
				osw.close();
				System.out.println(hurl.getResponseCode());
			}catch(Exception ex){
				System.out.println("Network connection error");
			}
			
		}
		public void run(){
			while(true){
				removeOldEvent();
				synchronized(eventList){
					Iterator<Event> i = eventList.iterator();
					while(i.hasNext()){
						Event e = i.next();
						if(!e.isProcessed()){
							sendEventHttp(e);
						}
						e.setProcessed();
						
					}
				}
			}
		}
		
	}
}
