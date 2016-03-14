package cs2103.v15_1j.jimjim.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class EventTime {

	private ObjectProperty<LocalDateTime> startDateTime;
	private ObjectProperty<LocalDateTime> endDateTime;

	public EventTime(LocalDateTime start, LocalDateTime end) {
	    this.setStartDateTime(start);
	    this.setEndDateTime(end);
    }

	public LocalDateTime getStartDateTime() {
		return startDateTime.get();
	}
	
	public void setStartDate(LocalDate startDate){
		LocalDateTime temp = LocalDateTime.of(startDate, startDateTime.get().toLocalTime());
		this.startDateTime = new SimpleObjectProperty<LocalDateTime>(temp);
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = new SimpleObjectProperty<LocalDateTime>(startDateTime);
	}
	
	public ObjectProperty<LocalDateTime> startDateTimeProperty(){
		return startDateTime;
	}
	
	public ObjectProperty<LocalDate> startDateProperty(){
		return new SimpleObjectProperty<LocalDate>(startDateTime.get().toLocalDate());
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime.get();
	}
	
	public void setEndDate(LocalDate endDate){
		LocalDateTime temp = LocalDateTime.of(endDate, startDateTime.get().toLocalTime());
		this.endDateTime = new SimpleObjectProperty<LocalDateTime>(temp);
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = new SimpleObjectProperty<LocalDateTime>(endDateTime);
	}
	
	public ObjectProperty<LocalDateTime> endDateTimeProperty(){
		return startDateTime;
	}
	
	public ObjectProperty<LocalDate> endDateProperty(){
		return new SimpleObjectProperty<LocalDate>(endDateTime.get().toLocalDate());
	}
}