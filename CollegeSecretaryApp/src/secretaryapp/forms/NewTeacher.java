package secretaryapp.forms;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import secretaryapp.components.BaseForm;
import secretaryapp.database.DBConnection;

// εισαγωγη νεου καθηγητη
public class NewTeacher extends BaseForm {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField txtfieldFirstName;
	private JTextField txtfieldLastName;
	private JTextField txtfieldAvailability;
	
	public NewTeacher(String title, String topMessage, String btnNameAboveExit) {
		super(title, topMessage, btnNameAboveExit);
		teacherFields();
	}
	
	private void teacherFields() {
		// Όνομα
		JLabel lblName = new JLabel("Όνομα");
		lblName.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(76, 60, 340, 35);
		contentPane.add(lblName);
		
		txtfieldFirstName = new JTextField();
		txtfieldFirstName.setFont(new Font("Segoe UI", Font.BOLD, 18));
		txtfieldFirstName.setHorizontalAlignment(SwingConstants.CENTER);
		txtfieldFirstName.setBounds(430, 60, 200, 35);
		contentPane.add(txtfieldFirstName);
		
		// Επώνυμο
		JLabel lblSurname = new JLabel("Επώνυμο");
		lblSurname.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblSurname.setHorizontalAlignment(SwingConstants.CENTER);
		lblSurname.setBounds(76, 105, 340, 35);
		contentPane.add(lblSurname);
		
		txtfieldLastName = new JTextField();
		txtfieldLastName.setFont(new Font("Segoe UI", Font.BOLD, 18));
		txtfieldLastName.setHorizontalAlignment(SwingConstants.CENTER);
		txtfieldLastName.setBounds(430, 105, 200, 35);
		contentPane.add(txtfieldLastName);
		
		// Διαθεσιμότητα
		JLabel lblAvailability = new JLabel("Διαθεσιμότητα Ωρών ανα εβδομάδα");
		lblAvailability.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblAvailability.setHorizontalAlignment(SwingConstants.CENTER);
		lblAvailability.setBounds(76, 150, 340, 35);
		contentPane.add(lblAvailability);
		
		txtfieldAvailability = new JTextField();
		txtfieldAvailability.setFont(new Font("Segoe UI", Font.BOLD, 18));
		txtfieldAvailability.setHorizontalAlignment(SwingConstants.CENTER);
		txtfieldAvailability.setBounds(430, 150, 200, 35);
		contentPane.add(txtfieldAvailability);
	}
	
	private void saveTeacherAndContinue() {
		String firstName = txtfieldFirstName.getText().trim();
		String lastName = txtfieldLastName.getText().trim();
		String availabilityStr = txtfieldAvailability.getText().trim();
		
		// ελεγχος για κενα πεδια
		if (firstName.isEmpty() || lastName.isEmpty() || availabilityStr.isEmpty()) {
			JOptionPane.showMessageDialog(contentPane,
				"Συμπληρώστε όλα τα πεδία",
				"Ελλιπή στοιχεία", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// δεν επιτρεπονται αριθμοι σε ονομα/επωνυμο
		if (hasDigit(firstName) || hasDigit(lastName)) {
			JOptionPane.showMessageDialog(contentPane,
				"Το όνομα και το επώνυμο δεν πρέπει να περιέχουν αριθμούς",
				"Λάθος μορφή", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// μετατροπη σε int, αλλιως βγάζει NumberFormatException
		int availability;
		try {
			availability = Integer.parseInt(availabilityStr);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(contentPane,
				"Η διαθεσιμότητα πρέπει να είναι αριθμός",
				"Λάθος μορφή", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String sqlInsert = "INSERT INTO Teachers (first_name, last_name, availability) VALUES (?, ?, ?)";
		try {
			// RETURN_GENERATED_KEYS για να επιστραφει το id του νεου καθηγητη
			PreparedStatement pst = DBConnection.conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, firstName);
			pst.setString(2, lastName);
			pst.setInt(3, availability);
			pst.executeUpdate();
			
			int newTeacherId = -1;
			ResultSet keys = pst.getGeneratedKeys();
			if (keys.next()) {
				newTeacherId = keys.getInt(1);
			}
			keys.close();
			pst.close();
			
			// δεν θα επρεπε να συμβει αλλα το κάνουμε το κάνουμε για ασφάλεια.
			if (newTeacherId == -1) {
				JOptionPane.showMessageDialog(contentPane,
					"Δεν παρθηκε νεο id, κατι πηγε στραβα",
					"Σφάλμα", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// μεταβαση στο Courses για αναθεση μαθηματων
			Courses courses = new Courses(
					"College Secretary App - Διδασκόμενα Μαθήματα",
					"Διδασκόμενα Μαθήματα: " + lastName + " " + firstName,
					"Αποθήκευση",
					newTeacherId,
					Courses.FROM_NEW_TEACHER,
					null);
				courses.setVisible(true);
			dispose();
			
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(contentPane,
				"Σφάλμα κατά την εισαγωγή:\n" + ex.getMessage(),
				"Σφάλμα", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	// ελεγχει αν το string περιεχει αριθμο
	private boolean hasDigit(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void aboveExitButtonActionListener(JButton btn) {
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveTeacherAndContinue();
			}
		});
	}
	
	// 2 κουμπια αντι 1
	@Override
	protected void exitButton() {
		JButton btnExit = new JButton("Έξοδος");
		btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		btnExit.setBounds(76, 333, 270, 68);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		contentPane.add(btnExit);
		
		JButton btnReturn = new JButton("Επιστροφή");
		btnReturn.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		btnReturn.setBounds(360, 333, 270, 68);
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Home home = new Home("College Secretary App - Home", "Αρχική Σελίδα", "Έκδοση Εφαρμογής");
				home.setVisible(true);
				dispose();
			}
		});
		contentPane.add(btnReturn);
	}
}