package bsbuilder.wizards.site;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;


public class BackboneAddPresenterPage extends WizardPage {
	private Text presenterName;
	org.eclipse.swt.widgets.List backboneViewList;
	private List<String> viewPaths;
	private Label lblPresenterName;

	public BackboneAddPresenterPage(String pageName) {
		super(pageName);
		setTitle("Backbone MVC Options");
		setDescription("Add A New Presenter View");
	}
	
	@Override
	public void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		lblPresenterName = new Label(container, SWT.NONE);
		lblPresenterName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPresenterName.setText("Presenter Name:");
		
		presenterName = new Text(container, SWT.BORDER);
		presenterName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				validatePresenterName();
			}
		});
		GridData gd_presenterName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_presenterName.widthHint = 413;
		presenterName.setLayoutData(gd_presenterName);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		backboneViewList = new org.eclipse.swt.widgets.List(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		backboneViewList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				validatePresenterName();
			}
		});
		
		for(String path : viewPaths){
			backboneViewList.add(path);
		}
		
		
		GridData gd_backboneViewList = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_backboneViewList.widthHint = 490;
		gd_backboneViewList.heightHint = 153;
		backboneViewList.setLayoutData(gd_backboneViewList);
		validatePresenterName();
	}
	
	private void validatePresenterName(){
		if(presenterName.getText() == null || 
				presenterName.getText().trim().equals("") ||
				!presenterName.getText().endsWith("Presenter") ||
				!presenterName.getText().substring(0, 1).matches("[A-Z]") ){
			updateStatus("Please specify a name with a pattern of <yourDesiredName>Presenter");
			return;
		}
		if(validateViewSelection())
			updateStatus(null);
			
	}
	
	private boolean validateViewSelection(){
		if(backboneViewList.getSelectionCount() < 1){
			updateStatus("Please select at least one View.");
			return false;
		}
		updateStatus(null);
		return true;
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public void setViewNamesFound(List<String> viewPaths){
		this.viewPaths = viewPaths;
	}
	
	public String[] getSelectedViews(){
		return backboneViewList.getSelection();
	}
	
	public String getPresenterName(){
		return presenterName.getText();
	}


}
