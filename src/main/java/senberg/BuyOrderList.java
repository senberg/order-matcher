package senberg;

class BuyOrderList extends OrderList{
    public void addOrder(Order order){
        // figure out where in the order list we should place this order
        // place the order behind existing orders with the same or a better price
        int position = 0;

        while(position < orders.size() && orders.get(position).getPrice() >= order.getPrice()){
            position++;
        }

        orders.add(position, order);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("--- BUY ---");

        for(Order order : orders){
            result.append(System.lineSeparator());
            result.append("BUY ");
            result.append(order.getVolume());
            result.append('@');
            result.append(order.getPrice());
        }

        return result.toString();
    }
}
