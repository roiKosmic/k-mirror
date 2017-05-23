import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HandTracker {
	private boolean firstDetection;
	private boolean handPresent;
	private boolean trackingInProgress;
	private boolean stopTrackingRequest;
	private Timer trackerTimer;
	private MoveBuffer mBuffer;
	private TrackingTask trackerTask;
	private EndTrackingTask endTrackerTask;
	private boolean taskSchedule;
	private MoveFactory moveAnalyser;
	
	public HandTracker(MoveBuffer buffer){
		this.firstDetection = false;
		this.taskSchedule = false;
		this.trackingInProgress = false;
		this.stopTrackingRequest = false;
		this.trackerTimer = new Timer();
		this.setHandPresent(false);
		this.setmBuffer(buffer);
		this.moveAnalyser = new MoveFactory(buffer.getMaxSize());
	}
	
	public boolean isFirstDetection(){
		
		return this.firstDetection;
	}
	
	public void setFirstDetection(boolean value){
		Event event;
		
		
		if(value && !taskSchedule && !trackingInProgress) {
			this.trackerTask = new TrackingTask(this);
			String[][] t = {{"reason","Hand first detection"}};
			event = new Event("HAND_DETECTED",t);
			//event.print();
			event.sendEvent();
			this.trackerTimer.schedule(trackerTask , Integer.parseInt(Config.getConfig("HAND_DETECTION_TIMER")),Integer.parseInt(Config.getConfig("HAND_TRACKING_INTERVAL")));
			this.taskSchedule = true;
			this.firstDetection = true;
			
		}else if(value && taskSchedule && trackingInProgress){
			this.firstDetection = false;
			this.endTrackerTask.cancel();
			this.stopTrackingRequest = false;
			String[][] t = {{"reason","Hand back before tracker timer has expired"}};
			event = new Event("HAND_DETECTED",t);
			//event.print();
			event.sendEvent();
			//reason back before tracking timer expired
			
		}
		else{
			this.firstDetection = false;
		}
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
		if(!handPresent && this.taskSchedule && this.trackingInProgress){
			if(!stopTrackingRequest){
				this.endTrackerTask = new EndTrackingTask();
				this.trackerTimer.schedule(this.endTrackerTask,Integer.parseInt(Config.getConfig("HAND_REMOVE_TIMER")));
				this.stopTrackingRequest = true;
			}
			
		}else if (!handPresent && this.taskSchedule && !this.trackingInProgress){
			String[][] t = {{"reason","Hand left before tracker timer has started"}};
			Event event = new Event("HAND_NOT_DETECTED",t);
			
			//event.print();
			event.sendEvent();
			this.trackerTask.cancel();
			this.taskSchedule = false;
		}
			
	}
	
	class EndTrackingTask extends TimerTask{
			
		public EndTrackingTask(){
			super();
		
		}
		public void run(){
			String [][] t = {{"reason","Hand left and tracker timer expired"}};
			Event event = new Event("HAND_NOT_DETECTED",t);
			event.sendEvent();
			//event.print();
			//reason : tracker timer expired
			
			trackerTask.cancel();
			taskSchedule=false;
			trackingInProgress = false;
			stopTrackingRequest = false;
			mBuffer.flush();
			this.cancel();
			
		}
		
	}
	class TrackingTask extends TimerTask{
		 
		 private boolean originSet;
		public TrackingTask(HandTracker ht){
			super();
			originSet = false;
			
		}
		public void run(){
			Event event;
			if(!originSet){
				//System.out.printf("Setting Origin of Hand Region - waiting for buffer %d/%d ",ref.mBuffer.size(),ref.mBuffer.getMaxSize());
				if(mBuffer.size()>=mBuffer.getMaxSize()){
					mBuffer.setOrigin();
					originSet = true;
					Object[][] t = {{"xorigin",new Integer(mBuffer.getOrigin()[0])},{"yorigin",new Integer(mBuffer.getOrigin()[1])}};
					event = new Event("HAND_ORIGIN_SET",t);
					//event.print();
					event.sendEvent();
				}
				
			}else{
				trackingInProgress = true;
				if(mBuffer.updateOrigin()!=null){
					Object[][] t = {{"xorigin",new Integer(mBuffer.getOrigin()[0])},{"yorigin",new Integer(mBuffer.getOrigin()[1])}};
					event = new Event("HAND_ORIGIN_UPDATED",t);
					//event.print();;
					event.sendEvent();
					
				}
				moveAnalyser.checkMove(mBuffer);
				mBuffer.checkHeightVariation();
			}
		}
		
	}


	
}

