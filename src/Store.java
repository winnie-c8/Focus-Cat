import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;

import javax.swing.*;

public class Store {
	private static Image bgImg;
	private static JLabel coinCount;
	private JPanel panel, buttonPanel, storePanel, pmPanel;
	private JButton XButton, buyButton;
	private JScrollPane sp;
	private GridBagConstraints c;
	private JPopupMenu pm, popUpMsg;
	private JLabel msg;
	private Item selectedItem;
	private JMenuItem itemImg;
	private Bag bag;
	private HashMap <String, ImageIcon> itemImgs;
	private final int COLUMNS=4;
	
	public Store() {
        bgImg = new ImageIcon (getClass().getResource("/images/Store_Background.png")).getImage();
		panel = new JPanel();
		buttonPanel = new JPanel();
		storePanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
			}
		};
		pmPanel = new JPanel();
		XButton = new JButton("X");
		buyButton = new JButton();
		sp = new JScrollPane(storePanel);
		c = new GridBagConstraints();
		pm = new JPopupMenu();
		popUpMsg = new JPopupMenu();
		msg = new JLabel();
		coinCount = new JLabel("Coins: " + Main.getData().getCoins());
		itemImg = new JMenuItem();
		bag = new Bag();
		
		coinCount.setFont(Main.getCustomFont().deriveFont(25f));
		buyButton.setFont(Main.getCustomFont().deriveFont(16f));
		
		//load all images
		itemImgs = new HashMap<String, ImageIcon>();
		for (Item item : Main.getData().getAllItems()) {
			URL imgURL = getClass().getResource("/images/" + item.getName().replace(" ", "_" )+ "_Store.png");
			if (imgURL == null) {
			    continue; // skip this item
			}
			Image scaled = new ImageIcon(imgURL).getImage().getScaledInstance(325, 325, Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(scaled);
			
			itemImgs.put(item.getName(), icon);
		}
		
		//initialize grid of items
		c.insets = new Insets(5,5,5,5); //space between buttons
		c.fill = GridBagConstraints.HORIZONTAL; //fill horizontal area
		c.weightx=0.5;
		
		//initialize pop up menu
		pmPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pmPanel.setOpaque(false);
		pmPanel.add(XButton);
		formatButton(buyButton);
		formatButton(XButton);
		itemImg.setHorizontalTextPosition(SwingConstants.CENTER);
		itemImg.setVerticalTextPosition(SwingConstants.TOP);
		pm.setLayout(new BorderLayout());
		pm.setBackground(new Color(242, 236, 231));
		pm.add(pmPanel, BorderLayout.NORTH);
		pm.add(buyButton, BorderLayout.SOUTH);
		pm.add(itemImg, BorderLayout.CENTER);
		
		//initialize pop up message
		msg.setFont(Main.getCustomFont().deriveFont(60f));
		msg.setForeground(Color.WHITE);
		popUpMsg.setBorderPainted(false);
		popUpMsg.setBackground(new Color(130, 88, 62));
		popUpMsg.add(msg);
		
		panel.setLayout(new BorderLayout());
		storePanel.setLayout(new GridBagLayout());
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.getVerticalScrollBar().setUnitIncrement(5);
		buttonPanel.add(coinCount);
		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.add(sp, BorderLayout.CENTER);
		
		showItems(Main.getData().getAllItems());
		
		panel.setVisible(true);
		
		Main.getCardPanel().add(panel, "Store");
		
		XButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pm.setVisible(false);
			}
		});	
		
		buyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Main.getData().getCoins() >= selectedItem.getPrice()) {
					//check prerequisite item bought
					if ((selectedItem.getPrereq()!=-1 && Main.getData().getItem(selectedItem.getPrereq()).getIsSold()) || selectedItem.getPrereq()==-1) {
						Main.getData().setCoins(Main.getData().getCoins() - selectedItem.getPrice());
						//Main.getData().coins-=selectedItem.getPrice();
						coinCount.setText("Coins: " + Main.getData().getCoins());
						Main.getData().getItemsBought().add(selectedItem);
						bag.updateBag(selectedItem);
						
						//find index of selected item in allItems array
						int index=0;
						for (int i=0; i<Main.getData().getAllItems().length; i++) {
							if (selectedItem.getName().equals(Main.getData().getItem(i).getName())) {
								index=i;
								break;
							}
						}
						
						Main.getData().getItem(index).setIsSold(true);
						msg.setText("Purchased!");
						showItems(Main.getData().getAllItems());
						Data.saveData(Main.getData());
					}
					else {
						msg.setText("Must first buy " + Main.getData().getItem(selectedItem.getPrereq()).getName());
					}
				}
				else {
					msg.setText("Not enough coins!");
				}
				
				popUpMsg.show(panel, (panel.getWidth() - popUpMsg.getPreferredSize().width) / 2, (panel.getHeight() - popUpMsg.getPreferredSize().height) / 2);
			}
		});
	}
	
	public void showItems(Item[] items) {
		storePanel.removeAll();
		int x=0,y=0;
		
		for (int i=0; i<items.length; i++) {
			Item item = items[i];
			//if item is not sold, display it
			if (!item.getIsSold()) { 
				ImageIcon icon = itemImgs.get(item.getName());
				JButton button = new JButton(icon);
				
				button.setText(item.getName());
				button.setHorizontalTextPosition(SwingConstants.CENTER);
				button.setVerticalTextPosition(SwingConstants.BOTTOM);
				button.setBackground(new Color(209, 190, 171));
				button.setForeground(new Color(130, 88, 62));
				button.setFont(Main.getCustomFont());
				
				c.gridx=x;
				c.gridy=y;
				storePanel.add(button, c);
				
				x++;
				if (x==COLUMNS) {
					x=0;
					y++;
				}
				
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedItem=item;
						if (item.getPrice()==0)
							buyButton.setText("FREE");
						else
							buyButton.setText(Integer.toString(item.getPrice())+ " coins");
						itemImg.setIcon(icon);
						itemImg.setText(item.getName());
						itemImg.setFont(Main.getCustomFont());
						itemImg.setForeground(new Color(130, 88, 62));
						pm.show(panel, (panel.getWidth() - pm.getPreferredSize().width) / 2, (panel.getHeight() - pm.getPreferredSize().height) / 2);
					}
				});
			}
		}
		storePanel.revalidate();
		storePanel.repaint();
	}
	
	private void formatButton(JButton button) {
		button.setBackground(new Color(130, 88, 62));
		button.setForeground(new Color(209, 190, 171));
		button.setFont(Main.getCustomFont().deriveFont(25f));
	}
	
	public static Image getBgImg() {
		return bgImg;
	}
	
	public static JLabel getCoinCount() {
		return coinCount;
	}
}
