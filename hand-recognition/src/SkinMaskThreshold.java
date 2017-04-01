import org.opencv.core.*;
import org.opencv.imgproc.*;
import java.util.*;


public class SkinMaskThreshold {
	private double hmax;
	private double hmin;
	private double smax;
	private double smin;
	private double vmax;
	private double vmin;
	private ArrayList<Integer> harray; // Hue 0 179, Saturation 0 255 Value 0 255
	private ArrayList<Integer> sarray;
	private ArrayList<Integer> varray;
	private Mat hsvFrame;
	private Mat skin;
	public SkinMaskThreshold(Mat im, Rect faceRegion){
		this.hsvFrame = new Mat();
		this.skin = new Mat();
		harray = new ArrayList<Integer>();
		sarray = new ArrayList<Integer>();
		varray = new ArrayList<Integer>();
		
		Imgproc.cvtColor(im, hsvFrame, Imgproc.COLOR_RGB2HSV);
		for(int i=faceRegion.x;i<faceRegion.x+faceRegion.width;i++){
			for(int j=faceRegion.y;j<faceRegion.y+faceRegion.height;j++){
				int hue = (int)hsvFrame.get(j,i)[0];
				int sat = (int)hsvFrame.get(j, i)[1];
				int val = (int)hsvFrame.get(j, i)[2];
				harray.add(hue);
				sarray.add(sat);
				varray.add(val);
			}
			
			
		}
		int hmoy = calculateAverage(harray);
		int hecart = calculEcartType(hmoy,harray);
		this.hmin=hmoy-2*hecart;
		this.hmax=hmoy+2*hecart;
		
		int smoy = calculateAverage(sarray);
		int secart = calculEcartType(smoy,sarray);
		this.smin=smoy-2*secart;
		this.smax=smoy+2*secart;
		
		int vmoy = calculateAverage(varray);
		int vecart = calculEcartType(vmoy,varray);
		this.vmin=vmoy-2*vecart;
		this.vmax=vmoy+2*vecart;
		
	/*	System.out.printf("Hue Threshold Min %f - Max %f\n",Math.max(0, this.hmin),Math.min(179,this.hmax));
		System.out.printf("Saturation Threshold Min %f - Max %f\n",Math.max(0,this.smin),Math.min(255, this.smax));
		System.out.printf("Value Threshold Min %f - Max %f\n",Math.max(0,this.vmin),Math.min(255,this.vmax));
		*/
		
	}
	public Mat getSkinSkeleton(){
		
		Mat kernel = new Mat();
		Scalar minValue = new Scalar(Math.max(0, this.hmin),Math.max(0,this.smin),Math.max(0,this.vmin));
		Scalar maxValue = new Scalar(Math.min(179,this.hmax),Math.min(255, this.smax),Math.min(255,this.vmax));
		Core.inRange(this.hsvFrame, minValue, maxValue, skin);
		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(11,11));
		Imgproc.erode(skin, skin, kernel);
		Imgproc.dilate(skin, skin, kernel);
		return skin;
		
		
	}
	
	public void removeHead(Mat im, Rect faceRegion){
		double [] black= {0,0,0};
		for(int i=faceRegion.x;i<faceRegion.x+faceRegion.width;i++){
			for(int j=faceRegion.y;j<faceRegion.y+faceRegion.height;j++){
				im.put(j, i,black );
			}
			
			
		}
		
	}
	private int calculateAverage(List<Integer> arrs){
		long sum = 0;
		for(Integer arr : arrs){
			sum +=arr;
			
		}
		return (int)(sum/arrs.size());
	}
	
	private int calculEcartType(int moy, List<Integer> arrs){
		long sum = 0;
		for(Integer arr : arrs){
			int ecart;
			ecart = (arr-moy);
			ecart = ecart*ecart;
			sum +=ecart;
			
		}
		return (int)(Math.sqrt(sum/arrs.size()));
	
	}
	public double getHmax() {
		return hmax;
	}
	public void setHmax(double hmax) {
		this.hmax = hmax;
	}
	public double getHmin() {
		return hmin;
	}
	public void setHmin(double hmin) {
		this.hmin = hmin;
	}
	public double getSmax() {
		return smax;
	}
	public void setSmax(double smax) {
		this.smax = smax;
	}
	public double getSmin() {
		return smin;
	}
	public void setSmin(double smin) {
		this.smin = smin;
	}
	public double getVmax() {
		return vmax;
	}
	public void setVmax(double vmax) {
		this.vmax = vmax;
	}
	public double getVmin() {
		return vmin;
	}
	public void setVmin(double vmin) {
		this.vmin = vmin;
	}
	
	

}
