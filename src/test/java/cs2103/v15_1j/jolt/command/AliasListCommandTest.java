package cs2103.v15_1j.jolt.command;

/* @@author A0124995R */

import static org.junit.Assert.*;

import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import cs2103.v15_1j.jolt.command.AliasAddCommand;
import cs2103.v15_1j.jolt.command.AliasListCommand;
import cs2103.v15_1j.jolt.command.UndoableCommand;
import cs2103.v15_1j.jolt.controller.Configuration;
import cs2103.v15_1j.jolt.controller.ControllerStates;
import cs2103.v15_1j.jolt.model.DataLists;
import cs2103.v15_1j.jolt.parser.JoltParser;
import cs2103.v15_1j.jolt.uifeedback.AliasListFeedback;
import cs2103.v15_1j.jolt.uifeedback.UIFeedback;

public class AliasListCommandTest {
    ControllerStates conStates;
    DataLists masterList;
    DataLists displayList;
    StubStorage storage;
    Configuration config;
    Stack<UndoableCommand> undoCommandHistory;
    Stack<UndoableCommand> redoCommandHistory;
    JoltParser parser;
    String alias1;
	String keywordString1;
	String alias2;
	String keywordString2;

    @Before
    public void setUp() throws Exception {
        masterList = new DataLists();
        displayList = new DataLists();
        storage = new StubStorage();
        config = new Configuration();
        undoCommandHistory = new Stack<UndoableCommand>();
        redoCommandHistory = new Stack<UndoableCommand>();
        parser = new JoltParser();

        conStates = new ControllerStates();
        conStates.masterList = masterList;
        conStates.displayList = displayList;
        conStates.storage = storage;
        conStates.config = config;
        conStates.undoCommandHistory = undoCommandHistory;
        conStates.redoCommandHistory = redoCommandHistory;
        conStates.parser = parser;
        
		alias1 = "be";
		int keyword1 = 1;
		keywordString1 = "buy eggs";
		
		alias2 = "del";
		int keyword2 = 2;
		keywordString1 = "delete";
		
		AliasAddCommand aliasAdd1 = new AliasAddCommand(alias1, keyword1, keywordString1);
		AliasAddCommand aliasAdd2 = new AliasAddCommand(alias2, keyword2, keywordString2);
		aliasAdd1.execute(conStates);
		aliasAdd2.execute(conStates);
		assertEquals(2, conStates.config.aliases.size());
    }
	@Test
	public void test() {
		AliasListCommand listCommand = new AliasListCommand();
		UIFeedback feedback = listCommand.execute(conStates);
		
		assertTrue(feedback instanceof AliasListFeedback);
	}

}
