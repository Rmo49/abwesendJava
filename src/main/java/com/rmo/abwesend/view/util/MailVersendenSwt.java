package com.rmo.abwesend.view.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** Versuch mit neuem Layout SWT: Standard Widget Toolkit
 *
 * @author ruedi
 *
 */
public class MailVersendenSwt extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MailVersendenSwt(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblMailVersenden = new Label(this, SWT.NONE);
		lblMailVersenden.setText("Mail versenden");
		new Label(this, SWT.NONE);

		Label lblBetreff = new Label(this, SWT.NONE);
		lblBetreff.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBetreff.setText("Betreff");

		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblMailText = new Label(this, SWT.NONE);
		lblMailText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMailText.setText("Mail Text");

		StyledText styledText = new StyledText(this, SWT.BORDER);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblVariable = new Label(this, SWT.NONE);
		lblVariable.setText("Variable");

		Label lblWerdenMit = new Label(this, SWT.NONE);
		lblWerdenMit.setText("<Vorname>, <Spiele> werden mit Daten ersetzt");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
