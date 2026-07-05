import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main{
	private static JPanel cardPanel;
	private JPanel mainPanel, topPanel;
	private static CardLayout cardLayout;
	private JComboBox<String> menu;
	private static Font customFont, textFont;
	private JFrame frame;
	private static Data data;
	private JPanel rightPanel;
	private JButton exitButton, minimizeButton, timerButton, toDoButton, progressTrackerButton, storeButton;
	private ProgressTracker pg;
	private CountdownTimer pt, ct;
	private static HashMap <String, Clip> audioClips = new HashMap<String, Clip>();
	
	public Main() {
		//load data from file
		data = new Data();
		data = Data.loadData();
        Image appIcon = new ImageIcon (getClass().getResource("/images/App_Icon.png")).getImage();
        Image bgImg = new ImageIcon (getClass().getResource("/images/Main_Menu_Background.png")).getImage();
        loadSound("/sounds/Cat Sound Effect.wav");
		loadSound("/sounds/Ding Sound Effect.wav");
		InputStream i = getClass().getResourceAsStream("/fonts/oliverFont.ttf");
		InputStream i2 = getClass().getResourceAsStream("/fonts/textFont.otf");
		
		//load fonts
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, i);
			customFont = customFont.deriveFont(Font.PLAIN, 24f);
			textFont = Font.createFont(Font.TRUETYPE_FONT, i2);
			textFont = textFont.deriveFont(Font.PLAIN, 24f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		cardLayout = new CardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		frame = new JFrame();
		mainPanel = new JPanel(null) {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
			}
		};
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		JPanel centerPanel = new JPanel();
		rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		String[] tabs = {"Main Menu", "Timer", "To-Do", "Progress Tracker", "Store"};
		menu = new JComboBox<String>(tabs);
		menu.setFont(customFont.deriveFont(15f));
		menu.setVisible(false);
		
		//create and format buttons
		exitButton = new JButton("X");
		minimizeButton = new JButton("-");
		timerButton = new JButton("");
		toDoButton = new JButton("");
		progressTrackerButton = new JButton("track progress");
		storeButton = new JButton("Store");
		formatUIButton(exitButton, new Color(176, 38, 38));
		formatUIButton(minimizeButton, new Color(161, 124, 92));
		minimizeButton.setPreferredSize(new Dimension(45,29));
		formatMainButton(timerButton, "Timer_Button", 80,325,350,150);
		formatMainButton(toDoButton, "To-Do_Button", 80,575,350,150);
		formatMainButton(progressTrackerButton, "Track_Productivity_Button", 1080,325,350,150);
		formatMainButton(storeButton, "Store_Button", 1080,575,350,150);
		
		//add elements to panels
		centerPanel.add(menu);
		centerPanel.setOpaque(false);
		rightPanel.add(minimizeButton);
		rightPanel.add(exitButton);
		topPanel.add(centerPanel, BorderLayout.CENTER);
		topPanel.add(rightPanel, BorderLayout.EAST);
		mainPanel.add(timerButton);
		mainPanel.add(toDoButton);
		mainPanel.add(progressTrackerButton);
		mainPanel.add(storeButton);
		
		frame.setUndecorated(true);
		frame.setLayout(new BorderLayout());
        frame.setIconImage(appIcon);
		frame.setSize(1920, 1080);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); // full screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(cardPanel, BorderLayout.CENTER);
		frame.add(topPanel, BorderLayout.NORTH);
				
		//initialize each panel screen
		pg = new ProgressTracker();
		new CheckList();
		pt = new PomodoroTimer(pg);
		ct = new CustomTimer(pg);
		new Store();

		cardPanel.add(mainPanel, "Main");
		cardLayout.show(cardPanel, "Main");
		frame.setVisible(true);
		
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Data.saveData(data);
				System.exit(0);
			}
		});
		
		minimizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setState(Frame.ICONIFIED);
			}
		});
		
		timerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanels("Timer");
			}
		});
		
		toDoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanels("To-Do");
			}
		});
		
		progressTrackerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanels("Progress Tracker");
			}
		});
		
		storeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanels("Store");
			}
		});
		
		menu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selected = (String) menu.getSelectedItem();
				
				ct.stopTimer();
				((CustomTimer)ct).changeVisibility(true);
				
				if (selected.equals("Timer")) {
					cardLayout.show(cardPanel, "Timer");
				}
				else {
					pt.stopTimer();
					if (selected.equals("Main Menu")) {
						cardLayout.show(cardPanel, "Main");
						menu.setVisible(false);
					}
					else {
						cardLayout.show(cardPanel, selected);
					}
				}
			}
		});
	}
	
	public static void main(String[] args) {
		// loading screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int w = screen.width / 2; 
		int h = (int)(screen.height / 2.5);
		
	    JWindow loading = new JWindow();
	    Image loadingImage = new ImageIcon(Main.class.getResource("/images/Loading_Image.png")).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
	    JLabel loadingScreen = new JLabel(new ImageIcon(loadingImage));
	    loading.add(loadingScreen);
	    loading.pack();
	    loading.setLocationRelativeTo(null);
	    loading.setVisible(true);
	    
		new Main();
		
		loading.dispose();
	}
	
	void switchPanels(String panel) {
		cardLayout.show(cardPanel, panel);
		menu.setSelectedItem(panel);
		menu.setVisible(true);
	}
	
	static void loadSound(String path) {
		URL url = Main.class.getResource(path);
		try {
			AudioInputStream input = AudioSystem.getAudioInputStream(url);
			Clip clip = AudioSystem.getClip();
			clip.open(input);
			audioClips.put(path, clip);
		} 
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	static void playSound(String path) {
		if (audioClips.containsKey(path)) {
			Clip clip = audioClips.get(path);
			clip.setFramePosition(0);
			clip.start();
		}
	}
	
	private static void formatUIButton(JButton button, Color color) {
		button.setFont(customFont.deriveFont(15f));
		button.setBackground(color);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
	}
	
	private static void formatMainButton(JButton button, String imgSource, int x, int y, int w, int h) {
		button.setIcon(new ImageIcon(new ImageIcon(Main.class.getResource("/images/"+imgSource+".png")).getImage().getScaledInstance(350,150, Image.SCALE_SMOOTH)));
		button.setBounds(x,y,w,h);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
	}
	
	public static JPanel getCardPanel() {
		return cardPanel;
	}
	
	public static CardLayout getCardLayout() {
		return cardLayout;
	}
	
	public static Font getCustomFont() {
		return customFont;
	}
	
	public static Font getTextFont() {
		return textFont;
	}
	
	public static Data getData() {
		return data;
	}
}
