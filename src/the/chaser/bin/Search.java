/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package the.chaser.bin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class Search {
    
    private ArrayList searchResults;
    private ResultSet resultSet;
    
    public ArrayList searchByName(String searchTerm, Statement statement) throws SQLException {
	searchResults = new ArrayList();
	
	String query = "select * from inventory where Name LIKE '%" + searchTerm + 
		"' or Name LIKE '" + searchTerm + "%'";
	resultSet = statement.executeQuery(query);
	
	if(!resultSet.isBeforeFirst()) {
	    return searchResults;
	}else {
	    while(resultSet.next()) {
		int id = resultSet.getInt("id");
		String barcode = resultSet.getString("Barcode");
		String name = resultSet.getString("Name");
		String category = resultSet.getString("Category");
		String console = resultSet.getString("Console");
		LocalDate date = resultSet.getDate("Date").toLocalDate();
		String shop = resultSet.getString("Shop");
		float boughtPrice = resultSet.getFloat("Price");
		float sellPrice = resultSet.getFloat("Sold");

		Item item = new Item(id,barcode,name,category,console,date,shop,boughtPrice,sellPrice);
		searchResults.add(item);
	    }

	    return searchResults;
	}
    }
    public ArrayList searchByBarcode(String searchTerm, Statement statement) throws SQLException {
	searchResults = new ArrayList();
	
	String query = "select * from inventory where Barcode = " + searchTerm;
	resultSet = statement.executeQuery(query);

	if(!resultSet.isBeforeFirst()) {
	    return searchResults;
	}else {
	    while(resultSet.next()) {
		int id = resultSet.getInt("id");
		String barcode = resultSet.getString("Barcode");
		String name = resultSet.getString("Name");
		String category = resultSet.getString("Category");
		String console = resultSet.getString("Console");
		LocalDate date = resultSet.getDate("Date").toLocalDate();
		String shop = resultSet.getString("Shop");
		float boughtPrice = resultSet.getFloat("Price");
		float sellPrice = resultSet.getFloat("Sold");

		Item item = new Item(id,barcode,name,category,console,date,shop,boughtPrice,sellPrice);
		searchResults.add(item);
	    }

	    return searchResults;
	}
    }
    
    public ArrayList searchByConsole(String searchTerm) {
	searchResults = new ArrayList();
	
	return searchResults;
    }
}
