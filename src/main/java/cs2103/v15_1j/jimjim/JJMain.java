package cs2103.v15_1j.jimjim;

import cs2103.v15_1j.jimjim.controller.Controller;
import cs2103.v15_1j.jimjim.controller.JJController;
import cs2103.v15_1j.jimjim.parser.JJParser;
import cs2103.v15_1j.jimjim.parser.Parser;
import cs2103.v15_1j.jimjim.storage.JJStorage;
import cs2103.v15_1j.jimjim.storage.Storage;
import cs2103.v15_1j.jimjim.ui.JJUI;
import cs2103.v15_1j.jimjim.ui.UI;
import javafx.application.Application;
import javafx.stage.Stage;

public class JJMain extends Application {

	private Controller con;
	private Storage storage;
	private Parser parser;
	private UI ui;
	private final String DEADLINE_TASK_FILE_NAME = "deadline_tasks.json";
	private final String TASK_FILE_NAME = "tasks.json";
	private final String EVENT_FILE_NAME = "events.json";

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		con = new JJController();
		storage = new JJStorage();
		parser = new JJParser();
		ui = new JJUI();

		storage.setSaveFiles(TASK_FILE_NAME, DEADLINE_TASK_FILE_NAME, EVENT_FILE_NAME);
		con.setParser(parser);
		con.setStorage(storage);
		ui.setController(con);
		ui.setStage(primaryStage);
	}
}
