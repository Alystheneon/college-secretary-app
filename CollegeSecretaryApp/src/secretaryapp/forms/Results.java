package secretaryapp.forms;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import secretaryapp.components.BaseForm;
import secretaryapp.database.DBConnection;

// φόρμα που δείχνει τα αποτελέσματα αναζήτησης καθηγητή
public class Results extends BaseForm {
	
	private static final long serialVersionUID = 1L;
	
	// μικρή εσωτερική κλάση - σαν κουτάκι για τα δεδομένα κάθε καθηγητή
	private static class Teacher {
	    int id;
	    String firstName;
	    String lastName;
	    int availability;
	}

	private String searchQuery;                                 // τι έγραψε ο user στην αναζήτηση
	private ArrayList<Teacher> teachers = new ArrayList<>();    // η λίστα με όσους βρήκα στη βάση
	private int currentIndex = 0;                               // δείκτης - σε ποιον καθηγητή είμαι τώρα

	// τα fields της φόρμας
	private JTextField txtfieldName;
	private JTextField txtfieldSurname;
	private JTextField txtfieldAvailability;
	private JTextArea txtaCourses;
	
	public Results(String title, String topMessage, String btnNameAboveExit, String searchQuery) {
		super(title, topMessage, btnNameAboveExit);   // BaseForm setup
		this.searchQuery = searchQuery;               // κρατάω τι ψάχνει ο user για να το χρησιμοποιήσω μετά
		teacherFields();                              // όνομα/επώνυμο/διαθεσιμότητα
		navigationButtons();                          // πλοήγηση < << >> >
		actionButtons();                              // ενημέρωση/διαγραφή
		coursesPanel();                               // textarea με τα μαθήματα
		loadDataListener();                           // όταν ανοίξει η φόρμα -> φέρε δεδομένα
	}
	 
	// φτιάχνει τα 3 textfields όπου θα φαίνονται τα στοιχεία του καθηγητή
	private void teacherFields() {
		
		// Όνομα
		JLabel lblName = new JLabel("Όνομα");
		lblName.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(76, 55, 90, 30);
		contentPane.add(lblName);
		
		txtfieldName = new JTextField();
		txtfieldName.setFont(new Font("Segoe UI", Font.BOLD, 18));
		txtfieldName.setHorizontalAlignment(SwingConstants.CENTER);
		txtfieldName.setBounds(170, 55, 160, 30);
		contentPane.add(txtfieldName);
		
		// Επώνυμο
		JLabel lblSurname = new JLabel("Επώνυμο");
		lblSurname.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblSurname.setHorizontalAlignment(SwingConstants.CENTER);
		lblSurname.setBounds(350, 55, 110, 30);
		contentPane.add(lblSurname);
		
		txtfieldSurname = new JTextField();
		txtfieldSurname.setFont(new Font("Segoe UI", Font.BOLD, 18));
		txtfieldSurname.setHorizontalAlignment(SwingConstants.CENTER);
		txtfieldSurname.setBounds(470, 55, 160, 30);
		contentPane.add(txtfieldSurname);
		
		// Διαθεσιμότητα - σε νέα γραμμή
		JLabel lblAvailability = new JLabel("Διαθεσιμότητα Ωρών ανα εβδομάδα");
		lblAvailability.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblAvailability.setHorizontalAlignment(SwingConstants.CENTER);
		lblAvailability.setBounds(76, 90, 340, 25);
		contentPane.add(lblAvailability);
		
		txtfieldAvailability = new JTextField();
		txtfieldAvailability.setFont(new Font("Segoe UI", Font.BOLD, 16));
		txtfieldAvailability.setHorizontalAlignment(SwingConstants.CENTER);
		txtfieldAvailability.setBounds(430, 90, 200, 25);
		contentPane.add(txtfieldAvailability);
	}
	
	// τα 4 κουμπιά πλοήγησης (First /Previous / Next / Last)
	private void navigationButtons() {
		
		// πάει στην πρώτη καταχώρηση της λίστας
		JButton btnFirst = new JButton(new ImageIcon(Results.class.getResource("/Icons/first.png")));
		btnFirst.setBounds(143, 120, 55, 50);
		btnFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (teachers.isEmpty()) return; // αν δεν έχει αποτελέσματα, επιστρέφει τίποτα
				currentIndex = 0;
				displayCurrent();
			}
		});
		contentPane.add(btnFirst);
		
		// προηγούμενος - μειώνει το index κατά 1
		JButton btnPrevious = new JButton(new ImageIcon(Results.class.getResource("/Icons/previous.png")));
		btnPrevious.setBounds(265, 120, 55, 50);
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (teachers.isEmpty()) return;
				if (currentIndex > 0) {    // αν είναι ήδη στον πρώτο, δεν πάει πιο πίσω (αποφυγή indexoutofbounds)
					currentIndex--;
					displayCurrent();
				}
			}
		});
		contentPane.add(btnPrevious);
		
		// επόμενος - αυξάνει το index κατά 1
		JButton btnNext = new JButton(new ImageIcon(Results.class.getResource("/Icons/next.png")));
		btnNext.setBounds(387, 120, 55, 50);
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (teachers.isEmpty()) return;
				if (currentIndex < teachers.size() - 1) {   // αν είναι στον τελευταίο, δεν πάει πιο μπροστά (αποφυγή indexoutofbounds)
					currentIndex++;
					displayCurrent();
				}
			}
		});
		contentPane.add(btnNext);
		
		// πάει στον τελευταίο
		JButton btnLast = new JButton(new ImageIcon(Results.class.getResource("/Icons/last.png")));
		btnLast.setBounds(509, 120, 55, 50);
		btnLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (teachers.isEmpty()) return;
				currentIndex = teachers.size() - 1;
				displayCurrent();
			}
		});
		contentPane.add(btnLast);
	}
	
	// τα 2 κουμπιά για ενημέρωση και διαγραφή του τρέχοντος καθηγητή
	private void actionButtons() {
		JButton btnUpdate = new JButton("Ενημέρωση");
		btnUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnUpdate.setBounds(76, 175, 260, 25);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTeacher();
			}
		});
		contentPane.add(btnUpdate);
		
		JButton btnDelete = new JButton("Διαγραφή");
		btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnDelete.setBounds(370, 175, 260, 25);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteTeacher();
			}
		});
		contentPane.add(btnDelete);
	}
	
	// το textarea όπου θα φαίνονται τα μαθήματα του καθηγητή (read-only)
	private void coursesPanel() {
		txtaCourses = new JTextArea();
		txtaCourses.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtaCourses.setEditable(false);     // read-only, ο user δεν γράφει εδώ
		txtaCourses.setLineWrap(true);      // αυτόματη αλλαγή γραμμής
		txtaCourses.setWrapStyleWord(true); // η αλλαγή γίνεται σε ολόκληρες λέξεις, δεν σπάει η λέξη
		txtaCourses.setBackground(new Color(245, 245, 245));
		
		JScrollPane scroll = new JScrollPane(txtaCourses);
		scroll.setBounds(76, 205, 554, 30);
		scroll.setBorder(new LineBorder(new Color(180, 180, 180)));
		contentPane.add(scroll);
	}
	
	// τρέχει το SELECT και γεμίζει τη λίστα teachers με τα αποτελέσματα
	private void loadData() {
		teachers.clear();      // καθαρίζω παλιά δεδομένα αν υπάρχουν
		currentIndex = 0;      // ξεκινάω από τον πρώτο
		
		try {
			// SELECT με LIKE και ? για να αποφύγω SQL injection
			PreparedStatement pst = DBConnection.conn.prepareStatement(
				"SELECT teacher_id, first_name, last_name, availability FROM Teachers " +
				"WHERE last_name LIKE ? ORDER BY last_name, first_name");
			pst.setString(1, searchQuery + "%");   // π.χ. "Παπ" -> "Παπ%" (όσοι αρχίζουν με Παπ)
			ResultSet rs = pst.executeQuery();
			
			// για κάθε γραμμή που γύρισε το query, φτιάχνω ένα teacher object και το βάζω στη λίστα
			while (rs.next()) {
				Teacher t = new Teacher();
				t.id = rs.getInt("teacher_id");
				t.firstName = rs.getString("first_name");
				t.lastName = rs.getString("last_name");
				t.availability = rs.getInt("availability");
				teachers.add(t);
			}
			
			rs.close();
			pst.close();
			
			// αν δεν βρήκε κανέναν, popup και επιστροφή στο Home
			if (teachers.isEmpty()) {
				JOptionPane.showMessageDialog(contentPane,
					"Δεν βρέθηκαν καθηγητές με αυτό το επώνυμο",
					"Καμία εύρεση", JOptionPane.INFORMATION_MESSAGE);
				Home home = new Home("College Secretary App - Home", "Αρχική Σελίδα", "Έκδοση Εφαρμογής");
				home.setVisible(true);
				dispose();
				return;
			}
			
			displayCurrent();    // δείχνει τον πρώτο καθηγητή
			
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(contentPane,
				"Σφάλμα κατά τη φόρτωση:\n" + ex.getMessage(),
				"Σφάλμα", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	// φέρνει τα μαθήματα που διδάσκει ο τρέχων καθηγητής και τα δείχνει στο textarea
	private void loadCoursesForCurrent() {
		txtaCourses.setText("");        // καθαρίζω το παλιό κείμενο
		if (teachers.isEmpty()) return;
		
		Teacher t = teachers.get(currentIndex);
		
		try {
			PreparedStatement pst = DBConnection.conn.prepareStatement(
				"SELECT title FROM Courses WHERE teacher_id = ? ORDER BY semester, title");
			pst.setInt(1, t.id);
			ResultSet rs = pst.executeQuery();
			
			// χτίζω string τύπου "Διδάσκει: μάθημα1, μάθημα2, μάθημα3"
			StringBuilder sb = new StringBuilder();
			sb.append("Διδάσκει: ");
			boolean first = true;   // flag για να ξέρω αν θα βάλω κόμμα πριν τη λέξη
			while (rs.next()) {
				if (!first) sb.append(", ");
				sb.append(rs.getString("title"));
				first = false;
			}
			if (first) {            // αν δεν μπήκε ποτέ σε while, σημαίνει 0 μαθήματα
				sb.append("(κανένα μάθημα)");
			}
			
			rs.close();
			pst.close();
			
			txtaCourses.setText(sb.toString());
		} catch (SQLException ex) {
			txtaCourses.setText("Σφάλμα φόρτωσης μαθημάτων");
			ex.printStackTrace();
		}
	}
	
	// γεμίζει τα 3 textfields με τα στοιχεία του καθηγητή στη θέση currentindex
	private void displayCurrent() {
		Teacher t = teachers.get(currentIndex);
		txtfieldName.setText(t.firstName);
		txtfieldSurname.setText(t.lastName);
		txtfieldAvailability.setText(String.valueOf(t.availability));   // int -> String
		loadCoursesForCurrent();   // φέρνει και τα μαθήματα του
	}
	
	// validation + UPDATE του τρέχοντος καθηγητή με τα δεδομένα από τα textfields
	private void updateTeacher() {
		if (teachers.isEmpty()) return;
		
		// διαβάζω τι έγραψε ο user
		String firstName = txtfieldName.getText().trim();
		String lastName = txtfieldSurname.getText().trim();
		String availabilityStr = txtfieldAvailability.getText().trim();
		
		// τσεκάρω για κενά πεδία
		if (firstName.isEmpty() || lastName.isEmpty() || availabilityStr.isEmpty()) {
			JOptionPane.showMessageDialog(contentPane,
				"Συμπληρώστε όλα τα πεδία",
				"Ελλιπή στοιχεία", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// τσεκάρω για αριθμούς μέσα σε όνομα/επώνυμο
		if (hasDigit(firstName) || hasDigit(lastName)) {
			JOptionPane.showMessageDialog(contentPane,
				"Το όνομα και το επώνυμο δεν πρέπει να περιέχουν αριθμούς",
				"Λάθος μορφή", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// η διαθεσιμότητα πρέπει να μετατραπεί σε int. Αν δεν είναι αριθμός, σκάει το parseInt
		int newAvailability;
		try {
			newAvailability = Integer.parseInt(availabilityStr);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(contentPane,
				"Η διαθεσιμότητα πρέπει να είναι αριθμός",
				"Λάθος μορφή", JOptionPane.WARNING_MESSAGE);
			return;
		}
		// δεν επιτρέπω αρνητική διαθεσιμότητα
		if (newAvailability < 0) {
			JOptionPane.showMessageDialog(contentPane,
				"Η διαθεσιμότητα δεν μπορεί να είναι αρνητική",
				"Λάθος μορφή", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		Teacher t = teachers.get(currentIndex);
		
		try {
			// πριν κάνω UPDATE, τσεκάρω: η νέα διαθεσιμότητα φτάνει για όσα μαθήματα έχει ήδη;
			// COALESCE επιστρέφει 0 αν το SUM είναι NULL (δηλαδή δεν έχει κανένα μάθημα)
			PreparedStatement pstCheck = DBConnection.conn.prepareStatement(
				"SELECT COALESCE(SUM(hours), 0) FROM Courses WHERE teacher_id = ?");
			pstCheck.setInt(1, t.id);
			ResultSet rsCheck = pstCheck.executeQuery();
			int assignedHours = 0;
			if (rsCheck.next()) {
				assignedHours = rsCheck.getInt(1);
			}
			rsCheck.close();
			pstCheck.close();
			
			// αν οι νέες ώρες δεν φτάνουν, popup
			if (newAvailability < assignedHours) {
				JOptionPane.showMessageDialog(contentPane,
					"Η νέα διαθεσιμότητα (" + newAvailability + " ώρες) είναι μικρότερη απο τις τρέχουσες ώρες μαθημάτων του καθηγητή (" + assignedHours + " ώρες).\n" +
					"Αφαιρέστε μαθήματα απο τη φόρμα 'Διδασκόμενα Μαθήματα' πρώτα.",
					"Υπέρβαση Διαθεσιμότητας",
					JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			// όλα ok, κάνω το UPDATE στη βάση
			PreparedStatement pst = DBConnection.conn.prepareStatement(
				"UPDATE Teachers SET first_name = ?, last_name = ?, availability = ? WHERE teacher_id = ?");
			pst.setString(1, firstName);
			pst.setString(2, lastName);
			pst.setInt(3, newAvailability);
			pst.setInt(4, t.id);
			pst.executeUpdate();
			pst.close();
			
			// ενημερώνω και το τοπικό object για να μη χρειάζεται να ξανατραβήξω από τη βάση
			t.firstName = firstName;
			t.lastName = lastName;
			t.availability = newAvailability;
			
			// τσεκάρω αν το νέο επώνυμο ταιριάζει ακόμα στο search query
			// case-insensitive για να ταιριάζει με τη συμπεριφορά του SQL LIKE
			boolean stillMatches = lastName.toLowerCase().startsWith(searchQuery.toLowerCase());
			
			if (stillMatches) {
				JOptionPane.showMessageDialog(contentPane,
					"Η ενημέρωση έγινε επιτυχώς",
					"Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
			} else {
				// το νέο επώνυμο δεν matchάρει πλέον -> τον βγάζω από τα αποτελέσματα
				JOptionPane.showMessageDialog(contentPane,
					"Η ενημέρωση έγινε επιτυχώς.\nΟ καθηγητής δεν συμπεριλαμβάνεται πλέον στα αποτελέσματα αναζήτησης.",
					"Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
				
				teachers.remove(currentIndex);
				
				// αν έμεινε άδεια η λίστα, πάμε πίσω στο Home
				if (teachers.isEmpty()) {
					Home home = new Home("College Secretary App - Home", "Αρχική Σελίδα", "Έκδοση Εφαρμογής");
					home.setVisible(true);
					dispose();
					return;
				}
				
				// προσοχή: αν η διαγραφή ήταν στο τέλος της λίστας, ο currentIndex δείχνει πλέον σε ανύπαρκτη θέση
				if (currentIndex >= teachers.size()) {
					currentIndex = teachers.size() - 1;
				}
				
				displayCurrent();
			}
			
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(contentPane,
				"Σφάλμα κατά την ενημέρωση:\n" + ex.getMessage(),
				"Σφάλμα", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	// διαγραφή καθηγητή με transaction για να μη χαθούν τα μαθήματα του στη μέση
	private void deleteTeacher() {
		if (teachers.isEmpty()) return;
		
		Teacher t = teachers.get(currentIndex);
		
		// confirmation prompt πριν τη διαγραφή - safety check
		int choice = JOptionPane.showConfirmDialog(contentPane,
			"Είστε σίγουροι ότι θέλετε να διαγράψετε τον καθηγητή " + t.lastName + " " + t.firstName + ";\n" +
			"Τα μαθήματα που διδάσκει θα αποδεσμευτούν (δεν θα διαγραφούν).",
			"Επιβεβαίωση Διαγραφής",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);
		
		if (choice != JOptionPane.YES_OPTION) return;   // αν πάτησε ΟΧΙ, ακύρωση
		
		try {
			// ξεκινάει transaction - τα queries δεν γίνονται μόνιμα μέχρι να καλέσω commit
			DBConnection.conn.setAutoCommit(false);
			
			// πρώτο query: ξεκολλάω τα μαθήματα του καθηγητή (γίνονται αδιάθετα)
			PreparedStatement pstUnassign = DBConnection.conn.prepareStatement(
				"UPDATE Courses SET teacher_id = NULL WHERE teacher_id = ?");
			pstUnassign.setInt(1, t.id);
			pstUnassign.executeUpdate();
			pstUnassign.close();
			
			// δεύτερο query: διαγραφή του καθηγητή
			PreparedStatement pstDelete = DBConnection.conn.prepareStatement(
				"DELETE FROM Teachers WHERE teacher_id = ?");
			pstDelete.setInt(1, t.id);
			pstDelete.executeUpdate();
			pstDelete.close();
			
			// πέρασαν και τα δύο queries -> commit (γίνονται μόνιμα)
			DBConnection.conn.commit();
			
		} catch (SQLException ex) {
			// κάποιο query απέτυχε -> rollback (γυρνάω τη βάση όπως ήταν πριν αρχίσω)
			try {
				DBConnection.conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
			JOptionPane.showMessageDialog(contentPane,
				"Σφάλμα κατά τη διαγραφή:\n" + ex.getMessage(),
				"Σφάλμα", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return;   // βγαίνω χωρίς να ενημερώσω το UI αφού η διαγραφή απέτυχε
			
		} finally {
			// πάντα επαναφορά του autoCommit σε true ώστε τα επόμενα queries να γίνονται αυτόματα
			try {
				DBConnection.conn.setAutoCommit(true);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		// φτάσαμε εδώ -> το commit πέρασε, ενημερώνω και το UI
		JOptionPane.showMessageDialog(contentPane,
			"Η διαγραφή έγινε επιτυχώς",
			"Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
		
		teachers.remove(currentIndex);   // αφαιρώ και από τη λίστα στη μνήμη
		
		// αν δεν έμεινε κανείς, επιστροφή στο home
		if (teachers.isEmpty()) {
			Home home = new Home("College Secretary App - Home", "Αρχική Σελίδα", "Έκδοση Εφαρμογής");
			home.setVisible(true);
			dispose();
			return;
		}
		
		// προσοχή στον currentIndex μετά το remove - μπορεί να δείχνει εκτός λίστας
		if (currentIndex >= teachers.size()) {
			currentIndex = teachers.size() - 1;
		}
		
		displayCurrent();   // δείχνω τον επόμενο
	}
	
	// helper: τσεκάρει αν ένα string περιέχει έστω και έναν αριθμό
	private boolean hasDigit(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	// listener που φορτώνει τα δεδομένα μόλις ανοίξει η φόρμα
	private void loadDataListener() {
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				loadData();
			}
		});
	}
	
	// το κουμπί "Διδασκόμενα Μαθήματα" - ανοίγει το Courses για τον τρέχοντα καθηγητή
	@Override
	protected void aboveExitButtonActionListener(JButton btn) {
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (teachers.isEmpty()) {
					JOptionPane.showMessageDialog(contentPane,
						"Δεν υπάρχει επιλεγμένος καθηγητής",
						"Σφάλμα", JOptionPane.WARNING_MESSAGE);
					return;
				}
				Teacher t = teachers.get(currentIndex);
				// δίνω στο Courses το id του καθηγητή + από πού ήρθα + το search query
				// για να μπορεί να γυρίσει πίσω εδώ με τα ίδια αποτελέσματα
				Courses courses = new Courses(
						"College Secretary App - Διδασκόμενα Μαθήματα",
						"Διδασκόμενα Μαθήματα: " + t.lastName + " " + t.firstName,
						"Αποθήκευση",
						t.id,
						Courses.FROM_RESULTS,
						searchQuery);
					courses.setVisible(true);	
				dispose();
			}
		});
	}
	
	// override του exit - σπάει το ένα κουμπί σε δύο: Έξοδος | Επιστροφή
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