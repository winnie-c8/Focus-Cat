import javax.swing.*;
import java.awt.*;

public class CountdownTimer {
	private Timer timer;
	private ProgressTracker pg;

	public CountdownTimer(ProgressTracker pg) {
		this.pg=pg;
  	}
	
	// create countdown timer format
	public static void formatCountdown(JLabel countdown) {
		countdown.setVisible(true);
		countdown.setHorizontalAlignment(JLabel.CENTER);
        countdown.setFont(countdown.getFont().deriveFont(200f));
        countdown.setForeground(Color.WHITE);
	}
	
	public void runTimer(JLabel countdown, String time, String tag, Runnable onFinish) {
		//use array so that reference position is effectively final
		int[] hour = {Integer.parseInt(time.substring(0,2))};
		int[] minute = {Integer.parseInt(time.substring(3,5))};
		int[] second = {Integer.parseInt(time.substring(6,8))};
		
		int ogHour = hour[0];
		int ogMinute = minute[0];
		
		timer = new javax.swing.Timer (1000, e-> {			
			String formatHour=String.format("%02d", hour[0]);
			String formatMinute=String.format("%02d", minute[0]);
			String formatSecond=String.format("%02d", second[0]);
			
			countdown.setText(formatHour + ":" + formatMinute + ":" + formatSecond);
			second[0]--;
			//timer ends
			if (minute[0]==0 && second[0]==-1 && hour[0]==0) {
				stopTimer();
				Main.playSound("/sounds/Cat Sound Effect.wav");
				
				//update minutes focused
				updateHoursFocused(ogHour, ogMinute, tag);
				
				//call methods in subclasses once timer ends
				if (onFinish!=null) {
					onFinish.run();
				}
			}
			if (second[0]==-1){
				second[0]=59;
				minute[0]--;
			}
			if (minute[0]==-1) {
				minute[0]=59;
				hour[0]--;
			}
		});
		
		timer.setInitialDelay(0);
		timer.start();
	}
	
	public void stopTimer() {
		if (timer!=null)
			timer.stop();
	}
	
	public void updateHoursFocused(int hour, int minute, String tag) {
		if (tag.equals("work")) {
			double h = hour + (double)minute/60;
			if (h>0) {
				Main.getData().setHoursFocused(Main.getData().getHoursFocused() + h);
				//Main.getData().hoursFocused += h;
				Main.getData().setCoins(Main.getData().getCoins() + (minute + 60*hour));
				//Main.getData().coins += minute + 60*hour;
				Store.getCoinCount().setText("Coins: " + Main.getData().getCoins());
				pg.addPoint(h);
			}
		}
		Data.saveData(Main.getData());
	}
	
	public Timer getTimer() {
		return timer;
	}

}