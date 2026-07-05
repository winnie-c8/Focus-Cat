import java.time.LocalDate;


public class Task {
	private String name;
	private String dateCreated;
	
	public Task(String name) {
		this.name=name;
		//convert to string so it can be easily saved to gson
		dateCreated=LocalDate.now().toString();
	}
	
	public String getName() {
		return name;
	}
	
	public void editTask(String n) {
		name = n;
	}
	
	public LocalDate getDateCreated() {
		return LocalDate.parse(dateCreated);
	}
}
