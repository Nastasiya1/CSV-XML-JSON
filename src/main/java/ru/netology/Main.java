package ru.netology;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Basket basket;

    public static void main(String[] args) throws IOException {
        String[] products = {
                "Хлеб",
                "Яблоки",
                "Молоко",
                "Каша",
                "Мука",
                "Творог"};
        int[] prices = {
                60,
                55,
                70,
                90,
                80,
                100};

//        File textFile = new File("basket.txt");
//
//        if (textFile.exists()) {
//            basket = Basket.loadFromTxtFile(textFile);
//        } else {
//            basket = new Basket(products, prices);
//        }

        File jsonFile = new File("basket.json");

        if (jsonFile.exists()) {
            basket = new Basket(products, prices);
            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(new FileReader("basket.json"));
                JSONObject basketParsedJson = (JSONObject) obj;

                JSONArray productsJson = (JSONArray) basketParsedJson.get("products");
                String[] productsFromJson = new String[productsJson.size()];
                for (int i = 0; i < productsJson.size(); i++) {
                    productsFromJson[i] = (String) productsJson.get(i);
                }
                basket.setProducts(productsFromJson);

                JSONArray pricesJson = (JSONArray) basketParsedJson.get("prices");
                String[] pricesFromJson = new String[pricesJson.size()];
                for (int i = 0; i < pricesJson.size(); i++) {
                    pricesFromJson[i] = (String) pricesJson.get(i);
                }
                int[] intPricesFromJson = new int[pricesFromJson.length];
                for (int i = 0; i < pricesFromJson.length; i++) {
                    intPricesFromJson[i] = Integer.parseInt(pricesFromJson[i]);
                }
                basket.setPrices(intPricesFromJson);

                JSONArray amountJson = (JSONArray) basketParsedJson.get("amount");
                String[] amountFromJson = new String[amountJson.size()];
                for (int i = 0; i < amountJson.size(); i++) {
                    amountFromJson[i] = (String) amountJson.get(i);
                }
                int[] intAmountFromJson = new int[amountFromJson.length];
                for (int i = 0; i < amountFromJson.length; i++) {
                    intAmountFromJson[i] = Integer.parseInt(amountFromJson[i]);
                }
                basket.setAmountOfProducts(intAmountFromJson);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            basket = new Basket(products, prices);
        }

        System.out.println("Список возможных товаров для покупки");
        for (int i = 0; i < products.length; i++) {
            System.out.println(i + 1 + "." + " " + products[i] + " " + prices[i] + " руб/шт");
        }

        Scanner scanner = new Scanner(System.in);
        ClientLog clientLog = new ClientLog();

        while (true) {
            System.out.println("Выберите товар и количество или введите `end`");
            String input = scanner.nextLine();
            if ("end".equals(input)) {
                break;
            }
            String[] parts = input.split(" ");
            int productNumber = Integer.parseInt(parts[0]) - 1;
            int productCount = Integer.parseInt(parts[1]);

            clientLog.log(productNumber + 1, productCount);

            basket.addToCart(productNumber, productCount);

//            basket.saveTxt(textFile);

            JSONObject basketJson = new JSONObject();

            JSONArray productsJson = new JSONArray();
            productsJson.addAll(List.of(basket.getProducts()));
            basketJson.put("products", productsJson);

            JSONArray pricesJson = new JSONArray();
            String[] stringPricesJson = Arrays.stream(basket.getPrices())
                    .mapToObj(String::valueOf)
                    .toArray(String[]::new);
            pricesJson.addAll(List.of(stringPricesJson));
            basketJson.put("prices", pricesJson);

            JSONArray amountJson = new JSONArray();
            String[] stringAmountJson = Arrays.stream(basket.getAmountOfProducts())
                    .mapToObj(String::valueOf)
                    .toArray(String[]::new);
            amountJson.addAll(List.of(stringAmountJson));
            basketJson.put("amount", amountJson);

            try (FileWriter fileWriter = new FileWriter("basket.json")) {
                fileWriter.write(basketJson.toJSONString());
                fileWriter.flush();
            }
        }
        basket.printCart();
        clientLog.exportAsCSV(new File("blog.csv"));
    }
}