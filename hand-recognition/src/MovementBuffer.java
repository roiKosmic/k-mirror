
public class MovementBuffer extends MoveBuffer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long timeStamp;
	private boolean processed;
	public MovementBuffer(int max) {
		super(max);
		setProcessed(false);
		// TODO Auto-generated constructor stub
		
	}
	
	
	public void setTimeStamp(){
		this.timeStamp = System.currentTimeMillis();
	}

	public long getTimeStamp(){
		return this.timeStamp;
	}


	public boolean isProcessed() {
		return processed;
	}


	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
}
