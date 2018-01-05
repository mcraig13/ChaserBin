/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package the.chaser.bin;

import java.sql.*;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Michael
 */
public class InventorySceneController implements Initializable {

    @FXML
    private Button deleteButton;
    @FXML
    private TextField searchBar;
    @FXML
    private TextField idField;
    @FXML
    private TextField barcodeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField soldField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField shopField;
    @FXML
    private Label numberSoldField;
    @FXML
    private Label highestSellField;
    @FXML
    private Label lowestSellField;
    @FXML
    private Label averageSellField;
    @FXML
    private Label qtyField;
    @FXML
    private Label highestBoughtField;
    @FXML
    private Label lowestBoughtField;
    @FXML
    private Label averageBoughtField;
    @FXML
    private DatePicker dateField;
    @FXML
    private ChoiceBox categoryField;
    @FXML
    private ChoiceBox categoryChooser;
    @FXML
    private ChoiceBox consoleChooser;
    @FXML
    private ChoiceBox consoleField;
    @FXML
    private CheckBox soldItemsCheck;
    @FXML
    public VBox resultHolder;
    @FXML
    public VBox gamesHolder;
    @FXML
    public VBox consoleHolder;
    @FXML
    private Label messageLabel;
    @FXML
    private TabPane tabPane;
    
    private Statement st;
    private ResultSet rs;
    private Connection conn;
    private Stage stage;
    private Parent root;
    private String defaultMessage;
    private ArrayList<Item> searchResults;
    private ObservableList<String> categorySelectionList;
    private ObservableList<String> consoleSelectionList;
    private SingleSelectionModel<Tab> selectionModel;
    private Statistics stats;
    private Search search;

    /**
     * Initialises the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	try {
	    defaultMessage = messageLabel.getText();
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.18:3306/chaserbin","admin","");
            st = conn.createStatement();
            categorySelectionList = FXCollections.observableArrayList("Video Game", "Console", "Peripheral");
	    consoleSelectionList = FXCollections.observableArrayList(
		    "PS1", "PS2", "PS3", "PS4", "PSP", "PS Vita", "PC", 
		    "Xbox", "Xbox 360", "Xbox One", 
		    "NES", "SNES", "Nintendo 64", 
		    "Nintendo GC", "Nintendo Wii", "Nintendo Wii U", "Nintendo Switch", 
		    "Nintendo DS", "Nintendo 3DS", "Sega Mega Drive", "Dreamcast");
	    consoleField.setItems(consoleSelectionList);
	    categoryField.setItems(categorySelectionList);
	    categoryChooser.setItems(consoleSelectionList);
	    consoleChooser.setItems(consoleSelectionList);
	    selectionModel = tabPane.getSelectionModel();
	    stats = new Statistics();
	    search = new Search();
	    
	    categoryChooser.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		    createGamesButtons((Integer) newValue);
		}
	    }
	    );
		    
	    consoleChooser.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		    createConsolesButtons((Integer) newValue);
		}
	    }
	    );
	               
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Error: " + e);
        }
    }    
    
    private void clearFields(){
        idField.clear();
        nameField.clear();
	barcodeField.clear();
        categoryField.getSelectionModel().clearSelection();
        consoleField.getSelectionModel().clearSelection();
	dateField.setValue(null);
        priceField.clear();
	shopField.clear();
        soldField.clear();
	numberSoldField.setText("0");
	highestSellField.setText("0");
	lowestSellField.setText("0");
	averageSellField.setText("0");
        qtyField.setText("0");
	highestBoughtField.setText("0");
	lowestBoughtField.setText("0");
	averageBoughtField.setText("0");
    }
    
    private void createConsolesButtons(int newValue) {
	String console = '"' + consoleSelectionList.get(newValue) + '"';
	consoleHolder.getChildren().clear();
	
	try{
	       String query = "select * from inventory where Category = 'Console' and Console = " + console + " GROUP BY Name ORDER BY Name";
	       rs = st.executeQuery(query);
	       System.out.println(rs.first());
	       do {
		   //for each item in PS1 category
		   //create a button labelled name of game
		   Button b = new Button();
		   String bc = rs.getString("Name");
		   b.setId(bc);
		   b.setText(rs.getString("Name"));
		   b.setMinWidth(400);
		   b.addEventHandler(MouseEvent.MOUSE_CLICKED, resultHolderUpdater(b));
		   consoleHolder.getChildren().add(b);
	       } while(rs.next());
	    } catch(SQLException e){
	       System.out.println("Error: " + e);
	    }   
    }
    
    private void createGamesButtons(int newValue) {
	String console = '"' + consoleSelectionList.get(newValue) + '"';
	gamesHolder.getChildren().clear();
	if(!soldItemsCheck.isSelected()) {
	    try{
		String query = "select * from inventory where Category = 'Video Game' and Console = " + console + " GROUP BY Barcode ORDER BY Name";
		rs = st.executeQuery(query);
		System.out.println(rs.first());
		do {
		    //for each item in PS1 category
		    //create a button labelled name of game
		    Button b = new Button();
		    String bc = rs.getString("Barcode");
		    b.setId(bc);
		    b.setText(rs.getString("Name"));
		    b.setMinWidth(400);
		    b.addEventHandler(MouseEvent.MOUSE_CLICKED, resultHolderUpdater(b));
		    gamesHolder.getChildren().add(b);
		} while(rs.next());
	    } catch(SQLException e){
		System.out.println("Error: " + e);
	    }   
	} else {
	    try{
		String query = "select * from inventory where Category = 'Video Game' and Console = " + console + " and (Sold > 0) GROUP BY Barcode ORDER BY Name";
		rs = st.executeQuery(query);
		System.out.println(rs.first());
		do {
		    //for each item in PS1 category
		    //create a button labelled name of game
		    Button b = new Button();
		    String bc = rs.getString("Barcode");
		    b.setId(bc);
		    b.setText(rs.getString("Name"));
		    b.setMinWidth(400);
		    b.addEventHandler(MouseEvent.MOUSE_CLICKED, resultHolderUpdater(b));
		    gamesHolder.getChildren().add(b);
		} while(rs.next());
	    } catch(SQLException e){
		System.out.println("Error: " + e);
	    }  
	}
    }
    
    public EventHandler<Event> resultHolderUpdater(Button b) {
	EventHandler<Event> resultButtonHandler = new EventHandler<Event>() {
	    
	    @Override
	    public void handle(Event event) {
		searchBar.setText(b.getId());
		search();
		selectionModel.select(0);
	    }
	};
	return resultButtonHandler;
    }
    
    public void search(){
	String searchValue = searchBar.getText();
	
        searchResults = new ArrayList<>();
        clearFields();
        if(searchValue.matches("[0-9]+") && searchValue.length() > 9) {
	    try {
		searchResults = search.searchByBarcode(searchValue, st);
	    } catch (SQLException ex) {
		System.out.println("Error: " + ex);
	    }
	} else {
	    try {
		searchResults = search.searchByName(searchValue, st);
	    } catch (SQLException ex) {
		System.out.println("Error: " + ex);
	    }
	}
	if(searchResults.isEmpty()) {
	    messageLabel.setText("No items found");
	    barcodeField.setText(searchValue);
	} else {
	    messageLabel.setText("Found " + searchResults.size() + " result(s)");
	    createResultButtons(searchResults);
	    averageBoughtField.setText(stats.getAverage(searchResults, "b"));
	    highestBoughtField.setText(stats.getHighest(searchResults, "b"));
	    lowestBoughtField.setText(stats.getLowest(searchResults, "b"));
	    averageSellField.setText(stats.getAverage(searchResults, "s"));
	    highestSellField.setText(stats.getHighest(searchResults, "s"));
	    lowestSellField.setText(stats.getLowest(searchResults, "s"));
	    numberSoldField.setText(stats.getNumberSold(searchResults));
	    qtyField.setText(stats.getQuantity(searchResults));   
        }
    }

    private void createResultButtons(ArrayList<Item> results) {
	resultHolder.getChildren().clear();
	for(Item r : results) {
	    Button b = new Button();
	    b.setId(Integer.toString(results.indexOf(r)));
	    b.setText("ID: " + r.getBarcode());
	    b.addEventHandler(MouseEvent.MOUSE_CLICKED, createResultButtonHandler(b, results));
	    b.setMinWidth(175);
	    resultHolder.getChildren().add(b);
	}
    }
    
    public EventHandler<Event> createResultButtonHandler(Button b, ArrayList<Item> results) {
	EventHandler<Event> resultButtonHandler = new EventHandler<Event>() {
	    
	    @Override
	    public void handle(Event event) {
		int buttonId = Integer.parseInt(b.getId());
		Item currentItem = results.get(buttonId);
		
		idField.setText(String.valueOf(currentItem.getId()));
		barcodeField.setText(String.valueOf(currentItem.getBarcode()));
		nameField.setText(currentItem.getName());
		categoryField.getSelectionModel().select(categorySelectionList.indexOf(currentItem.getCategory()));
		consoleField.getSelectionModel().select(consoleSelectionList.indexOf(currentItem.getConsole()));
		dateField.setValue(currentItem.getDate());
		priceField.setText(String.valueOf(currentItem.getBoughtPrice()));
		soldField.setText(String.valueOf(currentItem.getSellPrice()));
		shopField.setText(currentItem.getShop());	
		
		messageLabel.setText("Item ID: " + idField.getText() + " has been selected");
		deleteButton.setDisable(false);
		}
	};
	return resultButtonHandler;
    }
    
    
    @FXML
    private void addButtonHandler(ActionEvent event) {

	if(idField.getText().trim().isEmpty()) {
	    //add to database
	    String addQuery = " insert into inventory (Barcode, Name, Category, Console, Date, Shop, Price)"
	    + " values (?, ?, ?, ?, ?, ?, ?)";
	    
	    try {
	    PreparedStatement preparedStmt = conn.prepareStatement(addQuery);
	 
	    preparedStmt.setString  (1, barcodeField.getText());
	    preparedStmt.setString  (2, nameField.getText());
	    preparedStmt.setString  (3, categoryField.getSelectionModel().getSelectedItem().toString());
	    preparedStmt.setString  (4, consoleField.getSelectionModel().getSelectedItem().toString());
	    preparedStmt.setString  (5, dateField.getValue().toString());
	    if(!shopField.getText().trim().isEmpty()){
		preparedStmt.setString  (6, shopField.getText());
	    }
	    else {
		preparedStmt.setNull(6, Types.NULL);
	    }
	    preparedStmt.setFloat   (7, Float.parseFloat(priceField.getText()));
	    
	    preparedStmt.execute();
	    
	    messageLabel.setText("Added new item");
	    
	    } catch (SQLException ex) {
		System.out.println("Error: " + ex);
		messageLabel.setText("Failed to add new item, check the fields.");
	    }
	    
	} else {
	    //update database item
	    String updateQuery = " update inventory set Barcode=?, Name=?, Category=?, Console=?, Date=?, Shop=?, Price=?, Sold=?"
	    + " where id="+Integer.parseInt(idField.getText());
	    try {
		PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);

		preparedStmt.setString	(1, barcodeField.getText());
		preparedStmt.setString  (2, nameField.getText());
		preparedStmt.setString  (3, categoryField.getSelectionModel().getSelectedItem().toString());
		preparedStmt.setString  (4, consoleField.getSelectionModel().getSelectedItem().toString());
		preparedStmt.setString   (5, dateField.getValue().toString());
		if(!shopField.getText().trim().isEmpty()){
		    preparedStmt.setString   (6, shopField.getText());
		}
		else {
		    preparedStmt.setNull(6, Types.NULL);
		}
		preparedStmt.setFloat   (7, Float.parseFloat(priceField.getText()));
		if(!soldField.getText().trim().isEmpty()){
		    preparedStmt.setFloat   (8, Float.parseFloat(soldField.getText()));
		}
		else {
		    preparedStmt.setNull(8, Types.FLOAT);
		}
		preparedStmt.execute();

		messageLabel.setText("Updated item");

	    } catch (SQLException ex) {
		System.out.println("Error: " + ex);
		messageLabel.setText("Failed to update item, check the fields.");
	    }
	}
    }

    @FXML
    private void resetButtonHandler(ActionEvent event) {
	clearFields();
	searchBar.setText("");
	resultHolder.getChildren().clear();
	messageLabel.setText(defaultMessage);	
	searchBar.requestFocus();
    }
    
    @FXML
    private void deleteButtonHandler(ActionEvent event) {
	try {
	    String query = "delete from inventory where id = ?";
	    PreparedStatement preparedStmt = conn.prepareStatement(query);
	    preparedStmt.setInt(1, Integer.parseInt(idField.getText()));

	    // execute the preparedstatement
	    preparedStmt.execute();
	    deleteButton.setDisable(true);
	    clearFields();
	    searchBar.setText("");
	    resultHolder.getChildren().clear();
	    messageLabel.setText("Item deleted");
	} catch (SQLException ex) {
	    System.out.println("Error: " + ex);
	    messageLabel.setText("Failed to delete item, check the fields.");
	}
    }
}
