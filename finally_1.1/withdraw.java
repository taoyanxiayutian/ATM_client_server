import java.awt.EventQueue;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * 登陆界面
 */
public class withdraw {

    private JFrame frame;
    public JTextField textCardNumber_with;

    public JTextField textwithdrawnumber;
    public JButton withdrawconfirmButton;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    withdraw window = new withdraw();
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
    public withdraw() {
        initialize();
    }

    //具体登录界面和登录操作
    private void initialize() {
        frame = new JFrame();
        frame.setForeground(Color.PINK);
        frame.setTitle("中国建设银行ATM系统");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("D:\\论文下载\\ATM-Bank-Management-main\\ATMSystem\\logo\\ConstructionBank.jpg"));
        frame.setBounds(100, 100, 670, 471);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int windowWidth = frame.getWidth();                     //获得窗口宽
        int windowHeight = frame.getHeight();                   //获得窗口高
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();    //获取屏幕的尺寸
        int screenWidth = screenSize.width;                     //获取屏幕的宽
        int screenHeight = screenSize.height;                   //获取屏幕的高
        frame.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        //账号标签
        JLabel lblNewLabel = new JLabel("账  号：");
        lblNewLabel.setFont(new Font("宋体", Font.BOLD, 20));
        lblNewLabel.setBounds(184, 90, 83, 40);
        panel.add(lblNewLabel);

        //输入账号提示符and文本框
        textCardNumber_with = new JTextField();
        textCardNumber_with.setBounds(277, 90, 145, 30);
        panel.add(textCardNumber_with);
        textCardNumber_with.setColumns(10);

        //取钱标签
        JLabel lblNewLabel_2 = new JLabel("取钱金额：");
        lblNewLabel_2.setFont(new Font("宋体", Font.BOLD, 20));
        lblNewLabel_2.setBounds(165, 201, 150, 30);
        panel.add(lblNewLabel_2);

        //输入金额提示符and文本框
        textwithdrawnumber = new JTextField();
        textwithdrawnumber.setToolTipText("请输入金额");
        textwithdrawnumber.setBounds(277, 201, 145, 30);
        panel.add(textwithdrawnumber);

        //确认按钮
        withdrawconfirmButton = new JButton("确认");
        withdrawconfirmButton.setToolTipText("点击取钱");
        withdrawconfirmButton.setBackground(new Color(162,205,90));

        withdrawconfirmButton.setFont(new Font("宋体", Font.BOLD, 20));
        withdrawconfirmButton.setForeground(SystemColor.textHighlight);
        withdrawconfirmButton.setBounds(160, 309, 123, 49);
        panel.add(withdrawconfirmButton);

        //退出按钮
        JButton logoutButton = new JButton("退出");
        logoutButton.setToolTipText("点击取钱");
        logoutButton.setBackground(new Color(162,205,90));
       logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });

        logoutButton.setFont(new Font("宋体", Font.BOLD, 20));
        logoutButton.setForeground(SystemColor.textHighlight);
        logoutButton.setBounds(384, 309, 123, 49);
        panel.add(logoutButton);
    }
}