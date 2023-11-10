package assets.views.fileencryption;

import assets.AES.AESFilesEncDec;
import com.vaadin.flow.component.html.Anchor;
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
import com.vaadin.flow.server.StreamResource;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@PageTitle("File Encryption")
@Route(value = "file-encryption", layout = MainLayout.class)
@Uses(Icon.class)
public class FileEncryptionView extends HorizontalLayout {

    private Anchor downloadLink;
    private byte[] encryptedData;

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

        // Create a mutable container to store the uploaded file data and name
        class FileDataContainer {
            private byte[] data;
            private String fileName;

            public void setData(byte[] data) {
                this.data = data;
            }

            public byte[] getData() {
                return data;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public String getFileName() {
                return fileName;
            }
        }

        FileDataContainer fileDataContainer = new FileDataContainer();

        // Example for MemoryBuffer
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload singleFileUpload = new Upload(memoryBuffer);

        // Style the upload component
        singleFileUpload.setWidthFull();

        // Add a succeeded listener to handle file uploads
        singleFileUpload.addSucceededListener(event -> {
            // Store the uploaded file data and name in the container
            try {
                fileDataContainer.setData(memoryBuffer.getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fileDataContainer.setFileName(event.getFileName());
        });

        // Encrypt button
        Button encryptButton = new Button("Encrypt");
        encryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%")
                .set("cursor", "pointer");

        // Button action
        encryptButton.addClickListener(e -> {
            byte[] uploadedFileData = fileDataContainer.getData();
            String uploadedFileName = fileDataContainer.getFileName();

            if (uploadedFileName != null && uploadedFileData != null) {
                try {
                    String encryptionAlgorithm = "AES/" + encryptionMode.getValue() + "/PKCS5Padding";
                    SecretKey key = AESFilesEncDec.getKeyFromPassword(password.getValue(), new byte[16], keySize.getValue());
                    IvParameterSpec iv = AESFilesEncDec.generateIv();

                    encryptedData = AESFilesEncDec.encryptFile(encryptionAlgorithm, key, iv, uploadedFileData);

                    // Remove the old download link if it exists
                    if (downloadLink != null) {
                        mainContainer.remove(downloadLink);
                    }

                    // Save the encrypted data to a file
                    String encryptedFileName = uploadedFileName + ".enc";
                    File encryptedFile = new File(encryptedFileName);
                    FileOutputStream output = new FileOutputStream(encryptedFile);
                    output.write(encryptedData);
                    output.close();

                    // Create and add the new download link
                    downloadLink = downloadLink(encryptedFileName);
                    mainContainer.add(downloadLink);

                    // clear password field and the upload component
                    password.clear();


                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                         InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException |
                         InvalidKeySpecException exception) {
                    exception.printStackTrace();
                }
            } else {
                Notification.show("Please select a file to encrypt", 3000, Notification.Position.MIDDLE);
            }
        });

        // Add components to the mainContainer
        mainContainer.add(titlesContainer, singleFileUpload, password, keyModeContainer, encryptButton);

        // Add the mainContainer to the screen
        add(mainContainer);
    }

    private Anchor downloadLink(String encryptedFileName) {
        Anchor link = new Anchor(new StreamResource(encryptedFileName, () -> new ByteArrayInputStream(encryptedData)), "Download Encrypted File");
        link.getStyle().set("text-decoration", "none");
        link.getStyle().set("background-color", "#4CAF50");
        link.getStyle().set("color", "white");
        link.getStyle().set("padding", "10px 20px");
        link.getStyle().set("border", "1px solid #4CAF50");
        link.getStyle().set("border-radius", "5px");
        link.getStyle().set("cursor", "pointer");
        link.getElement().setAttribute("download", true);
        return link;
    }
}
