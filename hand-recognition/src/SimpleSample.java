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
        BackgroundSubtractorKNN bg;
        bg = Video.createBackgroundSubtractorKNN();
       // bg.setNMixtures(8);
        
        bg.setDetectShadows(false);
        
        String cascadeFile = "/usr/local/share/OpenCV/haarcascades/haarcascade_frontalface_alt.xml";
        CascadeClassifier cascade = new CascadeClassifier(cascadeFile);
        System.out.println("cascade loaded: "+(!cascade.empty())+" !");

        NamedWindow    frame = new NamedWindow("Face");
       // NamedWindow fgfr = new NamedWindow("fg");
        //NamedWindow fgfr2 = new NamedWindow("fg");
        VideoCapture cap = new VideoCapture(0);
       // cap.set(Videoio.CAP_PROP_FPS,5);
       
        if (! cap.isOpened()) {
            System.out.println("Sorry, we could not open you capture !");
        }
        cap.set(Videoio.CAP_PROP_FRAME_WIDTH,Integer.parseInt(Config.getConfig("CAPTURE_WIDTH")));
        cap.set(Videoio.CAP_PROP_FRAME_HEIGHT,Integer.parseInt(Config.getConfig("CAPTURE_HEIGHT")));
        Mat im = new Mat();
        Mat fg = new Mat();
        Mat skinfr = new Mat();
        Mat skinForground = new Mat();
        MoveBuffer mvBuffer = new MoveBuffer(Integer.parseInt(Config.getConfig("MOVE_BUFFER")));
        HandTracker myHand = new HandTracker(mvBuffer);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();        
        int learnBg = Integer.parseInt(Config.getConfig("BACKGROUND_LEARN"));
        SkinMaskThreshold skin = null;
        MatOfPoint selectedHand = null;
        Rect hand = null;
        int cX = 0;
        int cY = 0;
        while (cap.read(im)) {
        	//System.out.println(cap.get(Videoio.CAP_PROP_FPS));
              if (cascade != null ) {
            	  if(learnBg > 0) {
              		bg.apply(im, fg);
              		learnBg--;
              		
              	}else{
              		//System.out.println("bglearnt");
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
                	  //On prend que les grands contours
                	  if(Imgproc.contourArea(contours.get(j))>Integer.parseInt(Config.getConfig("HAND_CONTOUR_AREA"))){
                		  //On selectionne le plus grand contour
                		  if(Imgproc.contourArea(contours.get(j))>= Imgproc.contourArea(selectedHand)) selectedHand = contours.get(j);
                		
                		 
                		  //Voici la main possible
                		  hand = Imgproc.boundingRect(selectedHand);
                	
                	  }
                   }
                   
                   if(hand.height> Integer.parseInt(Config.getConfig("HAND_MIN_HEIGHT")) && hand.width>Integer.parseInt(Config.getConfig("HAND_MIN_WIDTH"))){
                	   boolean falsePositive = false;
                	   if(!myHand.isFirstDetection()&&!myHand.isHandPresent()){
                		   //Controle du positionnement de la main pour supprimer les faux positifs (cou par ex)
                		   //La main ne doit pas se trouver dans un rectange autour du visage
                		   if((hand.x > found.x && hand.x < (found.x + found.width))||hand.y < found.y){
                			   falsePositive = true;
                			   //System.out.println("False positive Detected");
                			   String[][] t = {{"reason","Hand object too close to face"}};
                			   Event e = new Event("FALSE_POSITIVE",t);
                			   //e.print();
                			   e.sendEvent();
                			   Imgproc.circle(im, new Point(hand.x,hand.y), 50, new Scalar(0,0,200),-1);
                			   
                		   }else{
                			   falsePositive = false;
                			   myHand.setFirstDetection(true);
                		   }
                	   }
                	   //Autre faux positif (petits objet a Notifier ?)
                		//  String[][] t = {{"reason","Hand object below HAND_MIN_HEIGHT and/or HAND_MIN_WIDTH"}};
                		 // Event e = new Event("FALSE_POSITIVE",t);
                		  //e.print();
                		   
                	   
                	   if(!falsePositive){
                		   Moments m =  Imgproc.moments(selectedHand);
                 		  cX = (int) (m.m10/m.m00);
                 		  cY =(int)(m.m01/m.m00);
                		   myHand.setHandPresent(true);
                		   myHand.setFirstDetection(false);
                		   
                			   mvBuffer.addPoint(cX, cY);
                		   
                		   Imgproc.rectangle(im, hand.tl(), hand.br(),new Scalar(200,0,0));
                		   for(int k=0;k<mvBuffer.size();k++){
                			   Imgproc.circle(im, new Point(mvBuffer.get(k)[0],mvBuffer.get(k)[1]), 7, new Scalar(0,200,0),-1);
                	   		}
                		   if(mvBuffer.getOrigin()[0]  != 0 && mvBuffer.getOrigin()[1] !=0){
                			   Imgproc.circle(im, new Point(mvBuffer.getOrigin()[0],mvBuffer.getOrigin()[1]), Integer.parseInt(Config.getConfig("ORIGIN_RADIUS")), new Scalar(0,0,200),1);
                		   }
                		   
                	   }
                	   
                   }else{
                	   myHand.setHandPresent(false);
                	   
                   }
                   contours.clear();
                   
                     int k = frame.waitKey(30);
                     if (k == 27){ // 'esc'
                    	 cap.release();
                    	 break;
                     }
                     
                    // fgfr.imshow(skinForground);
                }
               // fgfr2.imshow(fg);
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

