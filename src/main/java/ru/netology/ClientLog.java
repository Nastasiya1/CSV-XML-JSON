package ru.netology;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringJoiner;

public class ClientLog {
    private String num;
    private String quantity;
    private Collection<ClientLog> logs = new ArrayList<>();

    public ClientLog(String num, String quantity) {
        this.num = num;
        this.quantity = quantity;
    }

    public ClientLog() {
    }

    public void log(int productNum, int amount) {
        if (logs.isEmpty()) {
            logs.add(new ClientLog("productNum", "amount"));
        }
        logs.add(new ClientLog(String.valueOf(productNum), String.valueOf(amount)));
    }

    public void exportAsCSV(File txtFile) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(txtFile))) {
            for (ClientLog cl : logs) {
                StringJoiner logString = new StringJoiner(",")
                        .add(cl.num)
                        .add(cl.quantity);
                String[] logForCsv = logString.toString().split(",");
                writer.writeNext(logForCsv);
            }
        }
    }
}