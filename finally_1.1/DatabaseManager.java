import java.sql.*;

public class DatabaseManager {
    // 假设的数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost/test";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    //登陆验证的数据库方法
    public static boolean checkCredentials(String cardNumber, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean isValid = false;

        try {
            // 建立数据库连接
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 构建SQL查询语句
            String sql = "SELECT COUNT(*) FROM customers WHERE card_number = ? AND password = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.setString(2, password);

            // 执行查询
            resultSet = preparedStatement.executeQuery();

            // 如果结果大于0，表示用户名和密码匹配
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                isValid = count > 0;
            }
        } catch (SQLException e) {
            // 处理SQL异常
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

    //查询余额的数据库方法
    public static double getBalance(String cardNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        double balance = 0.0;

        try {
            // 建立数据库连接
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 构建SQL查询语句
            String sql = "SELECT balance FROM customers WHERE card_number = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, cardNumber);

            // 执行查询
            resultSet = preparedStatement.executeQuery();

            // 读取余额信息
            if (resultSet.next()) {
                balance = resultSet.getDouble("balance");
            }
        } catch (SQLException e) {
            // 处理SQL异常
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return balance;
    }

    //插入流水
    public static void logTransaction(String cardNumber, String actionType, double balance, long withdrawTime) throws SQLException {
        Timestamp timestamp = new Timestamp(withdrawTime);
        String sql = "INSERT INTO withdraw_record (card_number, action_type, balance, withdraw_time) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cardNumber);
            preparedStatement.setString(2, actionType);
            preparedStatement.setDouble(3, balance);
            // 设置 Timestamp 对象
            preparedStatement.setTimestamp(4, timestamp);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw e; // 重新抛出异常，以便调用者可以处理
        }
    }

    // 更新用户余额的方法
    public static void updateBalance(String cardNumber, double newBalance) throws SQLException {
        String sql = "UPDATE customers SET balance = ? WHERE card_number = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw e; // 重新抛出异常，以便调用者可以处理
        }
    }

    public static String retrieve(String cardNumber) throws SQLException {
        String sql = "SELECT card_number, action_type, balance, withdraw_time FROM withdraw_record WHERE card_number = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
               /* result.append(" ").append(resultSet.getString("card_number"))
                        .append(", action_type: ").append(resultSet.getString("action_type"))
                        .append(", balance: ").append(resultSet.getDouble("balance"))
                        .append(", withdraw_time: ").append(resultSet.getTimestamp("withdraw_time")).append("\n");*
                */
                result.append(" ").append(resultSet.getString("card_number"))
                        .append(", action_type: ").append(resultSet.getString("action_type"))
                        .append(", balance: ").append(resultSet.getDouble("balance"))
                        .append(", action_time: ").append(resultSet.getTimestamp("withdraw_time")).append(",")
                ;
            }
            return result.toString();
        } catch (SQLException e) {
            // 抛出 SQLException，以便调用者可以处理
            throw e;
        }
    }
    //登录和登出日志
    public static void logger(String cardNumber, String action,long actiontime){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            // 建立数据库连接
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 构建SQL插入语句
            String sql = "INSERT INTO log_record (card_number, login_logout,action_time) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.setString(2, action);
            preparedStatement.setTimestamp(3, new Timestamp(new Date(actiontime).getTime()));

            // 执行插入操作
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Log inserted successfully");
            } else {
                System.out.println("No logs inserted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
