import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.*;
import org.opencv.video.*;
import org.opencv.objdetect.*;

//import org.opencv.;
import org.opencv.utils.*;
import java.util.*;
import lowgui.*;
import java.io.File;
import java.io.IOException;
import lowgui.*;



//
// SimpleSample [persons_dir] [path/to/face_cascade]
//
class SimpleSample {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        BackgroundSubtractorMOG2 bg;
        bg = Video.createBackgroundSubtractorMOG2();
        //bg.setNMixtures(3);
        
        bg.setDetectShadows(false);
        
        String cascadeFile = "/usr/local/share/OpenCV/haarcascades/haarcascade_frontalface_alt.xml";
        CascadeClassifier cascade = new CascadeClassifier(cascadeFile);
        System.out.println("cascade loaded: "+(!cascade.empty())+" !");

        NamedWindow    frame = new NamedWindow("Face");
        NamedWindow fgfr = new NamedWindow("fg");
        NamedWindow fgfr2 = new NamedWindow("fg");
        VideoCapture cap = new VideoCapture(0);
        cap.set(Videoio.CAP_PROP_FRAME_WIDTH,200);
        cap.set(Videoio.CAP_PROP_FRAME_HEIGHT,400);
        if (! cap.isOpened()) {
            System.out.println("Sorry, we could not open you capture !");
        }

        Mat im = new Mat();
        Mat fg = new Mat();
        Mat skinfr = new Mat();
        Mat skinForground = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();        
        int learnBg = 500;
        SkinMaskThreshold skin = null;
        MatOfPoint selectedHand = null;
        Rect hand = null;
        int cX = 0;
        int cY = 0;
        while (cap.read(im)) {
        	
              if (cascade != null ) {
            	  if(learnBg > 0) {
              		bg.apply(im, fg);
              		learnBg--;
              		
              	}else{
              		System.out.println("bglearnt");
              		bg.apply(im, fg,0);
              	}
            	    
            	MatOfRect faces = new MatOfRect();
                cascade.detectMultiScale(im, faces);
                Rect[] facesArray = faces.toArray();
                if (facesArray.length != 0) {
                    Rect found = facesArray[0];
                    //Init du masque de découverte de la peau par rapport au visage
                    skin = new SkinMaskThreshold(im,found);
                    //Extraction uniquement des parties avec peau (visage et main généralement)
                    Core.bitwise_and(im,im,skinfr,skin.getSkinSkeleton());
                    //Remplissage de la tête par un carré noir
                    skin.removeHead(skinfr, found);
                    //Extraction du fond pour limiter les faux positifs du masque peau (si le mur est couleur peau par ex)
                   Core.bitwise_and(skinfr, skinfr,skinForground, fg);
                   
                  //Passage en gris  
                  Imgproc.cvtColor(skinForground, skinForground,Imgproc.COLOR_HSV2RGB);
                  Imgproc.cvtColor(skinForground, skinForground, Imgproc.COLOR_RGB2GRAY);
                 //Découverte des contours.
                  Imgproc.findContours(skinForground, contours,new Mat(),Imgproc.RETR_EXTERNAL ,Imgproc.CHAIN_APPROX_SIMPLE);
                  //Selection des contours
                  for(int j=0;j<contours.size();j++){
                	  if(j==0){ selectedHand = contours.get(j);}
                	  
                	  if(Imgproc.contourArea(contours.get(j))>50){
                		  if(Imgproc.contourArea(contours.get(j))>= Imgproc.contourArea(selectedHand)) selectedHand = contours.get(j);
                		
                		 Moments m =  Imgproc.moments(selectedHand);
                		 cX = (int) (m.m10/m.m00);
                		 cY =(int)(m.m01/m.m00);
                		 
                	 //  Imgproc.drawContours(im, contours,j, new Scalar(0,200,0));
                	   hand = Imgproc.boundingRect(selectedHand);
                	
                	  }
                   }
                   
                   if(hand.height> 50 && hand.width>20){
                	   Imgproc.rectangle(im, hand.tl(), hand.br(),new Scalar(200,0,0));
                	   Imgproc.circle(im, new Point(cX,cY), 7, new Scalar(0,200,0));
                	   }
                   contours.clear();
                   
                     int k = frame.waitKey(30);
                     if (k == 27){ // 'esc'
                    	 cap.release();
                    	 break;
                     }
                     fgfr2.imshow(fg);
                     fgfr.imshow(skinForground);
                }
                frame.imshow(im);
               
                im.release();
                fg.release();
                skinfr.release();
                skinForground.release();
                   
            }
        	
               
                
    
        
         
        }
        System.exit(0); // to break out of the ant shell.
    }
}

