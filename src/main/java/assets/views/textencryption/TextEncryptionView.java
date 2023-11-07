package assets.views.textencryption;

import assets.AES.AESTextEncDec;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import assets.views.MainLayout;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

import static assets.AES.AESFilesEncDec.generateIv;

@PageTitle("Text Encryption")
@Route(value = "text-encryption", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class TextEncryptionView extends HorizontalLayout {

    AESTextEncDec aes = new AESTextEncDec();


    public TextEncryptionView() {

        // Encryption UI

        // Place the mainContainer in the center of the screen
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();

        // Main Container
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setAlignItems(Alignment.CENTER);
        mainContainer.setWidth("50%");
        mainContainer.getStyle().set("padding", "0");

        // Title Container
        Div titlesContainer = new Div();
        H1 title = new H1("Text Encryption");
        H3 subtitle = new H3("Upload a file to encrypt");
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
                .set("padding", "3rem 0rem 0rem 0rem")
                .set("gap", ".5rem");

        // Plain text input
        TextArea plainTextArea = new TextArea();
        plainTextArea.getStyle().set("width", "100%");
        plainTextArea.setPlaceholder("Enter a text to encrypt");
        plainTextArea.setLabel("Plain Text");
        plainTextArea.setClearButtonVisible(true);
        plainTextArea.focus();

        //Key Size & Encryption mode options
        Div optionsContainer = new Div();
        optionsContainer.getStyle()
                .set("width", "100%")
                .set("display", "flex")
                .set("flex-direction", "column");

        // Password input
        PasswordField passwordField = new PasswordField();
        passwordField.setLabel("Secret Key");
        passwordField.setHelperText("Enter your secret key");

        Div modeKeyContainer = new Div();
        modeKeyContainer.getStyle()
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

        //Encryption mode options
        Select<String> encryptionMode = new Select<>();
        encryptionMode.setItems("CBC", "ECB");
        encryptionMode.setValue("CBC");
        encryptionMode.getStyle().set("width", "100%");
        encryptionMode.setLabel("Encryption Mode");
        encryptionMode.setHelperText("Select the encryption mode");

        // Add components to the modeKeyContainer
        modeKeyContainer.add(keySize, encryptionMode);

        // Add components to the options container
        optionsContainer.add(passwordField, modeKeyContainer);

        // Encrypt button
        Button encryptButton = new Button("Encrypt");
        encryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%").set("cursor", "pointer");

        // Enter key action
        plainTextArea.addKeyPressListener(Key.ENTER, e -> {
            Notification.show("Text encrypted");
        });

        // Add components to the action container
        actionContainer.add(plainTextArea, optionsContainer, encryptButton);

        // Result container
        Div resultContainer = new Div();
        resultContainer.getStyle()
                .set("flex", "1")
                .set("width", "80%")
                .set("display", "flex")
                .set("flex-direction", "column");

        // Result text area
        TextArea result = new TextArea();
        result.setLabel("Encrypted Text");
        result.getStyle().set("min-height", "8rem");

        // Button action
        encryptButton.addClickListener(e -> {
            try {
                String plainText = plainTextArea.getValue();
                String password = passwordField.getValue();
                byte[] salt = AESTextEncDec.generateSalt();
                SecretKey key = AESTextEncDec.getKeyFromPassword(password, salt);

                //IV
                IvParameterSpec iv = generateIv();
                // Perform encryption
                String encryptedText = AESTextEncDec.encrypt("AES/CBC/PKCS5Padding", plainText, key, iv);
                result.setValue(encryptedText);


                // SHOW NOTIFICATION
                Notification notification = new Notification(
                        "Text encrypted", 3000,
                        Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.open();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        // Add copy & decrypt button
        HorizontalLayout buttonsContainer = new HorizontalLayout();
        buttonsContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonsContainer.getStyle().set("gap", ".5rem");
        Button copyButton = new Button("Copy");
        copyButton.getStyle().set("cursor", "pointer");
        Button decryptButton = new Button("Decrypt");
        decryptButton.getStyle().set("cursor", "pointer");
        buttonsContainer.add(copyButton, decryptButton);
        result.setSuffixComponent(buttonsContainer);

        //Copy button functionality
        copyButton.addClickListener(e -> {


        });

        //Decrypt button functionality
        decryptButton.addClickListener(e -> {
            UI.getCurrent().navigate("text-decryption");
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
