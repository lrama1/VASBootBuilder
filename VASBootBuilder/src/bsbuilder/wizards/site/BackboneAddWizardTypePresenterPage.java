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


public class BackboneAddWizardTypePresenterPage extends WizardPage {
	private Text presenterName;
	org.eclipse.swt.widgets.List backboneViewList;
	org.eclipse.swt.widgets.List backboneSelectedViewList;
	private List<String> viewPaths;
	private Label lblPresenterName;
	private Composite composite;

	public BackboneAddWizardTypePresenterPage(String pageName) {
		super(pageName);
		setTitle("Backbone MVC Options");
		setDescription("Add A New Wizard-Like Presenter View");
	}
	
	@Override
	public void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		lblPresenterName = new Label(container, SWT.NONE);
		lblPresenterName.setText("Presenter Name:");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		presenterName = new Text(container, SWT.BORDER);
		presenterName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				validatePresenterName();
			}
		});
		GridData gd_presenterName = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_presenterName.widthHint = 274;
		presenterName.setLayoutData(gd_presenterName);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		backboneViewList = new org.eclipse.swt.widgets.List(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		backboneViewList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				validatePresenterName();
			}
		});
		
		for(String path : viewPaths){
			backboneViewList.add(path);
		}
		
		
		GridData gd_backboneViewList = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_backboneViewList.widthHint = 272;
		gd_backboneViewList.heightHint = 153;
		backboneViewList.setLayoutData(gd_backboneViewList);
		
		composite = new Composite(container, SWT.NONE);
		GridData gd_composite = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_composite.widthHint = 60;
		composite.setLayoutData(gd_composite);
		
		Button btnAddView = new Button(composite, SWT.NONE);
		btnAddView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String selected = backboneViewList.getSelection()[0];
				backboneViewList.remove(selected);
				backboneSelectedViewList.add(selected);
				validateViewSelection();
			}
		});
		btnAddView.setBounds(10, 22, 40, 29);
		btnAddView.setText(">");
		
		Button btnRemoveView = new Button(composite, SWT.NONE);
		btnRemoveView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String selected = backboneSelectedViewList.getSelection()[0];
				backboneSelectedViewList.remove(selected);
				backboneViewList.add(selected);
				validateViewSelection();
			}
		});
		btnRemoveView.setBounds(10, 72, 40, 29);
		btnRemoveView.setText("<");
		
		backboneSelectedViewList = new org.eclipse.swt.widgets.List(container, SWT.BORDER);
		GridData gd_backboneSelectedViewList = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_backboneSelectedViewList.heightHint = 104;
		gd_backboneSelectedViewList.widthHint = 244;
		backboneSelectedViewList.setLayoutData(gd_backboneSelectedViewList);
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(null);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		
		Button btnMoveUp = new Button(composite_1, SWT.NONE);
		btnMoveUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String selected =  backboneSelectedViewList.getSelection()[0];
				String[] listOfAllItems =  backboneSelectedViewList.getItems();
				for(int i = 0; i < listOfAllItems.length; i++){
					if(listOfAllItems[i].equals(selected) && i > 0){
						//swap this item with the item below it
						String thisItem = backboneSelectedViewList.getItem(i);
						String thatItem = backboneSelectedViewList.getItem(i-1);
						backboneSelectedViewList.setItem(i, thatItem);
						backboneSelectedViewList.setItem(i-1, thisItem);
						backboneSelectedViewList.setSelection(i-1);
					}
				}
			}
		});
		btnMoveUp.setBounds(0, 26, 58, 29);
		btnMoveUp.setText("Up");
		
		Button btnMoveDown = new Button(composite_1, SWT.NONE);
		btnMoveDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String selected =  backboneSelectedViewList.getSelection()[0];
				String[] listOfAllItems =  backboneSelectedViewList.getItems();
				for(int i = 0; i < listOfAllItems.length; i++){
					if(listOfAllItems[i].equals(selected) && i < listOfAllItems.length -1){
						//swap this item with the item below it
						String thisItem = backboneSelectedViewList.getItem(i);
						String thatItem = backboneSelectedViewList.getItem(i+1);
						backboneSelectedViewList.setItem(i, thatItem);
						backboneSelectedViewList.setItem(i+1, thisItem);
						backboneSelectedViewList.setSelection(i+1);
					}
				}
			}
		});
		btnMoveDown.setBounds(0, 66, 58, 29);
		btnMoveDown.setText("Down");
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
		if(backboneSelectedViewList.getItemCount() < 1){
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
		return backboneSelectedViewList.getItems();
	}
	
	public String getPresenterName(){
		return presenterName.getText();
	}
}
