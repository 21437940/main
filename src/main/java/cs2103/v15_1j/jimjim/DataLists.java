package cs2103.v15_1j.jimjim;

import java.util.ArrayList;
import java.util.List;

import cs2103.v15_1j.jimjim.model.DeadlineTask;
import cs2103.v15_1j.jimjim.model.FloatingTask;
import cs2103.v15_1j.jimjim.model.Event;

public class DataLists {
	private List<DeadlineTask> tasksList;
	private List<FloatingTask> floatingTasksList;
	private List<Event> eventsList;
	
	public DataLists() {
		this.tasksList = new ArrayList<DeadlineTask>();
		this.floatingTasksList = new ArrayList<FloatingTask>();
		this.eventsList = new ArrayList<Event>();
	}
	
	public List<DeadlineTask> getTasksList() {
		return tasksList;
	}

	public List<FloatingTask> getFloatingTasksList() {
		return floatingTasksList;
	}

	public List<Event> getEventsList() {
		return eventsList;
	}
}
