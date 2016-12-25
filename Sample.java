import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sample {

	private File file;
	private String filename;
	
	private Clip clip;
	private FloatControl gainCtrl;
	private FloatControl balCtrl;
	
	public Sample(String f){
		filename = f;
	}
	
	public void loadAudio(){
		file = new File("samples/" + filename);
		if(file.exists()){

            AudioInputStream stream;
			try {
				stream = AudioSystem.getAudioInputStream(file);
				AudioFormat format = stream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				
				if (!AudioSystem.isLineSupported(info)) System.out.println("Line not supported.");
				
				try {
					clip = (Clip) AudioSystem.getLine(info);
					clip.open(stream);
					
					if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN) && clip.isControlSupported(FloatControl.Type.BALANCE)){
						gainCtrl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
						balCtrl = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
						setVolume(-80.0f);
						setPan(0.0f);
					}
					else{
						System.out.println("Controls not supported.");
					}
					
				} catch (LineUnavailableException e) { System.out.println("Line unavailable supported."); }
			
			} catch (UnsupportedAudioFileException e) { System.out.println(filename + " not supported."); }
			catch(IOException e){ System.out.println(filename + "could not be opened."); }
			
		}
		else{
			System.out.println(filename + " not found.");
		}
	}
	
	public void loop(){
		clip.setFramePosition(0);
		clip.loop(100);
	}
	
	public void play(){
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void stop(){
		clip.stop();
	}
	
	public void setVolume(float vol){
		float temp = Math.max(-80.0f, vol);
		temp = Math.min(0.0f, temp);
		gainCtrl.setValue(temp);
	}
	
	public void setPan(float pan){
		float temp = Math.max(-1.0f, pan);
		temp = Math.min(1.0f, temp);
		balCtrl.setValue(temp);
	}
	
}
