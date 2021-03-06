package cs2103.v15_1j.jolt.command;

/* @@author A0124995R */

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import cs2103.v15_1j.jolt.command.AddCommand;
import cs2103.v15_1j.jolt.command.DeleteCommand;
import cs2103.v15_1j.jolt.command.UndoableCommand;
import cs2103.v15_1j.jolt.controller.ControllerStates;
import cs2103.v15_1j.jolt.model.DataLists;
import cs2103.v15_1j.jolt.model.DeadlineTask;
import cs2103.v15_1j.jolt.model.Event;
import cs2103.v15_1j.jolt.model.FloatingTask;
import cs2103.v15_1j.jolt.uifeedback.AddFeedback;
import cs2103.v15_1j.jolt.uifeedback.DeleteFeedback;
import cs2103.v15_1j.jolt.uifeedback.FailureFeedback;
import cs2103.v15_1j.jolt.uifeedback.UIFeedback;

public class DeleteCommandTest {

    ControllerStates conStates;
    DataLists masterList = new DataLists();
    FloatingTask task1 = new FloatingTask("task 1");
    DeadlineTask task2 = new DeadlineTask("task 2", LocalDateTime.of(2016, 10, 10, 10, 10));
    Event event3 = new Event("event 3", LocalDateTime.of(2016, 10, 10, 10, 10),
            LocalDateTime.of(2016, 11, 11, 11, 11));
    StubStorage storage;
    Stack<UndoableCommand> undoCommandHistory;
    Stack<UndoableCommand> redoCommandHistory;

    @Before
    public void setUp() throws Exception {
        masterList.add(task1);
        masterList.add(task2);
        masterList.add(event3);
        this.storage = new StubStorage();
        undoCommandHistory = new Stack<UndoableCommand>();
        redoCommandHistory = new Stack<UndoableCommand>();

        conStates = new ControllerStates();
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
        DeleteCommand command = new DeleteCommand('d', 1);

        UIFeedback result = command.execute(conStates);
        assertTrue(result instanceof DeleteFeedback);
        DeleteFeedback feedback = (DeleteFeedback) result;
        assertEquals(task2, feedback.getTaskEvent());
        assertTrue(masterList.getDeadlineTasksList().isEmpty());

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);

        command = new DeleteCommand('e', 1);
        result = command.execute(conStates);
        assertTrue(result instanceof DeleteFeedback);
        feedback = (DeleteFeedback) result;
        assertEquals(event3, feedback.getTaskEvent());
        assertTrue(masterList.getEventsList().isEmpty());

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);

        command = new DeleteCommand('f', 1);
        result = command.execute(conStates);
        assertTrue(result instanceof DeleteFeedback);
        feedback = (DeleteFeedback) result;
        assertEquals(task1, feedback.getTaskEvent());
        assertTrue(masterList.getFloatingTasksList().isEmpty());

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);
    }
    
    @Test
    public void testInvalidNumber() {
        DeleteCommand command = new DeleteCommand('e', -1);
        
        UIFeedback result = command.execute(conStates);
        assertTrue(result instanceof FailureFeedback);
        FailureFeedback feedback = (FailureFeedback) result;
        assertEquals("There is no item numbered e-1", feedback.getMessage());
        command = new DeleteCommand('d', 0);

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);

        result = command.execute(conStates);
        assertTrue(result instanceof FailureFeedback);
        feedback = (FailureFeedback) result;
        assertEquals("There is no item numbered d0", feedback.getMessage());
        command = new DeleteCommand('f', 100);

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);

        result = command.execute(conStates);
        assertTrue(result instanceof FailureFeedback);
        feedback = (FailureFeedback) result;
        assertEquals("There is no item numbered f100", feedback.getMessage());

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);
    }
    
    @Test
    public void testStorageError() {
        assertTrue(masterList.getDeadlineTasksList().contains(task2));
        assertTrue(masterList.getDeadlineTasksList().contains(task2));
        DeleteCommand command = new DeleteCommand('d', 1);
        storage.setStorageError();

        UIFeedback result = command.execute(conStates);
        assertTrue(result instanceof FailureFeedback);
        FailureFeedback feedback = (FailureFeedback) result;
        assertEquals("Some error has occured. Please try again.",
                feedback.getMessage());
        assertTrue(masterList.getDeadlineTasksList().contains(task2));

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);
    }

    @Test
    public void testUndo() {
		AddCommand addCommand = new AddCommand("buy eggs", LocalDateTime.now());
		addCommand.execute(conStates);
		AddFeedback expectedFeedback = new AddFeedback(addCommand.getTaskEvent());
		assertEquals(4, masterList.size());

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);
		
		DeleteCommand deleteCommand = new DeleteCommand('d', 1);
		deleteCommand.execute(conStates);
		assertEquals(3, masterList.size());

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);
		
		UIFeedback actualFeedback = deleteCommand.undo(conStates);
		assertEquals(4, masterList.size());

        conStates.displayList = new DataLists();
        conStates.displayList.copy(conStates.masterList);
		
		assertEquals(expectedFeedback, actualFeedback);
    }
}