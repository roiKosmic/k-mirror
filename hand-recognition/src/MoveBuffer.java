import java.util.Queue;
import java.util.ArrayList;

public class MoveBuffer extends ArrayList<int[]>{
	private int maxSize;
	
	public MoveBuffer(int max){
		this.maxSize = max;
		
	}
	
	public void addPoint(int x,int y){
		int[] pt = {x,y};
		add(pt);
		if(size()> this.maxSize){
			removeRange(0,this.size() - maxSize -1 );
						
		}
	}
	
	
		
	
	
}
