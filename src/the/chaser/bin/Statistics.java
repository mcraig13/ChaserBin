/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package the.chaser.bin;

import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class Statistics extends InventorySceneController{
    
    public String getHighest(ArrayList<Item> searchResults, String flag) {
	float highest = 0;
	float current = 0;
	
	for(Item i : searchResults) {
	    if(flag.equals("b")) {
		current = i.getBoughtPrice();
	    } else {
		current = i.getSellPrice();
	    }
	    if(highest < current) {
		    highest = current;
	    }
	}
	
	return String.format("%.02f", highest);
    }
    
    public String getLowest(ArrayList<Item> searchResults, String flag) {
	float lowestBought = searchResults.get(0).getBoughtPrice();
	float lowestSold;
	float current;
	
	ArrayList<Float> soldItems = new ArrayList();
	
	for(Item i : searchResults) {
	    if(i.getSellPrice() > 0) {
		soldItems.add(i.getSellPrice());
	    }
	}
	
	if(flag.equals("b")) {
	    for(Item i : searchResults) {
		current = i.getBoughtPrice();
		if(current != 0 && current < lowestBought) {
		    lowestBought = current;
		}
	    }
	    return String.format("%.02f", lowestBought);
	} else {
	    if(!soldItems.isEmpty()) {
		lowestSold = soldItems.get(0);
		for(int i = 1; i<soldItems.size(); i++) {
		    if(i < lowestSold) {
			lowestSold = i;
		    }
		}
		return String.format("%.02f", lowestSold);
	    } else {
		return "0";
	    }
	}
    }
    
    public String getAverage(ArrayList<Item> searchResults, String flag) {
	float average = 0;
	int count = 0;
	for(Item i : searchResults) {
	    if(flag.equals("b")) {
		if (i.getBoughtPrice() > 0) {
		    average = average + i.getBoughtPrice();
		    count++;
		}
	    } else {
		if(i.getSellPrice() > 0) {
		    average = average + i.getSellPrice();
		    count++;
		}
	    }
	}
	return String.format("%.02f", (average/count));
    }
    
    public String getQuantity(ArrayList<Item> searchResults) {
	int count = 0;
	
	for(Item i : searchResults) {
	    if(i.getSellPrice() == 0) {
		count++;
	    }
	}
	return Integer.toString(count);
    }
    
    public String getNumberSold(ArrayList<Item> searchResults) {
	int count = 0;
	
	for(Item i : searchResults) {
	    if(i.getSellPrice() > 0) {
		count++;
	    }
	}
	return Integer.toString(count);
    }    
}
