import java.lang.reflect.Type;
import org.jfree.data.time.*;
import com.google.gson.*;

//use custom serializer to store time series
public class TimeSeriesSerializer implements JsonSerializer<TimeSeries>{
	public JsonElement serialize(TimeSeries src, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray array = new JsonArray();
		
		for (int i=0; i<src.getItemCount(); i++) {
			TimeSeriesDataItem item = src.getDataItem(i);
			Day day = (Day) item.getPeriod();
			JsonObject obj = new JsonObject();
			
			obj.addProperty("year", day.getYear());
			obj.addProperty("month", day.getMonth());
			obj.addProperty("day", day.getDayOfMonth());
			obj.addProperty("value", item.getValue().doubleValue());
			
			array.add(obj);
		}
		return array;
	}
	
}
