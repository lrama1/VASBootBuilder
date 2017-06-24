package bsbuilder.wizards.site;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;


public class BackboneProjectWizardPageSix extends WizardPage {

	public BackboneProjectWizardPageSix(String pageName) {
		super(pageName);
		setTitle("Other Stuff");
		setDescription("Some stuff you might want to include in your web project");
	}
	private Group grpTemplateOptions;
	private Button btnAddWebService;
	
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
		
		grpTemplateOptions = new Group(container, SWT.NONE);
		GridData gd_grpTemplateOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpTemplateOptions.widthHint = 469;
		gd_grpTemplateOptions.heightHint = 159;
		grpTemplateOptions.setLayoutData(gd_grpTemplateOptions);
		grpTemplateOptions.setText("Other Features");
		
		btnAddWebService = new Button(grpTemplateOptions, SWT.CHECK);
		btnAddWebService.setBounds(0, 20, 331, 24);
		btnAddWebService.setText("Add Web Service Endpoints");
		
	}
	
	
	public boolean addWebServiceFeature(){
		return btnAddWebService.getSelection();
	}
	

}
