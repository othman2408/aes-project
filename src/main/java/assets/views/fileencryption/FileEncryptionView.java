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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import assets.views.MainLayout;
import org.apache.commons.compress.utils.IOUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

        /* Example for MemoryBuffer */
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload singleFileUpload = new Upload(memoryBuffer);
        singleFileUpload.setAcceptedFileTypes("text/plain");




            // Encrypt button
        Button encryptButton = new Button("Encrypt");
        encryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%")
                .set("cursor", "pointer");

        //----------------------------------------------------------------------------------------
        // Button action
        encryptButton.addClickListener(e -> {
            String fileName = memoryBuffer.getFileName();
            InputStream inputStream = memoryBuffer.getInputStream();

            if (fileName != null && inputStream != null) {
                try {
                    String encryptionAlgorithm = "AES/" + encryptionMode.getValue() + "/PKCS5Padding";
                    SecretKey key = AESFilesEncDec.getKeyFromPassword(password.getValue(), new byte[16], keySize.getValue());
                    IvParameterSpec iv = AESFilesEncDec.generateIv();

                    byte[] fileData = IOUtils.toByteArray(inputStream); // Read the entire file into a byte array
                    byte[] encryptedData = AESFilesEncDec.encryptFile(encryptionAlgorithm, key, iv, fileData);

                    // Save the encrypted data to a file
                    String encryptedFileName = fileName + ".enc";
                    File encryptedFile = new File(encryptedFileName);
                    FileOutputStream output = new FileOutputStream(encryptedFile);
                    output.write(encryptedData);
                    output.close();

                    // You can perform further actions here, such as downloading or displaying the encrypted file.
                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                         InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException |
                         InvalidKeySpecException exception) {
                    exception.printStackTrace();
                }
            } else {
                Notification.show("Please select a file to encrypt", 3000, Notification.Position.MIDDLE);
            }
        });
        //----------------------------------------------------------------------------------------

        // Add components to the mainContainer
        mainContainer.add(titlesContainer, singleFileUpload ,optionsContainer, encryptButton);

        // Add the mainContainer to the screen
        add(mainContainer);

    }
}