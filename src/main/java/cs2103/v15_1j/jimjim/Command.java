package cs2103.v15_1j.jimjim;
import java.util.List;

public interface Command {
	public String undo(List<TaskEvent> displayList,
					 Storage storage, Searcher searcher);
	public String execute(List<TaskEvent> displayList,
						Storage storage, Searcher searcher);
}
