package secretaryapp.forms;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import secretaryapp.components.BaseForm;

public class Version extends BaseForm {

	
	public Version(String title, String topMessage, String btnNameAboveExit) {
		super(title, topMessage, btnNameAboveExit);
		areaVersion();
	}

	private void areaVersion () {
		JPanel panelversion_is = new JPanel();
		panelversion_is.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelversion_is.setBounds(76, 67, 554, 159);
		contentPane.add(panelversion_is);
		panelversion_is.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblversion_is = new JLabel("Η Εφαρμογή βρίσκεται στην έκδοση :");
		lblversion_is.setVerticalAlignment(SwingConstants.TOP);
		lblversion_is.setHorizontalAlignment(SwingConstants.CENTER);
		lblversion_is.setFont(new Font("Segoe UI", Font.PLAIN, 30));
		panelversion_is.add(lblversion_is);
		
		JLabel lblappversion_number_1 = new JLabel("1.0");
		lblappversion_number_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblappversion_number_1.setFont(new Font("Segoe UI", Font.BOLD, 30));
		panelversion_is.add(lblappversion_number_1);
	}
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void aboveExitButtonActionListener (JButton btn) {
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Home home = new Home ("College Secretary App - Home", "Καλώς Ήρθατε στην Εφαρμογή Γραμματείας του Κολλεγίου!","Έκδοση Εφαρμογής");
				home.setVisible(true);
				dispose();
			}
		});
	}

}
