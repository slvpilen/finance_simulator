package socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONArray;
import org.json.JSONObject;

import socket.modelA.TrainingData;

public class SocketClient {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void fitModelA(TrainingData trainingData,
            int date) {
        JSONObject trainingDataJson = trainingData.toJson(date);

        JSONArray data = new JSONArray();
        data.put("model_a_fit");
        data.put(trainingDataJson);

        // System.out.println("Connecting to Python server on " + host + ":" + port);
        try (Socket socket = new Socket(HOST, PORT)) {

            // Send JSON data to Python server
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
            out.println(data.toString());

            // Receive the response from the Python server
            // System.out.println("Waiting for response from Python server...");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            String response = in.readLine();
            // System.out.println("Response from Python server: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static double getModelAPrediction(int dayOfWeek,
            int dayOfMonth, double oneDayChange,
            double twoDayChange, double treeDayChange,
            double foureDayChange, double fiveDayChange,
            double rsi_daily, double rsi_weekly,
            double rsi_monthly) {

        // System.out.println("Connecting to Python server on " + host + ":" + port);
        try (Socket socket = new Socket(HOST, PORT)) {

            JSONArray data = new JSONArray();
            // Create JSON data
            // Add the string "model_a" to the JSONArray
            data.put("model_a");

            // Create a JSONObject to hold the key-value pairs
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("day_of_week", dayOfWeek);
            jsonObject.put("day_of_month", dayOfMonth);
            jsonObject.put("1_day_change", oneDayChange);
            jsonObject.put("2_day_change", twoDayChange);
            jsonObject.put("3_day_change", treeDayChange);
            jsonObject.put("4_day_change", foureDayChange);
            jsonObject.put("5_day_change", fiveDayChange);
            jsonObject.put("rsi_daily", rsi_daily);
            jsonObject.put("rsi_weekly", rsi_weekly);
            jsonObject.put("rsi_monthly", rsi_monthly);

            // Add the JSONObject to the JSONArray
            data.put(jsonObject);

            // Send JSON data to Python server
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
            out.println(data.toString());

            // Receive the response from the Python server
            // System.out.println("Waiting for response from Python server...");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            String response = in.readLine();
            // System.out.println("Response from Python server: " + response);
            return Double.valueOf(response);

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static void main(String[] args) {
        double response = SocketClient.getModelAPrediction(1, 15,
                1, 2, 3, 4, 5, 20, 15, 10);

        System.out.println(
                "Response from Python server: " + response);

    }
}
