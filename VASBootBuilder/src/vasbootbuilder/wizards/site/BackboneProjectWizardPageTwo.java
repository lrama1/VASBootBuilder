package vasbootbuilder.wizards.site;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Combo;

public class BackboneProjectWizardPageTwo extends WizardPage {
	private Text textBasePackage;
	private Text textControllerPackage;
	private Text textDomainPackage;
	private Text textMongoHost;
	private Text textMongoPort;
	private Text textMongoDBName;
	private Button btnUseMongodb;
	private Button btnPrepForOracle;
	private Button btnPrepForMysql;
	private Button btnPrepForHSQL;
	private Combo cmbSpringVersion;
	private Label lblSpringVersion;
	private Text textOracleHost;
	private Text textOraclePort;
	private Text textOracleInstance;
	private Text textOracleUser;
	private Text textOraclePass;
	private Label lblNewLabel_7;
	private Label lblNewLabel_8;

	protected BackboneProjectWizardPageTwo(String pageName) {
		super(pageName);
		setTitle("Select Base Package");
		setDescription("Select a base package name to use for your Java Classes.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Base Package Name:");
		
		textBasePackage = new Text(container, SWT.BORDER);
		textBasePackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				validatePackage(textBasePackage.getText());
				textControllerPackage.setText(textBasePackage.getText() + ".controller" );
				textDomainPackage.setText(textBasePackage.getText() + ".web.domain" );
			}
		});
		textBasePackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblControllerPackage = new Label(container, SWT.NONE);
		lblControllerPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblControllerPackage.setText("Controller Package:");
		
		textControllerPackage = new Text(container, SWT.BORDER);
		textControllerPackage.setEnabled(false);
		textControllerPackage.setEditable(false);
		textControllerPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Domain Package:");
		
		textDomainPackage = new Text(container, SWT.BORDER);
		textDomainPackage.setEditable(false);
		textDomainPackage.setEnabled(false);
		textDomainPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		btnUseMongodb = new Button(container, SWT.CHECK);
		btnUseMongodb.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnUseMongodb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(btnUseMongodb.getSelection()){
					btnPrepForOracle.setSelection(false);
					btnPrepForMysql.setSelection(false);
					btnPrepForHSQL.setSelection(false);
					textMongoHost.setEnabled(true);
					textMongoPort.setEnabled(true);
					textMongoDBName.setEnabled(true);
					//
					textOracleHost.setEnabled(false);
					textOraclePort.setEnabled(false);
					textOracleInstance.setEnabled(false);
					textOracleUser.setEnabled(false);
					textOraclePass.setEnabled(false);
				}else{
					textMongoHost.setEnabled(false);
					textMongoPort.setEnabled(false);
					textMongoDBName.setEnabled(false);
				}
			}
		});
		btnUseMongodb.setText("Use MongoDB");
		
		btnPrepForOracle = new Button(container, SWT.CHECK);
		btnPrepForOracle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(btnPrepForOracle.getSelection()){
					btnUseMongodb.setSelection(false);
					btnPrepForMysql.setSelection(false);
					btnPrepForHSQL.setSelection(false);
					textOracleHost.setEnabled(true);
					textOraclePort.setEnabled(true);
					textOracleInstance.setEnabled(true);
					textOracleUser.setEnabled(true);
					textOraclePass.setEnabled(true);
					//
					textMongoHost.setEnabled(false);
					textMongoPort.setEnabled(false);
					textMongoDBName.setEnabled(false);
				}else{
					textOracleHost.setEnabled(false);
					textOraclePort.setEnabled(false);
					textOracleInstance.setEnabled(false);
					textOracleUser.setEnabled(false);
					textOraclePass.setEnabled(false);
				}
			}
		});
		
		GridData gd_btnPrepForOracle = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnPrepForOracle.heightHint = 33;
		btnPrepForOracle.setLayoutData(gd_btnPrepForOracle);
		btnPrepForOracle.setText("Prep For Oracle");
		new Label(container, SWT.NONE);
		
		Group grpMongoDbParams = new Group(container, SWT.NONE);
		GridData gd_grpMongoDbParams = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpMongoDbParams.widthHint = 348;
		gd_grpMongoDbParams.heightHint = 210;
		grpMongoDbParams.setLayoutData(gd_grpMongoDbParams);
		grpMongoDbParams.setText("Mongo DB Params");
		
		Label lblNewLabel_2 = new Label(grpMongoDbParams, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setBounds(10, 32, 102, 17);
		lblNewLabel_2.setText("Host:");
		
		Label lblNewLabel_3 = new Label(grpMongoDbParams, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		lblNewLabel_3.setBounds(10, 66, 102, 17);
		lblNewLabel_3.setText("Port:");
		
		Label lblDbName = new Label(grpMongoDbParams, SWT.NONE);
		lblDbName.setAlignment(SWT.RIGHT);
		lblDbName.setBounds(33, 102, 79, 17);
		lblDbName.setText("DB Name:");
		
		textMongoHost = new Text(grpMongoDbParams, SWT.BORDER);
		textMongoHost.setText("localhost");
		textMongoHost.setEnabled(false);
		textMongoHost.setBounds(118, 27, 219, 27);
		
		textMongoPort = new Text(grpMongoDbParams, SWT.BORDER);
		textMongoPort.setText("27017");
		textMongoPort.setEnabled(false);
		textMongoPort.setBounds(119, 61, 102, 27);
		
		textMongoDBName = new Text(grpMongoDbParams, SWT.BORDER);
		textMongoDBName.setText("localdb");
		textMongoDBName.setEnabled(false);
		textMongoDBName.setBounds(118, 97, 149, 27);
		
		Group grpOracleParam = new Group(container, SWT.NONE);
		grpOracleParam.setText("Oracle Params");
		GridData gd_grpOracleParam = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpOracleParam.heightHint = 210;
		gd_grpOracleParam.widthHint = 367;
		grpOracleParam.setLayoutData(gd_grpOracleParam);
		
		textOracleHost = new Text(grpOracleParam, SWT.BORDER);
		textOracleHost.setText("localhost");
		textOracleHost.setEnabled(false);
		textOracleHost.setBounds(87, 23, 265, 29);
		
		textOraclePort = new Text(grpOracleParam, SWT.BORDER);
		textOraclePort.setText("1521");
		textOraclePort.setEnabled(false);
		textOraclePort.setBounds(87, 60, 81, 29);
		
		textOracleInstance = new Text(grpOracleParam, SWT.BORDER);
		textOracleInstance.setText("localDB");
		textOracleInstance.setEnabled(false);
		textOracleInstance.setBounds(87, 97, 155, 29);
		
		Label lblNewLabel_4 = new Label(grpOracleParam, SWT.NONE);
		lblNewLabel_4.setBounds(10, 25, 71, 17);
		lblNewLabel_4.setText("Host:");
		
		Label lblNewLabel_5 = new Label(grpOracleParam, SWT.NONE);
		lblNewLabel_5.setBounds(10, 61, 71, 17);
		lblNewLabel_5.setText("Port:");
		
		Label lblNewLabel_6 = new Label(grpOracleParam, SWT.NONE);
		lblNewLabel_6.setBounds(10, 97, 71, 17);
		lblNewLabel_6.setText("DB Name:");
		
		textOracleUser = new Text(grpOracleParam, SWT.BORDER);
		textOracleUser.setText("system");
		textOracleUser.setEnabled(false);
		textOracleUser.setBounds(87, 134, 155, 29);
		
		textOraclePass = new Text(grpOracleParam, SWT.BORDER);
		textOraclePass.setText("password");
		textOraclePass.setEnabled(false);
		textOraclePass.setBounds(87, 171, 155, 29);
		
		lblNewLabel_7 = new Label(grpOracleParam, SWT.NONE);
		lblNewLabel_7.setBounds(13, 134, 71, 17);
		lblNewLabel_7.setText("User:");
		
		lblNewLabel_8 = new Label(grpOracleParam, SWT.NONE);
		lblNewLabel_8.setBounds(10, 171, 71, 17);
		lblNewLabel_8.setText("Password:");
		new Label(container, SWT.NONE);
		
		btnPrepForMysql = new Button(container, SWT.CHECK);
		btnPrepForMysql.setText("Prep For MySQL");
		
		btnPrepForHSQL = new Button(container, SWT.CHECK);
		btnPrepForHSQL.setText("Use HSQL");
		
		btnPrepForMysql.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(btnPrepForMysql.getSelection()) {
					btnUseMongodb.setSelection(false);
					btnPrepForOracle.setSelection(false);
					btnPrepForHSQL.setSelection(false);
					textMongoHost.setEnabled(false);
					textMongoPort.setEnabled(false);
					textMongoDBName.setEnabled(false);
					textOracleHost.setEnabled(false);
					textOraclePort.setEnabled(false);
					textOracleInstance.setEnabled(false);
					textOracleUser.setEnabled(false);
					textOraclePass.setEnabled(false);
				}
			}
		});
		
		
		btnPrepForHSQL.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(btnPrepForHSQL.getSelection()) {
					btnUseMongodb.setSelection(false);
					btnPrepForOracle.setSelection(false);
					btnPrepForMysql.setSelection(false);
					textMongoHost.setEnabled(false);
					textMongoPort.setEnabled(false);
					textMongoDBName.setEnabled(false);
					textOracleHost.setEnabled(false);
					textOraclePort.setEnabled(false);
					textOracleInstance.setEnabled(false);
					textOracleUser.setEnabled(false);
					textOraclePass.setEnabled(false);
				}
			}
		});
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		lblSpringVersion = new Label(container, SWT.NONE);
		lblSpringVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSpringVersion.setText("Spring Version?");
		
		cmbSpringVersion = new Combo(container, SWT.READ_ONLY);		
		cmbSpringVersion.add("4.x.x");
		cmbSpringVersion.add("3.x.x");
		cmbSpringVersion.select(0);
		cmbSpringVersion.setEnabled(false);
		cmbSpringVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		//Validate on display
		validatePackage(textBasePackage.getText());
	}
	
	private void validatePackage(String packageName){
		if(packageName.trim().length() == 0){
			updateStatus("Please specify a package name");
			return;			
		}
		if(packageName.trim().endsWith(".")){
			updateStatus("Please specify a valid format for your package name");
			return;			
		}
		
		updateStatus(null);
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String getBasePackageName(){
		return textBasePackage.getText();
	}
	
	public String getControllerPackage(){
		return textControllerPackage.getText();
	}
	
	public String getDomainPackage(){
		return textDomainPackage.getText();
	}
	
	public boolean useMongoDB(){
		return btnUseMongodb.getSelection();
	}
	
	public String getMongoHostName(){
		return textMongoHost.getText();
	}
	
	public String getMongoPort(){
		return textMongoPort.getText();
	}
	
	public String getMongoDBName(){
		return textMongoDBName.getText();
	}
	
	public boolean prepForOracle(){
		return btnPrepForOracle.getSelection();
	}
	
	public boolean prepForMySql(){
		return btnPrepForMysql.getSelection();
	}
	public boolean prepForHSQL() {
		return btnPrepForHSQL.getSelection();
	}
	
	
	public String getSpringVersion(){
		return cmbSpringVersion.getText();
	}
	
	public String getOracleHost(){
		return textOracleHost.getText();
	}
	
	public String getOraclePort(){
		return textOraclePort.getText();
	}
	
	public String getOracleInstance(){
		return textOracleInstance.getText();
	}
	
	public String getOracleUser(){
		return textOracleUser.getText();
	}
	
	public String getOraclePassword(){
		return textOraclePass.getText();
	}
}
