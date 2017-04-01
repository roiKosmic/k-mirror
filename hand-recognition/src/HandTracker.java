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
		if(value && !taskSchedule && !trackingInProgress) {
			this.trackerTask = new TrackingTask(this);
			System.out.println("Hand first Detected -> Starting tracker Timer");
			this.trackerTimer.schedule(trackerTask , 2000,200);
			this.taskSchedule = true;
			this.firstDetection = true;
		}else if(value && taskSchedule && trackingInProgress){
			this.firstDetection = false;
			this.endTrackerTask.cancel();
			this.stopTrackingRequest = false;
			System.out.println("Hand back in time");
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
				this.trackerTimer.schedule(this.endTrackerTask,1500);
				this.stopTrackingRequest = true;
			}
			
		}else if (!handPresent && this.taskSchedule && !this.trackingInProgress){
			System.out.println("Cancelling task before tracking");
			this.trackerTask.cancel();
			this.taskSchedule = false;
		}
			
	}
	
	class EndTrackingTask extends TimerTask{
			
		public EndTrackingTask(){
			super();
		
		}
		public void run(){
			System.out.println("Tracking Stopped ! No hand detected ");
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
			if(!originSet){
				//System.out.printf("Setting Origin of Hand Region - waiting for buffer %d/%d ",ref.mBuffer.size(),ref.mBuffer.getMaxSize());
				if(mBuffer.size()>=mBuffer.getMaxSize()){
					mBuffer.setOrigin();
					originSet = true;
					//System.out.println("Origin Set\n");
				}
				
			}else{
				trackingInProgress = true;
				mBuffer.updateOrigin();
				//System.out.println("Tracking in Progress - Passing buffer to Analyser");
				moveAnalyser.checkMove(mBuffer);
			}
		}
		
	}


	
}

