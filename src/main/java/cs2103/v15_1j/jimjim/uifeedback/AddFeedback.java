package cs2103.v15_1j.jimjim.uifeedback;

import cs2103.v15_1j.jimjim.model.TaskEvent;
import cs2103.v15_1j.jimjim.ui.MainViewController;

public class AddFeedback implements UIFeedback {

	private TaskEvent taskEvent;
	
	public AddFeedback(TaskEvent taskEvent){
		this.taskEvent = taskEvent;
	}
	
	public TaskEvent getTaskEvent() {
        return taskEvent;
    }
	
	@Override
	public void execute(MainViewController con) {
		con.showNotification("\""+taskEvent.getName() + "\" has been added.");
	}

}
