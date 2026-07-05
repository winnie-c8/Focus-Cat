import java.lang.reflect.Type;
import org.jfree.data.time.*;
import com.google.gson.*;

//use custom deserializer to read time series
public class TimeSeriesDeserializer implements JsonDeserializer<TimeSeries>{
	public TimeSeries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		TimeSeries series = new TimeSeries("Hours Productive");
		JsonArray array = json.getAsJsonArray();
		
		for (int i=0; i<array.size(); i++) {
			JsonObject obj = array.get(i).getAsJsonObject();
			int year = obj.get("year").getAsInt();
			int month = obj.get("month").getAsInt();
			int day = obj.get("day").getAsInt();
			double value = obj.get("value").getAsDouble();
			
			series.addOrUpdate(new Day(day, month, year), value);
		}
		
		return series;
	}
	
}
