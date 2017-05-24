import java.util.ArrayList;
import java.util.Iterator;

public class MoveBuffer extends ArrayList<int[]>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxSize;
	private int[] origin= {0,0};
	
	
	
	public MoveBuffer(int max){
		this.maxSize = max;
		
		
	}
	public int getMaxSize(){
		return this.maxSize;
	}
	public void setOrigin(int x, int y){
		synchronized(this){
		this.origin[0] = x;
		this.origin[1] = y;
		}
	}
	public int[] getWeightMean(){
		int sum_x = 0;
		int sum_y = 0;
		int[] wMean = {0,0};
		synchronized(this){
			for(int[] point : this){
				sum_x +=point[0];
				sum_y +=point[1];
				//System.out.printf("point x %d,point y %d\n",point[0],point[1]);
			
			}
		
		wMean[0] = sum_x/this.size();
		wMean[1]= sum_y/this.size();
		//System.out.printf("Origin %d %d", wMean[0],wMean[1]);
		}
		return wMean;
	}
		
	
	public int[] setOrigin(){
		int sum_x = 0;
		int sum_y = 0;
		synchronized(this){
			for(int[] point : this){
				sum_x +=point[0];
				sum_y +=point[1];
				//System.out.printf("point x %d,point y %d\n",point[0],point[1]);
			
			}
		
		origin[0] = sum_x/this.size();
		origin[1] = sum_y/this.size();
			//System.out.printf("Origin %d %d", origin[0],origin[1]);
		}
		return this.origin;
	}
	
	private boolean checkXmove(int x_coordinate){
		if(Math.abs(x_coordinate)- this.getOrigin()[0] > Integer.parseInt(Config.getConfig("HAND_CLICK_X_MOVE_LIMIT"))){
			//System.out.println("HAND Click cancel : false positive Hand moving");
			return true;
		}
		
		return false;
	}
	public void checkHeightVariation(){

		int prev_height=this.get(0)[2];
		//System.out.println("=====> Checking click event");
		synchronized(this){
			Iterator<int[]> mvi = this.iterator();
			while(mvi.hasNext()){
				int[] point = mvi.next();
			//	System.out.printf("%d %d \n",point[2],prev_height);
				if(point[2] != -1 && prev_height -point[2] >Integer.parseInt(Config.getConfig("HAND_CLICK_THRESHOLD"))){
					if(!checkXmove(point[0])){
						Object[][] t= {{"reason","height change"}};
						Event e = new Event("HAND_CLICK",t);
						e.sendEvent();
						point[2] = -1;
					}
					break;
				}
				prev_height = point[2];
				
				
				
			}
		}
	 
	}
	public int[] updateOrigin(){
		int prev_x = this.get(0)[0];
		int prev_y = this.get(0)[1];
		int sum_x = 0;
		int sum_y = 0;
		boolean updating = true;
		
		synchronized(this){
			for(int[] point:this){
				//Regarder si la main est fixe si oui on met à jour l'origin sinon on break
				//System.out.printf("%d %d - %d %d\n",point[0],prev_x,point[1],prev_y);
				if(Math.abs(point[0]-prev_x)< Integer.parseInt(Config.getConfig("HAND_STATIC_LIMIT")) && Math.abs(point[1]-prev_y)< Integer.parseInt(Config.getConfig("HAND_STATIC_LIMIT"))){
			
				
					sum_x +=point[0];
					sum_y +=point[1];
					
					
				
				}else{
					updating = false;
					//System.out.println("Hand moving not updating origin");
					break;
				
				}
				prev_x = point[0];
				prev_y = point[1];
			}
	
		if(updating){
			
			if(Math.abs(origin[0]-(sum_x/this.size()))> Integer.parseInt(Config.getConfig("ORIGIN_UPDATE_LIMIT")) || Math.abs(origin[1]-(sum_y/this.size()))> Integer.parseInt(Config.getConfig("ORIGIN_UPDATE_LIMIT"))){
				//System.out.printf("Updating Origin Old %d %d New : %d %d", origin[0],origin[1],sum_x/this.size(),sum_y/this.size());
				origin[0] = sum_x/this.size();
				origin[1] = sum_y/this.size();
				//La main est fixe
				
				
				return this.origin;
			}else{
				return null;
			}
			
			
		}
		}
		return null;
	}
	
	public int[] getOrigin(){
		return this.origin;
		
	}
	public void flush(){
		synchronized(this){
			this.clear();
			this.origin[0]=0;
			this.origin[1]=0;
		}
	}
	public void addPoint(int x,int y,int height){
		synchronized(this){
			int[] pt = {x,y,height};
			add(pt);
			if(size()> this.maxSize){
				removeRange(0,this.size() - maxSize -1 );
						
			}
		}
	}
	
	public void removeByRange(int begin,int end){
		synchronized(this){
			this.removeRange(begin, end);
		}
	}
	
}
