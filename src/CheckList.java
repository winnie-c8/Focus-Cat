import java.util.ArrayList;
import java.util.Stack;
import java.io.IOException;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

public class CheckList {
	private JPanel panel, buttonPanel;
	private JPopupMenu pm;
	private JScrollPane sp;
	private JList <String> listOfTasks;
	private DefaultListModel<String> listModel;
	private JTextField name;
	private JButton delete, newTaskButton, sortButton;
	private JLabel numCompleteTasks;
	private int completedTasks;
	private Stack<Task> undoStack, redoStack;
	
	
	public CheckList() {
        Image bgImg = new ImageIcon (getClass().getResource("/images/To-Do_Background.png")).getImage();
		panel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
			}
		};
		panel.setLayout(new BorderLayout());
		
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		completedTasks=0;
		numCompleteTasks = new JLabel("Tasks Completed Today: " + completedTasks);
		numCompleteTasks.setFont(Main.getCustomFont().deriveFont(20f));
		numCompleteTasks.setForeground(new Color(64, 34, 22));
		
		//initialize listOfTasks
		listModel = new DefaultListModel<>();
		listOfTasks = new JList<>(listModel);
		listOfTasks.setCellRenderer(new TaskRenderer());
		listOfTasks.setOpaque(false);
		listOfTasks.setBackground(new Color(235, 205, 188));
		listOfTasks.setFont(Main.getTextFont().deriveFont(20f));
		listOfTasks.setForeground(new Color(64, 34, 22));
		listOfTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//load all tasks from file
		for (int i=0; i<Main.getData().getAllTasks().size(); i++) {
			listModel.addElement(Main.getData().getAllTasks().get(i).getName());
		}
		
		sp = new JScrollPane(listOfTasks);
		sp.setOpaque(false);
		sp.getViewport().setOpaque(false);
		
		//pop up menu for creating new tasks
		pm = new JPopupMenu("New task");
		name = new JTextField();
		name.setFont(Main.getTextFont().deriveFont(20f));
		name.setForeground(new Color(64, 34, 22));
		delete = new JButton("Delete");
		formatButton(delete);
		JPanel pmPanel = new JPanel(new BorderLayout());
		pmPanel.add(name, BorderLayout.CENTER);
		pmPanel.add(delete, BorderLayout.EAST);
		pm.add(pmPanel);
		pm.setBorder(BorderFactory.createEmptyBorder());
		
		//initialize panel of buttons
		buttonPanel = new JPanel();
		newTaskButton = new JButton("New Task");
		sortButton = new JButton("Sort");
		formatButton(newTaskButton);
		formatButton(sortButton);
		buttonPanel.add(newTaskButton);
		buttonPanel.add(sortButton);
		buttonPanel.add(numCompleteTasks);

		panel.add(sp, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.setVisible(true);
		Main.getCardPanel().add(panel, "To-Do");
		
		//allow drag and drop to reorder list items
		listOfTasks.setDragEnabled(true); //enable drag & drop
		listOfTasks.setDropMode(DropMode.INSERT); //allow dropping between items
		listOfTasks.setTransferHandler(new TransferHandler() {
			int index;
			boolean aboveIndex = false; 
			
			//make items moveable
			public int getSourceActions(JComponent c) {
				return MOVE;
			}
			
			//bundle objects into Transferable object to be moved
			public Transferable createTransferable(JComponent c) {
				index = listOfTasks.getSelectedIndex();
				return new StringSelection(listOfTasks.getSelectedValue());
			}
			
			//remove list item once it has been moved
			public void exportDone(JComponent c, Transferable t, int action) {
				if (action == MOVE) {
					if (aboveIndex==true) {
						listModel.remove(index+1);
						Main.getData().getAllTasks().remove(index+1);
					}
					else {
						listModel.remove(index);
						Main.getData().getAllTasks().remove(index);
					}
					//save task to data file for permanent storage
					Data.saveData(Main.getData());
					pm.setVisible(false);
				}
			}
			
			public boolean canImport(TransferHandler.TransferSupport support) {
				return support.isDataFlavorSupported(DataFlavor.stringFlavor);
			}
			
			public boolean importData(TransferHandler.TransferSupport support) {
				try {
					String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor); //get moved list item in the form of a string
					JList.DropLocation dl = (JList.DropLocation) support.getDropLocation(); //get drop location
					listModel.add(dl.getIndex(), s); //add list item to dropped location
					Main.getData().getAllTasks().add(dl.getIndex(), Main.getData().getAllTasks().get(index));
					Data.saveData(Main.getData());
					
					if (dl.getIndex()<index)
						aboveIndex=true;
					else
						aboveIndex=false;
					return true;
					
				} catch (UnsupportedFlavorException | IOException e) {
					e.printStackTrace();
				}
				return false;
			}
		});
		
		newTaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				name.setText("");
				name.setColumns(100);
				listOfTasks.clearSelection();
				pm.show(panel, 600, 35);	
			}
		});
		
		sortButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.getData().setAllTasks(sortTasks(Main.getData().getAllTasks()));
				//Main.getData().allTasks = sortTasks(Main.getData().allTasks);
				
				//update JList with sorted tasks
				listModel.clear();
				for (int i=0; i<Main.getData().getAllTasks().size(); i++) {
					listModel.add(i, Main.getData().getAllTasks().get(i).getName());
				}
				Data.saveData(Main.getData());
			}
		});
		
		//if task has been selected
		listOfTasks.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int i=listOfTasks.getSelectedIndex();
				if (i!=-1) {
					name.setText(String.valueOf(listOfTasks.getSelectedValue()));
					Rectangle r = listOfTasks.getCellBounds(i, i);
					Point p = SwingUtilities.convertPoint(listOfTasks, r.x + 25, r.y, panel);
					pm.setPreferredSize(new Dimension(panel.getWidth()-25, r.height));
					pm.show(panel, p.x, p.y);
					panel.revalidate();
					panel.repaint();
				}
			}
		});
		
		listOfTasks.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				int i = listOfTasks.locationToIndex(e.getPoint());
				if (i!=-1) {
					Rectangle cellBounds = listOfTasks.getCellBounds(i, i);
					//check clicked area is within first 20 pixels of list item (area that icon fills)
					if (e.getX()-cellBounds.x<20) { 
						completedTasks++;
						numCompleteTasks.setText("Tasks Completed Today: " + completedTasks);
						removeTask(i);
						redoStack.clear();
						Main.playSound("/sounds/Ding Sound Effect.wav");
					}
				}
			}
			//necessary methods
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		name.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				//if enter key is pushed
				if(e.getKeyCode() == KeyEvent.VK_ENTER && !name.getText().isEmpty()) {
					int i=listOfTasks.getSelectedIndex();
					//if task name has been changed, edit task 
					if (!name.getText().equals(String.valueOf(listOfTasks.getSelectedValue()))){
						if(i!=-1) {
							listModel.setElementAt(name.getText(), i);
							Main.getData().getAllTasks().get(i).editTask(name.getText());
							Data.saveData(Main.getData());
						}
						else
							createTask(name.getText());
					}
					
					//reset selected list item
					listOfTasks.clearSelection();
					pm.setVisible(false);
				}
			}
		});
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i=listOfTasks.getSelectedIndex();
				if (i!=-1) {
					removeTask(i);
					redoStack.clear();
				}
				else
					pm.setVisible(false);
			}
		});
		
		// if mouse is clicked on button panel, remove selection on list
		buttonPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!sp.getBounds().contains(e.getPoint())) {
					listOfTasks.clearSelection();
				}
			}
		});
		
		//if ctrl + z is hit
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "undo");
		panel.getActionMap().put("undo", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!undoStack.isEmpty()) {
					Task currTask = undoStack.pop();
					
					listModel.addElement(currTask.getName());
					Main.getData().getAllTasks().add(currTask);
					Main.getData().addTask();
					//Main.getData().numTasks++;
					completedTasks--;
					numCompleteTasks.setText("Tasks Completed Today: " + completedTasks);
					Data.saveData(Main.getData());
					
					redoStack.push(currTask);
				}
			}
		});
		
		//if ctrl + y is hit
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "redo");
		panel.getActionMap().put("redo", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!redoStack.isEmpty()) {
					Task currTask = redoStack.pop();
					//find index of task to remove
					int index=0;
					for (int i=0; i<Main.getData().getAllTasks().size(); i++) {
						Task task = Main.getData().getAllTasks().get(i);
						if (task.getName().equals(currTask.getName())) {
							index=i;
							break;
						}
					}
					completedTasks++;
					numCompleteTasks.setText("Tasks Completed Today: " + completedTasks);
					removeTask(index);
				}
			}
		});
	}
	
	private void formatButton (JButton button) {
		button.setFont(Main.getCustomFont().deriveFont(20f));
		button.setBackground(new Color(64, 34, 22));
		button.setForeground(Color.WHITE);
	}
	
	private void createTask (String name) {
		//create new checklist item
		listModel.addElement(name);
		
		//add new task to arraylist
		Main.getData().getAllTasks().add(new Task(name));
		Main.getData().addTask();
		//Main.getData().numTasks++;
		
		Data.saveData(Main.getData());
	}
	
	private void removeTask (int i) {
		undoStack.push(Main.getData().getAllTasks().get(i));

		listModel.removeElementAt(i);
		Main.getData().getAllTasks().remove(i);
		Main.getData().subtractTask();
		//Main.getData().numTasks--;
		pm.setVisible(false);
		
		//reset selected list item
		listOfTasks.clearSelection();
		
		//save task to data file for permanent storage
		Data.saveData(Main.getData());
	}
	
	private ArrayList<Task> sortTasks(ArrayList<Task> nums) {
		if (nums.size()<=1)
			return nums;
		
		ArrayList<Task> left = new ArrayList<Task>();
		ArrayList<Task> right = new ArrayList<Task>();
		
		for (int i=0; i<nums.size()/2; i++)
			left.add(nums.get(i));
		for(int i=0; i<nums.size() - nums.size()/2; i++)
			right.add(nums.get(i+nums.size()/2));
		
		left=sortTasks(left);
		right=sortTasks(right);
		return merge(nums, left, right);
	}
	
	private ArrayList<Task> merge(ArrayList<Task> nums, ArrayList<Task> left, ArrayList<Task> right) {
		int i=0, j=0, k=0;
		
		while(i<left.size() && j<right.size()) {
			if(left.get(i).getDateCreated().isBefore(right.get(j).getDateCreated())) {
				nums.set(k, left.get(i));
				i++;
			}
			else {
				nums.set(k, right.get(j));
				j++;
			}
			k++;
		}
		
		while(i<left.size()) {
			nums.set(k, left.get(i));
			i++;
			k++;
		}
		while(j<right.size()) {
			nums.set(k, right.get(j));
			j++;
			k++;
		}
		
		return nums;
	}
	
	class TaskRenderer extends DefaultListCellRenderer{
		private final ImageIcon icon;
		
		public TaskRenderer() {
			URL imgURL = getClass().getResource("/images/Checkbox.png");
			Image scaled = new ImageIcon(imgURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
			icon = new ImageIcon(scaled);
		}
		
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(icon);
            return label;
		}
	}
}
