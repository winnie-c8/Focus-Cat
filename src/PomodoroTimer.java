import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class PomodoroTimer extends CountdownTimer {
	private JPanel panel, frontPanel, buttonPanel, centerPanel;
	private static JLayeredPane bgPanel;
	private JLabel countdown, status;
	private JButton startButton, pause, resume, switchButton, bagButton, customTimer;
	private int numWorkPeriods=1;
	
	public PomodoroTimer(ProgressTracker pg) {
		super(pg);
        Image bgImg = new ImageIcon (getClass().getResource("/images/Timer_Background.png")).getImage();
		panel = new JPanel();
		panel.setLayout(new OverlayLayout(panel));
		centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setOpaque(false);
		
		bgPanel = new JLayeredPane(){
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
			}
		}; 
		bgPanel.setOpaque(true);
		bgPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		frontPanel = new JPanel();
		frontPanel.setOpaque(false);
		frontPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		
		countdown = new JLabel("00:25:00");
		formatCountdown(countdown);
		
		startButton = new JButton("Start");
		formatButton(startButton, Main.getCustomFont());
		
		status = new JLabel("work");
		status.setHorizontalAlignment(JLabel.CENTER);
        status.setFont(Main.getCustomFont().deriveFont(100f));
        status.setForeground(Color.WHITE);
        
        pause = new JButton("⏸");
        resume = new JButton("▶");
        switchButton = new JButton("Skip");
        bagButton = new JButton("Bag");
        customTimer = new JButton("Custom Timer");
        formatButton(pause, pause.getFont());
        formatButton(resume, resume.getFont());
        formatButton(switchButton, Main.getCustomFont());
        formatButton(bagButton, Main.getCustomFont());
        formatButton(customTimer, Main.getCustomFont());
        
        buttonPanel.add(pause);
        buttonPanel.add(resume);
        buttonPanel.add(switchButton);
        buttonPanel.add(startButton);
        pause.setVisible(false);
        resume.setVisible(false);
        switchButton.setVisible(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;       
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 0;
        centerPanel.add(status, gbc);
        gbc.gridy = 1;
        centerPanel.add(countdown, gbc);
        gbc.gridy = 2;
        centerPanel.add(buttonPanel, gbc);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST; 
        gbc.weightx = 1.0; 
        centerPanel.add(customTimer, gbc);
        gbc.gridy = 1;
        centerPanel.add(bagButton, gbc);
        
        
        panel.add(frontPanel);
        panel.add(centerPanel);
        panel.add(bgPanel);
		Main.getCardPanel().add(panel, "Timer");
		
        
        pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTimer().stop();
			}
		});
		
        resume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTimer().start();
			}
		});
        
        startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				countdown.setVisible(true);
				runTimer(countdown, status);
			}
		});
        
        switchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getTimer()!=null) {
					getTimer().stop();
				}
				runTimer(countdown, status);
			}
		});
        
        bagButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(getTimer()!=null)
					getTimer().stop();
				Main.getCardLayout().show(Main.getCardPanel(), "Bag");
			}
		});  	
        
        customTimer.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (getTimer()!=null)
        			getTimer().stop();
        		Main.getCardLayout().show(Main.getCardPanel(), "Custom Timer");
        	}
        });
	}
	
	public void runTimer(JLabel countdown, JLabel status) {
		startButton.setVisible(false);
		switchButton.setVisible(true);
		pause.setVisible(true);
        resume.setVisible(true);
		
    	//15 min (long) break
    	if (numWorkPeriods==8) {
    		status.setText("long break"); 
     		super.runTimer(countdown, "00:15:00", "break", () -> startButton.setVisible(true));
    	}
    	// work period
    	else if (numWorkPeriods%2!=0) {
    		status.setText("work" + (numWorkPeriods-numWorkPeriods/2));
     		super.runTimer(countdown, "00:25:00", "work", () -> startButton.setVisible(true));
    	}
    	//5 min (short) break
    	else {
    		status.setText("break");
     		super.runTimer(countdown, "00:05:00", "break", () -> startButton.setVisible(true));
    	}
 		numWorkPeriods++;
 		if (numWorkPeriods>=9) { //reset after full rotation
 			numWorkPeriods=1;
 		}
	}
	
	public void formatButton (JButton button, Font font) {
		button.setFont(font.deriveFont(25f));
		button.setBackground(new Color(130, 88, 62));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
	}
	
	public static JLayeredPane getBgPanel() {
		return bgPanel;
	}
}
