package simulation.entities.onlinestore;

import akka.Done;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulation.clock.Clock;
import simulation.entities.onlinestore.messages.BrowseOffers;
import simulation.entities.onlinestore.messages.CheckOpenOrders;
import simulation.entities.onlinestore.messages.MarkOrderAsShipped;
import simulation.entities.onlinestore.messages.PlaceOrder;
import systems.sales.SalesManagementSystem;
import systems.sales.values.Customer;

import systems.sales.values.Order;
import systems.sales.values.StockProduct;
import tech.tablesaw.api.*;
import tech.tablesaw.api.Table;
import tech.tablesaw.filtering.*;

import java.util.List;
import java.util.stream.Collectors;

public final class OnlineStore extends AbstractBehavior<OnlineStore.Message> {

    public interface Message {
    }

    private static final Logger LOG = LoggerFactory.getLogger(OnlineStore.class);

    private final SalesManagementSystem salesManagementSystem;

    public OnlineStore(ActorContext<Message> actor, SalesManagementSystem salesManagementSystem) {
        super(actor);
        this.salesManagementSystem = salesManagementSystem;
    }

    public static Behavior<Message> create(SalesManagementSystem sms) {
        return Behaviors.setup(actor -> new OnlineStore(actor, sms));
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(BrowseOffers.class, msg -> {
                this.onBrowseOffers(msg);
                return Behaviors.same();
            })
            .onMessage(CheckOpenOrders.class, msg -> {
                this.onCheckOpenOrders(msg);
                return Behaviors.same();
            })
            .onMessage(MarkOrderAsShipped.class, msg -> {
                this.onMarkOrderAsShipped(msg);
                return Behaviors.same();
            })
            .onMessage(PlaceOrder.class, msg -> {
                this.onPlaceOrder(msg);
                return Behaviors.same();
            })
            .build();

    }

    private void onBrowseOffers(BrowseOffers msg) {
        this.log("Received a get request");

        var inventory = this
            .salesManagementSystem
            .getProducts()
            .getAllStockProducts()
            .stream()
            .filter(p -> p.getAmountAvailable() > 0)
            .collect(Collectors.toList());

        var table = stockProductsToTable(inventory);
        this.log(table.print());

        msg.getResponse().tell(BrowseOffers.BrowseOffersResponse.apply(inventory));


    }

    private void onCheckOpenOrders(CheckOpenOrders msg) {
        var orders = this.salesManagementSystem.getOrders().getAllOrders();
        // optional filter
        var table = ordersToTable(orders);
        table = table.where(t -> t.instantColumn("delivery_date").isMissing());
        if(table.isEmpty()){
            log("Checking Open Orders - No orders");
        }
        else{
            log("Checking Open Orders - View currents orders: \n %s ", table.print());
        }
        msg.getOrders()
            .tell(CheckOpenOrders.CheckOrdersResponse.apply(this.salesManagementSystem.getOrders().getAllOrders()));
    }

    private void onMarkOrderAsShipped(MarkOrderAsShipped msg) {
        var order = this.salesManagementSystem
            .getOrders()
            .getOrderById(msg.getOrderId())
            .withDelivered(Clock.getInstance().getNowAsInstant());

        order
            .getItems()
            .forEach(item -> salesManagementSystem.getProducts().removeFromStock(item.getBeer(), item.getBottles()));

        this.salesManagementSystem.getOrders().updateOrder(order);
        msg.getAck().tell(Done.getInstance());
    }

    private void onPlaceOrder(PlaceOrder msg) {
        var customer = createOrGetCustomer(msg.getCustomer());
        var orderId = this.salesManagementSystem.getOrders()
            .insertOrder(customer.getId(), Clock.getInstance().getNowAsInstant(), msg.getItems());
        var response = PlaceOrder.PlaceOrderResponse.apply(customer.getId(), orderId);

        msg.getConfirmTo().tell(response);
    }

    private Customer createOrGetCustomer(PlaceOrder.Customer customer) {
        if (customer instanceof PlaceOrder.NewCustomer) {
            var c = (PlaceOrder.NewCustomer) customer;

            return this
                .salesManagementSystem
                .getCustomers()
                .insertCustomer(c.getEmail(), c.getFirstname(), c.getName(), c.getAddress());
        } else if (customer instanceof PlaceOrder.RegisteredCustomer) {
            return this
                .salesManagementSystem
                .getCustomers()
                .getCustomerById(((PlaceOrder.RegisteredCustomer) customer).getId());
        } else {
            throw new RuntimeException("Unknown instance of customer");
        }
    }

    public void log(String message, Object... args) {
        LOG.info(String.format("%s -- %s", Clock.getInstance().getNow(), String.format(message, args)));
    }


    private String stockToString() {
        var available = salesManagementSystem.getProducts().getAllStockProducts();
        var result = new StringBuilder();

        if (available.isEmpty()) {
            result.append("<EMPTY>");
        } else {
            available.forEach(prod -> result
                .append("> ")
                .append(prod.getProduct().getProductName())
                .append(", amount:")
                .append(prod.getAmount())
                .append(", reserved: ")
                .append(prod.getReserved())
                .append("\n"));
        }

        return result.toString();
    }

    private Table ordersToTable(List<Order> orders){
        String result;

        var table = Table
                                .create()
                                .addColumns(IntColumn.create("id"))
                                .addColumns(StringColumn.create("customer"))
                                .addColumns(InstantColumn.create("order_date"))
                                .addColumns(InstantColumn.create("delivery_date"))
                                .addColumns(StringColumn.create("items"));

        orders.forEach(order -> {
            var row = table.appendRow();
            row.setInt("id", order.getOrderId());
            row.setString("customer", order.getCustomer().getEmail());
            row.setInstant("order_date", order.getOrdered());
            if(order.getDelivered().isPresent()){
                row.setInstant("delivery_date", order.getDelivered().get());
            }
            var items = order.getItems().stream().map(item -> { return item.getBeer().getProductName() + " " + item.getBottles() + " ";}).collect(Collectors.toList()).toString();
            row.setString("items", items);
        });
        table.sortDescendingOn("order_date");
        return table;
    }

    private Table stockProductsToTable(List<StockProduct> stockproducts){
        String result;

        var table = Table
                .create()
                .addColumns(StringColumn.create("product_name"))
                .addColumns(DoubleColumn.create("price"))
                .addColumns(IntColumn.create("bottles"))
                .addColumns(IntColumn.create("reserved"));

        stockproducts.forEach(stockproduct -> {
            var row = table.appendRow();
            row.setString("product_name", stockproduct.getProduct().getProductName());
            row.setDouble("price", stockproduct.getProduct().getPrice());
            row.setInt("bottles", stockproduct.getAmount());
            row.setInt("reserved", stockproduct.getReserved());
        });
        table.sortDescendingOn("product_name");
        return table;
    }
}
