package nine.bolo.puzzle.data;

import org.json.JSONException;
import org.json.JSONObject;

public class VOScore {
	public VOScore(String level, int mapid, String name, int moved,
			double timeused) {
		super();
		this.level = level;
		this.mapid = mapid;
		this.name = name;
		this.moved = moved;
		this.timeused = timeused;
	}
	public VOScore(JSONObject jo) throws JSONException {
		this.level = jo.getString("level");
		this.mapid = Integer.parseInt(jo.getString("mapid"));
		this.name = jo.getString("name");
		this.moved = Integer.parseInt(jo.getString("moved"));
		this.timeused = Double.parseDouble(jo.getString("timeused"));
	}
	public VOScore() {
		// TODO Auto-generated constructor stub
	}
	public String level;
	public int mapid;
	public String name;
	public int moved;
	public double timeused;
}
