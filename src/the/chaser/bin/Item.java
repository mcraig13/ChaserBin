/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package the.chaser.bin;

import java.time.LocalDate;

/**
 *
 * @author Michael
 */
public class Item {
   
    private int id;
    private String barcode;
    private String name;
    private String category;
    private String console;
    private LocalDate date;
    private String shop;
    private float boughtprice;
    private float sellprice;
    
    public Item(){}
    
    Item(int id, String barcode, String name, String category, String console, LocalDate date, String shop, float boughtprice, float sellprice) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.category = category;
        this.console = console;
	this.date = date;
	this.shop = shop;
        this.boughtprice = boughtprice;
        this.sellprice = sellprice;
    }

    int getId() { return id; }
    String getBarcode() { return barcode;}
    String getName() { return name; }
    String getCategory() { return category; }
    String getConsole() { return console; }
    LocalDate getDate() { return date; }
    String getShop() { return shop; }
    float getBoughtPrice() { return boughtprice; }
    float getSellPrice() { return sellprice; }
        
}
