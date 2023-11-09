package assets.views.fileencryption;

import assets.AES.AESFilesEncDec;
import assets.views.FileComp.FileProcessingComponent;
import assets.views.FileComp.UploadDownloadView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import assets.views.MainLayout;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@PageTitle("File Encryption")
@Route(value = "file-encryption", layout = MainLayout.class)
@Uses(Icon.class)
public class FileEncryptionView extends HorizontalLayout {

    public FileEncryptionView() {

        // Place the mainContainer in the center of the screen
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();

        // Main Container
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setAlignItems(Alignment.CENTER);
        mainContainer.setWidth("50%");
        mainContainer.getStyle().set("gap", "2rem");

        // Title Container
        Div titlesContainer = new Div();
        H1 title = new H1("File Encryption");
        H3 subtitle = new H3("Upload a file to encrypt it");
        subtitle.getStyle().set("font-weight", "normal").set("font-size", "1.3rem").set("margin-top", ".5rem");
        subtitle.getStyle().set("text-align", "center");
        titlesContainer.add(title, subtitle);

        // Action Container
        Div uploadContainer = new Div();
        uploadContainer.getStyle()
                .set("width", "100%")
                .set("height", "auto")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("padding", "2rem 0rem 0rem 0rem")
                .set("gap", ".5rem");

        // File Upload
        FileProcessingComponent uploadDownloadView = new FileProcessingComponent();

        // Key Size & Encryption mode options
        Div optionsContainer = new Div();
        optionsContainer.getStyle()
                .set("display", "flex")
                .set("width", "100%")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-direction", "column")
                .set("gap", ".5rem");

        // Password Field
        PasswordField password = new PasswordField();
        password.setLabel("Password");
        password.setPlaceholder("Enter your password");
        password.setHelperText("Enter your password");
        password.setRequired(true);
        password.setRequiredIndicatorVisible(true);
        password.getStyle().set("width", "100%");

        // Key and mode options Container
        Div keyModeContainer = new Div();
        keyModeContainer.getStyle()
                .set("display", "flex")
                .set("width", "100%")
                .set("flex-direction", "row")
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

        // Key and mode options Container
        keyModeContainer.add(keySize, encryptionMode);

        // Add components to the optionsContainer
        optionsContainer.add(password, keyModeContainer);


        // Encrypt button
        Button encryptButton = new Button("Encrypt");
        encryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%")
                .set("cursor", "pointer");

        // Button action
        encryptButton.addClickListener(e -> {
            // Get the selected password, key size, and encryption mode
            String enteredPassword = password.getValue();
            Integer selectedKeySize = keySize.getValue();
            String selectedEncryptionMode = encryptionMode.getValue();

            // Check if a file has been uploaded
            if (uploadDownloadView.downloadedFiles.isEmpty()) {
                Notification.show("Please upload a file before encrypting.");
                return;
            }

            // Get the uploaded file
            File inputFile = uploadDownloadView.downloadedFiles.get(0);

            // Prepare the output file for encryption
            File outputFile = new File(uploadDownloadView.uploadFolder, "encryptedFile.enc");

            try {
                // Generate a key based on the entered password and key size
                byte[] salt = AESFilesEncDec.generateSalt();
                AESFilesEncDec.encryptFile(selectedEncryptionMode, AESFilesEncDec.getKeyFromPassword(enteredPassword, salt, selectedKeySize),
                        AESFilesEncDec.generateIv(), inputFile, outputFile);

                // Display a success message
                Notification.show("File encrypted successfully and saved as encryptedFile.enc");
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | NoSuchPaddingException |
                     InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException |
                     IllegalBlockSizeException ex) {
                Notification.show("Encryption failed: " + ex.getMessage());
            }

        });

        // Add components to the mainContainer
        mainContainer.add(titlesContainer, uploadDownloadView ,optionsContainer, encryptButton);

        // Add the mainContainer to the screen
        add(mainContainer);

    }
}
