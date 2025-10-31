package esb;

public class Order {
	private String orderNumber;
	private double amount;
	private String orderType;

	public Order(String orderNumber, double amount, String orderType) {
		this.orderNumber = orderNumber;
		this.amount = amount;
		this.orderType = orderType;
	}

	public static Order makeDomestic(String orderNumber, double amount) {
		return new Order(orderNumber, amount, "domestic");
	}

	public static Order makeInternational(String orderNumber, double amount) {
		return new Order(orderNumber, amount, "international");
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String toString(){
		return "order: nr="+orderNumber+" amount="+amount;
	}

}
