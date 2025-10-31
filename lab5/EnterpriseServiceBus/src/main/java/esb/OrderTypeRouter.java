package esb;

public class OrderTypeRouter {
    
    public String route(Order order) {
        String type = order.getOrderType();
        System.out.println("type: " + type);
        if (type != null && type.equalsIgnoreCase("international")) {
            return "internationalShippingChannel";
        }
        // domestic -> let DomesticShippingRouter decide nextDay vs normal
        return "domesticShippingChannel";
    }
}
