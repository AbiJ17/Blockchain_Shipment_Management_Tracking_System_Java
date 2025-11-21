package ui;

import model.Role;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame {

    private final Map<String, User> demoUsers = new HashMap<>();

    public LoginFrame() {
        seedDemoUsers();
        initUI();
    }

    private void seedDemoUsers() {
        demoUsers.put("shipper", new User(1, "shipper", "pass", Role.SHIPPER, "shipper@example.com"));
        demoUsers.put("buyer", new User(2, "buyer", "pass", Role.BUYER, "buyer@example.com"));
        demoUsers.put("admin", new User(3, "admin", "pass", Role.ADMIN, "admin@example.com"));
    }

    private void initUI() {
        // Colors
        Color bg = new Color(5, 8, 22);
        Color cardBg = new Color(11, 18, 36);
        Color accent = new Color(40, 120, 255);
        Color accentHover = new Color(70, 150, 255);
        Color textPrimary = new Color(240, 244, 255);
        Color textSecondary = new Color(160, 170, 190);

        setTitle("Blockchain Shipment Tracking - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(bg);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 0, 5, 0);

        JLabel title = new JLabel("Blockchain Console Login");
        title.setForeground(textPrimary);
        title.setFont(new Font("Inter", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Use: shipper / buyer / admin  with password 'pass'");
        subtitle.setForeground(textSecondary);
        subtitle.setFont(new Font("Inter", Font.PLAIN, 12));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        styleTextField(usernameField, cardBg, textPrimary);
        styleTextField(passwordField, cardBg, textPrimary);

        JButton loginBtn = createPrimaryButton("Login", accent, accentHover, textPrimary);
        JButton exitBtn = createSecondaryButton("Exit", cardBg.darker(), textSecondary);

        // layout
        c.gridy = 0;
        card.add(title, c);
        c.gridy = 1;
        card.add(subtitle, c);

        c.gridy = 2;
        c.insets = new Insets(20, 0, 3, 0);
        JLabel uLabel = new JLabel("Username");
        uLabel.setForeground(textSecondary);
        uLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        card.add(uLabel, c);

        c.gridy = 3;
        c.insets = new Insets(0, 0, 10, 0);
        card.add(usernameField, c);

        c.gridy = 4;
        c.insets = new Insets(10, 0, 3, 0);
        JLabel pLabel = new JLabel("Password");
        pLabel.setForeground(textSecondary);
        pLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        card.add(pLabel, c);

        c.gridy = 5;
        c.insets = new Insets(0, 0, 20, 0);
        card.add(passwordField, c);

        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.setLayout(new GridLayout(1, 2, 10, 0));
        btnRow.add(loginBtn);
        btnRow.add(exitBtn);

        c.gridy = 6;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(btnRow, c);

        add(card, new GridBagConstraints());

        // Actions
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            User user = demoUsers.get(username);
            if (user != null && user.authenticate(password)) {
                // open main UI
                SwingUtilities.invokeLater(() -> new MainUI(user));
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void styleTextField(JTextField field, Color bg, Color fg) {
        field.setBackground(bg.darker());
        field.setForeground(fg);
        field.setCaretColor(fg);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                new EmptyBorder(8, 10, 8, 10)));
    }

    private JButton createPrimaryButton(String text, Color accent, Color accentHover, Color fg) {
        JButton btn = new JButton(text);
        btn.setForeground(fg);
        btn.setBackground(accent);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Inter", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(8, 10, 8, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(accentHover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(accent);
            }
        });
        return btn;
    }

    private JButton createSecondaryButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Inter", Font.PLAIN, 13));
        btn.setBorder(new EmptyBorder(8, 10, 8, 10));
        return btn;
    }
}
