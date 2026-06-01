package mainApp;

import secretaryapp.forms.*;

// entry point της εφαρμογης - εδω ξεκιναει το προγραμμα
public class App {
	
	public static Home home;
	public static Version version;
	
	public static void main(String[] args) {
		// δημιουργια και εμφανιση της αρχικης φορμας (Home)
		// το windowOpened της Home θα κανει αυτοματα τη συνδεση με τη βαση
		home = new Home(
			"College Secretary App - Home",
			"Καλώς Ήρθατε στην Εφαρμογή Γραμματείας του Κολλεγίου!",
			"Έκδοση Εφαρμογής");
		home.setVisible(true);
	}
}