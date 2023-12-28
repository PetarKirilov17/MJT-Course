package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MJTOrderRepository implements OrderRepository {
    Collection<Order> orders;

    private static final int INVALID_ID = -1;
    private int orderId = 1;

    public MJTOrderRepository() {
        orders = new ArrayList<>();
    }

    @Override
    public Response request(String size, String color, String destination) {
        if (size == null || color == null || destination == null) {
            throw new IllegalArgumentException("Order elements cannot be null!");
        }
        Size sizeEnum = getSize(size);
        Color colorEnum = getColor(color);
        Destination destEnum = getDest(destination);
        TShirt tShirt = new TShirt(sizeEnum, colorEnum);

        StringBuilder invalidMessage = new StringBuilder();
        if (!assertEnums(sizeEnum, colorEnum, destEnum, invalidMessage).get()) {
            orders.add(new Order(INVALID_ID, tShirt, destEnum));
            return Response.decline(invalidMessage.toString());
        }
        Order newOrder = new Order(orderId, tShirt, destEnum);
        orders.add(newOrder);
        return Response.create(orderId++);
    }

    private AtomicBoolean assertEnums(Size sizeEnum, Color colorEnum, Destination destEnum,
                                      StringBuilder invalidMessage) {
        AtomicBoolean isValid = new AtomicBoolean(true);
        if (sizeEnum.equals(Size.UNKNOWN)) {
            isValid.set(false);
            invalidMessage.append("invalid=size");
        }
        if (colorEnum.equals(Color.UNKNOWN)) {
            if (isValid.get()) {
                invalidMessage.append("invalid=color");
            } else {
                invalidMessage.append(",color");
            }
            isValid.set(false);
        }
        if (destEnum.equals(Destination.UNKNOWN)) {
            if (isValid.get()) {
                invalidMessage.append("invalid=destination");
            } else {
                invalidMessage.append(",destination");
            }
            isValid.set(false);
        }
        return isValid;
    }

    private Size getSize(String sizeStr) {
        Size sizeEnum;
        try {
            sizeEnum = Size.valueOf(sizeStr.toUpperCase());

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            sizeEnum = Size.UNKNOWN;
        }
        return sizeEnum;
    }

    private Color getColor(String colorStr) {
        Color colorEnum;
        try {
            colorEnum = Color.valueOf(colorStr.toUpperCase());

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            colorEnum = Color.UNKNOWN;
        }
        return colorEnum;
    }

    private Destination getDest(String destStr) {
        Destination destEnum;
        try {
            destEnum = Destination.valueOf(destStr.toUpperCase());

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            destEnum = Destination.UNKNOWN;
        }
        return destEnum;
    }

    @Override
    public Response getOrderById(int id) {
        Optional<Order> order = orders.stream().filter(o -> o.id() == id).findAny();
        if (order.isPresent()) {
            return Response.ok(List.of(order.get()));
        }
        return Response.notFound(id);
    }

    @Override
    public Response getAllOrders() {
        return Response.ok(orders);
    }

    @Override
    public Response getAllSuccessfulOrders() {
        return Response.ok(
            orders.stream().filter(o -> o.id() != INVALID_ID).toList()
        );
    }
}
