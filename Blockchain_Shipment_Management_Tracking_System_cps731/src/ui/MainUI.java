package ui;

import controller.ShipmentComplianceController;
import controller.ShipmentLifecycleController;
import external.BlockchainNetwork;
import external.OffChainStorage;
import external.PaymentService;
import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import gateway.PaymentServiceAdapter;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main blockchain console GUI.
 */
public class MainUI extends JFrame {

    private final ShipmentLifecycleController lifecycleController;
    private final ShipmentComplianceController complianceController;
    private final User currentUser;

    private JTextArea activityArea;

    // central cards
    private JPanel contentCards;
    private static final String CARD_CREATE = "create";
    private static final String CARD_UPLOAD = "upload";
    private static final String CARD_UPDATE = "update";
    private static final String CARD_QUERY = "query";

    // colors
    private final Color bg = new Color(5, 8, 22);
    private final Color sidebarBg = new Color(7, 14, 32);
    private final Color cardBg = new Color(11, 18, 36);
    private final Color cardInnerBg = new Color(13, 22, 44);
    private final Color accent = new Color(40, 120, 255);
    private final Color accentHover = new Color(70, 150, 255);
    private final Color textPrimary = new Color(240, 244, 255);
    private final Color textSecondary = new Color(160, 170, 190);
    private final Font fontBody = new Font("Inter", Font.PLAIN, 13);
    private final Font fontBold = new Font("Inter", Font.BOLD, 13);

    public MainUI(User user) {
        this.currentUser = user;

        // wire components
        BlockchainNetwork blockchainNetwork = new BlockchainNetwork();
        OffChainStorage offChainStorage = new OffChainStorage();
        PaymentService paymentService = new PaymentService();

        BlockchainNetworkGateway networkGateway = new BlockchainNetworkGateway(blockchainNetwork);
        OffChainStorageAdapter storageAdapter = new OffChainStorageAdapter(offChainStorage);
        PaymentServiceAdapter paymentAdapter = new PaymentServiceAdapter(paymentService);

        this.lifecycleController = new ShipmentLifecycleController(
                networkGateway, storageAdapter, paymentAdapter);
        this.complianceController = new ShipmentComplianceController(networkGateway);

        initUI();
    }

    private void initUI() {
        setTitle("Blockchain Shipment Tracking - " + currentUser.getUsername()
                + " (" + currentUser.getRole() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bg);

        // sidebar, content, activity log
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);
        add(buildActivityLog(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ---------- sidebar ----------

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(270, 0));
        sidebar.setBackground(sidebarBg);
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(new EmptyBorder(20, 16, 16, 16));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Blockchain Console");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        title.setForeground(textPrimary);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loggedIn = new JLabel(
                "<html><span style='font-size:11px;color:#A0AABE;'>Logged in as:<br>"
                        + currentUser.getUsername() + " (" + currentUser.getRole() + ")</span></html>");
        loggedIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loggedIn.setBorder(new EmptyBorder(10, 0, 10, 0));

        top.add(title);
        top.add(Box.createVerticalStrut(8));
        top.add(loggedIn);
        top.add(Box.createVerticalStrut(16));

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        JButton createBtn = createNavButton("Create Shipment");
        JButton uploadBtn = createNavButton("Upload Document");
        JButton updateBtn = createNavButton("Update Status");
        JButton queryBtn = createNavButton("Query / Audit");
        JButton logoutBtn = createSecondaryNavButton("Logout");

        createBtn.addActionListener(e -> switchCard(CARD_CREATE));
        uploadBtn.addActionListener(e -> switchCard(CARD_UPLOAD));
        updateBtn.addActionListener(e -> switchCard(CARD_UPDATE));
        queryBtn.addActionListener(e -> switchCard(CARD_QUERY));
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(LoginFrame::new);
        });

        menu.add(wrapInRoundedMenuPanel(createBtn));
        menu.add(Box.createVerticalStrut(10));
        menu.add(wrapInRoundedMenuPanel(uploadBtn));
        menu.add(Box.createVerticalStrut(10));
        menu.add(wrapInRoundedMenuPanel(updateBtn));
        menu.add(Box.createVerticalStrut(10));
        menu.add(wrapInRoundedMenuPanel(queryBtn));
        menu.add(Box.createVerticalGlue());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.add(wrapInRoundedMenuPanel(logoutBtn));

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(menu, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel wrapInRoundedMenuPanel(JButton button) {
        JPanel rounded = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 24, 48)); // higher contrast than sidebar
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        rounded.setOpaque(false);
        rounded.setLayout(new BorderLayout());
        rounded.setBorder(new EmptyBorder(6, 10, 6, 10));
        rounded.add(button, BorderLayout.CENTER);
        return rounded;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(16, 24, 48));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFont(fontBold);
        return btn;
    }

    private JButton createSecondaryNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(new Color(200, 90, 90));
        btn.setBackground(new Color(16, 24, 48));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFont(fontBody);
        return btn;
    }

    // ---------- content area (tabs in the middle) ----------

    private JPanel buildContentArea() {
        contentCards = new JPanel(new CardLayout());
        contentCards.setOpaque(false);
        contentCards.setBorder(new EmptyBorder(30, 40, 10, 40));

        contentCards.add(buildCreateShipmentCard(), CARD_CREATE);
        contentCards.add(buildUploadCard(), CARD_UPLOAD);
        contentCards.add(buildUpdateCard(), CARD_UPDATE);
        contentCards.add(buildQueryCard(), CARD_QUERY);

        // default card
        switchCard(CARD_CREATE);

        return contentCards;
    }

    private void switchCard(String card) {
        CardLayout cl = (CardLayout) contentCards.getLayout();
        cl.show(contentCards, card);
    }

    private JPanel createRoundedCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        return card;
    }

    private JTextField styledTextField() {
        JTextField field = new JTextField();
        field.setBackground(cardInnerBg);
        field.setForeground(textPrimary);
        field.setCaretColor(textPrimary);
        field.setFont(fontBody);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(28, 40, 72)),
                new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private JTextArea styledTextArea(int rows) {
        JTextArea area = new JTextArea(rows, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(cardInnerBg);
        area.setForeground(textPrimary);
        area.setCaretColor(textPrimary);
        area.setFont(fontBody);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(28, 40, 72)),
                new EmptyBorder(8, 10, 8, 10)));
        return area;
    }

    private JButton primaryActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(accent);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(fontBold);
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
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

    // ---- specific cards ----

    private JPanel buildCreateShipmentCard() {
        JPanel outer = createRoundedCardPanel();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel("Create Shipment");
        title.setForeground(textPrimary);
        title.setFont(new Font("Inter", Font.BOLD, 22));

        JTextField originField = styledTextField();
        JTextField destField = styledTextField();
        JTextField descField = styledTextField();

        JButton createBtn = primaryActionButton("Create Shipment");

        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 25, 0);
        form.add(title, c);

        c.gridwidth = 1;
        c.insets = new Insets(4, 0, 2, 0);
        c.gridy = 1;
        form.add(label("Origin"), c);
        c.gridy = 2;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(originField, c);

        c.gridy = 3;
        c.insets = new Insets(4, 0, 2, 0);
        form.add(label("Destination"), c);
        c.gridy = 4;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(destField, c);

        c.gridy = 5;
        c.insets = new Insets(4, 0, 2, 0);
        form.add(label("Description"), c);
        c.gridy = 6;
        c.insets = new Insets(0, 0, 18, 0);
        form.add(descField, c);

        c.gridy = 7;
        c.insets = new Insets(10, 0, 0, 0);
        form.add(createBtn, c);

        createBtn.addActionListener(e -> {
            try {
                String id = lifecycleController.createShipment(
                        currentUser,
                        originField.getText(),
                        destField.getText(),
                        descField.getText());
                log("> Created shipment with ID: " + id);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildUploadCard() {
        JPanel outer = createRoundedCardPanel();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel("Upload Document");
        title.setForeground(textPrimary);
        title.setFont(new Font("Inter", Font.BOLD, 22));

        JTextField shipmentIdField = styledTextField();
        JTextField nameField = styledTextField();
        JTextArea contentArea = styledTextArea(6);

        JButton uploadBtn = primaryActionButton("Upload Document");

        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 25, 0);
        form.add(title, c);

        c.gridwidth = 1;
        c.insets = new Insets(4, 0, 2, 0);
        c.gridy = 1;
        form.add(label("Shipment ID"), c);
        c.gridy = 2;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(shipmentIdField, c);

        c.gridy = 3;
        c.insets = new Insets(4, 0, 2, 0);
        form.add(label("Document Name"), c);
        c.gridy = 4;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(nameField, c);

        c.gridy = 5;
        c.insets = new Insets(4, 0, 2, 0);
        form.add(label("Content"), c);
        c.gridy = 6;
        c.insets = new Insets(0, 0, 18, 0);
        form.add(new JScrollPane(contentArea), c);

        c.gridy = 7;
        c.insets = new Insets(10, 0, 0, 0);
        form.add(uploadBtn, c);

        uploadBtn.addActionListener(e -> {
            try {
                String docId = lifecycleController.uploadDocument(
                        currentUser,
                        shipmentIdField.getText(),
                        nameField.getText(),
                        contentArea.getText());
                log("> Uploaded document with ID: " + docId);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildUpdateCard() {
        JPanel outer = createRoundedCardPanel();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel("Update Shipment Status");
        title.setForeground(textPrimary);
        title.setFont(new Font("Inter", Font.BOLD, 22));

        JTextField shipmentIdField = styledTextField();
        JComboBox<ShipmentStatus> statusCombo = new JComboBox<>(ShipmentStatus.values());
        statusCombo.setBackground(cardInnerBg);
        statusCombo.setForeground(textPrimary);
        statusCombo.setFont(fontBody);
        statusCombo.setBorder(new EmptyBorder(4, 6, 4, 6));

        JTextField descField = styledTextField();

        JButton updateBtn = primaryActionButton("Update Status");

        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 25, 0);
        form.add(title, c);

        c.gridwidth = 1;
        c.insets = new Insets(4, 0, 2, 0);
        c.gridy = 1;
        form.add(label("Shipment ID"), c);
        c.gridy = 2;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(shipmentIdField, c);

        c.gridy = 3;
        c.insets = new Insets(4, 0, 2, 0);
        form.add(label("New Status"), c);
        c.gridy = 4;
        c.insets = new Insets(0, 0, 10, 0);
        form.add(statusCombo, c);

        c.gridy = 5;
        c.insets = new Insets(4, 0, 2, 0);
        form.add(label("Description"), c);
        c.gridy = 6;
        c.insets = new Insets(0, 0, 18, 0);
        form.add(descField, c);

        c.gridy = 7;
        c.insets = new Insets(10, 0, 0, 0);
        form.add(updateBtn, c);

        updateBtn.addActionListener(e -> {
            try {
                lifecycleController.updateStatus(
                        currentUser,
                        shipmentIdField.getText(),
                        (ShipmentStatus) statusCombo.getSelectedItem(),
                        descField.getText());
                log("> Updated status for shipment: " + shipmentIdField.getText());
            } catch (Exception ex) {
                showError(ex);
            }
        });

        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildQueryCard() {
        JPanel outer = createRoundedCardPanel();

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel("Query Shipment / Audit");
        title.setForeground(textPrimary);
        title.setFont(new Font("Inter", Font.BOLD, 22));

        JTextField shipmentIdField = styledTextField();
        JButton queryBtn = primaryActionButton("Query Shipment");
        JButton auditBtn = primaryActionButton("Generate Audit Trail");

        // ensure both buttons look the same weight
        queryBtn.setFont(fontBold);
        auditBtn.setFont(fontBold);

        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(queryBtn);
        buttonRow.add(auditBtn);

        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 25, 0);
        form.add(title, c);

        c.gridwidth = 1;
        c.gridy = 1;
        c.insets = new Insets(4, 0, 2, 0);
        form.add(label("Shipment ID"), c);

        c.gridy = 2;
        c.insets = new Insets(0, 0, 18, 0);
        form.add(shipmentIdField, c);

        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        form.add(buttonRow, c);

        queryBtn.addActionListener(e -> {
            try {
                String result = lifecycleController.queryStatus(
                        currentUser,
                        shipmentIdField.getText());
                log("> Query Shipment\n" + result);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        auditBtn.addActionListener(e -> {
            try {
                Report report = complianceController.generateAuditTrail(
                        shipmentIdField.getText());
                log("> Audit Trail\n" + report.toString());
            } catch (Exception ex) {
                showError(ex);
            }
        });

        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(textSecondary);
        lbl.setFont(fontBody);
        return lbl;
    }

    // ---------- activity log (terminal vibe) ----------

    private JPanel buildActivityLog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(4, 7, 18));
        panel.setBorder(new EmptyBorder(4, 10, 8, 10));

        JLabel label = new JLabel("Activity Log");
        label.setForeground(new Color(130, 180, 255));
        label.setFont(new Font("Inter", Font.PLAIN, 12));

        activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setBackground(new Color(3, 5, 12));
        activityArea.setForeground(new Color(144, 238, 144)); // terminal green
        activityArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        activityArea.setBorder(new EmptyBorder(6, 8, 6, 8));
        activityArea.setLineWrap(true);
        activityArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(activityArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(18, 28, 54)));

        panel.add(label, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(0, 130));

        log("> Blockchain console ready.");
        return panel;
    }

    private void log(String msg) {
        activityArea.append(msg + "\n");
        activityArea.setCaretPosition(activityArea.getDocument().getLength());
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        log("! ERROR: " + ex.getMessage());
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
