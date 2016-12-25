
import java.awt.Color;

public class AudibleObject {
	
	private Sample sample;
	private Color color;
	private double x, y, z;
	
	public AudibleObject(String filename, Color color, double a, double b, double c){
		sample = new Sample(filename);
		this.color = color;
		x=a;
		y=b;
		z=c;
	}
	
	public Sample getSample(){
		return sample;
	}
	
	// Pan for left and right ear based on relative location of object
	public void adjust(double px, double py, double pz, double degree){
		
		// Translate Angle
		double angle = Math.toDegrees(Math.atan2((x-px), (y-py)));
		angle = -(angle-90d);
		if(angle < 0d) angle+=360d;
		
		// Pan
		double dAngle = Math.abs(degree-angle);
		if(dAngle >= 180d) dAngle = 360d-dAngle;
		
		double opDegree = (180d+degree)%360d;
		if((degree >= angle && angle >= opDegree) || (degree <= opDegree && (angle <= degree || angle >= opDegree))){
			dAngle = -dAngle;
		}
		
		if(dAngle >= 90d){ dAngle = 180d - dAngle; }
		else if(dAngle <= -90d){ dAngle = -180d - dAngle; }
		
		// Panning Functions
		double linearPan = dAngle/90d;
		sample.setPan((float)linearPan);
		
			/*
			double sinePan = Math.sin(linearPan*Math.PI/2);
			
			double twoSinePan;
			if(sinePan > 0) twoSinePan = Math.pow(sinePan, 0.2f);
			else twoSinePan = -Math.pow(-sinePan, 0.2f);
			
			double fiveSinePan;
			if(sinePan > 0) fiveSinePan = Math.pow(sinePan, 0.2f);
			else fiveSinePan = -Math.pow(-sinePan, 0.2f);
			
			System.out.println("Linear Pan : " + linearPan + "  Sine Pan : " + sinePan + "  2-Sine Pan : " + twoSinePan + "  5-Sine Pan : " + (float)fiveSinePan);
			*/
			
		// Volume
		double distance = Math.pow(x-px, 2) + Math.pow(y-py, 2) + Math.pow(z-pz, 2);
		distance = Math.pow(distance, 0.5d);
		
		if(distance < 1d) distance = 1d;
		
		sample.setVolume(-(float)distance/6f);
	}
	
	public Color getColor(){
		return color;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getZ(){
		return z;
	}
	
	public void setX(double a){
		x=a;
	}
	
	public void setY(double b){
		y=b;
	}
	
	public void setZ(double c){
		z=c;
	}
	
}
