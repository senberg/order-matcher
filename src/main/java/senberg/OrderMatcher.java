package senberg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderMatcher {
    private static final Pattern commandPattern = Pattern.compile("^([A-Z]+)(\\s([0-9]+)@([0-9]+))?$");
    private static final Pattern digitsPattern = Pattern.compile("^[0-9]+$");

    private final BuyOrderList buyOrderList = new BuyOrderList();
    private final SellOrderList sellOrderList = new SellOrderList();

    public static void main(String[] args) {
        new OrderMatcher();
    }

    private OrderMatcher() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String input = bufferedReader.readLine().toUpperCase();
                Matcher inputMatcher = commandPattern.matcher(input);

                if(validCommand(inputMatcher)){
                    String command = inputMatcher.group(1);

                    switch(command){
                        case "BUY":
                        case "SELL":
                            int volume = Integer.parseInt(inputMatcher.group(3));
                            int price = Integer.parseInt(inputMatcher.group(4));
                            addOrder(command, volume, price);
                            break;
                        case "PRINT":
                            printOrderBook();
                            break;
                        case "HELP":
                            printHelp();
                            break;
                        case "EXIT":
                            System.exit(0);
                    }
                }
                else{
                    System.err.println("Invalid command. ");
                }
            }
        }
        catch(IOException exception){
            System.err.println("IOException while reading input.");
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private void addOrder(String type, int volume, int price) {
        while (volume > 0) {
            if(type.equals("BUY")) {
                if (sellOrderList.isEmpty() || sellOrderList.getBest().getPrice() > price) {
                    // no existing orders good enough, add this as a new buy order
                    buyOrderList.addOrder(new Order(volume, price));
                    return;
                } else {
                    Order bestSellOrder = sellOrderList.getBest();
                    int actualVolume = Math.min(volume, bestSellOrder.getVolume());
                    trade(actualVolume, bestSellOrder.getPrice());
                    sellOrderList.decreaseBest(actualVolume);
                    volume -= actualVolume;
                    // A trade has been completed. The next loop iteration will handle any remaining volume.
                }
            }
            else{
                if (buyOrderList.isEmpty() || buyOrderList.getBest().getPrice() < price) {
                    // no existing orders good enough, add this as a new sell order
                    sellOrderList.addOrder(new Order(volume, price));
                    return;
                } else {
                    Order bestBuyOrder = buyOrderList.getBest();
                    int actualVolume = Math.min(volume, bestBuyOrder.getVolume());
                    trade(actualVolume, bestBuyOrder.getPrice());
                    buyOrderList.decreaseBest(actualVolume);
                    volume -= actualVolume;
                    // A trade has been completed. The next loop iteration will handle any remaining volume.
                }
            }
        }
    }

    private void trade(int volume, int price){
        System.out.println("TRADE " + volume + "@" + price);
    }

    private void printOrderBook() {
        System.out.println(buyOrderList);
        System.out.println(sellOrderList);
    }

    private void printHelp(){
        System.out.println("Allowed commands are:");
        System.out.println();
        System.out.println("\tBUY volume@price");
        System.out.println("\tSELL volume@price");
        System.out.println("\t\tNote that volume and price should be positive integers. Example: BUY 1000@25");
        System.out.println("\tPRINT");
        System.out.println("\tHELP");
        System.out.println("\tEXIT");
        System.out.println();
    }

    private boolean validCommand(Matcher matcher){
        if(matcher.find()){
            switch(matcher.group(1)){
                case "BUY":
                case "SELL":
                    return isPositiveInteger(matcher.group(3)) && isPositiveInteger(matcher.group(4));
                case "PRINT":
                case "HELP":
                case "EXIT":
                    return matcher.group(2) == null;
                default:
                    return false;
            }
        }
        else{
            return false;
        }
    }

    private boolean isPositiveInteger(String integerString) {
        // ensure the integer is specified in the command
        if(integerString == null){
            System.err.println("Input error. A BUY or SELL command must be followed by a volume and price.");
            return false;
        }

        // ensure the string contains one or more digits and nothing else
        if(!digitsPattern.matcher(integerString).matches()){
            System.err.println("Input error. A BUY or SELL command must be followed by a volume and price. The volume and price can only contain digits.");
            return false;
        }

        // ensure there aren't too many digits to fit inside an integer
        if(integerString.length() > Integer.toString(Integer.MAX_VALUE).length()){
            System.err.println("Input error. A volume or quantity can not be higher than 2147483647.");
            return false;
        }

        // ensure it is a positive number small enough to fit into an integer
        long actual = Long.parseLong(integerString);
        if(actual <= 0){
            System.err.println("Input error. A volume or quantity must be a positive integer (1-2147483647).");
            return false;
        }
        else if(actual > Integer.MAX_VALUE){
            System.err.println("Input error. A volume or quantity can not be higher than 2147483647.");
            return false;
        }

        return true;
    }
}
