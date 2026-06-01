package secretaryapp.forms;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import secretaryapp.components.BaseForm;
import secretaryapp.database.DBConnection;

// Αρχική Σελίδα - η κεντρική φόρμα
public class Home extends BaseForm {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField txtfieldSearchSurnameArea; // το επώνυμο για αναζήτηση
	
	public Home(String title, String topMessage, String btnNameAboveExit) {
		super(title, topMessage, btnNameAboveExit); // BaseForm setup
		searchTeacherArea(); //αναζήτηση καθηγητή
		addNewTeacherButton(); //προσθήκη νέου καθηγητή
		showPersonnelButton(); //εμφάνιση όλου του προσωπικού
		dbConnectionListener(); // εδώ γίνεται η σύνδεση στη βάση
	}
	
	// search panel
	private void searchTeacherArea() {
		JPanel panelTeachersearch = new JPanel();
		panelTeachersearch.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelTeachersearch.setBounds(76, 66, 554, 68);
		contentPane.add(panelTeachersearch);
		panelTeachersearch.setLayout(null); // null layout
		
		JLabel lblTeacherSearch = new JLabel("Αναζήτηση Καθηγητή");
		lblTeacherSearch.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		lblTeacherSearch.setHorizontalAlignment(SwingConstants.CENTER);
		lblTeacherSearch.setBounds(6, 20, 195, 27);
		panelTeachersearch.add(lblTeacherSearch);
		
		txtfieldSearchSurnameArea = new JTextField();
		txtfieldSearchSurnameArea.setFont(new Font("Segoe UI", Font.BOLD, 20));
		txtfieldSearchSurnameArea.setHorizontalAlignment(SwingConstants.CENTER);
		txtfieldSearchSurnameArea.setBounds(211, 17, 186, 33);
		txtfieldSearchSurnameArea.setColumns(10);
		panelTeachersearch.add(txtfieldSearchSurnameArea);
		
		JButton btnSearch = new JButton("Αναζήτηση");
		btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		btnSearch.setBounds(403, 16, 141, 35);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = txtfieldSearchSurnameArea.getText().trim();
				
				// αν δεν γράψουμε τίποτα, βγαίνει popup
				if (query.isEmpty()) {
					JOptionPane.showMessageDialog(contentPane,
						"Παρακαλώ εισάγετε επώνυμο για την αναζήτηση.",
						"Κενό πεδίο",
						JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				Results results = new Results(
					"College Secretary App - Αποτελέσματα",
					"Αποτελέσματα Αναζήτησης",
					"Διδασκόμενα Μαθήματα",
					query);
				results.setVisible(true);
				dispose();
			}
		});
		panelTeachersearch.add(btnSearch);
	}
	
	// εισαγωγή νέου καθηγητή
	private void addNewTeacherButton() {
		JButton btnNewteacherinsert = new JButton("Εισαγωγή Νέου Καθηγητή");
		btnNewteacherinsert.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		btnNewteacherinsert.setBounds(76, 155, 270, 68);
		btnNewteacherinsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewTeacher newTeacher = new NewTeacher("College Secretary App - Νέος Καθηγητής", "Εισαγωγή Νέου Καθηγητή", "Συνέχεια");
				newTeacher.setVisible(true);
				dispose(); // κλείνει το home
			}
		});
		contentPane.add(btnNewteacherinsert);
	}
	
	// εμφάνιση όλου του προσωπικού
	private void showPersonnelButton() {
		JButton btnPersonnel = new JButton("Εμφάνιση Προσωπικού");
		btnPersonnel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		btnPersonnel.setBounds(360, 155, 270, 68);
		btnPersonnel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Personnel personnel = new Personnel(
					"College Secretary App - Προσωπικό",
					"Όλο το Διδακτικό Προσωπικό",
					"Επιστροφή");
				personnel.setVisible(true);
				dispose();
			}
		});
		contentPane.add(btnPersonnel);
	}
	
	// db σύνδεση μόλις ανοίξει η φόρμα
	private void dbConnectionListener() {
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				DBConnection.connect();
			}
		});
	}
	
	// πατώντας το aboveExit πάει στο Version
	@Override
	protected void aboveExitButtonActionListener(JButton btn) {
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Version version = new Version("College Secretary App - App Version", "Έκδοση Εφαρμογής", "Επιστροφή");
				version.setVisible(true);
				dispose();
			}
		});
	}
}