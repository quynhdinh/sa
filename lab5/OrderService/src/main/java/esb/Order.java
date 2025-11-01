package esb;

public class Order {
	private String orderNumber;
	private double amount;
	private String orderType;
	private String paymentMethod;

	public Order(String orderNumber, double amount, String orderType, String paymentMethod) {
		this.orderNumber = orderNumber;
		this.amount = amount;
		this.orderType = orderType;
		this.paymentMethod = paymentMethod;
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

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String toString(){
		return "order: nr="+orderNumber+" amount="+amount;
	}

}
