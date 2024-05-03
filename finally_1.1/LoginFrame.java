import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import javax.swing.*;

public class LoginFrame {
private  static  String ip="192.168.31.64";
    private JFrame frame;
    private JTextField textCardNumber;
    private JPasswordField textPassword;
    private JButton confirmButton;


    private JButton loginButton;
    //private JTextArea responseArea; // 用于显示服务器响应的文本区域
    private Socket socket; // 定义socket对象，用于建立连接
    private PrintWriter out; // 用于向服务器发送数据
    private BufferedReader in; // 用于接收服务器数据

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame window = new LoginFrame();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LoginFrame() {
        initialize();
        establishConnection(); // 初始化时建立连接
    }

    //连接
    private void establishConnection() {
        try {
            socket = new Socket(ip, 2525); // 假设服务器运行在7788端口
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 连接成功后，启用确认按钮
            if (confirmButton != null) {
                confirmButton.setEnabled(true);
        }
    } catch (IOException e) {
        e.printStackTrace();
        // 连接失败，可以选择重试或退出
        String message = "无法连接到服务器: " + e.getMessage();
        JOptionPane.showMessageDialog(frame, message, "错误", JOptionPane.ERROR_MESSAGE);
        System.exit(0); // 退出程序
        }
    }

    //发送请求并接受response
    private String sendRequest(String request) {
        if (out == null || in == null) {
            return null; // 如果没有建立连接，返回null
        }
        out.println(request);
        out.flush(); // 确保请求被发送
        try {
            return in.readLine(); // 读取并返回响应
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //账号确认按钮的监听器
    private void confirmButtonAction(ActionEvent e) {
        String inputNumber = textCardNumber.getText().trim();
        if (inputNumber.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "请输入账号", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String response = sendRequest("HELO " + inputNumber);
        if (response == null) {
            JOptionPane.showMessageDialog(frame, "请求发送失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if ("500 AUTH REQUIRE".equalsIgnoreCase(response)) {
            JOptionPane.showMessageDialog(frame, "认证需要，请继续输入密码。", "提示", JOptionPane.INFORMATION_MESSAGE);
            // 启用登录按钮，以便用户可以输入密码
            if (loginButton != null) {
                loginButton.setEnabled(true);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "账户错误: " + response, "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    //登录确认按钮的监听器
    private void loginButtonAction(ActionEvent e) {
        String inputPassword = new String(textPassword.getPassword()).trim();
        // 确保输入的密码不为空
        if (inputPassword.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "请输入密码", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // 发送账号和密码到服务器
        String request = "PASS " + inputPassword;
        String response = sendRequest(request);
        if (response == null) {
            // 发送请求失败，通知用户
            JOptionPane.showMessageDialog(frame, "请求发送失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 根据服务器响应进行操作
        if ("525 OK!".equalsIgnoreCase(response)) {
            // 登录成功，隐藏当前窗口并打开主界面
            frame.setVisible(false);
            MainFrame mainFrame = new MainFrame();
            //主界面设置卡号
            mainFrame.textCardNumber.setText(textCardNumber.getText());
            mainFrame.getFrame().setVisible(true);
            //向主界面的获取余额按钮添加监听器
            mainFrame.getbalanceButton.addActionListener(this::balanceButtonAction);
            //向主界面的取钱按钮添加监听器
            mainFrame.withdrawMoneyButton.addActionListener(this::withdrawButtonAction);
            //向主界面的取钱按钮添加监听器
            mainFrame.saveMoneyButton.addActionListener(this::saveButtonAction);
            mainFrame.exitButton.addActionListener(this::exitButtonAction);
            mainFrame.chargeRecordButton.addActionListener(this::chargeRecordButtonAction);
        } else {
            // 登录失败，显示错误信息
            JOptionPane.showMessageDialog(frame, "账户或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    //主界面获取余额按钮的监听器
    private void balanceButtonAction(ActionEvent e) {

        // 发送账号和密码到服务器
        String request = "BALA";
        String response = sendRequest(request);

        if (response == null) {
            // 发送请求失败，通知用户
            JOptionPane.showMessageDialog(frame, "请求发送失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 解析响应并更新余额显示
        String[] parts = response.split(",");
        if (parts[0].equals("success") && parts.length == 3) {
            double balance = Double.parseDouble(parts[2]);
            // 显示一个信息对话框告知用户余额信息
            JOptionPane.showMessageDialog(null, "余额: ￥" + balance, "余额信息", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 显示错误信息
            JOptionPane.showMessageDialog(null, "查询失败，请稍后再试。", "余额查询", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exitButtonAction(ActionEvent e){
        // 发送账号和密码到服务器
        String request = "BYE";
        String response = sendRequest(request);
        if (response == null) {
            // 发送请求失败，通知用户
            JOptionPane.showMessageDialog(frame, "请求发送失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 解析响应并更新余额显示
        String[] parts = response.split(",");
        if (parts[0].equals("BYE")) {
            System.out.println("BYE" );
            JOptionPane.showMessageDialog(null, "退卡成功。","退卡", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else {
            // 显示错误信息
            JOptionPane.showMessageDialog(null, "退卡失败，请稍后再试。", "余额查询", JOptionPane.ERROR_MESSAGE);
        }
    }

    //主界面取钱按钮的监听器
    private void withdrawButtonAction(ActionEvent e) {
        // 创建withdrawFrame实例
        withdraw withdrawFrame = new withdraw();
        // 显示WithdrawFrame窗口
        withdrawFrame.getFrame().setVisible(true);

        // 设置取款界面的卡号文本框为当前MainFrame界面的卡号
        withdrawFrame.textCardNumber_with.setText(textCardNumber.getText().trim());

        // 向确取钱具体界面的确认按钮添加监听器，以便在用户点击确认按钮时执行取款操作
        withdrawFrame.withdrawconfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户输入的取款金额
                String withdraw = withdrawFrame.textwithdrawnumber.getText().trim();
                //如果不为空则继续
                if (!withdraw.isEmpty()) {
                    try {
                        double withdrawnAmount = Double.parseDouble(withdraw);
                        //初步处理，需要金额大于0
                        if (withdrawnAmount > 0) {
                            // 发送请求到服务器
                            String request = "WDRA " + withdrawnAmount;
                            //System.out.println(request);
                            //获得response
                            String response = sendRequest(request);
                            // 解析响应并显示
                            if (response != null) {
                                if (response.equals("525 OK!")) {
                                    // 显示一个信息对话框告知用户取钱成功
                                    JOptionPane.showMessageDialog(null, "取钱成功: " , "取款成功", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    // 显示错误信息
                                    JOptionPane.showMessageDialog(null, "取钱失败" , "取款失败", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "未收到服务器响应。", "通信错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "请输入有效的取钱金额。", "取款", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "请输入有效的取钱金额。", "取款", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
    //主界面存钱按钮的监听器
    private void saveButtonAction(ActionEvent e) {
        // 创建saveFrame实例
        saveFrame saveFrame = new saveFrame();
        // 显示saveFrame窗口
        saveFrame.getFrame().setVisible(true);

        // 设置取款界面的卡号文本框为当前MainFrame界面的卡号
        saveFrame.textCardNumber_with.setText(textCardNumber.getText().trim());

        // 向确存钱具体界面的确认按钮添加监听器，以便在用户点击确认按钮时执行取款操作
        saveFrame.saveconfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户输入的存款金额
                String save =saveFrame.textsavenumber.getText().trim();
                //如果不为空则继续
                if (!save.isEmpty()) {
                    try {
                        double saveAmount = Double.parseDouble(save);
                        //初步处理，需要金额大于0
                        if (saveAmount >= 0) {
                            // 发送请求到服务器
                            String request = "DEPOSIT " + saveAmount;
                            //System.out.println(request);
                            //获得response
                            String response = sendRequest(request);
                            // 解析响应并显示
                            if (response != null) {
                                if (response.equals("525 OK!")) {
                                    // 显示一个信息对话框告知用户取钱成功
                                    JOptionPane.showMessageDialog(null, "存钱成功: " , "存钱成功", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    // 显示错误信息
                                    JOptionPane.showMessageDialog(null, "存钱失败" , "存钱失败", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "未收到服务器响应。", "通信错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "请输入有效的存钱金额。", "存钱", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "请输入有效的存钱金额。", "存钱", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }


    //流水记录
    private void chargeRecordButtonAction(ActionEvent e) {
        // 创建 ChargeRecordFrame 实例
        String request = "RECORD";
        String response = sendRequest(request);
        // 检查请求是否成功
        if (response != null) {
            // 创建并显示 ChargeRecordFrame
            ChargeRecordFrame chargeFrame = null;
            try {
                chargeFrame = new ChargeRecordFrame(textCardNumber.getText(),response);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            chargeFrame.setBounds(100, 100, 703, 502);
            chargeFrame.setVisible(true);
        } else {
            // 显示错误信息
            JOptionPane.showMessageDialog(frame, "查询失败，请稍后再试。", "流水查询", JOptionPane.ERROR_MESSAGE);
        }
    }


    //登录具体界面
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

        //字 登陆界面
        JLabel lblNewLabel_2 = new JLabel("登录界面");
        lblNewLabel_2.setForeground(Color.BLUE);
        lblNewLabel_2.setFont(new Font("宋体", Font.BOLD, 25));
        lblNewLabel_2.setBounds(258, 28, 201, 66);
        panel.add(lblNewLabel_2);

        //账号标签
        JLabel lblNewLabel = new JLabel("账  号：");
        lblNewLabel.setFont(new Font("宋体", Font.BOLD, 20));
        lblNewLabel.setBounds(154, 150, 83, 40);
        panel.add(lblNewLabel);

        //输入账号提示符
        textCardNumber = new JTextField();
        textCardNumber.setToolTipText("请输入账号");
        textCardNumber.setBounds(277, 150, 145, 40);
        panel.add(textCardNumber);
        textCardNumber.setColumns(10);

        // 登录确认按钮
        confirmButton = new JButton("确认");
        confirmButton.setToolTipText("点击确认账号");
        confirmButton.setBackground(new Color(162,205,90));
        confirmButton.addActionListener(this::confirmButtonAction);
        confirmButton.setFont(new Font("宋体", Font.BOLD, 20));
        confirmButton.setForeground(SystemColor.textHighlight);
        confirmButton.setBounds(500, 150, 123, 40);
        panel.add(confirmButton);

        //密码标签
        JLabel lblNewLabel_1 = new JLabel("密  码：");
        lblNewLabel_1.setFont(new Font("宋体", Font.BOLD, 20));
        lblNewLabel_1.setBounds(154, 301, 83, 30);
        panel.add(lblNewLabel_1);

        //输入密码提示符
        textPassword = new JPasswordField();
        textPassword.setToolTipText("请输入密码");
        textPassword.setBounds(277, 301, 145, 40);
        panel.add(textPassword);

        // 登录按钮
        loginButton = new JButton("登录");
        loginButton.setToolTipText("点击登录");
        loginButton.setBackground(new Color(162,205,90));
        loginButton.addActionListener(this::loginButtonAction);

        loginButton.setFont(new Font("宋体", Font.BOLD, 20));
        loginButton.setForeground(SystemColor.textHighlight);
        loginButton.setBounds(500, 301, 123, 40);
        panel.add(loginButton);
    }
}