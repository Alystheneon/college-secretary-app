package secretaryapp.forms;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import secretaryapp.components.BaseForm;
import secretaryapp.database.DBConnection;

// μαθηματα του καθηγητη - view + assign
public class Courses extends BaseForm {
	
	private static final long serialVersionUID = 1L;
	
	// constants για το source - τα χρησιμοποιουν οι callers
	public static final String FROM_NEW_TEACHER = "NEW_TEACHER";
	public static final String FROM_RESULTS = "RESULTS";
	
	private int teacherId;
	private int availability;
	private String source;        // απο που ηρθε (NewTeacher ή Results)
	private String searchQuery;   // μονο αν source = RESULTS, για re-open
	
	private JPanel coursesCheckPanel;
	private JLabel lblHoursTotal;
	private ArrayList<JCheckBox> courseCheckboxes = new ArrayList<>();
	
	public Courses(String title, String topMessage, String btnNameAboveExit,
				   int teacherId, String source, String searchQuery) {
		super(title, topMessage, btnNameAboveExit);
		this.teacherId = teacherId;
		this.source = source;
		this.searchQuery = searchQuery;
		coursesPanel();
		loadDataListener();
	}
	
	private void coursesPanel() {
		JLabel lblHeader = new JLabel("Επιλέξτε τα διδασκόμενα μαθήματα:");
		lblHeader.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblHeader.setBounds(76, 55, 350, 22);
		contentPane.add(lblHeader);
		
		lblHoursTotal = new JLabel("Σύνολο: 0 / 0 ώρες", SwingConstants.RIGHT);
		lblHoursTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblHoursTotal.setBounds(430, 55, 200, 22);
		contentPane.add(lblHoursTotal);
		
		// τα checkboxes μπαινουν εδω μεσα σε scroll
		coursesCheckPanel = new JPanel();
		coursesCheckPanel.setLayout(new BoxLayout(coursesCheckPanel, BoxLayout.Y_AXIS));
		coursesCheckPanel.setBackground(new Color(245, 245, 245));
		
		JScrollPane scroll = new JScrollPane(coursesCheckPanel);
		scroll.setBounds(76, 80, 554, 155);
		scroll.setBorder(new LineBorder(new Color(180, 180, 180)));
		contentPane.add(scroll);
	}
	
	// φορτωνει availability + ολα τα μαθηματα και φτιαχνει τα checkboxes
	private void loadData() {
		// availability του καθηγητη
		try {
			PreparedStatement pst1 = DBConnection.conn.prepareStatement(
				"SELECT availability FROM Teachers WHERE teacher_id = ?");
			pst1.setInt(1, teacherId);
			ResultSet rs1 = pst1.executeQuery();
			if (rs1.next()) {
				availability = rs1.getInt("availability");
			}
			rs1.close();
			pst1.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(contentPane, "Σφάλμα: " + ex.getMessage());
			ex.printStackTrace();
		}
		
		// ολα τα μαθηματα + ποιος καθηγητης τα εχει
		coursesCheckPanel.removeAll();
		courseCheckboxes.clear();
		
		try {
			String sql = "SELECT c.c_id, c.title, c.semester, c.hours, " +
					"c.teacher_id, t.first_name, t.last_name " +
					"FROM Courses c " +
					"LEFT JOIN Teachers t ON c.teacher_id = t.teacher_id " +
					"ORDER BY c.semester, c.title";
			PreparedStatement pst = DBConnection.conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			
			boolean any = false;
			while (rs.next()) {
				any = true;
				int cId = rs.getInt("c_id");
				String title = rs.getString("title");
				int semester = rs.getInt("semester");
				int hours = rs.getInt("hours");
				int currentTeacherId = rs.getInt("teacher_id");
				boolean unassigned = rs.wasNull();
				
				// 3 cases: δικο μας / ελευθερο / σε αλλον
				String state;
				String otherName = null;
				if (unassigned) {
					state = "free";
				} else if (currentTeacherId == teacherId) {
					state = "ours";
				} else {
					state = "other";
					otherName = rs.getString("last_name") + " " + rs.getString("first_name");
				}
				
				String text = title + "  (Εξάμηνο: " + semester + ", Ώρες: " + hours + ")";
				if (state.equals("other")) {
					text += "  —  Ανατεθειμένο: " + otherName;
				}
				
				JCheckBox cb = new JCheckBox(text);
				cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
				cb.setBackground(new Color(245, 245, 245));
				cb.putClientProperty("c_id", cId);
				cb.putClientProperty("title", title);
				cb.putClientProperty("hours", hours);
				cb.putClientProperty("state", state);
				cb.putClientProperty("other_name", otherName);
				
				// pre-check πριν μπει ο listener για να μη σκαει popup χωρις λογο
				if (state.equals("ours")) {
					cb.setSelected(true);
				}
				
				cb.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							// check 1: ξεπερναει τις ωρες; (γρηγορο, χωρις popup interaction)
							int total = 0;
							for (JCheckBox c : courseCheckboxes) {
								if (c.isSelected()) {
									total += (int) c.getClientProperty("hours");
								}
							}
							if (total > availability) {
								int h = (int) cb.getClientProperty("hours");
								JOptionPane.showMessageDialog(contentPane,
									"Δεν μπορεί να επιλεχθεί αυτό το μάθημα (" + h + " ώρες).\n" +
									"Θα ξεπερνούσε τη διαθεσιμότητα του καθηγητή (" + availability + " ώρες/εβδομάδα).\n" +
									"Σύνολο πριν την επιλογή: " + (total - h) + " ώρες.",
									"Υπέρβαση Διαθεσιμότητας",
									JOptionPane.WARNING_MESSAGE);
								cb.setSelected(false);
								return;
							}
							
							// check αν το εχει αλλος  καθηγητής
							String s = (String) cb.getClientProperty("state");
							if (s.equals("other")) {
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										String t = (String) cb.getClientProperty("title");
										String o = (String) cb.getClientProperty("other_name");
										int choice = JOptionPane.showConfirmDialog(contentPane,
											"Το μάθημα \"" + t + "\" είναι ήδη ανατεθειμένο στον " + o +
											".\nΕπιθυμείτε να γίνει αλλαγή;",
											"Επανανάθεση Μαθήματος",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE);
										if (choice != JOptionPane.YES_OPTION) {
											cb.setSelected(false);
										}
										updateHoursLabel();
									}
								});
								return;
							}
						}
						
						// update label και στο select και στο deselect
						updateHoursLabel();
					}
				});
				
				coursesCheckPanel.add(cb);
				courseCheckboxes.add(cb);
			}
			
			if (!any) {
				JLabel lblNone = new JLabel("  Δεν υπάρχουν μαθήματα στη βάση");
				lblNone.setFont(new Font("Segoe UI", Font.ITALIC, 14));
				coursesCheckPanel.add(lblNone);
			}
			
			rs.close();
			pst.close();
			
			updateHoursLabel(); // αρχικη τιμη απο τα pre-checked
			coursesCheckPanel.revalidate();
			coursesCheckPanel.repaint();
			
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(contentPane,
				"Σφάλμα φόρτωσης μαθημάτων:\n" + ex.getMessage(),
				"Σφάλμα", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	// υπολογιζει συνολο ωρων και κανει το label κοκκινο αν ξεπερνα
	private void updateHoursLabel() {
		int total = 0;
		for (JCheckBox cb : courseCheckboxes) {
			if (cb.isSelected()) {
				total += (int) cb.getClientProperty("hours");
			}
		}
		lblHoursTotal.setText("Σύνολο: " + total + " / " + availability + " ώρες");
		if (total > availability) {
			lblHoursTotal.setForeground(Color.RED);
		} else {
			lblHoursTotal.setForeground(Color.BLACK);
		}
	}
	
	private void saveAssignments() {
		// extra check μπας και ξεμεινε σε υπερβαση
		int total = 0;
		for (JCheckBox cb : courseCheckboxes) {
			if (cb.isSelected()) {
				total += (int) cb.getClientProperty("hours");
			}
		}
		if (total > availability) {
			JOptionPane.showMessageDialog(contentPane,
				"Το σύνολο ωρών (" + total + ") ξεπερνά τη διαθεσιμότητα (" + availability + ").\n" +
				"Αποεπιλέξτε μαθήματα πριν την αποθήκευση.",
				"Υπέρβαση Διαθεσιμότητας",
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			PreparedStatement pstAssign = DBConnection.conn.prepareStatement(
				"UPDATE Courses SET teacher_id = ? WHERE c_id = ?");
			PreparedStatement pstUnassign = DBConnection.conn.prepareStatement(
				"UPDATE Courses SET teacher_id = NULL WHERE c_id = ?");
			
			for (JCheckBox cb : courseCheckboxes) {
				String state = (String) cb.getClientProperty("state");
				boolean isChecked = cb.isSelected();
				int cId = (int) cb.getClientProperty("c_id");
				
				// ητανε δικο μας και το ξετσεκαρε
				if (state.equals("ours") && !isChecked) {
					pstUnassign.setInt(1, cId);
					pstUnassign.executeUpdate();
				}
				// δεν ητανε δικο μας και τσεκαριστηκε
				else if (!state.equals("ours") && isChecked) {
					pstAssign.setInt(1, teacherId);
					pstAssign.setInt(2, cId);
					pstAssign.executeUpdate();
				}
				// αλλιως no change
			}
			
			pstAssign.close();
			pstUnassign.close();
			
			JOptionPane.showMessageDialog(contentPane,
				"Η καταχώρηση έγινε επιτυχώς",
				"Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
			
			goBack(); // επιστροφη στη φορμα που μας καλεσε
			
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(contentPane,
				"Σφάλμα κατά την αποθήκευση:\n" + ex.getMessage(),
				"Σφάλμα", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	// επιστροφη στη φορμα που μας καλεσε - οχι παντα στο Home
	private void goBack() {
		if (FROM_NEW_TEACHER.equals(source)) {
			NewTeacher newTeacher = new NewTeacher(
				"College Secretary App - Νέος Καθηγητής",
				"Εισαγωγή Νέου Καθηγητή",
				"Συνέχεια");
			newTeacher.setVisible(true);
		} else if (FROM_RESULTS.equals(source)) {
			Results results = new Results(
				"College Secretary App - Αποτελέσματα",
				"Αποτελέσματα Αναζήτησης",
				"Διδασκόμενα Μαθήματα",
				searchQuery);
			results.setVisible(true);
		} else {
			// fallback - αν δεν εχει οριστει source πας στο Home
			Home home = new Home(
				"College Secretary App - Home",
				"Αρχική Σελίδα",
				"Έκδοση Εφαρμογής");
			home.setVisible(true);
		}
		dispose();
	}
	
	// φορτωνει τα δεδομενα μολις ανοιξει η φορμα. Η συνδεση εχει ηδη γινει στο Home.
	private void loadDataListener() {
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				loadData();
			}
		});
	}
	
	protected void aboveExitButtonActionListener(JButton btn) {
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAssignments();
			}
		});
	}
	
	// split του exit σε 2 κουμπια
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
				goBack(); // επιστροφη στη φορμα που μας καλεσε
			}
		});
		contentPane.add(btnReturn);
	}
}