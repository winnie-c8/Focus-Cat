
public class Item {
	private String name;
	private int price, prerequisiteIndex, x, y, width, height, layer;
	private boolean isSold, isVisible;
		
	public Item(String name, int price, int prerequisiteIndex, int x, int y, int width, int height, int layer) {
		this.name=name;
		this.price=price;
		this.prerequisiteIndex = prerequisiteIndex;
		isSold=false;
		isVisible=true;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.layer = layer;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getPrereq() {
		return prerequisiteIndex;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLayer() {
		return layer;
	}
	
	public boolean getIsSold() {
		return isSold;
	}
	
	public boolean getIsVisible() {
		return isVisible;
	}
	
	public void setIsSold (boolean isSold) {
		this.isSold = isSold;
	}
	
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
}
