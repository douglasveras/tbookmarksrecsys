package br.cin.tbookmarks.client;

import java.util.HashMap;

import br.cin.tbookmarks.shared.FieldVerifier;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TBookmarksRS implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final UserWebResourcesServiceAsync userWebResourcesService = GWT
			.create(UserWebResourcesService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		String userAgent = Window.Navigator.getUserAgent().toLowerCase();
		
		String androidPage = "AndroidTBookmarksServerRS.html";
		String server = "http://tbookmarksrecommend.appspot.com/";
		
		boolean isAndroidAndPCpage = userAgent.contains("android") && !Window.Location.getPath().contains(androidPage);
		boolean isIphoneAndPCpage = userAgent.contains("iphone") && !Window.Location.getPath().contains(androidPage);
		
		if(isAndroidAndPCpage || isIphoneAndPCpage){
			Window.open(server+androidPage, "_self", "");
		}/*else if(!userAgent.contains("android") && Window.Location.getPath().contains(androidPage)){
			Window.open(server, "_self", "");
		}*/
		
		final Button sendButtonTest = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("ID");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		//sendButton.setStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel rootPanel = RootPanel.get("nameFieldContainer");
		rootPanel.add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButtonTest);
		RootPanel.get("errorLabelContainer").add(errorLabel);
		
		Label labelFieldMessage = new Label();
		labelFieldMessage.setText("Please enter the T-Bookmarks ID from TV:");
		labelFieldMessage.setStyleName("field-Message");
		
		RootPanel.get("fieldMessage").add(labelFieldMessage);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);

		nameField.selectAll();

		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter a nonnegative integer");
					return;
				}

				// Then, we send the input to the server.
				// sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");

				RootPanel.get("resultsCurrent").clear();
				RootPanel.get("resultsHistory").clear();
				
				
				try {
					userWebResourcesService.getUserWebResources(textToServer,
							new AsyncCallback<HashMap<String,String>>() {
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									
								}

								public void onSuccess(HashMap<String,String> results) {
									Tree currentResultsTree = createTreeToRecommendationType(results,EnumRecommendationType.CURRENT);
									addResultPanelWithTree(currentResultsTree,"Current Recommendation","resultsCurrent", "results-panel-current");
									
									Tree historyResultsTree = createTreeToRecommendationType(results,EnumRecommendationType.HISTORY);
									addResultPanelWithTree(historyResultsTree,"History Recommendation","resultsHistory", "results-panel-history");
								}
								
								private Tree createTreeToRecommendationType(
										HashMap<String,String> response, EnumRecommendationType recommendationType) {
									Tree resultsTree = new Tree();
									
									
									for(EnumCategoryType categoryType : EnumCategoryType.values()){
										TreeItem categoryItem = new TreeItem();
										categoryItem.setText(categoryType.toString());
										
										if (response.get("results"+recommendationType+categoryType) != null) {
											
											resultsTree.addItem(categoryItem);
											
											String urls = response.get("results"+recommendationType+categoryType);
											
											String[] results = urls.split(";");
										
											for (int i = 0; i < results.length; i++) {
												
												Anchor anchor = new Anchor(results[i]);
												anchor.setHref(results[i]);
												anchor.setStyleName("results-anchor");
												
												categoryItem.addItem(anchor);
												//categoryItem.setHeight("100px");
												
											}
											
										}
									}
									return resultsTree;
								}
							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButtonTest.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}

	private void addResultPanelWithTree(Tree t, String resultsName, String htmlElementId, String style) {
		HorizontalPanel horizontalPanelCurrentResults = new HorizontalPanel();
		horizontalPanelCurrentResults.setBorderWidth(1);
		horizontalPanelCurrentResults.addStyleName("results-panel");
		horizontalPanelCurrentResults.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);

		horizontalPanelCurrentResults.add(t);
		
		final Label labelCurrentResults = new Label();
		labelCurrentResults.setText(resultsName);
		labelCurrentResults.addStyleName("results-label");
		
		RootPanel.get(htmlElementId).clear();
		
		RootPanel.get(htmlElementId).add(labelCurrentResults);
		
		RootPanel.get(htmlElementId).add(horizontalPanelCurrentResults);
		
		RootPanel.get(htmlElementId).addStyleName(style);
	}
}
