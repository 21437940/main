package cs2103.v15_1j.jimjim.parser;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.antlr.v4.runtime.tree.TerminalNode;

import cs2103.v15_1j.jimjim.antlr4.UserCommandBaseVisitor;
import cs2103.v15_1j.jimjim.antlr4.UserCommandParser;
import cs2103.v15_1j.jimjim.antlr4.UserCommandParser.OverdueFilterContext;
import cs2103.v15_1j.jimjim.command.*;
import cs2103.v15_1j.jimjim.searcher.*;

public class JJCommandVisitor extends UserCommandBaseVisitor<Command> {
	
	private LocalDateTime dateTime = LocalDateTime.MIN;
	private String string;
	private String userCommand;
	private ArrayList<Filter> filters;
	private ArrayList<String> keywords;
	
	public JJCommandVisitor(String userCommand) {
		this.userCommand = userCommand;
	}
	
	//----------TYPES OF COMMAND-------------

	@Override
	public Command visitAddFloatingTask(
			UserCommandParser.AddFloatingTaskContext ctx) {
		visit(ctx.string());
		return new AddCommand(string);
	}

	@Override
	public Command visitAddTask(UserCommandParser.AddTaskContext ctx) {
		visit(ctx.string());
		if (ctx.date() != null) {
            dateTime = dateTime.with(LocalTime.MAX);
            visit(ctx.date());
		} else if (ctx.time() != null) {
            dateTime = LocalDate.now().atStartOfDay();
            visit(ctx.time());
		} else if (ctx.datetime() != null) {
		    visit(ctx.datetime());
		} else {
		    assert false; // shouldn't happen
		}
		return new AddCommand(string, dateTime);
	}

	@Override
	public Command visitAddEvent(UserCommandParser.AddEventContext ctx) {
		visit(ctx.string());
		visit(ctx.datetime(0));
		LocalDateTime start = dateTime;
		visit(ctx.datetime(1));
		LocalDateTime end = dateTime;
		if (start.isBefore(end)) {
		    return new AddCommand(string, start, end);
		} else {
		    return new InvalidCommand("Please ensure that the event's"
		            + " ending time is after its starting time");
		}
    }

	@Override
	public Command visitAddEventCommonDate(
	        UserCommandParser.AddEventCommonDateContext ctx) {
		visit(ctx.string());
		visit(ctx.date());
		LocalDate date = dateTime.toLocalDate();
		visit(ctx.time(0));
		LocalDateTime start = dateTime;
		dateTime = date.atStartOfDay();
		visit(ctx.time(1));
		LocalDateTime end = dateTime;
		if (start.isBefore(end)) {
		    return new AddCommand(string, start, end);
		} else {
		    return new InvalidCommand("Please ensure that the event's"
		            + " ending time is after its starting time");
		}
    }
	
	@Override
	public Command visitAddEventWithoutEndTime(
	        UserCommandParser.AddEventWithoutEndTimeContext ctx) {
		visit(ctx.string());
		visit(ctx.datetime());
		// default duration is 1 hour
		return new AddCommand(string, dateTime, dateTime.plusHours(1));
	}

	@Override
	public Command visitAddEventMissingEndDate(
	        UserCommandParser.AddEventMissingEndDateContext ctx) {
		visit(ctx.string());
		visit(ctx.datetime());
		LocalDateTime start = dateTime;
		visit(ctx.time());
		LocalDateTime end = dateTime;
		if (start.isBefore(end)) {
		    return new AddCommand(string, start, end);
		} else {
		    return new InvalidCommand("Please ensure that the event's"
		            + " ending time is after its starting time");
		}
    }

	@Override
    public Command visitDelCmd(UserCommandParser.DelCmdContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
        return new DeleteCommand(itemNum.charAt(0),
                Integer.parseInt(itemNum.substring(1)));
    }

	@Override
	public Command visitMarkDoneCmd(UserCommandParser.MarkDoneCmdContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
	    if (itemNum.charAt(0) == 'e') {
	        return new InvalidCommand(itemNum + " is not a valid task!");
	    } else {
	        return new MarkDoneCommand(itemNum.charAt(0),
                Integer.parseInt(itemNum.substring(1)));
	    }
    }
	
	@Override
	public Command visitUnmarkCmd(UserCommandParser.UnmarkCmdContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
	    if (itemNum.charAt(0) == 'e') {
	        return new InvalidCommand(itemNum + " is not a valid task!");
	    } else {
	        return new UnmarkCommand(itemNum.charAt(0),
                Integer.parseInt(itemNum.substring(1)));
	    }
    }
	
	@Override
	public Command visitSearchCmd(UserCommandParser.SearchCmdContext ctx) {
	    filters = new ArrayList<>();
	    keywords = new ArrayList<>();
	    visitChildren(ctx);
	    // combine all keyword filters into 1 for efficiency
	    if (keywords.size() > 0) {
	        filters.add(new KeywordFilter(keywords));
	    }
	    return new SearchCommand(filters);
	}
	
	@Override
	public Command visitHideSearchCmd(UserCommandParser.HideSearchCmdContext ctx) {
	    return new HideSearchCommand();
	}

	@Override
	public Command visitHelpCmd(UserCommandParser.HelpCmdContext ctx) {
	    return new HelpCommand();
	}
	
	@Override
	public Command visitUndoCmd(UserCommandParser.UndoCmdContext ctx) {
	    return new UndoCommand();
	}
	
	@Override
	public Command visitRedoCmd(UserCommandParser.RedoCmdContext ctx) {
	    return new RedoCommand();
	}
	
	@Override
	public Command visitRename(UserCommandParser.RenameContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
        char prefix = itemNum.charAt(0);
        int taskNum = Integer.parseInt(itemNum.substring(1));
	    visit(ctx.string());
	    return new ChangeCommand(prefix, taskNum, string, null, null, null, null);
	};
	
	@Override
	public Command visitChangeDate(UserCommandParser.ChangeDateContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
        char prefix = itemNum.charAt(0);
        int taskNum = Integer.parseInt(itemNum.substring(1));
	    visit(ctx.date());
	    return new ChangeCommand(prefix, taskNum, null, dateTime.toLocalDate(),
	            null, null, null);
	};
	
	@Override
	public Command visitChangeTime(UserCommandParser.ChangeTimeContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
        char prefix = itemNum.charAt(0);
        int taskNum = Integer.parseInt(itemNum.substring(1));
	    visit(ctx.time());
	    return new ChangeCommand(prefix, taskNum, null, null,
	            dateTime.toLocalTime(), null, null);
	};
	
	@Override
	public Command visitChangeDateTime(UserCommandParser.ChangeDateTimeContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
        char prefix = itemNum.charAt(0);
        int taskNum = Integer.parseInt(itemNum.substring(1));
	    visit(ctx.datetime());
	    return new ChangeCommand(prefix, taskNum, null, dateTime.toLocalDate(),
	            dateTime.toLocalTime(), null, null);
	};
	
	@Override
	public Command visitChangeEndDate(UserCommandParser.ChangeEndDateContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
        char prefix = itemNum.charAt(0);
        int taskNum = Integer.parseInt(itemNum.substring(1));
	    visit(ctx.date());
	    return new ChangeCommand(prefix, taskNum, null, null,
	            null, dateTime.toLocalDate(), null);
	};
	
	@Override
	public Command visitChangeEndTime(UserCommandParser.ChangeEndTimeContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
        char prefix = itemNum.charAt(0);
        int taskNum = Integer.parseInt(itemNum.substring(1));
	    visit(ctx.time());
	    return new ChangeCommand(prefix, taskNum, null, null,
	            null, null, dateTime.toLocalTime());
	};
	
	@Override
	public Command visitChangeEndDateTime(
	        UserCommandParser.ChangeEndDateTimeContext ctx) {
	    String itemNum = ctx.ITEM_NUM().getText().toLowerCase();
        char prefix = itemNum.charAt(0);
        int taskNum = Integer.parseInt(itemNum.substring(1));
	    visit(ctx.datetime());
	    return new ChangeCommand(prefix, taskNum, null, null, null, 
	            dateTime.toLocalDate(), dateTime.toLocalTime());
	};

	//----------------STRING-----------------
	
	@Override
	public Command visitString(UserCommandParser.StringContext ctx) { 
		string = userCommand.substring(ctx.getStart().getStartIndex(),
				                       ctx.getStop().getStopIndex()+1);
		return null;
	}

	//----------------TYPES OF DATETIME----------------- 

	@Override
	public Command visitTimeThenDate(UserCommandParser.TimeThenDateContext ctx) {
		visit(ctx.date());
		visit(ctx.time());
		return null;
	}
	
	@Override
	public Command visitDateThenTime(UserCommandParser.DateThenTimeContext ctx) {
		visit(ctx.date());
		visit(ctx.time());
		return null;
	}

	//----------------TYPES OF DATE---------------------
	
	@Override
	public Command visitToday(UserCommandParser.TodayContext ctx) {
	    dateTime = dateTime.with(LocalDate.now());
		return null;
	}

	@Override
	public Command visitTomorrow(UserCommandParser.TomorrowContext ctx) {
	    dateTime = dateTime.with(LocalDate.now()).plusDays(1);
		return null;
	}

	@Override
	public Command visitDayOfWeekOnly(UserCommandParser.DayOfWeekOnlyContext ctx) {
		int dayInt = getDayOfWeekInt(ctx);
		DayOfWeek.of(dayInt);	// check that dayInt is valid
		LocalDate today = LocalDate.now();
		int todayInt = today.getDayOfWeek().getValue();
		if (todayInt < dayInt) {
			// referring to this week
		    dateTime = dateTime.with(today.plusDays(dayInt - todayInt));
		} else {
			// referring to next week
		    dateTime = dateTime.with(today.plusDays(dayInt - todayInt + 7));
		}
		return null;
	}

	@Override
	public Command visitThisDayOfWeek(UserCommandParser.ThisDayOfWeekContext ctx) {
		//TODO
		return visitChildren(ctx);
	}

	@Override
	public Command visitNextDayOfWeek(UserCommandParser.NextDayOfWeekContext ctx) {
		//TODO
		return visitChildren(ctx);
	}

	@Override
	public Command visitFullDate(UserCommandParser.FullDateContext ctx) {
		int day = Integer.parseInt(ctx.INT(0).getText());
		int month = Integer.parseInt(ctx.INT(1).getText());
		int year = Integer.parseInt(ctx.INT(2).getText());
		dateTime = dateTime.with(LocalDate.of(year, month, day));
		return null;
	}

	@Override
	public Command visitDayMonth(UserCommandParser.DayMonthContext ctx) {
		int year = LocalDate.now().getYear();
		int day = Integer.parseInt(ctx.INT(0).getText());
		int month = Integer.parseInt(ctx.INT(1).getText());
		dateTime = dateTime.with(LocalDate.of(year, month, day));
		return null;
	}

	@Override
	public Command visitFullDateWordMonth(
	        UserCommandParser.FullDateWordMonthContext ctx) {
		int day = Integer.parseInt(ctx.INT(0).getText());
		int month = getMonth(ctx.MONTH_NAME());
		int year = Integer.parseInt(ctx.INT(1).getText());
		dateTime = dateTime.with(LocalDate.of(year, month, day));
		return null;
    }

	@Override
	public Command visitDayMonthWordMonth(
	        UserCommandParser.DayMonthWordMonthContext ctx) {
		int year = LocalDate.now().getYear();
		int month = getMonth(ctx.MONTH_NAME());
		int day = Integer.parseInt(ctx.INT().getText());
		dateTime = dateTime.with(LocalDate.of(year, month, day));
		return null;
    }

	@Override
	public Command visitFullDateWordMonthMonthFirst(
	        UserCommandParser.FullDateWordMonthMonthFirstContext ctx) {
		int day = Integer.parseInt(ctx.INT(0).getText());
		int month = getMonth(ctx.MONTH_NAME());
		int year = Integer.parseInt(ctx.INT(1).getText());
		dateTime = dateTime.with(LocalDate.of(year, month, day));
		return null;
    }

	@Override
	public Command visitDayMonthWordMonthMonthFirst(
	        UserCommandParser.DayMonthWordMonthMonthFirstContext ctx) {
		int year = LocalDate.now().getYear();
		int month = getMonth(ctx.MONTH_NAME());
		int day = Integer.parseInt(ctx.INT().getText());
		dateTime = dateTime.with(LocalDate.of(year, month, day));
		return null;
    }
	
	// date helper functions
	
	private int getDayOfWeekInt(UserCommandParser.DayOfWeekOnlyContext ctx) {
		String day = ctx.getText().substring(0, 3).toLowerCase();
		String[] days = {"", "mon", "tue", "wed", "thu", "fri", "sat", "sun"};
		for (int i=0; i<days.length; i++) {
			if (days[i].equals(day)) {
				return i;
			}
		}
		assert false; // shouldn't happen
		return 0;
	}
	private int getMonth(TerminalNode terminalNode) {
		String month = terminalNode.getText().substring(0, 3).toLowerCase();
		String[] months = {"", "jan", "feb", "mar", "apr", "may", "jun", "jul",
		        "aug", "sep", "oct", "nov", "dec"};
		for (int i=0; i<months.length; i++) {
			if (months[i].equals(month)) {
				return i;
			}
		}
		assert false; // shouldn't happen
		return 0;
	}

	//----------------TYPES OF TIME------------------------

	@Override
	public Command visitTimeWithoutPeriod(
	        UserCommandParser.TimeWithoutPeriodContext ctx) {
		int hour = Integer.parseInt(ctx.INT(0).getText());
		int minute = ctx.INT(1) == null
		        ? 0 : Integer.parseInt(ctx.INT(1).getText());
		dateTime = dateTime.with(LocalTime.of(hour, minute));
		return null;
	}
    
    @Override
    public Command visitTimeWithPeriod(
            UserCommandParser.TimeWithPeriodContext ctx) {
        int hour = Integer.parseInt(ctx.INT(0).getText());
        hour = process12Hour(hour, ctx.AM() == null);
        int minute = ctx.INT(1) == null
                ? 0 : Integer.parseInt(ctx.INT(1).getText());
		dateTime = dateTime.with(LocalTime.of(hour, minute));
        return null;
    }
    
    // time helper functions
    
    private int process12Hour(int hour, boolean isPm) {
        if ((hour > 12) || (hour <= 0)) {
            throw new DateTimeException(
                    "Invalid time, please use correct 12-hour format: " + hour);
        }
        int offset = isPm ? 12 : 0;
        hour += offset;
        if (hour == 12) {   // those are corner cases
            // 12am
            hour = 0;
        } else if (hour == 24) {
            // 12pm
            hour = 12;
        }
        return hour;
    }
    
    //--------------TYPES OF FILTER-----------------
    
    @Override
    public Command visitKeywordFilter(UserCommandParser.KeywordFilterContext ctx) {
        visit(ctx.string());
        keywords.addAll(Arrays.asList(string.split(" ")));
        return null;
    }

    @Override
    public Command visitTimeRangeFilter(
            UserCommandParser.TimeRangeFilterContext ctx) {
        visit(ctx.time());
        if (ctx.BEFORE() == null) {
            // after command
            filters.add(new TimeFilter(dateTime.toLocalTime(), LocalTime.MAX));
        } else {
            // before command
            filters.add(new TimeFilter(LocalTime.MIN, dateTime.toLocalTime()));
        }
        return null;
    }

    @Override
    public Command visitTimeFilter(UserCommandParser.TimeFilterContext ctx) {
        visit(ctx.time());
        LocalTime time = dateTime.toLocalTime();
        filters.add(new TimeFilter(time.minusMinutes(30),
                                   time.plusMinutes(30)));
        return null;
    }

    @Override 
    public Command visitBetweenTimeFilter(
            UserCommandParser.BetweenTimeFilterContext ctx) {
        visit(ctx.time(0));
        LocalTime start = dateTime.toLocalTime();
        visit(ctx.time(1));
        LocalTime end = dateTime.toLocalTime();
        filters.add(new TimeFilter(start, end));
        return null;
    }

    @Override 
    public Command visitDateRangeFilter(
            UserCommandParser.DateRangeFilterContext ctx) {
        visit(ctx.date());
        if (ctx.BEFORE() == null) {
            // after command
            filters.add(new DateTimeFilter(dateTime.with(LocalTime.MIN),
                                           LocalDateTime.MAX));
        } else {
            // before command
            filters.add(new DateTimeFilter(LocalDateTime.MIN,
                                           dateTime.with(LocalTime.MAX)));
        }
        return null;
    }

    @Override
    public Command visitDateFilter(UserCommandParser.DateFilterContext ctx) {
        visit(ctx.date());
        filters.add(new DateTimeFilter(dateTime.with(LocalTime.MIN),
                                   dateTime.with(LocalTime.MAX)));
        return null;
    }

    @Override
    public Command visitBetweenDateFilter(
            UserCommandParser.BetweenDateFilterContext ctx) {
        visit(ctx.date(0));
        LocalDateTime start = dateTime.with(LocalTime.MIN);
        visit(ctx.date(1));
        LocalDateTime end = dateTime.with(LocalTime.MAX);
        filters.add(new DateTimeFilter(start, end));
        return null;
    }

    @Override
    public Command visitDateTimeRangeFilter(
            UserCommandParser.DateTimeRangeFilterContext ctx) {
        visit(ctx.datetime());
        if (ctx.BEFORE() == null) {
            // after command
            filters.add(new DateTimeFilter(dateTime, LocalDateTime.MAX));
        } else {
            // before command
            filters.add(new DateTimeFilter(LocalDateTime.MIN, dateTime));
        }
        return null;
    }

    @Override
    public Command visitBetweenDateTimeFilter(
            UserCommandParser.BetweenDateTimeFilterContext ctx) {
        visit(ctx.datetime(0));
        LocalDateTime start = dateTime;
        visit(ctx.datetime(1));
        LocalDateTime end = dateTime;
        filters.add(new DateTimeFilter(start, end));
        return null;
    }

    @Override
    public Command visitThisWeekFilter(
            UserCommandParser.ThisWeekFilterContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        filters.add(
            new DateTimeFilter(now.with(DayOfWeek.MONDAY).with(LocalTime.MIN),
                               now.with(DayOfWeek.SUNDAY).with(LocalTime.MAX)));
        return null;
    }

    @Override
    public Command visitNextWeekFilter(
            UserCommandParser.NextWeekFilterContext ctx) {
        LocalDateTime nextWeek = LocalDateTime.now().plusWeeks(1);
        filters.add(
            new DateTimeFilter(nextWeek.with(DayOfWeek.MONDAY).with(LocalTime.MIN),
                               nextWeek.with(DayOfWeek.SUNDAY).with(LocalTime.MAX)));
        return null;
    }

    @Override
    public Command visitThisMonthFilter(
            UserCommandParser.ThisMonthFilterContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        filters.add(
            new DateTimeFilter(now.withDayOfMonth(1).with(LocalTime.MIN),
                               now.withDayOfMonth(1).plusMonths(1)
                                   .minusDays(1).with(LocalTime.MAX)));
        return null;
    }

    @Override
    public Command visitNextMonthFilter(
            UserCommandParser.NextMonthFilterContext ctx) {
        LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
        filters.add(
            new DateTimeFilter(nextMonth.withDayOfMonth(1).with(LocalTime.MIN),
                               nextMonth.withDayOfMonth(1).plusMonths(1)
                                   .minusDays(1).with(LocalTime.MAX)));
        return null;
    }
    
    @Override
    public Command visitOverdueFilter(OverdueFilterContext ctx) {
        filters.add(new OverdueFilter());
        return null;
    }
	
}
