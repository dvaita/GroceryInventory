package com.dvait_a.groceryinventory;

public class item {
    public String itemName;
    public double itemCount;

    public item() {
    }

    public item(String itemName, double itemCount) {
        this.itemName = itemName;
        this.itemCount = itemCount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double  getItemCount() {
        return itemCount;
    }

    public void setItemCount(double itemCount) {
        this.itemCount = itemCount;
    }
}
