package secretaryapp.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

// σύνδεση με τη βάση HR_app
public class DBConnection {
	// στοιχεία σύνδεσης
	private static final String URL = "jdbc:mysql://localhost:3306/HR_app";
	private static final String USER = "root";
	private static final String PASSWORD = "root";

	// η connection που μοιράζονται όλα τα forms
	public static Connection conn;
	// private για να μην φτιάχνουμε instance
	private DBConnection() {}

	// ανοίγει τη σύνδεση. true = ok, false = fail
	// αν υπάρχει ήδη ενεργή σύνδεση, δεν την ξανα-ανοίγει
	public static boolean connect() {
		if (conn != null) return true;

		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			return true;
		} catch (SQLException ex) {
			// 1ο μηνυμα - το σφαλμα
			JOptionPane.showMessageDialog(
				null,
				"Αποτυχία σύνδεσης στη βάση:\n" + ex.getMessage(),
				"Σφάλμα Σύνδεσης",
				JOptionPane.ERROR_MESSAGE
			);
			ex.printStackTrace();

			// 2ο μηνυμα - τι πρεπει να κανει ο χρηστης
			JOptionPane.showMessageDialog(
				null,
				"Βεβαιωθείτε ότι ο MySQL server είναι ενεργός\n" +
				"και ότι τα στοιχεία σύνδεσης είναι σωστά.\n\n" +
				"Η εφαρμογή θα κλείσει.",
				"Τι να κάνετε",
				JOptionPane.INFORMATION_MESSAGE
			);

			// κλείσιμο Εφαρμογής αφού δεν υπάρχει λόγος χρήσης της χωρίς ΒΔ
			System.exit(0);

			return false; // δεν φτανει ποτε εδω, αλλα χωρις αυτο δεν κανει Compile
		}
	}
}