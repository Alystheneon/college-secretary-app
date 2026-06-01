package secretaryapp.components;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

// η βάση για όλα τα παράθυρα
public abstract class BaseForm extends JFrame {
	
	private static final long serialVersionUID = 1L;
	protected JPanel contentPane; // protected για να το βλέπουν τα subclasses
	
	public BaseForm(String title, String topMessage, String btnNameAboveExit) {
		mainPanel(title);
		topMessage(topMessage);
		aboveExitButton(btnNameAboveExit);
		exitButton();
		unFocus();
	}
	
	
	private void mainPanel(String title) {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\mihal\\Desktop\\eclipse\\CollegeSecretaryApp\\src\\Icons\\logo.png"));
		setTitle(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // κλείνει μόνο με το exit
		setSize(720, 460);
		setLocationRelativeTo(null); // το κεντράρει
		contentPane = new JPanel();
		contentPane.setBackground(new Color(245, 245, 245));
		contentPane.setBorder(null);
		setContentPane(contentPane);
		setResizable(false); // αλλιώς χαλάει όλο το layout
	}
	
	private void topMessage(String topMessage) {
		JLabel lblTopMessage = new JLabel(topMessage);
		lblTopMessage.setBounds(0, 0, 706, 45);
		lblTopMessage.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTopMessage.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.setLayout(null); // null layout, τα bounds θα τα βάλουμε manually
		contentPane.add(lblTopMessage);
	}
	
	// το κουμπί πάνω από το exit, αλλάζει ανάλογα το παράθυρο
	private void aboveExitButton(String btnNameAboveExit) {
		JButton btnAboveExit = new JButton(btnNameAboveExit);
		btnAboveExit.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		btnAboveExit.setBounds(76, 244, 554, 68);
		aboveExitButtonActionListener(btnAboveExit); // το ορίζει το κάθε subclass
		contentPane.add(btnAboveExit);
	}
	
	// abstract για να την κάνουν override τα subclasses και να βάλουν την δική τους λειτουργία στο κουμπί
	protected abstract void aboveExitButtonActionListener(JButton btn);
	
	// exit button. protected για να μπορεί να γίνει override
	protected void exitButton() {
		JButton btnExit = new JButton("Έξοδος");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		btnExit.setBounds(76, 333, 554, 68);
		contentPane.add(btnExit);
	}
	
	// για να μην έχει focus κάπου όταν ανοίγει το παράθυρο
	private void unFocus() {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        contentPane.setFocusable(true);
		        contentPane.requestFocusInWindow();
		    }
		});
	}
}