/**
 *
 */
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import cipher.publickey.RSA;
import util.DataUtil;
import util.FileUtil;

/**
 * @author Yopiyama
 *
 */
public class RegisterPanel extends JPanel {
	/*
	 *
	 */
	@SuppressWarnings("unused")
	private MainFrame mf;
	private String name;
	private JButton registerBtn;
	private JButton backBtn;
	private JLabel idText;
	private JTextField userId;
	private JLabel passText;
	private JPasswordField password;
	private JLabel confText;
	private JPasswordField passConf;
	private int xPos = 390;
	private int width = 120;
	private int height = 40;
	boolean processFlag;

	/**
	 *
	 */
	private static final long serialVersionUID = -8590749289574833259L;

	public RegisterPanel(MainFrame mf, String name) {
		this.mf = mf;
		this.name = name;

		setLayout(null);

		idText = new JLabel("ID");
		userId = new JTextField("");
		passText = new JLabel("Password");
		password = new JPasswordField("");
		confText = new JLabel("Password Confirm");
		passConf = new JPasswordField("");

		registerBtn = new JButton("Register");
		registerBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				String user = userId.getText();
				String pass1 = String.valueOf(password.getPassword());
				String pass2 = String.valueOf(passConf.getPassword());
				if (inputChk(user, pass1, pass2) == false || existChk(user) == true) {
					password.setText("");
					passConf.setText("");
					return;
				} else {
					mf.dialog.openDialog("Initialize", "Processing...");
					initialSetting(user, pass1);
					userId.setText("");
					password.setText("");
					passConf.setText("");
					mf.dialog.closeDialog();
					mf.setIdPassToPanels(user, pass1);
					mf.setPanel(mf.menuPanel);
				}
			}
		});
		backBtn = new JButton("Go to " + mf.panelNames[0]);
		backBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				userId.setText("");
				password.setText("");
				passConf.setText("");
				mf.setPanel(mf.mainPanel);
			}
		});

		idText.setBounds(xPos, 60, width, height);
		userId.setBounds(xPos, 100, width, height);
		passText.setBounds(xPos, 140, width, height);
		password.setBounds(xPos, 180, width, height);
		confText.setBounds(xPos, 220, width, height);
		passConf.setBounds(xPos, 260, width, height);
		registerBtn.setBounds(xPos, 380, width, height);
		backBtn.setBounds(xPos, 500, width, height);

		this.add(idText);
		this.add(userId);
		this.add(passText);
		this.add(password);
		this.add(confText);
		this.add(passConf);
		this.add(registerBtn);
		this.add(backBtn);
	}

	protected void initialSetting(String id, String pass) {
		processFlag = false;
		new Thread(new Runnable() {
			@Override
			public void run(){
				String hashText = DataUtil.idPassToHash(id, pass);
				FileUtil.writeFile("./.data/users", new String[] {id + ", " + hashText}, true);

				RSA rsa = new RSA(RSA.BIT1024);
				rsa.generateKey();
				String[] keys = rsa.getKeys();
				FileUtil.writeFile("./.data/pass/" + id, keys, true);
				processFlag = true;
			}
		}).start();

		int count = 0;
		while(!processFlag) {
			mf.dialog.setText("Processing" + new String(new char[count % 5]).replace("\0", "."));
		}
		JOptionPane.showMessageDialog(this, "Making key is complete", "Complete", JOptionPane.INFORMATION_MESSAGE);

	}

	protected boolean existChk(String id) {
		String[] data = FileUtil.readFile("./.data/users");
		String[] idList = new String[data.length];
		for(int i = 0; i < data.length; i++) {
			String[] tmp = data[i].split(", ");
			idList[i] = tmp[0];
		}
		boolean idExist = Arrays.asList(idList).contains(id);
		if(idExist) {
			JOptionPane.showMessageDialog(this, "ID : " + id + " is already exists.\nPlease enter other ID.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return idExist;
	}

	protected boolean inputChk(String id, String pass, String confirm) {
		boolean flag = true;
		if("".equals(id)) {
			JOptionPane.showMessageDialog(this, "ID is required", "Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}
		if("".equals(pass)) {
			JOptionPane.showMessageDialog(this, "Password is required", "Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}
		if("".equals(confirm)) {
			JOptionPane.showMessageDialog(this, "Password Confirm is required", "Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}

		if(!pass.equals(confirm)) {
			JOptionPane.showMessageDialog(this, "Password and Confirm don't match", "Error", JOptionPane.ERROR_MESSAGE);
			flag = false;
		}
		return flag;
	}

	public String getPanelName() {
		return this.name;
	}
}
