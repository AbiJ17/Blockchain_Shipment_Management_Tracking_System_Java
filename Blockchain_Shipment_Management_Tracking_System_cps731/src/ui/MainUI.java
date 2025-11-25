package ui;

import controller.ShipmentComplianceController;
import controller.ShipmentLifecycleController;
import external.BlockchainNetwork;
import external.OffChainStorage;
import gateway.BlockchainNetworkGateway;
import gateway.OffChainStorageAdapter;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MainUI
 * - Single console for all roles.
 * - Left nav with rounded “block” buttons.
 * - Center card area for each action.
 * - Bottom activity log with terminal look.
 */
public class MainUI extends JFrame {

    private final User currentUser;

    // Shared backend objects (passed from LoginFrame)
    private final BlockchainNetwork blockchainNetwork;
    private final BlockchainNetworkGateway blockchainGateway;
    private final OffChainStorage offChainStorage;
    private final OffChainStorageAdapter offChainAdapter;
    private final SmartContract smartContract;
    private final ShipmentLifecycleController lifecycleController;
    private final ShipmentComplianceController complianceController;

    // Swing components
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JTextArea activityLogArea = new JTextArea();

    // Create Shipment fields
    private JTextField csOriginField;
    private JTextField csDestinationField;
    private JTextField csDescriptionField;

    // Track Shipment fields
    private JTextField trackShipmentIdField;

    // Upload Document fields
    private JTextField udShipmentIdField;
    private JTextField udDocNameField;
    private JTextArea udContentArea;

    // Update Status fields
    private JTextField usShipmentIdField;
    private JTextField usNewStatusField;

    // Query / Audit fields
    private JTextField qaShipmentIdField;
    private JTextArea qaResultArea;

    // Confirm Delivery Fields
    private JTextField cdShipmentIdField;

    // Raise Dispute Fields
    private JTextField rdShipmentIdField;
    private JTextArea rdDescriptionArea;

    // Verify Document Fields
    private JTextField vdShipmentIdField;
    private JTextField vdDocNameField;

    private final DateTimeFormatter logTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    public MainUI(
            User user,
            BlockchainNetwork blockchainNetwork,
            BlockchainNetworkGateway blockchainGateway,
            OffChainStorage offChainStorage,
            OffChainStorageAdapter offChainAdapter,
            SmartContract smartContract,
            ShipmentLifecycleController lifecycleController,
            ShipmentComplianceController complianceController) {

        this.currentUser = user;
        this.blockchainNetwork = blockchainNetwork;
        this.blockchainGateway = blockchainGateway;
        this.offChainStorage = offChainStorage;
        this.offChainAdapter = offChainAdapter;
        this.smartContract = smartContract;
        this.lifecycleController = lifecycleController;
        this.complianceController = complianceController;

        initFrame();
        buildLayout();
        setVisible(true);
    }

    // ---------- Frame & base layout ----------

    private void initFrame() {
        setTitle("Blockchain Shipment Tracking – " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 650));
    }

    private void buildLayout() {
        // “Blockchain” dark-neon palette
        Color bg = new Color(4, 9, 24);
        Color navBg = new Color(8, 14, 34);
        Color navButtonBg = new Color(15, 23, 42);
        Color cardBg = new Color(10, 20, 48);
        Color accent = new Color(56, 189, 248); // cyan
        Color accentBorder = new Color(37, 99, 235); // indigo border
        Color text = new Color(230, 235, 255);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);
        setContentPane(root);

        // ---------- Left navigation ----------
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(navBg);
        nav.setBorder(new EmptyBorder(20, 18, 20, 18));
        nav.setPreferredSize(new Dimension(260, 0));

        JLabel title = new JLabel("Blockchain Console");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel loggedIn = new JLabel("Logged in as: " + currentUser.getUsername()
                + " (" + currentUser.getRole() + ")");
        loggedIn.setForeground(new Color(148, 163, 184));
        loggedIn.setFont(loggedIn.getFont().deriveFont(12f));

        nav.add(title);
        nav.add(Box.createVerticalStrut(4));
        nav.add(loggedIn);
        nav.add(Box.createVerticalStrut(20));

        addRoleBasedMenu(nav, navButtonBg, accentBorder);

        JButton logout = new JButton("Log out");
        logout.setBackground(navButtonBg);
        logout.setForeground(text);
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentBorder, 1, true),
                new EmptyBorder(8, 18, 8, 18)));
        logout.addActionListener(e -> doLogout());
        nav.add(Box.createVerticalStrut(24));
        nav.add(logout);

        root.add(nav, BorderLayout.WEST);

        // ---------- Center cards ----------
        cardPanel.setBackground(bg);
        cardPanel.setBorder(new EmptyBorder(24, 24, 8, 24));

        cardPanel.add(buildCreateShipmentCard(cardBg, text, accent, accentBorder), "CREATE");
        cardPanel.add(buildTrackShipmentCard(cardBg, text, accent, accentBorder), "TRACK");
        cardPanel.add(buildUploadDocumentCard(cardBg, text, accent, accentBorder), "UPLOAD");
        cardPanel.add(buildUpdateStatusCard(cardBg, text, accent, accentBorder), "STATUS");
        cardPanel.add(buildQueryAuditCard(cardBg, text, accent, accentBorder), "QUERY");
        cardPanel.add(buildConfirmDeliveryCard(cardBg, text, accent, accentBorder), "CONFIRM_DELIVERY");
        cardPanel.add(buildRaiseDisputeCard(cardBg, text, accent, accentBorder), "DISPUTE");
        cardPanel.add(buildVerifyDocumentCard(cardBg, text, accent, accentBorder), "VERIFY_DOCUMENT");
        cardPanel.add(buildClearanceApprovalCard(cardBg, text, accent, accentBorder), "CLEARANCE");
        cardPanel.add(buildComplianceReportCard(cardBg, text, accent, accentBorder), "COMPLIANCE");
        cardPanel.add(buildFraudDetectionCard(cardBg, text, accent, accentBorder), "FRAUD");
        cardPanel.add(buildManageUsersCard(cardBg, text, accent, accentBorder), "MANAGE_USERS");
        cardPanel.add(buildAssignRolesCard(cardBg, text, accent, accentBorder), "ASSIGN_ROLES");

        root.add(cardPanel, BorderLayout.CENTER);

        // ---------- Activity log ----------
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new EmptyBorder(4, 18, 8, 18));
        logPanel.setBackground(new Color(3, 7, 18));

        JLabel logLabel = new JLabel("Activity Log");
        logLabel.setForeground(new Color(148, 163, 184));
        logLabel.setFont(logLabel.getFont().deriveFont(12f));

        activityLogArea.setEditable(false);
        activityLogArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        activityLogArea.setBackground(Color.BLACK);
        activityLogArea.setForeground(new Color(45, 212, 191)); // teal terminal text
        activityLogArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        activityLogArea.setLineWrap(true);
        activityLogArea.setWrapStyleWord(true);

        JScrollPane logScroll = new JScrollPane(activityLogArea);
        logScroll.setBorder(BorderFactory.createLineBorder(new Color(31, 41, 55)));

        logPanel.add(logLabel, BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);
        logPanel.setPreferredSize(new Dimension(0, 130));

        root.add(logPanel, BorderLayout.SOUTH);

        showInitialCardForRole(currentUser.getRole());
    }

    // decide which screen shows first based on role
    private void showInitialCardForRole(String role) {
        switch (role) {
            case "SHIPPER":
                showCard("CREATE");
                break;
            case "LOGISTICS_PROVIDER":
            case "WAREHOUSE":
                showCard("STATUS");
                break;
            case "BUYER":
                showCard("TRACK");
                break;
            case "CUSTOMS_OFFICER":
                showCard("CLEARANCE");
                break;
            case "AUDITOR":
                showCard("COMPLIANCE");
                break;
            case "ADMIN":
                showCard("MANAGE_USERS");
                break;
            default:
                showCard("CREATE");
                break;
        }
    }

    // Method to add main menu features/options based on the user role
    private void addRoleBasedMenu(JPanel nav, Color bg, Color border) {
        String role = currentUser.getRole();
        switch (role) {

            // ---------------- SHIPPER ----------------
            case "SHIPPER":
                nav.add(createNavButton("Create Shipment", bg, border, e -> showCard("CREATE")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Upload Document", bg, border, e -> showCard("UPLOAD")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Update Status", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("TRACK")));
                break;

            // ---------------- LOGISTICS_PROVIDER ----------------
            case "LOGISTICS_PROVIDER":
                nav.add(createNavButton("Update Status", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("TRACK")));
                break;

            // ---------------- WAREHOUSE ----------------
            case "WAREHOUSE":
                nav.add(createNavButton("Update Status", bg, border, e -> showCard("STATUS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("TRACK")));
                break;

            // ---------------- BUYER ----------------
            case "BUYER":
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("TRACK")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Confirm Delivery", bg, border, e -> showCard("CONFIRM_DELIVERY")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Raise Dispute", bg, border, e -> showCard("DISPUTE")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Verify Document", bg, border, e -> showCard("VERIFY_DOCUMENT")));
                break;

            // ---------------- CUSTOMS_OFFICER ----------------
            case "CUSTOMS_OFFICER":
                nav.add(createNavButton("Clearance Approval", bg, border, e -> showCard("CLEARANCE")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Review Documents", bg, border, e -> showCard("VERIFY_DOCUMENT")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Track Shipment", bg, border, e -> showCard("TRACK")));
                break;

            // ---------------- AUDITOR ----------------
            case "AUDITOR":
                nav.add(createNavButton("Compliance Report", bg, border, e -> showCard("COMPLIANCE")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Generate Audit Trail", bg, border, e -> showCard("QUERY")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Verify Documents", bg, border, e -> showCard("VERIFY_DOCUMENT")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Fraud Detection", bg, border, e -> showCard("FRAUD")));
                break;

            // ---------------- ADMIN ----------------
            case "ADMIN":
                nav.add(createNavButton("Manage Users", bg, border, e -> showCard("MANAGE_USERS")));
                nav.add(Box.createVerticalStrut(12));
                nav.add(createNavButton("Assign Roles", bg, border, e -> showCard("ASSIGN_ROLES")));
                break;

            default:
                nav.add(new JLabel("Unknown role: " + role));
                break;
        }
    }

    private JButton createNavButton(String text, Color bg, Color borderColor, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setForeground(new Color(226, 232, 255));
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(10, 18, 10, 18)));

        btn.addActionListener(listener);
        return btn;
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    private void log(String message) {
        String ts = LocalDateTime.now().format(logTimeFormat);
        activityLogArea.append("[" + ts + "] " + message + "\n");
        activityLogArea.setCaretPosition(activityLogArea.getDocument().getLength());
    }

    // ---------- Helper for label + field rows (left-aligned) ----------

    private JTextField createLabeledField(JPanel container, String label,
            Color textColor, Color borderColor) {

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel l = new JLabel(label);
        l.setForeground(textColor);
        l.setPreferredSize(new Dimension(140, 24));

        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        field.setBackground(new Color(8, 14, 32));
        field.setForeground(textColor);
        field.setCaretColor(textColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(4, 8, 4, 8)));

        row.add(l);
        row.add(Box.createHorizontalStrut(12));
        row.add(field);

        container.add(row);
        container.add(Box.createVerticalStrut(10));
        return field;
    }

    // ---------- Create Shipment card ----------

    private JComponent buildCreateShipmentCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 280));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Create Shipment");
        title.setForeground(text);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        csOriginField = createLabeledField(card, "Origin", text, borderColor);
        csDestinationField = createLabeledField(card, "Destination", text, borderColor);
        csDescriptionField = createLabeledField(card, "Description", text, borderColor);

        JButton createBtn = new JButton("Create Shipment");
        createBtn.setForeground(Color.WHITE);
        createBtn.setBackground(accent);
        createBtn.setFocusPainted(false);
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        createBtn.addActionListener(e -> handleCreateShipment());

        card.add(Box.createVerticalStrut(20));
        card.add(createBtn);

        outer.add(card);
        return outer;
    }

    private void handleCreateShipment() {
        // Only shippers can create shipments
        if (!(currentUser instanceof Shipper)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only SHIPPER role can create shipments.",
                    "Not allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String origin = csOriginField.getText().trim();
        String destination = csDestinationField.getText().trim();
        String description = csDescriptionField.getText().trim();

        if (origin.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Origin and Destination are required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipper shipper = (Shipper) currentUser;

        // Simple ID generator for the UI
        String shipmentId = "S" + System.currentTimeMillis();

        Shipment shipment = lifecycleController.createShipment(
                shipper,
                shipmentId,
                origin,
                destination,
                description);

        log("Shipment created: " + shipment.getShipmentId()
                + " by " + currentUser.getUsername());

        JOptionPane.showMessageDialog(
                this,
                "Shipment created with ID: " + shipment.getShipmentId(),
                "Created",
                JOptionPane.INFORMATION_MESSAGE);

        csOriginField.setText("");
        csDestinationField.setText("");
        csDescriptionField.setText("");
    }

    // ---------- Track Shipment card ----------

    // Class field (if you don't already have it)
    private JTextField tsShipmentIdField;

    // ---------- Track Shipment card ----------
    private JComponent buildTrackShipmentCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        // Title is centered
        JLabel title = new JLabel("Track Shipment");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(18));

        // Row for "Shipment ID" label + field, left-aligned
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT); // <- key: row anchored to the left

        JLabel idLabel = new JLabel("Shipment ID");
        idLabel.setForeground(text);

        tsShipmentIdField = new JTextField();
        tsShipmentIdField.setMaximumSize(new Dimension(260, 32));
        tsShipmentIdField.setBackground(new Color(8, 14, 32));
        tsShipmentIdField.setForeground(text);
        tsShipmentIdField.setCaretColor(text);
        tsShipmentIdField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(4, 8, 4, 8)));

        row.add(idLabel);
        row.add(Box.createHorizontalStrut(16));
        row.add(tsShipmentIdField);

        card.add(row);
        card.add(Box.createVerticalStrut(20));

        // Button centered horizontally
        JButton trackBtn = new JButton("Track Shipment");
        trackBtn.setForeground(Color.WHITE);
        trackBtn.setBackground(accent);
        trackBtn.setFocusPainted(false);
        trackBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        trackBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        trackBtn.setAlignmentX(Component.CENTER_ALIGNMENT); // <- centers button
        trackBtn.addActionListener(e -> handleTrackShipment()); // reuse your existing handler

        card.add(trackBtn);

        outer.add(card);
        return outer;
    }

    private void handleTrackShipment() {
        String shipmentId = trackShipmentIdField.getText().trim();
        if (shipmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID is required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this,
                    "Shipment not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = complianceController.queryShipmentStatus(shipment);
        log("Track shipment " + shipmentId + " → " + result);

        JOptionPane.showMessageDialog(this,
                result,
                "Shipment Status",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Upload Document card ----------

    private JComponent buildUploadDocumentCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 340));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Upload Document");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        udShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);
        udDocNameField = createLabeledField(card, "Document Name", text, borderColor);

        JLabel contentLabel = new JLabel("Content");
        contentLabel.setForeground(text);
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(contentLabel);
        card.add(Box.createVerticalStrut(4));

        udContentArea = new JTextArea(5, 40);
        udContentArea.setLineWrap(true);
        udContentArea.setWrapStyleWord(true);
        udContentArea.setBackground(new Color(8, 14, 32));
        udContentArea.setForeground(text);
        udContentArea.setCaretColor(text);
        udContentArea.setBorder(new LineBorder(borderColor, 1, true));

        JScrollPane contentScroll = new JScrollPane(udContentArea);
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentScroll.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        card.add(contentScroll);
        card.add(Box.createVerticalStrut(18));

        JButton uploadBtn = new JButton("Upload Document");
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setBackground(accent);
        uploadBtn.setFocusPainted(false);
        uploadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        uploadBtn.addActionListener(e -> handleUploadDocument());

        card.add(uploadBtn);
        outer.add(card);
        return outer;
    }

    private void handleUploadDocument() {
        String shipmentId = udShipmentIdField.getText().trim();
        String docName = udDocNameField.getText().trim();
        String content = udContentArea.getText();

        if (shipmentId.isEmpty() || docName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID and Document Name are required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this,
                    "Shipment not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Document doc = lifecycleController.uploadDocument(shipment, docName, content);
        offChainAdapter.connect();
        offChainAdapter.uploadFile(doc);

        log("Document '" + docName + "' uploaded for shipment " + shipmentId);
        JOptionPane.showMessageDialog(this,
                "Document uploaded and stored off-chain.",
                "Uploaded",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Update Status card ----------

    private JComponent buildUpdateStatusCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 280));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Update Shipment Status");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        usShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);
        usNewStatusField = createLabeledField(card,
                "New Status (e.g., IN_TRANSIT, DELIVERED)", text, borderColor);

        JButton updateBtn = new JButton("Update Status");
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setBackground(accent);
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        updateBtn.addActionListener(e -> handleUpdateStatus());

        card.add(Box.createVerticalStrut(12));
        card.add(updateBtn);

        outer.add(card);
        return outer;
    }

    private void handleUpdateStatus() {
        String shipmentId = usShipmentIdField.getText().trim();
        String newStatus = usNewStatusField.getText().trim();

        if (shipmentId.isEmpty() || newStatus.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID and new status are required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this,
                    "Shipment not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = lifecycleController.updateShipmentStatus(shipment, newStatus);
        log(result);
        JOptionPane.showMessageDialog(this, result, "Status updated",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- Query / Audit card (for auditors) ----------

    private JComponent buildQueryAuditCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(900, 380));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Query Shipment / Audit");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        qaShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton queryBtn = new JButton("Query Shipment");
        queryBtn.setForeground(text);
        queryBtn.setBackground(new Color(15, 23, 42));
        queryBtn.setFocusPainted(false);
        queryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        queryBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(8, 18, 8, 18)));

        JButton auditBtn = new JButton("Generate Audit Trail");
        auditBtn.setForeground(text);
        auditBtn.setBackground(new Color(15, 23, 42));
        auditBtn.setFocusPainted(false);
        auditBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        auditBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(8, 18, 8, 18)));

        buttonRow.add(queryBtn);
        buttonRow.add(auditBtn);

        qaResultArea = new JTextArea(10, 60);
        qaResultArea.setEditable(false);
        qaResultArea.setLineWrap(true);
        qaResultArea.setWrapStyleWord(true);
        qaResultArea.setBackground(new Color(8, 14, 32));
        qaResultArea.setForeground(text);
        qaResultArea.setBorder(new LineBorder(borderColor, 1, true));

        JScrollPane resultScroll = new JScrollPane(qaResultArea);
        resultScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultScroll.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));

        queryBtn.addActionListener(e -> handleQueryShipment());
        auditBtn.addActionListener(e -> handleGenerateAudit());

        card.add(buttonRow);
        card.add(Box.createVerticalStrut(12));
        card.add(resultScroll);

        outer.add(card);
        return outer;
    }

    private void handleQueryShipment() {
        String shipmentId = qaShipmentIdField.getText().trim();
        if (shipmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID is required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        String result = complianceController.queryShipmentStatus(shipment);
        qaResultArea.setText(result);
        log("Query shipment " + shipmentId + " → " + result);
    }

    private void handleGenerateAudit() {
        String shipmentId = qaShipmentIdField.getText().trim();
        if (shipmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Shipment ID is required.",
                    "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        Report report = complianceController.generateAuditTrail(shipment);
        qaResultArea.setText(report.toString());

        log("Generated audit trail for shipment " + shipmentId);
    }

    // ---------- Confirm Delivery card ----------

    private JComponent buildConfirmDeliveryCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Confirm Delivery");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        cdShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);

        JButton confirmBtn = new JButton("Confirm Delivery");
        confirmBtn.setForeground(text);
        confirmBtn.setBackground(new Color(15, 23, 42));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        confirmBtn.addActionListener(e -> handleConfirmDelivery());

        card.add(Box.createVerticalStrut(12));
        card.add(confirmBtn);

        outer.add(card);
        return outer;
    }

    private void handleConfirmDelivery() {

        // Permission check (only Buyer)
        if (!(currentUser instanceof Buyer)) {
            JOptionPane.showMessageDialog(this, "Only BUYER role can confirm deliveries.", "Not allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String shipmentId = cdShipmentIdField.getText().trim();

        if (shipmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shipment ID is required.", "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this, "Shipment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call controller
        String result = lifecycleController.confirmDelivery(shipment);

        log(result);
        JOptionPane.showMessageDialog(this, result);
        cdShipmentIdField.setText("");
    }

    // ---------- Raise Dispute Card ----------

    private JComponent buildRaiseDisputeCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 340));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Raise Dispute");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        rdShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);

        JLabel descLabel = new JLabel("Dispute Description");
        descLabel.setForeground(text);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descLabel);
        card.add(Box.createVerticalStrut(4));

        rdDescriptionArea = new JTextArea(5, 40);
        rdDescriptionArea.setLineWrap(true);
        rdDescriptionArea.setWrapStyleWord(true);
        rdDescriptionArea.setBackground(new Color(8, 14, 32));
        rdDescriptionArea.setForeground(text);
        rdDescriptionArea.setCaretColor(text);
        rdDescriptionArea.setBorder(new LineBorder(borderColor, 1, true));

        JScrollPane scroll = new JScrollPane(rdDescriptionArea);
        scroll.setBorder(new LineBorder(borderColor, 1, true));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(scroll);
        card.add(Box.createVerticalStrut(18));

        JButton raiseBtn = new JButton("Submit Dispute");
        raiseBtn.setForeground(Color.WHITE);
        raiseBtn.setBackground(accent);
        raiseBtn.setFocusPainted(false);
        raiseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        raiseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        raiseBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        raiseBtn.addActionListener(e -> handleRaiseDispute());

        card.add(raiseBtn);
        outer.add(card);
        return outer;
    }

    private void handleRaiseDispute() {
        if (!(currentUser instanceof Buyer)) {
            JOptionPane.showMessageDialog(this, "Only BUYERS may raise disputes.", "Not allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String shipmentId = rdShipmentIdField.getText().trim();
        String description = rdDescriptionArea.getText().trim();

        if (shipmentId.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shipment ID and description are required.", "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this, "Shipment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = complianceController.logDispute(shipment, description);

        log(result);
        JOptionPane.showMessageDialog(this, result);

        rdShipmentIdField.setText("");
        rdDescriptionArea.setText("");
    }

    // ---------- Verify Document Card ----------

    private JComponent buildVerifyDocumentCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 320));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Verify Document Integrity");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        vdShipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);
        vdDocNameField = createLabeledField(card, "Document Name", text, borderColor);

        JButton verifyBtn = new JButton("Verify Document");
        verifyBtn.setForeground(Color.WHITE);
        verifyBtn.setBackground(accent);
        verifyBtn.setFocusPainted(false);
        verifyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        verifyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        verifyBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        verifyBtn.addActionListener(e -> handleVerifyDocument());

        card.add(verifyBtn);
        outer.add(card);
        return outer;
    }

    private void handleVerifyDocument() {
        String shipmentId = vdShipmentIdField.getText().trim();
        String docName = vdDocNameField.getText().trim();

        if (shipmentId.isEmpty() || docName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shipment ID and Document Name are required.", "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = lifecycleController.findShipmentById(shipmentId);
        if (shipment == null) {
            JOptionPane.showMessageDialog(this, "Shipment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = complianceController.verifyDocument(shipment, docName);

        log(result);
    }

    // ---------- Clearance Approval card ----------

    private JComponent buildClearanceApprovalCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 300));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Customs Clearance Approval");
        title.setForeground(text);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JTextField shipmentIdField = createLabeledField(card, "Shipment ID", text, borderColor);

        JTextField decisionField = createLabeledField(
                card,
                "Decision (APPROVE / REJECT)",
                text,
                borderColor);

        JButton approveBtn = new JButton("Submit Clearance");
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setBackground(accent);
        approveBtn.setFocusPainted(false);
        approveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        approveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        approveBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));

        approveBtn.addActionListener(e -> {
            String shipmentId = shipmentIdField.getText().trim();
            String decision = decisionField.getText().trim().toUpperCase();

            if (shipmentId.isEmpty() || decision.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Shipment ID and decision are required.",
                        "Missing data",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Shipment shipment = lifecycleController.findShipmentById(shipmentId);
            if (shipment == null) {
                JOptionPane.showMessageDialog(this,
                        "Shipment not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Smart contract customs rule
            String result = complianceController.approveClearance(shipment, decision);

            log("Clearance decision: " + result);
            JOptionPane.showMessageDialog(this, result, "Clearance", JOptionPane.INFORMATION_MESSAGE);
        });

        card.add(Box.createVerticalStrut(20));
        card.add(approveBtn);

        outer.add(card);
        return outer;
    }

    // ---------- Compliance Report card ----------

    private JComponent buildComplianceReportCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 350));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Generate Compliance Report");
        title.setForeground(text);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JTextField filterField = createLabeledField(card, "Filter (optional)", text, borderColor);

        JButton generateBtn = new JButton("Generate Report");
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setBackground(accent);
        generateBtn.setFocusPainted(false);
        generateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        generateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));

        JTextArea reportArea = new JTextArea(8, 50);
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setBackground(new Color(8, 14, 32));
        reportArea.setForeground(text);
        reportArea.setBorder(new LineBorder(borderColor, 1, true));

        JScrollPane scroll = new JScrollPane(reportArea);
        scroll.setBorder(new LineBorder(borderColor, 1, true));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        generateBtn.addActionListener(e -> {
            String filter = filterField.getText().trim();

            Report report = complianceController.generateComplianceSummary(filter);
            reportArea.setText(report.toString());

            log("Compliance report generated");
        });

        card.add(generateBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(scroll);

        outer.add(card);
        return outer;
    }

    // ---------- Fraud Detection card ----------

    private JComponent buildFraudDetectionCard(Color cardBg, Color text, Color accent, Color borderColor) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 300));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Fraud Detection");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));

        JTextField fdShipmentField = createLabeledField(card, "Shipment ID", text, borderColor);

        JTextArea fdResultArea = new JTextArea(6, 50);
        fdResultArea.setEditable(false);
        fdResultArea.setLineWrap(true);
        fdResultArea.setWrapStyleWord(true);
        fdResultArea.setBackground(new Color(8, 14, 32));
        fdResultArea.setForeground(text);
        fdResultArea.setBorder(new LineBorder(borderColor, 1, true));

        JScrollPane resultScroll = new JScrollPane(fdResultArea);
        resultScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(resultScroll);
        card.add(Box.createVerticalStrut(18));

        JButton analyzeBtn = new JButton("Analyze Fraud Risk");
        analyzeBtn.setForeground(Color.WHITE);
        analyzeBtn.setBackground(accent);
        analyzeBtn.setFocusPainted(false);
        analyzeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        analyzeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        analyzeBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));

        analyzeBtn.addActionListener(e -> {
            String id = fdShipmentField.getText().trim();
            Shipment s = lifecycleController.findShipmentById(id);
            String res = handleFraudDetection(s);
            fdResultArea.setText(res);
            log("Fraud Detection → " + res);
        });

        card.add(analyzeBtn);

        outer.add(card);
        return outer;
    }

    private String handleFraudDetection(Shipment shipment) {
        if (shipment == null)
            return "Shipment not found.";

        boolean integrity = smartContract.verifyLedgerIntegrity(shipment);
        boolean missingDocs = shipment.getDocuments().isEmpty();

        if (!integrity)
            return "HIGH RISK: Ledger integrity violation detected.";

        if (missingDocs)
            return "MEDIUM RISK: Missing documentation.";

        return "No fraud indicators found.";
    }

    // ---------- Manage Users card ----------

    private JComponent buildManageUsersCard(Color cardBg, Color text, Color accent, Color borderColor) {

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(720, 350));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Manage Users");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(12));

        // Table model
        String[] columns = { "Username", "Role", "Email" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Populate table (assumes LoginFrame or MainUI has user list)
        for (User u : LoginFrame.demoUsersStatic()) { // Add a static getter in LoginFrame
            model.addRow(new Object[] { u.getUsername(), u.getRole(), u.getEmail() });
        }

        JTable table = new JTable(model);
        table.setBackground(new Color(8, 14, 32));
        table.setForeground(text);
        table.setGridColor(borderColor);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tableScroll);
        card.add(Box.createVerticalStrut(18));

        JButton deleteBtn = new JButton("Delete Selected User");
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(accent);
        deleteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a user first.");
                return;
            }

            String username = model.getValueAt(row, 0).toString();
            LoginFrame.deleteUserStatic(username); // add static method in LoginFrame
            model.removeRow(row);

            log("User deleted: " + username);
        });

        card.add(deleteBtn);

        outer.add(card);
        return outer;
    }

    // ---------- Assign Roles card ----------

    private JComponent buildAssignRolesCard(Color cardBg, Color text, Color accent, Color borderColor) {

        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel();
        card.setBackground(cardBg);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(620, 300));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 1, true),
                new EmptyBorder(28, 48, 28, 48)));

        JLabel title = new JLabel("Assign Roles");
        title.setForeground(text);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(18));

        JComboBox<String> userSelect = new JComboBox<>();
        for (User u : LoginFrame.demoUsersStatic()) {
            userSelect.addItem(u.getUsername());
        }
        userSelect.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(userSelect);
        card.add(Box.createVerticalStrut(12));

        JComboBox<String> roleSelect = new JComboBox<>(new String[] {
                "SHIPPER", "BUYER", "LOGISTICS_PROVIDER", "WAREHOUSE",
                "CUSTOMS_OFFICER", "AUDITOR", "ADMIN"
        });
        roleSelect.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(roleSelect);
        card.add(Box.createVerticalStrut(18));

        JButton assignBtn = new JButton("Assign Role");
        assignBtn.setForeground(Color.WHITE);
        assignBtn.setBackground(accent);
        assignBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        assignBtn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));

        assignBtn.addActionListener(e -> {
            String username = (String) userSelect.getSelectedItem();
            String newRole = (String) roleSelect.getSelectedItem();

            User u = LoginFrame.getUserStatic(username);
            if (u != null) {
                u.setRole(newRole);
                log("Updated role for " + username + " → " + newRole);
                JOptionPane.showMessageDialog(this, "Role updated.");
            }
        });

        card.add(assignBtn);

        outer.add(card);
        return outer;
    }

    // ---------- Logout ----------
    private void doLogout() {
        dispose();
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
