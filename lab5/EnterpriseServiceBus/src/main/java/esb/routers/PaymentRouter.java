package esb.routers;

import esb.Order;

public class PaymentRouter {
    public String route(Order order) {
        System.out.println("PaymentRouter::route");
        String payment = order.getPaymentMethod();
        if(payment != null) {
            if(payment.equalsIgnoreCase("visa")) {
                return "visaPaymentChannel";
            } else if(payment.equalsIgnoreCase("mastercard")) {
                return "mastercardPaymentChannel";
            } else if(payment.equalsIgnoreCase("paypal")) {
                return "paypalPaymentChannel";
            }
        }
        return "unknownPaymentChannel";
    }
}