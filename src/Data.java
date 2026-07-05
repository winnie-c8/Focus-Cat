import java.util.*;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class Data {
	private ArrayList<Task> allTasks = new ArrayList<Task>();
	private int numTasks;
	private double hoursFocused;
	private int coins=0;
	private TimeSeries series = new TimeSeries("Hours Productive");
	private transient TimeSeriesCollection seriesColl = new TimeSeriesCollection();
	private ArrayList<Item> itemsBought = new ArrayList<Item>();
	private Item[] allItems = {
			new Item("Black Cat", 0, -1, 950, 500, 400, 400, 10), new Item("Grey Cat", 150, -1, 930, 345, 400, 400, 9), 
			new Item("Brown Cat", 150, 9, 135, 350, 530, 555, 5), new Item("Orange Cat", 150, 12, 1275, 30, 300, 300, 8), 
			new Item("Calico Cat", 150, 12, 1275, 500, 285, 265, 11), new Item("White Cat", 150, 10, 540, 355, 480, 510, 6), 
			new Item("Ragdoll Cat", 150, -1, 550, 200, 350, 350, 7), new Item("Hanging Plant", 70, -1, 850, -20, 520, 510, 12), 
			new Item("Floor Plant", 70, -1, -125, 385, 475, 515, 13), new Item("Cardboard Box", 50, -1, 135, 350, 530, 555, 4), 
			new Item("Cat Bed", 40, -1, 520, 380, 560, 570, 3), new Item("Bookshelf", 100, -1, 645, -50, 545, 625, 1), 
			new Item("Cat Tree", 100, -1, 1095, 175, 600, 600, 2), new Item("Curtains", 60, -1, 0, 0, 1550, 800, 0)
	};
	
	private static final Gson gson = new GsonBuilder()
			.registerTypeAdapter(TimeSeries.class, new TimeSeriesSerializer())
			.registerTypeAdapter(TimeSeries.class, new TimeSeriesDeserializer())
			.setPrettyPrinting()
			.create();
	
	
	public Data() {
	}
	
	public static void saveData(Data data) {
		try (FileWriter writer = new FileWriter("data.json")) { 
	        gson.toJson(data, writer);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public static Data loadData() {
		try (FileReader reader = new FileReader("data.json")) {
			Data loaded = gson.fromJson(reader, Data.class);
			
			if (loaded.series == null) {
			    loaded.series = new TimeSeries("Hours Productive");
			}
			
			loaded.loadChart();
			
	        return loaded;
	    } catch (IOException e) {
	    	Data newData = new Data();
	    	newData.loadChart();
	        return newData;
	    }
	}
	
	public void loadChart() {
		seriesColl.addSeries(series);
	}
	
	public ArrayList<Task> getAllTasks(){
		return allTasks;
	}
	
	public void setAllTasks(ArrayList<Task> tasks) {
		allTasks = tasks;
	}
	
	public TimeSeries getSeries() {
		return series;
	}
	
	public TimeSeriesCollection getSeriesColl() {
		return seriesColl;
	}
	
	public ArrayList<Item> getItemsBought(){
		return itemsBought;
	}
	
	public Item[] getAllItems() {
		return allItems;
	}
	
	public Item getItem(int i) {
		return allItems[i];
	}
	
	public int getCoins() {
		return coins;
	}
	
	public void setCoins(int coins) {
		this.coins = coins;
	}
	
	public double getHoursFocused() {
		return hoursFocused;
	}
	
	public void setHoursFocused(double h) {
		hoursFocused = h;
	}
	
	public void addTask() {
		numTasks++;
	}
	
	public void subtractTask() {
		numTasks--;
	}
}

