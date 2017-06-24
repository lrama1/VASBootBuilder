package bsbuilder.wizards.site;

import java.util.Arrays;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;


public class BackboneProjectWizardPageFive extends WizardPage {
	
	private String uiType = "";

	public BackboneProjectWizardPageFive(String pageName) {
		super(pageName);
		setTitle("UI Options");
		setDescription("Some options to customize the Backbone Code");
	}
	
	public BackboneProjectWizardPageFive(String pageName, String uiType){
		this(pageName);
		this.uiType = uiType;
	}

	Button btnRadioButton;
	Button btnRadioButton_1;
	private Group grpTemplateOptions;
	Combo cmbUIType;
	private Button btnInjectLocalizedMessages;
	
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
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("UIType");
		new Label(container, SWT.NONE);
		
		cmbUIType = new Combo(container, SWT.READ_ONLY);
		cmbUIType.add("VueJS");
		cmbUIType.add("BackboneJS");
		cmbUIType.add("AngularJS");		
		cmbUIType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//cmbUIType.select(0);		
		int index = Arrays.binarySearch(cmbUIType.getItems(), this.uiType);
		if(index >= 0){
			cmbUIType.select(index);
		}else{
			cmbUIType.select(0);
		}
		new Label(container, SWT.NONE);
		
		grpTemplateOptions = new Group(container, SWT.NONE);
		GridData gd_grpTemplateOptions = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpTemplateOptions.widthHint = 337;
		gd_grpTemplateOptions.heightHint = 133;
		grpTemplateOptions.setLayoutData(gd_grpTemplateOptions);
		grpTemplateOptions.setText("Template Options");
		
		btnRadioButton = new Button(grpTemplateOptions, SWT.RADIO);
		btnRadioButton.setEnabled(false);
		btnRadioButton.setBounds(10, 74, 321, 24);
		btnRadioButton.setText("Generate JSP Templates [DEPRECATED]");
		
		btnRadioButton_1 = new Button(grpTemplateOptions, SWT.RADIO);
		btnRadioButton_1.setSelection(true);
		btnRadioButton_1.setBounds(10, 33, 239, 24);
		btnRadioButton_1.setText("Generate Plain HTML Templates");
		new Label(container, SWT.NONE);
		
		Group grpLocalization = new Group(container, SWT.NONE);
		GridData gd_grpLocalization = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpLocalization.heightHint = 91;
		gd_grpLocalization.widthHint = 281;
		grpLocalization.setLayoutData(gd_grpLocalization);
		grpLocalization.setText("Localization");
		
		btnInjectLocalizedMessages = new Button(grpLocalization, SWT.CHECK);
		btnInjectLocalizedMessages.setBounds(10, 29, 215, 24);
		btnInjectLocalizedMessages.setText("Inject Localized Messages");
		
	}
	
	public boolean isJSPTemplate(){
		return btnRadioButton.getSelection();
	}
	
	public boolean isHTMLTemplate(){
		return btnRadioButton_1.getSelection();
	}
	
	public boolean injectLocalizedMessages(){
		return btnInjectLocalizedMessages.getSelection();
	}
	
	public String getUIType(){
		return cmbUIType.getText();
	}
}