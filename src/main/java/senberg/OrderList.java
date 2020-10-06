package senberg;

import java.util.LinkedList;

abstract class OrderList {
    final LinkedList<Order> orders = new LinkedList<>();

    public boolean isEmpty(){
        return orders.isEmpty();
    }

    public Order getBest(){
        return orders.getFirst();
    }

    public void removeBest(){
        orders.removeFirst();
    }

    public abstract void addOrder(Order order);

    public void decreaseBest(int volume){
        if(volume == getBest().getVolume()){
            removeBest();
        }
        else{
            getBest().decreaseVolume(volume);
        }
    }
}
