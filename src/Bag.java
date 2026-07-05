import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import javax.swing.*;

public class Bag {
	private HashMap <String, ImageIcon> images;
	private JPanel panel, bagPanel, topPanel;
	private JScrollPane sp;
	private GridBagConstraints c;
	private JButton backButton;
	private final int COLUMNS = 4;
	private int x=0,y=0;
	
	public Bag() {
		panel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(Store.getBgImg(), 0, 0, getWidth(), getHeight(), this);
			}
		};
		panel.setLayout(new BorderLayout());
        bagPanel = new JPanel(new GridBagLayout());
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bagPanel.setOpaque(false);
        topPanel.setOpaque(false);
        sp = new JScrollPane(bagPanel);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);      
		sp.getVerticalScrollBar().setUnitIncrement(5);
		sp.setBorder(null);
		sp.setOpaque(false);
		sp.getViewport().setOpaque(false);
		backButton = new JButton("Back");
		backButton.setForeground(new Color(130, 88, 62));
		backButton.setBackground(new Color(209, 190, 171));
		backButton.setFont(Main.getCustomFont().deriveFont(20f));
		topPanel.add(backButton);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

		//initalize grid of items
        c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5); //space between buttons
		c.fill = GridBagConstraints.HORIZONTAL; //fill horizontal area
		c.weightx=0.5;
		
		images = new HashMap<String, ImageIcon>();
		
		for (Item item : Main.getData().getItemsBought()) {
			updateBag(item);
		}
        
        Main.getCardPanel().add(panel, "Bag");
        
        backButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                Main.getCardLayout().show(Main.getCardPanel(), "Timer");
        	}
        });
	}
	
	public void updateBag(Item item) {
		
		JPanel p = new JPanel(new BorderLayout());
		ImageIcon icon;
		
		if (images.containsKey(item.getName())==false) {
			URL imgURL = getClass().getResource("/images/" + item.getName().replace(" ", "_") + ".png");
			Image scaled = new ImageIcon(imgURL).getImage().getScaledInstance(item.getWidth(), item.getHeight(), Image.SCALE_SMOOTH);
			icon = new ImageIcon(scaled);
			images.put(item.getName(), icon);
		}
		else {
			icon = images.get(item.getName());
		}
		
		//make image in bag a constant size
		JLabel imgInBag = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(325, 325, Image.SCALE_SMOOTH)));
		JLabel imgDisplay = new JLabel(icon);
		
		imgDisplay.setBounds(item.getX(), item.getY(), item.getWidth(), item.getHeight());
		PomodoroTimer.getBgPanel().add(imgDisplay, Integer.valueOf(item.getLayer()));
		
		JButton toggleVisible = new JButton();
		if (item.getIsVisible()) {
			displayItem(imgDisplay);
			toggleVisible.setText("Hide");
		}
		else {
			hideItem(imgDisplay);
			toggleVisible.setText("Show");
		}
		
		toggleVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (item.getIsVisible()) {
					toggleVisible.setText("Show");
					item.setIsVisible(false);
					hideItem(imgDisplay);
				}
				else {
					toggleVisible.setText("Hide");
					item.setIsVisible(true);
					displayItem(imgDisplay);
				}
			}
		});
		
		toggleVisible.setForeground(new Color(130, 88, 62));
		toggleVisible.setBackground(new Color(209, 190, 171));
		toggleVisible.setFont(Main.getCustomFont().deriveFont(20f));
		p.setBackground(new Color(243, 236, 222));
		
		p.add(imgInBag, BorderLayout.CENTER);
		p.add(toggleVisible, BorderLayout.NORTH);
		
		c.gridx=x;
		c.gridy=y;
		bagPanel.add(p, c);
		
		x++;
		if (x==COLUMNS) {
			x=0;
			y++;
		}
		
		bagPanel.revalidate();
		bagPanel.repaint();
	}
	
	public void displayItem(JLabel imgDisplay) {
		imgDisplay.setVisible(true);
		PomodoroTimer.getBgPanel().revalidate();
		PomodoroTimer.getBgPanel().repaint();
	}
	
	public void hideItem(JLabel imgDisplay) {
		imgDisplay.setVisible(false);
		PomodoroTimer.getBgPanel().revalidate();
		PomodoroTimer.getBgPanel().repaint();
	}
}
