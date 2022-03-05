package main;

public class Ordersclass {
	//LINKED BY ORDER ID
    protected String orderid;
    private String date;
    private String time;
    
    public Ordersclass() {
    	orderid = null;
    	date = null;
    	time = null;
    }
    
    public Ordersclass(String orderid, String date, String time) {
    	this.orderid = orderid;
    	this.date = date;
    	this.time = time;
    }
    
    public void setneworderid(String orderid) {
    	this.orderid = orderid;
    }
    
    public void setdate(String date) {
    	this.date = date;
    }
    
    public void settime(String time) {
    	this.time = time;
    }
    
    public String getorderid() {
    	return orderid;
    }
    
    public String getdate() {
    	return date;
    }
    
    public String getordertime() {
    	return time;
    }
}
