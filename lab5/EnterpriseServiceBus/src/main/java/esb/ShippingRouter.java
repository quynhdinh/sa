package esb;

public class ShippingRouter {
    
    public String route(Order order) {
        if (order.getAmount() > 175.0) {
            return "nextDayShippingChannel";
        } else {
            return "shippingchannel";
        }
    }
}
