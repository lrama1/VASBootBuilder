package bsbuilder.wizards.site;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;

public class BackboneProjectWizardPageFour extends WizardPage {
	private Button xssCheckbox;
	private Button csrfCheckbox;
	private Group grpSecurityOptions;

	public BackboneProjectWizardPageFour(String pageName) {
		super(pageName);
		setTitle("Add OWASP Security Features");
		setDescription("Creates scaffolding for security features.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		grpSecurityOptions = new Group(container, SWT.NONE);
		GridData gd_grpSecurityOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpSecurityOptions.widthHint = 350;
		gd_grpSecurityOptions.heightHint = 106;
		grpSecurityOptions.setLayoutData(gd_grpSecurityOptions);
		grpSecurityOptions.setText("Security Options");
		
		xssCheckbox = new Button(grpSecurityOptions, SWT.CHECK);
		xssCheckbox.setBounds(10, 29, 251, 24);
		xssCheckbox.setText("Add code for XSS prevention");
		
		csrfCheckbox = new Button(grpSecurityOptions, SWT.CHECK);
		csrfCheckbox.setBounds(10, 69, 227, 24);
		csrfCheckbox.setText("Add code for CSRF prevention");

	}

	public Button getXssCheckbox() {
		return xssCheckbox;
	}
	public Button getCsrfCheckbox() {
		return csrfCheckbox;
	}
}
