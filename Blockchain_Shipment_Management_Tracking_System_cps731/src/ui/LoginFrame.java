package ui;

import controller.ShipmentComplianceController;
import controller.ShipmentLifecycleController;
import external.BlockchainNetwork;
import external.OffChainStorage;
import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import model.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LoginFrame
 * - Dark "blockchain" themed login screen.
 * - Seeds a few demo users in memory.
 * - On successful login opens MainUI with the logged-in User.
 */
public class LoginFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    private final List<User> demoUsers = new ArrayList<>();

    // Shared “in-memory backend” so MainUI doesn’t recreate everything
    private final BlockchainNetwork blockchainNetwork;
    private final BlockchainNetworkGateway blockchainGateway;
    private final OffChainStorage offChainStorage;
    private final OffChainStorageAdapter offChainAdapter;
    private final SmartContract smartContract;
    private final ShipmentLifecycleController lifecycleController;
    private final ShipmentComplianceController complianceController;

    public LoginFrame() {
        // ---------- Backend wiring (single instance for whole app) ----------
        blockchainNetwork = new BlockchainNetwork();
        blockchainGateway = new BlockchainNetworkGateway(blockchainNetwork);

        offChainStorage = new OffChainStorage();
        offChainAdapter = new OffChainStorageAdapter(offChainStorage);

        smartContract = new SmartContract();
        lifecycleController = new ShipmentLifecycleController(
                blockchainGateway,
                offChainAdapter,
                smartContract);
        complianceController = new ShipmentComplianceController(
                blockchainNetwork,
                blockchainGateway,
                offChainAdapter,
                smartContract);

        seedDemoUsers();
        demoUsersStaticRef = demoUsers;

        // ---------- Look & feel ----------
        setTitle("Blockchain Shipment Tracking – Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // a bit wider / taller so everything fits nicely
        setSize(780, 460);
        setLocationRelativeTo(null);
        setResizable(false);

        Color bg = new Color(5, 10, 25);
        Color card = new Color(12, 18, 40);
        Color accent = new Color(59, 130, 246); // blockchain blue
        Color text = new Color(230, 235, 255);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(bg);
        add(root);

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(card);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(640, 320));

        JLabel title = new JLabel("Blockchain Console Login");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // use HTML so the subtitle wraps instead of cutting off
        JLabel subtitle = new JLabel(
                "<html>Enter your username and password to access the ledger.</html>");
        subtitle.setForeground(new Color(160, 170, 210));
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleWrap = new JPanel();
        titleWrap.setBackground(card);
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleWrap.add(title);
        titleWrap.add(Box.createVerticalStrut(6));
        titleWrap.add(subtitle);

        // ---------- Form ----------
        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        styleTextField(usernameField, card, text);
        styleTextField(passwordField, card, text);

        JPanel userRow = createFormRow("Username", usernameField, text);
        JPanel passRow = createFormRow("Password", passwordField, text);

        JButton loginButton = createPrimaryButton("Log in", accent);
        JButton exitButton = createSecondaryButton("Exit");

        loginButton.addActionListener(e -> attemptLogin());
        exitButton.addActionListener(e -> {
            // Close entire application
            dispose();
            System.exit(0);
        });

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.add(exitButton);
        buttonRow.add(loginButton);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(252, 165, 165));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardPanel.add(titleWrap);
        cardPanel.add(Box.createVerticalStrut(24));
        cardPanel.add(userRow);
        cardPanel.add(Box.createVerticalStrut(14));
        cardPanel.add(passRow);
        cardPanel.add(Box.createVerticalStrut(24));
        cardPanel.add(buttonRow);
        cardPanel.add(Box.createVerticalStrut(14));
        cardPanel.add(statusLabel);

        root.add(cardPanel);

        setVisible(true);
    }

    private JPanel createFormRow(String labelText, JComponent field, Color textColor) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setForeground(textColor);
        // fixed width so the fields line up nicely
        label.setPreferredSize(new Dimension(90, 26));

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        row.add(label);
        row.add(Box.createHorizontalStrut(16));
        row.add(field);

        return row;
    }

    private void styleTextField(JTextField field, Color bg, Color fg) {
        field.setBackground(new Color(8, 14, 32));
        field.setForeground(fg);
        field.setCaretColor(fg);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 64, 175)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }

    private JButton createPrimaryButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(accent);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accent.brighter(), 1, true), // rounded rectangle
                new InsetsEmptyBorder(8, 26, 8, 26)));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        Color outline = new Color(55, 65, 81);
        JButton btn = new JButton(text);
        btn.setForeground(new Color(209, 213, 219));
        btn.setBackground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(outline, 1, true), // rounded rectangle
                new InsetsEmptyBorder(8, 22, 8, 22)));
        return btn;
    }

    /**
     * Simple EmptyBorder subclass so we can create it inline without long code.
     */
    private static class InsetsEmptyBorder extends javax.swing.border.EmptyBorder {
        public InsetsEmptyBorder(int top, int left, int bottom, int right) {
            super(top, left, bottom, right);
        }
    }

    // ---------- Demo users ----------

    private void seedDemoUsers() {
        demoUsers.clear();

        Shipper shipper = new Shipper(
                1,
                "shipper",
                "shipper",
                "shipper@example.com",
                "Global Shipper Inc.", // companyName
                "123 Dock Street", // address
                "Primary Shipper" // shipperRole
        );
        demoUsers.add(shipper);

        // Buyer
        Buyer buyer = new Buyer();
        buyer.setUserID(2);
        buyer.setUsername("buyer");
        buyer.setPassword("buyer");
        buyer.setEmail("buyer@example.com");
        buyer.setRole("BUYER");
        buyer.setRetailID("2001");
        buyer.setBuyerRole("Retail Customer");
        demoUsers.add(buyer);

        // Logistics provider
        LogisticsProvider lp = new LogisticsProvider();
        lp.setUserID(3);
        lp.setUsername("logistics");
        lp.setPassword("logistics");
        lp.setEmail("logistics@example.com");
        lp.setRole("LOGISTICS_PROVIDER");
        lp.setVehicleID(501);
        lp.setRouteInfo("Default Route");
        demoUsers.add(lp);

        // Warehouse
        Warehouse wh = new Warehouse();
        wh.setUserID(4);
        wh.setUsername("warehouse");
        wh.setPassword("warehouse");
        wh.setEmail("warehouse@example.com");
        wh.setRole("WAREHOUSE");
        wh.setWarehouseID(10);
        wh.setCapacity(5000);
        wh.setAddress("Main Warehouse");
        demoUsers.add(wh);

        // Customs
        CustomsOfficer co = new CustomsOfficer();
        co.setUserID(5);
        co.setUsername("customs");
        co.setPassword("customs");
        co.setEmail("customs@example.com");
        co.setRole("CUSTOMS_OFFICER");
        co.setOfficerID(77);
        co.setAgencyName("Border Agency");
        demoUsers.add(co);

        // Auditor
        Auditor auditor = new Auditor();
        auditor.setUserID(6);
        auditor.setUsername("auditor");
        auditor.setPassword("auditor");
        auditor.setEmail("auditor@example.com");
        auditor.setRole("AUDITOR");
        auditor.setAuditID(900);
        auditor.setOrganization("Independent Audit Co.");
        demoUsers.add(auditor);

        // Administrator
        Administrator admin = new Administrator();
        admin.setUserID(7);
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setEmail("admin@example.com");
        admin.setRole("ADMIN");
        demoUsers.add(admin);
    }

    // ---------- Login logic ----------

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        User found = null;
        for (User u : demoUsers) {
            if (u.getUsername().equalsIgnoreCase(username)
                    && u.getPassword().equals(password)) {
                found = u;
                break;
            }
        }

        if (found == null) {
            statusLabel.setText("Invalid credentials. Try 'shipper/shipper', 'buyer/buyer', etc.");
            return;
        }

        // Success – open the main console UI and close login
        User loggedIn = found;
        SwingUtilities.invokeLater(() -> new MainUI(
                loggedIn,
                blockchainNetwork,
                blockchainGateway,
                offChainStorage,
                offChainAdapter,
                smartContract,
                lifecycleController,
                complianceController));
        dispose();
    }

    // A static reference so MainUI can access the same list
    private static List<User> demoUsersStaticRef;

    /** Returns the static user list */
    public static List<User> demoUsersStatic() {
        return demoUsersStaticRef;
    }

    /** Get a specific user by username */
    public static User getUserStatic(String username) {
        if (demoUsersStaticRef == null)
            return null;
        for (User u : demoUsersStaticRef) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    /** Delete a user from the static list */
    public static void deleteUserStatic(String username) {
        if (demoUsersStaticRef == null)
            return;
        demoUsersStaticRef.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
    }
}
