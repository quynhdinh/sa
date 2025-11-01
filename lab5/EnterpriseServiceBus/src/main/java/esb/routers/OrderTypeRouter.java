package esb.routers;

import esb.Order;

public class OrderTypeRouter {
    
    public String route(Order order) {
        String type = order.getOrderType();
        if (type != null && type.equalsIgnoreCase("international"))
            return "internationalShippingChannel";
        return "domesticShippingChannel";
    }
}
