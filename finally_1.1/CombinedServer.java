import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class CombinedServer {
    private static final int PORT = 2525;
    private static String cardNumber;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Combined server is listening on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException,SQLException {
        BufferedReader in = null;
        PrintWriter out = null ;
        String inputLine;
        String requestType = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            while ((inputLine = in.readLine()) != null) {
                // 根据请求的类型进行处理
                if (inputLine.startsWith("HELO")) {
                    requestType = "hello";
                    cardNumber = inputLine.split(" ")[1];
                } else if (inputLine.startsWith("PASS")) {
                    requestType = "login";
                } else if (inputLine.startsWith("BALA")) {
                    requestType = "get balance";
                } else if (inputLine.startsWith("WDRA")) {
                    requestType = "withdraw";
                }else if(inputLine.startsWith("BYE")){
                    requestType = "bye";
                }else if(inputLine.startsWith("transaction")){
                    requestType = "get";
                }else if(inputLine.startsWith("DEPOSIT")){
                    requestType = "deposit";}
                else if(inputLine.startsWith("RECORD")){
                    requestType = "record";}
                else {
                    out.println("fail, invalid request");
                    break; // 退出循环，发送错误消息后不再处理
                }
                System.out.println(requestType);
                // 根据请求类型执行相应的操作
                switch (requestType) {
                    case "hello":
                        String response = String.format("500 AUTH REQUIRE");
                        System.out.println(response);
                        out.println(response);
                        //保存输入的卡号
                        break;
                    case "login":
                        String[] credentials = inputLine.split(" ");
                        if (credentials.length == 2) { // 假设请求格式为 "PASS password"
                            String password = credentials[1];
                            //System.out.println(cardNumber + "hhh" + password);
                            boolean loginSuccess = DatabaseManager.checkCredentials(cardNumber, password);
                            //System.out.println(loginSuccess);
                            response = loginSuccess ? "525 OK!" : "404 ERROR!";
                            if ("525 OK!".equalsIgnoreCase(response)) {
                                // 记录登录日志
                                DatabaseManager.logger( cardNumber,"login",System.currentTimeMillis());
                            }
                            else{
                                DatabaseManager.logger( cardNumber,"login_error",System.currentTimeMillis());
                            }
                            //System.out.println(response);
                            out.println(response);
                        }
                        break;
                    case "get balance":
                        double balance = DatabaseManager.getBalance(cardNumber);
                        response = String.format("success,balance,%.2f", balance);
                        out.println(response);
                        break;
                    case "withdraw":
                        String[] withdrawalDetails = inputLine.split(" ");
                        balance = DatabaseManager.getBalance(cardNumber);
                        if (withdrawalDetails.length == 2) {
                            double amount_withdrawal = Double.parseDouble(withdrawalDetails[1]);
                            if (balance >= amount_withdrawal) {
                                // 记录取款操作
                                DatabaseManager.logTransaction(cardNumber, "withdraw", balance - amount_withdrawal, System.currentTimeMillis());
                                // 取款成功，更新余额
                                DatabaseManager.updateBalance(cardNumber, balance - amount_withdrawal);

                                response = "525 OK!";

                            } else {
                                response = "401 ERROR!";
                            }
                        } else {
                            response = "fail, 验证失败";
                        }
                        System.out.println(response);
                        out.println(response);
                        break;
                    case "record":
                        response = DatabaseManager.retrieve(cardNumber);
                        System.out.println(response);
                        out.println(response);
                        break;
                    case "bye":
                        response ="BYE";
                        if ("BYE".equalsIgnoreCase(response)) {
                            // 记录登录日志
                            DatabaseManager.logger( cardNumber,"logout",System.currentTimeMillis());}
                        System.out.println(response);
                        out.println(response);
                        System.exit(0);
                        break;
                    case "deposit":
                        String[] saveDetails = inputLine.split(" ");
                        balance = DatabaseManager.getBalance(cardNumber);
                        if (saveDetails.length == 2) {
                            double amount_save = Double.parseDouble(saveDetails[1]);
                            if ( amount_save>=0) {
                                // 记录取款操作
                                DatabaseManager.logTransaction(cardNumber, "save", balance + amount_save, System.currentTimeMillis());
                                // 取款成功，更新余额
                                DatabaseManager.updateBalance(cardNumber, balance + amount_save);

                                response = "525 OK!";

                            } else {
                                response = "401 ERROR!";
                            }
                        } else {
                            response = "fail, 验证失败";
                        }
                        System.out.println(response);
                        out.println(response);
                        break;
                    default:
                        out.println("fail, invalid request");
                        break;

                }
            }
        } finally {
            if (out != null) {
                out.close(); // 关闭PrintWriter
            }
            if (in != null) {
                in.close(); // 关闭BufferedReader
            }
            clientSocket.close(); // 关闭Socket连接
        }
    }
}