import java.util.*;

public class MoveFactory {
	private List<MovementBuffer> move;
	private MovementBuffer currentMove;
	private int bufferSize;
	private AnalysingThread aThread;
	
	public MoveFactory(int buffer){
		currentMove = null;
		move = new ArrayList<MovementBuffer>();
		bufferSize = buffer;
		aThread  = new AnalysingThread();
		aThread.start();
		
		
	}
	
	private boolean outOfOrigin(int[] point, int[] origin){
		int x = point[0]-origin[0];
		int y = point[1]-origin[1];
		int radius = Integer.parseInt(Config.getConfig("ORIGIN_RADIUS"));
		if((Math.pow(x,2)+Math.pow(y, 2))> (radius*radius)) return true;
		return false;
	}

	private boolean isMovePresent(MoveBuffer handPosition){
		this.currentMove = new MovementBuffer(this.bufferSize);
		
		boolean moveOpen = false;
		boolean moveClose = false;
		int begin =0,end=0,compteur =0;
		
		synchronized(handPosition){
		for(int[] point : handPosition){
			
			if(outOfOrigin(point,handPosition.getOrigin()) && !moveClose && !moveOpen){
				//System.out.println("Move started");
				moveOpen = true;
				begin= compteur;
			}
			if(!outOfOrigin(point,handPosition.getOrigin()) && moveOpen ){
				//System.out.println("Move ended");
				this.currentMove.addPoint(point[0], point[1]);
				this.currentMove.setOrigin(handPosition.getOrigin()[0],handPosition.getOrigin()[1]);
				moveClose = true;
				end = compteur;
				break;
			}
			if(moveOpen){
				this.currentMove.addPoint(point[0], point[1]);
			}
			compteur++;
		}
		}
		if(!moveClose&&moveOpen || !moveOpen){
			currentMove.flush();
			currentMove = null;
			return false;
		}
		handPosition.removeByRange(begin, end);
		return true;
	}
	
	
	public boolean checkMove(MoveBuffer handPosition){
		synchronized(handPosition){
			if(isMovePresent(handPosition)){
				//System.out.println("We have a move -> Adding to the list");
				synchronized(this.move){
					
					this.currentMove.setTimeStamp();
					this.move.add(this.currentMove);
					
				}
			}
			
		}
		
		return true;
		
	}
	
	public class AnalysingThread extends Thread{
		
		
		public AnalysingThread( ){
			
			
		}
		private void calculateDirection(MovementBuffer mv){
			int [] mean;
			double angle;
			Event e = null;
			
			
			mean = mv.getWeightMean();
			angle = (double)(mean[1]-mv.getOrigin()[1])/(mean[0]-mv.getOrigin()[0]);
			angle = Math.atan(angle);
		
			double angleLimit = Double.parseDouble(Config.getConfig("MOVE_ANGLE_LIMIT"));
			
			if((mean[0]-mv.getOrigin()[0])<0){
				if(angle>-angleLimit && angle<angleLimit){
					System.out.println("<============== Move Right ===============>");	
					Object[][] t= {{"xMove","RIGHT"},{"yMove",null},{"angle",new Float(angle)}};
					e = new Event("MOVE",t);
					
				}else if (angle<-angleLimit){
					System.out.println("<============== Move DOWN Right ===============>");
					Object[][] t= {{"xMove","RIGHT"},{"yMove","DOWN"},{"angle",new Float(angle)}};
					e = new Event("MOVE",t);
					
				}else{
					System.out.println("<============== Move UP Right ===============>");
					
					Object[][] t= {{"xMove","RIGHT"},{"yMove","UP"},{"angle",new Float(angle)}};
					e = new Event("MOVE",t);
				}
			}else{
				if(angle>-angleLimit && angle<angleLimit){
					System.out.println("<============== Move Left ===============>");
					Object[][] t= {{"xMove","LEFT"},{"yMove",null},{"angle",new Float(angle)}};
					e = new Event("MOVE",t);
					
				}else if (angle<-angleLimit){
					System.out.println("<============== Move UP Left ===============>");
					Object[][] t= {{"xMove","LEFT"},{"yMove","UP"},{"angle",new Float(angle)}};
					e = new Event("MOVE",t);
				}else{
					System.out.println("<============== Move DOWN Left ===============>");
					Object[][] t= {{"xMove","LEFT"},{"yMove","DOWN"},{"angle",new Float(angle)}};
					e = new Event("MOVE",t);
				}
			}
			
			e.print();
			
		}
		
		public void run(){
			System.out.println("Starting analysing Thread");
			while(true){
				//System.out.printf("Move list %d\n",move.size());
				if(move.size()>0){
					synchronized(move){
					Iterator<MovementBuffer> mvi = move.iterator();
					while(mvi.hasNext()){
						MovementBuffer mv = mvi.next();
						if(mv.size()>=(mv.getMaxSize()/2) && !mv.isProcessed()){
							calculateDirection(mv);
							mv.setProcessed(true);
						}else if (!mv.isProcessed()){
							String[][] t = {{"reason","Too short to decide"}};
							Event e = new Event("MOVE_CANCEL",t);
							e.print();
							mvi.remove();
						}else if (mv.isProcessed() && System.currentTimeMillis()> (mv.getTimeStamp()+Integer.parseInt(Config.getConfig("MOVE_AGE_DELETE")))){
							mvi.remove();
						}
					}
					}
				}
				try {
					sleep(Integer.parseInt(Config.getConfig("MOVE_DETECTION_INTERVAL")));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
}
