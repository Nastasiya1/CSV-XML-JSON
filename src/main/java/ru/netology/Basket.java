package ru.netology;

import java.io.*;

public class Basket {
    private String[] products;
    private int[] prices;
    private int[] amountOfProducts;

    public Basket(String[] products, int[] prices) {
        this.products = products;
        this.prices = prices;
        this.amountOfProducts = new int[products.length];
    }

    public void addToCart(int productNum, int amount) {
        amountOfProducts[productNum] += amount;
    }

    public void printCart() {
        int sum = 0;
        System.out.println("Ваша корзина:");
        for (int i = 0; i < amountOfProducts.length; i++) {
            if (amountOfProducts[i] > 0) {
                System.out.println(products[i] + " - " + amountOfProducts[i] + " шт., " + prices[i] + " руб. за шт., " + amountOfProducts[i] * prices[i] + " руб. в сумме");
                sum += amountOfProducts[i] * prices[i];
            }
        }
        System.out.println("Итого " + sum + " руб");
    }

    public void saveTxt(File textFile) throws IOException {
        try (FileWriter file = new FileWriter(textFile);
             PrintWriter out = new PrintWriter(file)) {
            for (String product : products) {
                out.print(product + " ");
            }
            out.println();
            for (int price : prices) {
                out.print(price + " ");
            }
            out.println();
            for (int amount : amountOfProducts) {
                out.print(amount + " ");
            }
        }
    }

    public static Basket loadFromTxtFile(File textFile) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(textFile))) {
            String[] products = in.readLine().split(" ");
            String[] prices = in.readLine().split(" ");
            int[] convertedPrices = new int[prices.length];
            for (int i = 0; i < prices.length; i++) {
                convertedPrices[i] = Integer.parseInt(prices[i]);
            }
            String[] amount = in.readLine().split(" ");
            int[] convertedAmount = new int[amount.length];
            for (int ii = 0; ii < amount.length; ii++) {
                convertedAmount[ii] = Integer.parseInt(amount[ii]);
            }
            Basket result = new Basket(products, convertedPrices);
            result.amountOfProducts = convertedAmount;
            return result;
        }
    }
}