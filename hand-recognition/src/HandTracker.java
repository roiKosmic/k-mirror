import java.util.Timer;
import java.util.TimerTask;

public class HandTracker {
	private boolean firstDetection;
	private boolean handPresent;
	private Timer trackerTimer;
	private MoveBuffer mBuffer;
	private MyTrackerTask trackerTask;
	private boolean taskSchedule;
	
	public HandTracker(MoveBuffer buffer){
		this.firstDetection = false;
		this.taskSchedule = false;
		this.trackerTimer = new Timer();
		this.setHandPresent(false);
		this.setmBuffer(buffer);
		;
	}
	
	public boolean isFirstDetection(){
		
		return this.firstDetection;
	}
	
	public void setFirstDetection(boolean value){
		if(value && !taskSchedule) {
			this.trackerTask = new MyTrackerTask();
			System.out.println("Hand first Detected -> Starting tracker Timer");
			this.trackerTimer.schedule(trackerTask , 1000,500);
			this.taskSchedule = true;
		}
		this.firstDetection = value;
		
	}

	public MoveBuffer getmBuffer() {
		return mBuffer;
	}

	public void setmBuffer(MoveBuffer mBuffer) {
		this.mBuffer = mBuffer;
	}

	public boolean isHandPresent() {
		return handPresent;
	}

	public void setHandPresent(boolean handPresent) {
		this.handPresent = handPresent;
		if(!handPresent && taskSchedule){
			trackerTask.cancel();
			taskSchedule=false;
			System.out.println("Tracking Stopped");
		}
			
	}
	
	
	class MyTrackerTask extends TimerTask{
		public void run(){
			System.out.println("Tracking started");
		}
		
	}


	
}

