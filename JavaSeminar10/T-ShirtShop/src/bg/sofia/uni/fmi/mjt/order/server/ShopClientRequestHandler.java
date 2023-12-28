package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopClientRequestHandler implements Runnable {
    private static final int SIZE_IDX = 0;
    private static final int COLOR_IDX = 1;
    private static final int DEST_TO_IDX = 2;
    private Socket socket;
    private final OrderRepository repo;

    public ShopClientRequestHandler(Socket socket, OrderRepository repository) {
        this.socket = socket;
        this.repo = repository;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Shop Client Request Handler for " + socket.getRemoteSocketAddress());

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // autoflush on
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) { // read the message from the client
                processClientMessage(inputLine, out);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processClientMessage(String message, PrintWriter out) {
        String[] messageTokens = message.split(" ");
        String command = messageTokens[0];
        command = command.toLowerCase();
        switch (command) {
            case "request" ->
                processRequest(Arrays.copyOfRange(messageTokens, 1, messageTokens.length), out);
            case "get" ->
                processGet(Arrays.copyOfRange(messageTokens, 1, messageTokens.length), out);
            default ->
                out.println("Unknown command");
        }
    }

    private void processRequest(String[] reqMessageTokens, PrintWriter out) {
        Pattern sizePattern = Pattern.compile("size=(\\w+)");
        Pattern colorPattern = Pattern.compile("color=(\\w+)");
        Pattern destPattern = Pattern.compile("shipTo=(\\w+)");

        String sizeStr = reqMessageTokens[SIZE_IDX];
        String colorStr = reqMessageTokens[COLOR_IDX];
        String destStr = reqMessageTokens[DEST_TO_IDX];

        String size = getElementFromReq(sizeStr, sizePattern);
        String color = getElementFromReq(colorStr, colorPattern);
        String dest = getElementFromReq(destStr, destPattern);

        out.println(this.repo.request(size, color, dest));
    }

    private String getElementFromReq(String str, Pattern pattern) {
        Matcher matcher;
        matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private void processGet(String[] getTokens, PrintWriter out) {
        String getCommand = getTokens[0];
        getCommand = getCommand.toLowerCase();
        switch (getCommand) {
            case "all" ->
                out.println(this.repo.getAllOrders());
            case "all-successful" ->
                out.println(this.repo.getAllSuccessfulOrders());
            case "my-order" ->
                processGetOrderById(getTokens[1], out);
            default ->
                out.println("Unknown command");
        }
    }

    private void processGetOrderById(String idToken, PrintWriter out) {
        Pattern orderPattern = Pattern.compile("id=(\\w+)");
        try {
            int orderId = Integer.parseInt(getElementFromReq(idToken, orderPattern));
            out.println(this.repo.getOrderById(orderId));
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Invalid id");
        }
    }
}
