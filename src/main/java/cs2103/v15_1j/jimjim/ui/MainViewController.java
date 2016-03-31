package cs2103.v15_1j.jimjim.ui;


import org.controlsfx.control.MasterDetailPane;

import cs2103.v15_1j.jimjim.model.DataLists;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class MainViewController {

	private BorderPane mainPane;
	private Pane leftPane;
	private MasterDetailPane rightPane;
	private BorderPane bottomPane;

	private JJUI uiController;
	private BottomPaneController bottomPaneController;
	private DayPickerPaneController dayPickerPaneController;
	private FloatingTaskPaneController floatingTaskPaneController;
	private SearchPaneController searchPaneController;

	private DataLists masterList;
	private DataLists displayList;

	private final double BORDER_WIDTH = 14.0;
	private final double LEFT_PANE_WIDTH = 500.0;
	private final double PANE_WIDTH = 420.0;
	private final double PANE_HEIGHT = 500.0;
	private final double WINDOW_WIDTH = 1000.0;
	private final double WINDOW_HEIGHT = 600.0;

	public MainViewController(JJUI uiController, DataLists masterList) {
		this.masterList = masterList;
		displayList = new DataLists();
		setUIController(uiController);
	}

	public BorderPane initialize() {
		setUpMainView();

		return mainPane;
	}

	private void setUpMainView(){
		setUpPaneControllers();
		setUpMainPane();
		setUpLeftPane();
		setUpRightPane();
		setUpBottomPane();
	}

	private void setUpPaneControllers(){
		bottomPaneController = new BottomPaneController(this);
		dayPickerPaneController = new DayPickerPaneController(this, masterList, displayList);
		floatingTaskPaneController = new FloatingTaskPaneController(this, masterList, displayList);
		searchPaneController = new SearchPaneController(this, masterList, displayList);
	}

	private void setUpMainPane(){
		mainPane = new BorderPane();
		mainPane.getStyleClass().add("pane");
		mainPane.setPrefWidth(WINDOW_WIDTH);
		mainPane.setPrefHeight(WINDOW_HEIGHT);
		mainPane.setPadding(new Insets(14.0));
	}

	private void setUpLeftPane(){
		leftPane = dayPickerPaneController.getDayPickerPane();
		leftPane.setPrefWidth(LEFT_PANE_WIDTH);
		leftPane.setPrefHeight(PANE_HEIGHT);

		mainPane.setLeft(leftPane);
	}

	private void setUpRightPane(){
		rightPane = new MasterDetailPane();
		rightPane.setMasterNode(floatingTaskPaneController.getFloatingTaskPane());
		rightPane.setDetailNode(searchPaneController.getSearchPane());
		rightPane.setDetailSide(Side.BOTTOM);
		rightPane.setShowDetailNode(false);
		rightPane.setPrefWidth(PANE_WIDTH);
		rightPane.setPrefHeight(PANE_HEIGHT);
		rightPane.setDividerPosition(0.5);
		rightPane.setAnimated(true);

		mainPane.setRight(rightPane);
	}

	private void setUpBottomPane(){
		bottomPane = bottomPaneController.getBottomPane();

		mainPane.setBottom(bottomPane);
	}

	public void updateData(DataLists tempList){
		DataLists searchResults = uiController.getSearchResults();
		this.masterList = tempList;
		displayList = new DataLists();
		dayPickerPaneController.refreshData(masterList, displayList);
		floatingTaskPaneController.refreshData(masterList, displayList);
		searchPaneController.refreshData(masterList, searchResults, displayList);
	}
	
	public void updateData(){
		DataLists searchResults = uiController.getSearchResults();
		displayList = new DataLists();
		dayPickerPaneController.refreshData(masterList, displayList);
		floatingTaskPaneController.refreshData(masterList, displayList);
		searchPaneController.refreshData(masterList, searchResults, displayList);
	}
	
	public DataLists getDisplayLists(){
		return displayList;
	}

	public void showNotification(String msg){
		bottomPaneController.showNotification(msg);
	}

	public void showHelp(){
		bottomPaneController.toggleHelp();
	}

	public void showSearchResults(){
		updateData();
		rightPane.setShowDetailNode(true);
	}

	public void hideSearchResults(){
		rightPane.setShowDetailNode(false);
	}

	public void focusCommandBar(){
		bottomPaneController.focusCommandBar();
	}

	public void executeCommand(String command){
		uiController.executeCommand(command, displayList);
	}

	public void setUIController(JJUI uiController){
		this.uiController = uiController;
	}

}
