package cs2103.v15_1j.jolt.command;

import static org.junit.Assert.*;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import cs2103.v15_1j.jolt.command.Command;
import cs2103.v15_1j.jolt.command.HelpCommand;
import cs2103.v15_1j.jolt.command.UndoableCommand;
import cs2103.v15_1j.jolt.controller.ControllerStates;
import cs2103.v15_1j.jolt.model.DataLists;
import cs2103.v15_1j.jolt.uifeedback.HelpFeedback;
import cs2103.v15_1j.jolt.uifeedback.UIFeedback;

public class HelpCommandTest {
	ControllerStates conStates;
	DataLists masterList;
	StubStorage storage;
	Stack<UndoableCommand> undoCommandHistory;
	Stack<UndoableCommand> redoCommandHistory;
	
	@Before
	public void setUp() {
		conStates = new ControllerStates();
		masterList = new DataLists();
        undoCommandHistory = new Stack<UndoableCommand>();
        redoCommandHistory = new Stack<UndoableCommand>();
		storage = new StubStorage();
		
        conStates.masterList = masterList;
        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);
        conStates.searchResultsList = new DataLists();
        conStates.storage = storage;
        conStates.undoCommandHistory = undoCommandHistory;
        conStates.redoCommandHistory = redoCommandHistory;
	}

    @Test
    public void testExecute() {
        Command help = new HelpCommand("index");
        UIFeedback feedback = help.execute(null);
        assertTrue(feedback instanceof HelpFeedback);
        assertEquals("index", ((HelpFeedback)feedback).getPage());
    }
}
