package assets.views.textdecryption;

import assets.views.textencryption.TextEncryptionView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import assets.views.MainLayout;

@PageTitle("Text Decryption")
@Route(value = "text-decryption", layout = MainLayout.class)
public class TextDecryptionView extends HorizontalLayout {
    public TextDecryptionView() {
        // Decryption UI

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
        H1 title = new H1("Text Decryption");
        H3 subtitle = new H3("Enter an encrypted text to decrypt");
        subtitle.getStyle().set("font-weight", "normal").set("font-size", "1.3rem").set("margin-top", ".5rem");
        subtitle.getStyle().set("text-align", "center");
        titlesContainer.add(title, subtitle);

        // Action Container
        Div actionContainer = new Div();
        actionContainer.getStyle()
                .set("width", "80%")
                .set("height", "30%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("padding", "3rem 0rem 1rem 0rem")
                .set("gap", ".5rem");

        // Encrypted text input
        TextArea encryptedTextArea = new TextArea();
        encryptedTextArea.getStyle().set("width", "100%");
        encryptedTextArea.setPlaceholder("Enter the encrypted text");
        encryptedTextArea.setLabel("Encrypted Text");
        encryptedTextArea.setClearButtonVisible(true);
        encryptedTextArea.focus();

        // Key Size & Encryption mode options
        Div optionsContainer = new Div();
        optionsContainer.getStyle()
                .set("display", "flex")
                .set("width", "100%")
                .set("flex-direction", "row")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", ".5rem");

        // Key size options
        Select<Integer> keySize = new Select<>();
        keySize.setItems(128, 192, 256);
        keySize.setValue(128);
        keySize.getStyle().set("width", "100%");
        keySize.setLabel("Key Size");
        keySize.setHelperText("Select the key size");

        // Encryption mode options
        Select<String> encryptionMode = new Select<>();
        encryptionMode.setItems("CBC", "ECB");
        encryptionMode.setValue("CBC");
        encryptionMode.getStyle().set("width", "100%");
        encryptionMode.setLabel("Encryption Mode");
        encryptionMode.setHelperText("Select the encryption mode");

        // Add components to the options container
        optionsContainer.add(keySize, encryptionMode);

        // Decrypt button
        Button decryptButton = new Button("Decrypt");
        decryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%").set("cursor", "pointer");

        // Button action
        decryptButton.addClickListener(e -> {
            Notification.show("Text decrypted");
        });

        // Enter key action
        encryptedTextArea.addKeyPressListener(Key.ENTER, e -> {
            Notification.show("Text decrypted");
        });

        // Add components to the action container
        actionContainer.add(encryptedTextArea, optionsContainer, decryptButton);

        // Result container
        Div resultContainer = new Div();
        resultContainer.getStyle()
                .set("flex", "1")
                .set("width", "80%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", ".5rem");

        // Result text area
        TextArea result = new TextArea();
        result.setLabel("Decrypted Text");

        // Add copy & encrypt button
        HorizontalLayout buttonsContainer = new HorizontalLayout();
        buttonsContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonsContainer.getStyle().set("gap", ".5rem");
        Button copyButton = new Button("Copy");
        copyButton.getStyle().set("cursor", "pointer");
        Button encryptButton = new Button("Encrypt");
        encryptButton.getStyle().set("cursor", "pointer");
        buttonsContainer.add(copyButton, encryptButton);
        result.setSuffixComponent(buttonsContainer);

        // Copy button functionality
        copyButton.addClickListener(e -> {
            // Implement the copy functionality here
        });

        // Encrypt button functionality
        encryptButton.addClickListener(e -> {
            // Navigate to the encryption view
            UI.getCurrent().navigate(TextEncryptionView.class);
        });

        // Make the TextArea grow and take full available height
        result.getStyle().set("flex", "1");

        // Add components to the result container
        resultContainer.add(result, buttonsContainer);

        // Add all containers to the main container
        mainContainer.add(titlesContainer, actionContainer, resultContainer);

        // Add components to the layout
        add(mainContainer);
    }
}
