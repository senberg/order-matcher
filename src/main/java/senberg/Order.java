package senberg;

class Order {
    private int volume;
    private final int price;

    public Order(int volume, int price){
        this.volume = volume;
        this.price = price;
    }

    public void decreaseVolume(int volume){
        this.volume -= volume;
    }

    public int getVolume(){
        return this.volume;
    }

    public int getPrice(){
        return this.price;
    }
}
