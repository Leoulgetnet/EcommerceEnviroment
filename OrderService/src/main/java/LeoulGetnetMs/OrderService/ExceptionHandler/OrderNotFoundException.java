package LeoulGetnetMs.OrderService.ExceptionHandler;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("There is no order with this id!");
    }
}
