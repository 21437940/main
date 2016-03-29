package cs2103.v15_1j.jimjim.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Stack;

import cs2103.v15_1j.jimjim.model.DataLists;
import cs2103.v15_1j.jimjim.model.DeadlineTask;
import cs2103.v15_1j.jimjim.model.Event;
import cs2103.v15_1j.jimjim.model.EventTime;
import cs2103.v15_1j.jimjim.model.FloatingTask;
import cs2103.v15_1j.jimjim.model.TaskEvent;
import cs2103.v15_1j.jimjim.searcher.Searcher;
import cs2103.v15_1j.jimjim.storage.Storage;
import cs2103.v15_1j.jimjim.uifeedback.ChangeFeedback;
import cs2103.v15_1j.jimjim.uifeedback.DeleteFeedback;
import cs2103.v15_1j.jimjim.uifeedback.FailureFeedback;
import cs2103.v15_1j.jimjim.uifeedback.UIFeedback;

public class ChangeCommand implements UndoableCommand {

    private int taskNum;
    private char prefix;
    private String newName;
    private LocalDate newStartDate;
    private LocalTime newStartTime;
    private LocalDate newEndDate;
    private LocalTime newEndTime;
    private TaskEvent backup;
    
    public ChangeCommand(char prefix, int num, String newName, LocalDate newDate,
            LocalTime newTime, LocalDate newEndDate, LocalTime newEndTime) {
        this.taskNum = num;
        this.prefix = prefix;
        this.newName = newName;
        this.newStartDate = newDate;
        this.newStartTime = newTime;
        this.newEndDate = newEndDate;
        this.newEndTime = newEndTime;
    }
    
    public int getTaskNum() {
        return this.taskNum;
    }
    
    public char getPrefix() {
        return this.prefix;
    }
    
    public String getNewName() {
        return newName;
    }
    
    public LocalDate getNewDate() {
        return newStartDate;
    }
    
    public LocalTime getNewTime() {
        return newStartTime;
    }
    
    public LocalDate getNewEndDate() {
        return newEndDate;
    }
    
    public LocalTime getNewEndTime() {
        return newEndTime;
    }
    
	@Override
	public UIFeedback undo(DataLists searchResultsList, DataLists masterList, Storage storage, Searcher searcher,
			Stack<UndoableCommand> undoCommandHistory, Stack<UndoableCommand> redoCommandHistory) {
		TaskEvent temp = masterList.removeTaskEvent(taskNum-1, prefix);
		masterList.add(taskNum-1, backup);
		if (storage.save(masterList)) {
			redoCommandHistory.push(this);
	        return new ChangeFeedback("Task/Event successfully changed back!");
	    } else {
	        masterList.removeTaskEvent(taskNum-1, prefix);
	        masterList.add(taskNum, backup);
	        undoCommandHistory.push(this);
	        return new FailureFeedback("Some error has occured. Please try again.");
	    }	
	}
	
	@Override
	public UIFeedback execute(DataLists searchResultsList, DataLists masterList, 
							  Storage storage, Searcher searcher, Stack<UndoableCommand> undoCommandHistory,
							  Stack<UndoableCommand> redoCommandHistory) {
		try {
			TaskEvent temp = masterList.getTaskEvent(taskNum-1, prefix);
			if (temp instanceof FloatingTask) {
				backup = (FloatingTask) temp;
			} else if (temp instanceof DeadlineTask) {
				backup = (DeadlineTask) temp;
			} else {
				backup = (Event) temp;
			}
            DeadlineTask tempDeadlineTask = null;
            Event tempEvent = null;
            if (temp instanceof DeadlineTask) {
            	tempDeadlineTask = (DeadlineTask) temp;
            } else if (temp instanceof Event) {
            	tempEvent = (Event) temp;
            }
            
            if (newName != null) {
            	temp.setName(newName);
            }
            if (newStartDate != null) {
            	if (temp instanceof DeadlineTask) {
            		tempDeadlineTask.setDate(newStartDate);
            	} else if (temp instanceof Event) {
            		EventTime currentEventTime = tempEvent.getDateTimes().get(0);
            		currentEventTime.setStartDate(newStartDate);
            	}
            }
            if (newStartTime != null) {
            	if (temp instanceof DeadlineTask) {
            		tempDeadlineTask.setTime(newStartTime);
            	} else if (temp instanceof Event) {
            		EventTime currentEventTime = tempEvent.getDateTimes().get(0);
            		currentEventTime.setStartTime(newStartTime);
            	}
            }
            if (newEndDate != null) {
            	if (temp instanceof Event) {
            		EventTime currentEventTime = tempEvent.getDateTimes().get(0);
            		currentEventTime.setEndDate(newEndDate);
            	}
            }
            if (newEndTime != null) {
            	if (temp instanceof Event) {
            		EventTime currentEventTime = tempEvent.getDateTimes().get(0);
            		currentEventTime.setEndTime(newEndTime);
            	}
            }
            if (storage.save(masterList)) {
            	undoCommandHistory.push(this);
                return new ChangeFeedback("Task/Event successfully changed!");
            } else {
            	redoCommandHistory.push(this);
            	masterList.remove(temp);
            	masterList.add(taskNum-1, backup);
                return new FailureFeedback("Some error has occured. Please try again.");
            }
		} catch (IndexOutOfBoundsException e) {
			return new FailureFeedback(
                    "There is no item numbered " + this.prefix + this.taskNum);
		}
	}
}