package cs2103.v15_1j.jimjim;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cs2103.v15_1j.jimjim.command.AddCommand;
import cs2103.v15_1j.jimjim.model.DataLists;
import cs2103.v15_1j.jimjim.model.DeadlineTask;
import cs2103.v15_1j.jimjim.model.Event;
import cs2103.v15_1j.jimjim.model.EventTime;
import cs2103.v15_1j.jimjim.model.Task;
import cs2103.v15_1j.jimjim.uifeedback.AddFeedback;
import cs2103.v15_1j.jimjim.uifeedback.FailureFeedback;
import cs2103.v15_1j.jimjim.uifeedback.UIFeedback;

public class AddCommandTest {
    
    DataLists displayList;
    DataLists masterList;
    StubStorage storage;

    @Before
    public void setUp() throws Exception {
        this.displayList = new DataLists();
        this.masterList = new DataLists();
        this.storage = new StubStorage();
    }

    @Test
    public void testAddFloatingTask() {
        AddCommand command =
                new AddCommand("Buy oranges");
        UIFeedback result = command.execute(displayList, masterList, storage, null);
        assertTrue(result instanceof AddFeedback);
        AddFeedback addFeedback = (AddFeedback) result;
        assertEquals(command.getTaskEvent(), addFeedback.getTaskEvent());
        assertEquals(1, displayList.getFloatingTasksList().size());
        assertEquals("Buy oranges", displayList.getFloatingTasksList().get(0).getName());
        assertTrue(displayList.getFloatingTasksList().get(0)instanceof Task);
    }

    @Test
    public void testAddDeadlineTask() {
        AddCommand command =
                new AddCommand("Buy oranges",
                                   LocalDateTime.of(2016, 4, 30, 12, 00));
        UIFeedback result = command.execute(displayList, masterList, storage, null);
        assertTrue(result instanceof AddFeedback);
        AddFeedback addFeedback = (AddFeedback) result;
        assertEquals(command.getTaskEvent(), addFeedback.getTaskEvent());
        assertEquals(1, displayList.getDeadlineTasksList().size());
        assertEquals("Buy oranges", displayList.getDeadlineTasksList().get(0).getName());
        assertTrue(displayList.getDeadlineTasksList().get(0)instanceof DeadlineTask);
        assertEquals(LocalDateTime.of(2016, 4, 30, 12, 00),
                ((DeadlineTask)displayList.getDeadlineTasksList().get(0)).getDateTime());
    }
    
    @Test
    public void testAddEvent() {
    	LocalDateTime startDateTime = LocalDateTime.of(2016, 4, 30, 12, 00);
    	LocalDateTime endDateTime = LocalDateTime.of(2016, 4, 30, 16,00);
        AddCommand command =
                new AddCommand("Meeting with boss", startDateTime, endDateTime);
        UIFeedback result = command.execute(displayList, masterList, storage, null);
        
        assertTrue(result instanceof AddFeedback);
        AddFeedback addFeedback = (AddFeedback) result;
        assertEquals(command.getTaskEvent(), addFeedback.getTaskEvent());
        assertEquals(1, displayList.getEventsList().size());
        
        assertEquals("Meeting with boss", displayList.getEventsList().get(0).getName());
        assertTrue(displayList.getEventsList().get(0) instanceof Event);
        Event event = (Event) displayList.getEventsList().get(0);
        List<EventTime> eventTimeList = event.getDateTimes();
        
        assertEquals(startDateTime, eventTimeList.get(0).getStartDateTime());
        assertEquals(endDateTime, eventTimeList.get(0).getEndDateTime());
    }

    @Test
    public void testStorageError() {
        AddCommand command =
                new AddCommand("Storage error",
                                   LocalDateTime.of(2016, 4, 30, 12, 00));
        // Make sure storage fails
        storage.setStorageError();
        UIFeedback result = command.execute(displayList, masterList, storage, null);
        assertTrue(result instanceof FailureFeedback);
        FailureFeedback feedback = (FailureFeedback) result;
        assertEquals("Some error has occured. Please try again.",
                feedback.getMessage());
        assertEquals(true, displayList.getDeadlineTasksList().isEmpty());
    }

}