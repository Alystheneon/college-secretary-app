package secretaryapp.forms;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import secretaryapp.components.BaseForm;
import secretaryapp.database.DBConnection;

// εμφανιση ολου του διδακτικου προσωπικου με τα μαθηματα τους
public class Personnel extends BaseForm {
	
	private static final long serialVersionUID = 1L;
	
	private JTable tblPersonnel;
	private DefaultTableModel model;
	
	public Personnel(String title, String topMessage, String btnNameAboveExit) {
		super(title, topMessage, btnNameAboveExit);
		personnelTable();
		loadDataListener();
	}
	
	private void personnelTable() {
		String[] columns = {"Επώνυμο", "Όνομα", "Διαθεσιμότητα", "Διδασκόμενα Μαθήματα"};
		
		// model με 0 γραμμες, γεμιζει απο τη βαση στο windowOpened
		model = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;
			
			// τα κελια read-only, δεν αλλαζει δεδομενα απο εδω
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		tblPersonnel = new JTable(model);
		tblPersonnel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tblPersonnel.setRowHeight(26);
		tblPersonnel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		tblPersonnel.getTableHeader().setReorderingAllowed(false); // δεν επιτρεπω σερνεται στηλη
		
		// πλατος στηλων - η τελευταια εχει τα μαθηματα οποτε θελει πιο πολυ χωρο
		tblPersonnel.getColumnModel().getColumn(0).setPreferredWidth(140);
		tblPersonnel.getColumnModel().getColumn(1).setPreferredWidth(120);
		tblPersonnel.getColumnModel().getColumn(2).setPreferredWidth(100);
		tblPersonnel.getColumnModel().getColumn(3).setPreferredWidth(300);
		
		// scroll για να σηκωνει πολλα δεδομενα
		JScrollPane scroll = new JScrollPane(tblPersonnel);
		scroll.setBounds(20, 55, 666, 185);
		contentPane.add(scroll);
	}
	
	// φορτωνει ολους τους καθηγητες + τα μαθηματα τους ενωμενα σε ενα string
	private void loadData() {
		try {
			// GROUP_CONCAT ενωνει ολους τους τιτλους μαθηματων του καθηγητη σε ενα string
			// LEFT JOIN για να εμφανιζονται και οσοι δεν διδασκουν κανενα μαθημα
			String sql = "SELECT t.last_name, t.first_name, t.availability, " +
					"GROUP_CONCAT(c.title ORDER BY c.title SEPARATOR ', ') AS courses " +
					"FROM Teachers t " +
					"LEFT JOIN Courses c ON t.teacher_id = c.teacher_id " +
					"GROUP BY t.teacher_id, t.last_name, t.first_name, t.availability " +
					"ORDER BY t.last_name, t.first_name";
			
			PreparedStatement pst = DBConnection.conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			
			// καθαριζω τυχον παλιες γραμμες
			model.setRowCount(0);
			
			while (rs.next()) {
				String courses = rs.getString("courses");
				if (courses == null) {
					courses = "(κανένα)"; // καθηγητης χωρις αναθεσεις
				}
				
				model.addRow(new Object[]{
					rs.getString("last_name"),
					rs.getString("first_name"),
					rs.getInt("availability"),
					courses
				});
			}
			
			rs.close();
			pst.close();
			
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(contentPane,
				"Σφάλμα φόρτωσης δεδομένων:\n" + ex.getMessage(),
				"Σφάλμα",
				JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	// φορτωνει τα δεδομενα μολις ανοιξει η φορμα. Η συνδεση εχει ηδη γινει στο Home.
	private void loadDataListener() {
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				loadData();
			}
		});
	}
	
	// το aboveExit κουμπι επιστρεφει στο Home
	@Override
	protected void aboveExitButtonActionListener(JButton btn) {
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Home home = new Home(
					"College Secretary App - Home",
					"Αρχική Σελίδα",
					"Έκδοση Εφαρμογής");
				home.setVisible(true);
				dispose();
			}
		});
	}
}