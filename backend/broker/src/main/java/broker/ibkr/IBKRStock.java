package broker.ibkr;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IBKRStock {
    final String ticker;
    final String secType;
    final String currency;
    final String exchange;

    @Override
    public String toString() { return ticker + " " + secType + " " + currency + " " + exchange; }
}
