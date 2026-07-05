import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class CustomTimer extends CountdownTimer {
	private JPanel panel, inputPanel, hourPanel, minutePanel, secondPanel;
	private JButton startButton;
	private JLabel countdown, colon1, colon2;

	public CustomTimer(ProgressTracker pg) {
		super(pg);
		GridBagConstraints gbc = new GridBagConstraints();
		panel = new JPanel();
		countdown = new JLabel();
		inputPanel = new JPanel();
		formatCountdown(countdown);

		JSpinner hours = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
		JSpinner minutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		JSpinner seconds = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		hourPanel = labeledSpinner(hours, "hours");
		minutePanel = labeledSpinner(minutes, "minutes");
		secondPanel = labeledSpinner(seconds, "seconds");
		
		startButton = new JButton("Start");
		startButton.setFont(Main.getCustomFont().deriveFont(25f));
		startButton.setBackground(new Color(158, 203, 170));
		startButton.setForeground(new Color(60, 100, 71));
		startButton.setBorderPainted(false);
		
		panel.setLayout(new GridBagLayout());
		panel.setBackground(new Color(60, 100, 71));
		inputPanel.setOpaque(false);
		
		colon1 = new JLabel(":");
		colon2 = new JLabel(":");
		colon1.setFont(Main.getCustomFont().deriveFont(150f));
		colon2.setFont(Main.getCustomFont().deriveFont(150f));
		colon1.setForeground(Color.WHITE);
		colon2.setForeground(Color.WHITE);

		inputPanel.add(hourPanel);
		inputPanel.add(colon1);
		inputPanel.add(minutePanel);
		inputPanel.add(colon2);
		inputPanel.add(secondPanel);
		
		gbc.gridx = 0;
		gbc.insets = new Insets(10, 0, 10, 0);
		gbc.gridy = 0;
		panel.add(inputPanel, gbc);
		gbc.gridy = 1;
		panel.add(startButton, gbc);
		
		panel.add(countdown, gbc);
		countdown.setVisible(false);
		
		Main.getCardPanel().add(panel, "Custom Timer");
		
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String time = String.format("%02d:%02d:%02d", (int) hours.getValue(), (int) minutes.getValue(), (int) seconds.getValue());
				if (time.equals("00:00:00")==false){
					changeVisibility(false);
					runTimer(countdown, time, "work", ()->timerEnds());
				}
			}
		});
	}
	
	public void timerEnds() {
		changeVisibility(true);
	}

	private JPanel labeledSpinner(JSpinner spinner, String labelText) {
		//set default to show two digits
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "00");
		spinner.setEditor(editor);
		
		//remove white background
		editor.getTextField().setOpaque(false);
		editor.getTextField().setBorder(null);
		editor.setOpaque(false);
		
		editor.getTextField().setForeground(Color.WHITE);
		spinner.setFont(Main.getCustomFont().deriveFont(150f));
		spinner.setOpaque(false);
		spinner.setBorder(null);
		
	    JPanel container = new JPanel();
	    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
	    container.setOpaque(false);

	    JLabel label = new JLabel(labelText);
	    label.setForeground(Color.WHITE);
	    label.setFont(Main.getCustomFont().deriveFont(20f));
	    label.setAlignmentX(Component.CENTER_ALIGNMENT);
	    spinner.setAlignmentX(Component.CENTER_ALIGNMENT);

	    container.add(label);
	    container.add(spinner);
	    return container;
	}
	
	public void changeVisibility(boolean isVisible) {
		startButton.setVisible(isVisible);
		hourPanel.setVisible(isVisible);
		minutePanel.setVisible(isVisible);
		secondPanel.setVisible(isVisible);
		colon1.setVisible(isVisible);
		colon2.setVisible(isVisible);
		countdown.setVisible(!isVisible);
	}
}
