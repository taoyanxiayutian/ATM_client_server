import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.util.Vector;
/**
 * ATM主界面(存款,取款,查询交易记录)
 */
public class MainFrame {

    private JFrame frame;

    public JButton getbalanceButton;
    public JButton exitButton;
    public JButton withdrawMoneyButton;
    public JButton saveMoneyButton;
    public JButton chargeRecordButton;
    public JTextField textCardNumber = new JTextField();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame window = new MainFrame();
                    //LoginFrame login=new LoginFrame();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * Create the application.
     */
    public MainFrame() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBackground(Color.WHITE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("D:\\论文下载\\ATM-Bank-Management-main\\ATMSystem\\logo\\ConstructionBank.jpg"));
        frame.setTitle("中国建设银行ATM系统");
        frame.setBounds(100, 100, 703, 502);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JLabel lblNewLabel = new JLabel("账户：");
        lblNewLabel.setFont(new Font("宋体", Font.BOLD, 17));
        lblNewLabel.setBounds(95, 200, 77, 44);
        panel.add(lblNewLabel);
        textCardNumber = new JTextField();
        textCardNumber.setFont(new Font("宋体", Font.BOLD, 17));
        textCardNumber.setBounds(182, 200, 104, 43);
        panel.add(textCardNumber);
        textCardNumber.setColumns(10);

        //存钱相关操作
        withdrawMoneyButton = new JButton("取钱");
        withdrawMoneyButton.setBackground(Color.PINK);
        withdrawMoneyButton.setFont(new Font("宋体", Font.BOLD, 17));
        withdrawMoneyButton.setBounds(369, 209, 104, 46);
        panel.add( withdrawMoneyButton);


        saveMoneyButton = new JButton("存钱");
        saveMoneyButton.setBackground(Color.PINK);
        saveMoneyButton.setFont(new Font("宋体", Font.BOLD, 17));
        saveMoneyButton.setBounds(369, 101, 104, 46);
        panel.add(saveMoneyButton);



        //交易记录查询相关操作
        chargeRecordButton = new JButton("交易记录");
        chargeRecordButton.setBackground(Color.PINK);
        chargeRecordButton.setFont(new Font("宋体", Font.BOLD, 17));
        chargeRecordButton.setBounds(520, 101, 104, 44);
        panel.add(chargeRecordButton);

        //余额查询相关操作
        getbalanceButton = new JButton("余额查询");
        getbalanceButton.setBackground(Color.PINK);
        getbalanceButton.setFont(new Font("宋体", Font.BOLD, 17));
        getbalanceButton.setBounds(520, 210, 104, 44);
        panel.add(getbalanceButton);

        //退出按钮
        exitButton = new JButton("退卡");
        exitButton.setBackground(new Color(255, 0, 0));
        exitButton.setForeground(new Color(255, 255, 255));
        exitButton.setFont(new Font("宋体", Font.BOLD, 17));
        exitButton.setBounds(440, 380, 104, 44);
        panel.add(exitButton);

        //主界面标签
        JLabel lblNewLabel_2 = new JLabel("主界面");
        lblNewLabel_2.setForeground(Color.BLUE);
        lblNewLabel_2.setFont(new Font("宋体", Font.BOLD, 25));
        lblNewLabel_2.setBounds(281, 34, 192, 56);
        panel.add(lblNewLabel_2);

    }

}