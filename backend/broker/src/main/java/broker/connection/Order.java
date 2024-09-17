package broker.connection;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.util.Objects;

/**
 * Class representing an order in the order book.
 */
@Getter
@Setter
@Builder
public class Order {
    private double price;
    private int quantity;
    private boolean isBid;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Order other = (Order) obj;
        return Double.compare(other.getPrice(), price) == 0
                && other.getQuantity() == quantity
                && other.isBid() == isBid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, quantity, isBid);
    }
}
