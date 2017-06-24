package bsbuilder.wizards.site;

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


public class BackboneAddJSLibPage extends WizardPage {
	private Text text;

	public BackboneAddJSLibPage(String pageName) {
		super(pageName);
		setTitle("Backbone MVC Options");
		setDescription("Some options to customize the Backbone Code");
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
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				FileDialog fileDialog = new FileDialog(parent.getShell());
			    // Set the text
			    fileDialog.setText("Select File");
			    // Set filter on .txt files
			    fileDialog.setFilterExtensions(new String[] { "*.js" });
			    // Put in a readable name for the filter
			    fileDialog.setFilterNames(new String[] { "Javascript(*.js)" });
			    // Open Dialog and save result of selection
			    String selected = fileDialog.open();
			    text.setText(selected);
			}
		});

		btnNewButton.setText("Browse..");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnCheckButton = new Button(container, SWT.CHECK);
		btnCheckButton.setText("Shim this Lib");
		new Label(container, SWT.NONE);
		
	}
	


}
