package cs2103.v15_1j.jolt.uifeedback;

import cs2103.v15_1j.jolt.model.TaskEvent;
import cs2103.v15_1j.jolt.ui.MainViewController;

public class UnmarkFeedback implements UIFeedback {

	private TaskEvent task;

	public UnmarkFeedback(TaskEvent task) {
		this.task = task;
	}

	public TaskEvent getTask() {
		return task;
	}

	@Override
	public void execute(MainViewController con) {
		con.showNotification("\"" + task.getName() + "\" has been marked as not completed.");
	}

	@Override
	public boolean equals(Object t) {
		if (t == null || !(t instanceof UnmarkFeedback)) {
			return false;
		}
		UnmarkFeedback other = (UnmarkFeedback) t;
		return this.task.equals(other.task);
	}
}
