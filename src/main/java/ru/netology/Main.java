package ru.netology;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    static Basket basket;
    static String fileToLoadName;
    static String fileToSaveName;
    static String fileToLogName;

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
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

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("shop.xml"));
        NodeList nodeList = document.getChildNodes().item(0).getChildNodes();
        String methodToLoad = null;
        String methodToSave = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                if (element.getTagName().equals("load")) {
                    if (element.getElementsByTagName("enabled").item(0).getTextContent().equals("true")) {
                        fileToLoadName = element.getElementsByTagName("fileName").item(0).getTextContent();
                        if (element.getElementsByTagName("format").item(0).getTextContent().equals("json")) {
                            methodToLoad = "json";
                        }
                        if (element.getElementsByTagName("format").item(0).getTextContent().equals("txt")) {
                            methodToLoad = "txt";
                        }
                    }
                }
                if (element.getTagName().equals("save")) {
                    if (element.getElementsByTagName("enabled").item(0).getTextContent().equals("true")) {
                        fileToSaveName = element.getElementsByTagName("fileName").item(0).getTextContent();
                        if (element.getElementsByTagName("format").item(0).getTextContent().equals("json")) {
                            methodToSave = "json";
                        }
                        if (element.getElementsByTagName("format").item(0).getTextContent().equals("txt")) {
                            methodToSave = "txt";
                        }
                    }
                }
                if (element.getTagName().equals("log")) {
                    if (element.getElementsByTagName("enabled").item(0).getTextContent().equals("true")) {
                        fileToLogName = element.getElementsByTagName("fileName").item(0).getTextContent();
                    }
                }
            }
        }

        if (Objects.equals(methodToLoad, "json")) {
            loadFromJson(products, prices);
        }
        if (Objects.equals(methodToLoad, "txt")) {
            loadFromTxt(products, prices);
        } else {
            basket = new Basket(products, prices);
        }

        System.out.println("Список возможных товаров для покупки");
        for (int ii = 0; ii < products.length; ii++) {
            System.out.println(ii + 1 + "." + " " + products[ii] + " " + prices[ii] + " руб/шт");
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
            if (Objects.equals(methodToSave, "json")) {
                saveToJson();
            }
            if (Objects.equals(methodToSave, "txt")) {
                basket.saveTxt(new File(fileToSaveName));
            }
            basket.printCart();
            clientLog.exportAsCSV(new File(fileToLogName));
        }
    }

    public static void saveToJson() throws IOException {
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

    public static void loadFromTxt(String[] products, int[] prices) throws IOException {
        File textFile = new File(fileToLoadName);
        if (textFile.exists()) {
            basket = Basket.loadFromTxtFile(textFile);
        } else {
            basket = new Basket(products, prices);
        }
    }

    public static void loadFromJson(String[] products, int[] prices) throws IOException {
        File jsonFile = new File(fileToLoadName);
        if (jsonFile.exists()) {
            basket = new Basket(products, prices);
            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(new FileReader(fileToLoadName));
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
    }
}