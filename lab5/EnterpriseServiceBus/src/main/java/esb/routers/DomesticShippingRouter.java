package esb.routers;

import esb.Order;

public class DomesticShippingRouter {
    public String route(Order order) {
        if (order.getAmount() > 175.0) {
            return "nextDayShippingChannel";
        } else {
            return "normalShippingChannel";
        }
    }
}
