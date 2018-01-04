/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package the.chaser.bin;

import java.sql.*;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.ArrayList;

import java.time.LocalDate;
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
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button resetButton;
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
    private Label soldCountField;
    @FXML
    private Label qtyField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField shopField;
    @FXML
    private DatePicker dateField;
    @FXML
    private ChoiceBox categoryField;
    @FXML
    private ChoiceBox categoryChooser;
    @FXML
    private ChoiceBox consoleField;
    @FXML
    public VBox resultHolder;
    @FXML
    public VBox gamesHolder;
    @FXML
    private Label messageLabel;
    @FXML
    private Label avgPriceLabel;
    @FXML
    private Label avgSoldLabel;
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
		    "Nintendo DS", "Nintendo 3DS", "Sega Mega Drive");
	    consoleField.setItems(consoleSelectionList);
	    categoryField.setItems(categorySelectionList);
	    categoryChooser.setItems(consoleSelectionList);
	    selectionModel = tabPane.getSelectionModel();
	    
	    categoryChooser.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		    createGamesButtons((Integer) newValue);
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
        soldCountField.setText("0");
        qtyField.setText("0");
    }
    
    private void createGamesButtons(int newValue) {
	String console = '"' + consoleSelectionList.get(newValue) + '"';
	gamesHolder.getChildren().clear();
	try{
            String query = "select * from inventory where Console = " + console + " and (Sold = 0 or Sold IS NULL) GROUP BY Barcode ORDER BY Name";
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
    
    private void createResultButtons(ArrayList<Item> results) {
	resultHolder.getChildren().clear();
	for(Item r : results) {
	    Button b = new Button();
	    b.setId(Integer.toString(results.indexOf(r)));
	    b.setText("ID: " + Integer.toString(r.getId()));
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
		qtyField.setText(Integer.toString(results.size()));	
		
		messageLabel.setText("Item ID: " + idField.getText() + " has been selected");
		deleteButton.setDisable(false);
		}
	};
	return resultButtonHandler;
    }
    
    public void search(){
	String searchValue = searchBar.getText();
        searchResults = new ArrayList<>();
        clearFields();
        if(searchValue.matches("[0-9]+") && searchValue.length() > 11) {
            try{
                String query = "select * from inventory where Barcode = " + searchValue;
                rs = st.executeQuery(query);
            
                if(!rs.isBeforeFirst()) {
                    clearFields();
                    resultHolder.getChildren().clear();
                    messageLabel.setText("No Results Found. Please add the item details above.");
                    barcodeField.setText(searchValue);
                }else {
                    while(rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("Name");
                        String category = rs.getString("Category");
                        String console = rs.getString("Console");
                        LocalDate date = rs.getDate("Date").toLocalDate();
                        String shop = rs.getString("Shop");
                        float boughtPrice = rs.getFloat("Price");
                        float sellPrice = rs.getFloat("Sold");

                        Item item = new Item(id,searchValue,name,category,console,date,shop,boughtPrice,sellPrice);
                        searchResults.add(item);
                    }
		qtyField.setText(Integer.toString(searchResults.size()));
		messageLabel.setText("Found " + searchResults.size() + " result(s)");
		createResultButtons(searchResults);
                }
            } catch(SQLException e){
                System.out.println("Error: " + e);
            }   
        } else {
            messageLabel.setText("Not a valid barcode");
            //search for names here else not valid search term
        }
    }

    @FXML
    private void onEnter(ActionEvent event) throws IOException {
        search();
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
    private void searchButtonHandler(ActionEvent event) {
	search();
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
