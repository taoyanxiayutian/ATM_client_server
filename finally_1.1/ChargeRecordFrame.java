import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ChargeRecordFrame extends JFrame {
    private JTable table;
    private Vector<Object[]> dataVector; // 用于存储数据的向量

    public ChargeRecordFrame(String carnumber, String serverResponse) throws SQLException {
        setTitle("查询流水");
        setPreferredSize(new Dimension(600, 700));
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));// 创建一个面板，并使用GridLayout布局管理器，4行1列，行间距和列间距为10像素
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));// 将面板添加到窗口的中间位置
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        dataVector = new Vector<>(); // 初始化数据向量

        // 解析服务器响应并填充数据向量
        parseServerResponse(serverResponse);
        table = createTable(dataVector);
        table.setBounds(100,100,500,700);
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void parseServerResponse(String serverResponse) {
        // 假设服务器响应格式为 "record,card_number,action_type,balance,withdraw_time"
        // 并且响应中不包含 "balance:" 这样的前缀
        String[] records = serverResponse.split(","); // 分割字符串为字段数组
      //  if (records.length == 4) { // 确保字段数量正确
        for(int i=0;i< records.length/4;i++){
            Object[] rowData = new Object[]{records[i*4], records[i*4+1], records[i*4+2], records[i*4+3]};
            dataVector.add(rowData); // 添加解析后的数据到数据向量//
             }
       // }
    }

    private JTable createTable(Vector<Object[]> dataVector) {
        // 列名数组
        String[] columnNames = {"Card Number", "Action Type", "Balance", "Withdraw Time"};

        // 创建表格模型
        DefaultTableModel model = new DefaultTableModel(dataVector.toArray(new Object[1][1]), columnNames);

        // 创建表格并返回
        table = new JTable(model);
        return table;
    }

    // 主方法用于测试 ChargeRecordFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ChargeRecordFrame frame = new ChargeRecordFrame("123456", "record,123456,deposit,1000.00,2023-04-10 10:30:00");
                    //frame.pack();

                    frame.setVisible(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}