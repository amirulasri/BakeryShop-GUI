package main;

public class Paymentclass extends Ordersclass{
	private String paymenttype;  //TYPE PAYMENT
	private double totalprice; //ALL PRICE TOTAL
	private double custpay;  //HOW MUCH CUSTOMER PAY
	
	public Paymentclass(String orderid, String paymenttype, double totalprice, double custpay) {
		this.orderid = orderid;
		this.paymenttype = paymenttype;
		this.totalprice = totalprice;
		this.custpay = custpay;
	}
	
	public String getorderid() {
		return orderid;
	}
	
	public String getpaymenttype() {
		return paymenttype;
	}
	
	public double gettotalprice() {
		return totalprice;
	}
	
	public double getcustpay() {
		return custpay;
	}
}
