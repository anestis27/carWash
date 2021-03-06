/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkout;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Anestis
 */
public class ServerInput {

    private ArrayList<String> inputData;

    public ServerInput() {
        try {
            ServerSocket serverSocket = new ServerSocket(7896);
            Socket clientSocket = serverSocket.accept();
            Scanner clientIn = new Scanner(clientSocket.getInputStream());
            PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
            inputData = new ArrayList<>(8);
            do {
                String message = clientIn.nextLine();
                inputData.add(message);
                clientOut.println(message);
            } while (clientIn.hasNextLine());
            addCostInList();
            clientOut.close();
            clientIn.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    
    public void addCostInList() {
        String vehicleType;
        if (Integer.parseInt(inputData.get(1)) == 0) {
            vehicleType = "Αυτοκίνητο";
        } else if (Integer.parseInt(inputData.get(1)) == 1) {
            vehicleType = "Φορτηγό";
        } else {
            vehicleType = "Μηχανή";
        }

        String vehicleNumber = inputData.get(0);

        ArrayList<Integer> servArray = new ArrayList<>();
        for (int i = 2; i < inputData.size(); i++) {
            servArray.add(Integer.parseInt(inputData.get(i)));
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String time = dateFormat.format(date);

        int cost = 0;
        for (int i = 0; i < servArray.size(); i++) {
            cost += AvailableServices.getListServices().get(servArray.get(i)).getCost()[Integer.parseInt(inputData.get(1))];
        }


        Costumer cos = new Costumer(vehicleType, vehicleNumber, servArray, cost, time);
        CheckOutWin.getArrayCos().add(cos);
        //Grafei ton pinakka se arxeio
        ManagmentFile.refreshNowFile(CheckOutWin.getArrayCos());

        DefaultTableModel model = (DefaultTableModel) CheckOutWin.getCarTable().getModel();
        model.addRow(new Object[]{vehicleNumber, vehicleType, cost + "", time});

        //biblio eisodou
        ManagmentFile.writeToFile(cos,0);
        inputData.clear();
    }
}
