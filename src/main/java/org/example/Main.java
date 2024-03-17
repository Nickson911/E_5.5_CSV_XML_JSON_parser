package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //CSV - JSON парсер
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> CSV = parseCSV(columnMapping, "data.csv");
        writeString(listToJson(CSV), "new data.json");

        //XML - JSON парсер
        List<Employee> XML = parseXML("data.xml");
        writeString(listToJson(XML), "new data2.json");

        //JSON парсер
        String json = readString("new data.json");
        System.out.println(jsonToList(json));
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String str, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(str);
            file.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(new File(fileName));

        Node node = document.getDocumentElement();
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node1 = nodeList.item(i);
            if (Node.ELEMENT_NODE == node1.getNodeType()) {
                Element element = (Element) node1;
                int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                Employee employee = new Employee(id, firstName, lastName, country, age);
                list.add(employee);
            }
        }
        return list;
    }

    public static String readString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            for (Object o : jsonArray) {
                Employee employee = gson.fromJson(o.toString(), Employee.class);
                list.add(employee);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}