package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.*;

import controller.AuthController;
import database.UserDAO;
import model.Settings;
import model.User;
import utils.SessionManager;

public class LoginFrame extends JFrame {

	private JPanel mainPanel, cardPanel, leftPanel, rightPanel;
	private JTextField txtEmail;
	private JPasswordField txtPassword;
	private JButton btnLogin;
	private JButton btnRegister;
	private JButton btnForgotPassword;
	private JCheckBox chkStayLoggedIn;

	public LoginFrame() {
		setTitle("Personal Finance System - Login");
		setSize(1100, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		// No auto‑login here – main() already handled it.
		// If we reach this point, the user must log in manually.

		mainPanel = new JPanel(null);
		mainPanel.setBackground(new Color(238, 217, 191));
		setContentPane(mainPanel);

		cardPanel = new JPanel(null);
		cardPanel.setBounds(160, 80, 780, 500);
		cardPanel.setBackground(Color.WHITE);
		cardPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 2));
		mainPanel.add(cardPanel);

		leftPanel = new JPanel(null);
		leftPanel.setBounds(0, 0, 460, 500);
		leftPanel.setBackground(new Color(240, 220, 194));
		cardPanel.add(leftPanel);

		rightPanel = new JPanel(null);
		rightPanel.setBounds(460, 0, 320, 500);
		rightPanel.setBackground(new Color(245, 245, 245));
		cardPanel.add(rightPanel);

		addLeftPanelComponents();
		addRightPanelComponents();
		addActions();
	}

	// ---------- New static helper used by main ----------
	public static User tryAutoLogin() {
		int savedUserId = Settings.getSavedUserId();
		if (savedUserId <= 0) return null;
		UserDAO userDAO = new UserDAO();
		User user = userDAO.getUserById(savedUserId);
		if (user != null) {
			return user;
		} else {
			Settings.clearSavedUserId();
			return null;
		}
	}

	// ---------- UI building methods (unchanged) ----------
	private void addLeftPanelComponents() {
		JLabel lblImage = new JLabel();
		lblImage.setBounds(35, 28, 390, 307);
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);

		ImageIcon icon = new ImageIcon("src/resources/my_image.png");
		Image scaledImage = icon.getImage().getScaledInstance(390, 300, Image.SCALE_SMOOTH);
		lblImage.setIcon(new ImageIcon(scaledImage));
		leftPanel.add(lblImage);

		JLabel lblCircle1 = new JLabel("●");
		lblCircle1.setFont(new Font("Dialog", Font.BOLD, 70));
		lblCircle1.setForeground(new Color(180, 70, 75));
		lblCircle1.setBounds(10, -10, 90, 90);
		leftPanel.add(lblCircle1);

		JLabel lblCircle2 = new JLabel("●");
		lblCircle2.setFont(new Font("Dialog", Font.BOLD, 25));
		lblCircle2.setForeground(new Color(160, 60, 110));
		lblCircle2.setBounds(355, 80, 40, 40);
		leftPanel.add(lblCircle2);

		JLabel lblCircle3 = new JLabel("●");
		lblCircle3.setFont(new Font("Dialog", Font.BOLD, 18));
		lblCircle3.setForeground(new Color(210, 130, 150));
		lblCircle3.setBounds(360, 295, 30, 30);
		leftPanel.add(lblCircle3);

		JLabel lblTitle = new JLabel("Welcome Back!");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(new Color(112, 23, 79));
		lblTitle.setBounds(35, 395, 380, 35);
		leftPanel.add(lblTitle);

		JLabel lblSubtitle = new JLabel("Access your financial dashboard and manage money");
		lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblSubtitle.setForeground(new Color(112, 23, 79));
		lblSubtitle.setBounds(45, 432, 360, 22);
		leftPanel.add(lblSubtitle);
	}

	private void addRightPanelComponents() {
		JLabel lblLogo = new JLabel("✣");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
		lblLogo.setForeground(new Color(134, 33, 95));
		lblLogo.setBounds(135, 35, 50, 40);
		rightPanel.add(lblLogo);

		JLabel lblHeading = new JLabel("Welcome");
		lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblHeading.setForeground(new Color(70, 70, 70));
		lblHeading.setBounds(52, 90, 230, 35);
		rightPanel.add(lblHeading);

		JLabel lblDesc = new JLabel("Login to your finance system");
		lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblDesc.setForeground(new Color(130, 130, 130));
		lblDesc.setBounds(52, 125, 220, 20);
		rightPanel.add(lblDesc);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblEmail.setForeground(new Color(120, 120, 120));
		lblEmail.setBounds(42, 180, 80, 20);
		rightPanel.add(lblEmail);

		txtEmail = new JTextField();
		txtEmail.setBounds(42, 203, 235, 32);
		txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtEmail.setBackground(Color.WHITE);
		txtEmail.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtEmail);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblPassword.setForeground(new Color(120, 120, 120));
		lblPassword.setBounds(42, 250, 80, 20);
		rightPanel.add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setBounds(42, 273, 235, 32);
		txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtPassword.setBackground(Color.WHITE);
		txtPassword.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtPassword);

		chkStayLoggedIn = new JCheckBox("Stay logged in");
		chkStayLoggedIn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		chkStayLoggedIn.setForeground(new Color(120, 120, 120));
		chkStayLoggedIn.setBackground(new Color(245, 245, 245));
		chkStayLoggedIn.setBounds(42, 310, 120, 20);
		rightPanel.add(chkStayLoggedIn);

		btnLogin = new JButton("Login");
		btnLogin.setBounds(42, 345, 235, 34);
		btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnLogin.setBackground(new Color(134, 33, 95));
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setFocusPainted(false);
		btnLogin.setBorderPainted(false);
		rightPanel.add(btnLogin);

		JLabel lblBottom = new JLabel("Not registered?");
		lblBottom.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblBottom.setForeground(new Color(120, 120, 120));
		lblBottom.setBounds(52, 400, 130, 20);
		rightPanel.add(lblBottom);

		btnRegister = new JButton("Create Account");
		btnRegister.setBounds(178, 398, 120, 24);
		btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnRegister.setForeground(new Color(134, 33, 95));
		btnRegister.setBackground(new Color(245, 245, 245));
		btnRegister.setBorderPainted(false);
		btnRegister.setFocusPainted(false);
		rightPanel.add(btnRegister);

		btnForgotPassword = new JButton("Forgot Password?");
		btnForgotPassword.setBounds(85, 435, 130, 24);
		btnForgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnForgotPassword.setForeground(new Color(134, 33, 95));
		btnForgotPassword.setBackground(new Color(245, 245, 245));
		btnForgotPassword.setBorderPainted(false);
		btnForgotPassword.setFocusPainted(false);
		rightPanel.add(btnForgotPassword);
	}

	private void addActions() {
		btnLogin.addActionListener(e -> login());
		btnRegister.addActionListener(e -> {
			dispose();
			new RegistrationFrame().setVisible(true);
		});
		btnForgotPassword.addActionListener(e -> {
			dispose();
			new ForgotPasswordFrame().setVisible(true);
		});
	}

	private void login() {
		String email = txtEmail.getText().trim();
		String password = new String(txtPassword.getPassword()).trim();

		if (email.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter email and password.");
			return;
		}

		try {
			AuthController authController = new AuthController();
			User user = authController.login(email, password);
			if (user != null) {
				if (chkStayLoggedIn.isSelected()) {
					Settings.setSavedUserId(user.getId());
				} else {
					Settings.clearSavedUserId();
				}
				SessionManager.setCurrentUser(user);
				dispose();
				new MainFrame(user).setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "Invalid email or password.");
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage());
		}
	}

	// ---------- Entry point ----------
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// 1. Attempt auto‑login
			User autoUser = LoginFrame.tryAutoLogin();
			if (autoUser != null) {
				SessionManager.setCurrentUser(autoUser);
				new MainFrame(autoUser).setVisible(true);
				return;                       // ✅ LoginFrame is never created
			}

			// 2. Normal login required
			new LoginFrame().setVisible(true);
		});
	}
}