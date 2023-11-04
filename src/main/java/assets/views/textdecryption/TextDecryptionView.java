package assets.views.textdecryption;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import assets.views.MainLayout;

@PageTitle("Text Decryption")
@Route(value = "text-decryption", layout = MainLayout.class)
public class TextDecryptionView extends HorizontalLayout {
    public TextDecryptionView() {
        // Create UI

        // Place the mainContainer in the center of the screen
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();

        // Main Container
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setAlignItems(Alignment.CENTER);
        mainContainer.setWidth("50%");
        mainContainer.setHeight("70%");
        mainContainer.getStyle().set("gap", "2rem");

        // Title Container
        Div titlesContainer = new Div();
        titlesContainer.getStyle().set("text-align", "center");
        H1 title = new H1("Text Decryption");
        H3 subtitle = new H3("Enter an encrypted text to decrypt");
        titlesContainer.add(title, subtitle);

        // Action Container
        Div actionContainer = new Div();
        actionContainer.getStyle()
                .set("width", "50%")
                .set("height", "30%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", ".5rem");

        TextArea encryptedTextArea = new TextArea();
        encryptedTextArea.getStyle().set("width", "100%");
        encryptedTextArea.setPlaceholder("Enter the encrypted text");
        encryptedTextArea.setLabel("Encrypted Text");

        encryptedTextArea.setAutoselect(true);
        encryptedTextArea.focus();



        Button decryptButton = new Button("Decrypt");
        decryptButton.getStyle().set("width", "100%").set("cursor", "pointer");

        // Button action
        decryptButton.addClickListener(e -> {
            Notification.show("Text decrypted");
        });

        // Enter key action
        encryptedTextArea.addKeyPressListener(Key.ENTER, e -> {
            Notification.show("Text decrypted");
        });

        // Add components to the action container
        actionContainer.add(encryptedTextArea, decryptButton);

        // Result container
        Div resultContainer = new Div();
        resultContainer.getStyle()
                .set("flex", "1")
                .set("width", "80%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", ".5rem");

        TextArea decryptedTextArea = new TextArea();
        decryptedTextArea.setReadOnly(true);
        decryptedTextArea.setLabel("Decrypted Text");

        // Make the TextArea grow and take full available height
        decryptedTextArea.getStyle().set("flex", "1");

        // Add components to the result container
        resultContainer.add(decryptedTextArea);

        // Add all containers to the main container
        mainContainer.add(titlesContainer, actionContainer, resultContainer);

        // Add components to the layout
        add(mainContainer);
    }

}
