package assets.views.fileencryption;

import assets.AES.AESFileEncDec;
import assets.views.sharedComponents.Notify;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@PageTitle("File Encryption")
@Route(value = "file-encryption", layout = MainLayout.class)
@Uses(Icon.class)
public class FileEncryptionView extends HorizontalLayout {

    private Anchor downloadLink;
    private Anchor downloadIvLink;
    private Anchor downloadSaltLink;
    private byte[] encryptedData;
    private byte[] salt;
    private byte[] iv;

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
        mainContainer.getStyle().set("gap", "1rem");

        // Title Container
        Div titlesContainer = new Div();
        H1 title = new H1("File Encryption");
        H3 subtitle = new H3("Upload a file to encrypt it");
        subtitle.getStyle()
                .set("font-weight", "normal")
                .set("font-size", "1.3rem")
                .set("margin-top", ".8rem")
                .set("text-align", "center")
                .set("margin-bottom", "2rem");
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

        // Create an instance of the mutable container
        FileDataContainer fileDataContainer = new FileDataContainer();

        // File Upload component
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
                    salt = AESFileEncDec.generateSalt();
                    SecretKey key = AESFileEncDec.getKeyFromPassword(password.getValue(), salt, keySize.getValue());
                    IvParameterSpec ivSpec = AESFileEncDec.generateIv();

                    // Update the IV byte array
                    iv = ivSpec.getIV();

                    encryptedData = AESFileEncDec.encryptFile(encryptionAlgorithm, key, ivSpec, uploadedFileData);

                    // Remove the old download links if they exist
                    if (downloadLink != null) {
                        mainContainer.remove(downloadLink);
                    }
                    if (downloadIvLink != null) {
                        mainContainer.remove(downloadIvLink);
                    }
                    if (downloadSaltLink != null) {
                        mainContainer.remove(downloadSaltLink);
                    }

                    // Save the encrypted data to a file
                    String encryptedFileName = uploadedFileName + ".enc";
                    File encryptedFile = new File(encryptedFileName);
                    try (FileOutputStream output = new FileOutputStream(encryptedFile)) {
                        output.write(encryptedData);
                    }

                    // Save the IV to a file
                    String ivFileName = uploadedFileName + "_iv.enc";
                    File ivFile = new File(ivFileName);
                    try (FileOutputStream ivOutput = new FileOutputStream(ivFile)) {
                        ivOutput.write(iv);
                    }

                    // Save the salt to a file
                    String saltFileName = uploadedFileName + "_salt.enc";
                    File saltFile = new File(saltFileName);
                    try (FileOutputStream saltOutput = new FileOutputStream(saltFile)) {
                        saltOutput.write(salt);
                    }

                    // Create and add the new download links
                    downloadLink = downloadLink(encryptedFileName);
                    downloadIvLink = downloadLink(ivFileName);
                    downloadSaltLink = downloadLink(saltFileName);

                    mainContainer.add(downloadLink, downloadIvLink, downloadSaltLink);

                    // Clear password field and the upload component
                    password.clear();

                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                         InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException |
                         InvalidKeySpecException exception) {
                    exception.printStackTrace();
                }
            } else {
                Notify.notify("Please upload a file to encrypt", 3000, "error");
            }
        });


        // Add components to the mainContainer
        mainContainer.add(titlesContainer, singleFileUpload, password, keyModeContainer, encryptButton);

        // Add the mainContainer to the screen
        add(mainContainer);
    }

    private Anchor downloadLink(String fileName) {
        Anchor link = new Anchor(new StreamResource(fileName, () -> {
            try {
                return new ByteArrayInputStream(Files.readAllBytes(Paths.get(fileName)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), "Download File");
        link.getStyle().set("text-decoration", "none")
                .set("background-color", "#4CAF50")
                .set("color", "white")
                .set("padding", "10px 20px")
                .set("border", "1px solid #4CAF50")
                .set("border-radius", "5px")
                .set("cursor", "pointer");
        link.getElement().setAttribute("download", true);
        return link;
    }
}
