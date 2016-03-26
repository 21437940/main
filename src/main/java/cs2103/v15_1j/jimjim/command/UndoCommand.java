package cs2103.v15_1j.jimjim.command;

import java.util.Stack;

import cs2103.v15_1j.jimjim.model.DataLists;
import cs2103.v15_1j.jimjim.searcher.Searcher;
import cs2103.v15_1j.jimjim.storage.Storage;

class UndoCommand implements Command {

	@Override
	public String undo(DataLists displayList, DataLists masterList, Storage storage, Searcher searcher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String execute(DataLists displayList, DataLists masterList, Storage storage, Searcher searcher) { 
		return null;
	}
	
	public String execute(DataLists displayList, DataLists masterList, Storage storage, 
						  Searcher searcher, Stack<Command> undoCommandHistory) {
		if (undoCommandHistory.empty()) {
			return "Nothing to undo!";
		}
		Command latestCommand = undoCommandHistory.pop();
		latestCommand.execute(displayList, masterList, storage, searcher);
		return null;
	}

}
