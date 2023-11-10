package assets.views.filedecryption;

import assets.AES.AESFileEncDec;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import assets.views.MainLayout;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@PageTitle("File Decryption")
@Route(value = "file-decryption", layout = MainLayout.class)
@Uses(Icon.class)
public class FileDecryptionView extends HorizontalLayout {


    private Anchor downloadLink;
    private byte[] decryptedData;
    private boolean isEncryptedFileUploaded = false;
    private boolean isIvFileUploaded = false;
    private boolean isSaltFileUploaded = false;

    public FileDecryptionView() {
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
        H1 title = new H1("File Decryption");
        title.getStyle().set("text-align", "center");
        H3 subtitle = new H3("Upload encrypted file, IV, and Salt to decrypt it");
        subtitle.getStyle().set("font-weight", "normal")
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

        // Encrypted File Upload component
        MemoryBuffer encryptedFileBuffer = new MemoryBuffer();
        Upload encryptedFileUpload = generateUploadComponent(encryptedFileBuffer, new Span("Upload Encrypted File"));

        // IV File Upload component
        MemoryBuffer ivBuffer = new MemoryBuffer();
        Upload ivUpload = generateUploadComponent(ivBuffer, new Span("Upload Initialization Vector (IV) File"));

        // Salt File Upload component
        MemoryBuffer saltBuffer = new MemoryBuffer();
        Upload saltUpload = generateUploadComponent(saltBuffer, new Span("Upload Salt File"));

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

        // Decryption mode options
        Select<String> decryptionMode = new Select<>();
        decryptionMode.setItems("CBC", "ECB");
        decryptionMode.setValue("CBC");
        decryptionMode.getStyle().set("width", "100%");
        decryptionMode.setLabel("Decryption Mode");
        decryptionMode.setHelperText("Select the decryption mode");

        // Key and mode options Container
        keyModeContainer.add(keySize, decryptionMode);

        // Create a mutable container to store the uploaded file data and name
        class FileDataContainer {
            private byte[] encryptedData;
            private byte[] iv;
            private byte[] salt;
            private String fileName;

            public void setEncryptedData(byte[] encryptedData) {
                this.encryptedData = encryptedData;
            }

            public void setIv(byte[] iv) {
                this.iv = iv;
            }

            public void setSalt(byte[] salt) {
                this.salt = salt;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }

            public byte[] getEncryptedData() {
                return encryptedData;
            }

            public byte[] getIv() {
                return iv;
            }

            public byte[] getSalt() {
                return salt;
            }

            public String getFileName() {
                return fileName;
            }
        }

        // Create an instance of the mutable container
        FileDataContainer fileDataContainer = new FileDataContainer();

        // Add a succeeded listener to handle file uploads for encrypted file
        encryptedFileUpload.addSucceededListener(event -> {
            // Store the uploaded file data and name in the container
            try {
                fileDataContainer.setEncryptedData(encryptedFileBuffer.getInputStream().readAllBytes());
                isEncryptedFileUploaded = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fileDataContainer.setFileName(event.getFileName());
        });

        // Add a succeeded listener to handle IV file uploads
        ivUpload.addSucceededListener(event -> {
            // Store the uploaded IV data
            try {
                fileDataContainer.setIv(ivBuffer.getInputStream().readAllBytes());
                isIvFileUploaded = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Add a succeeded listener to handle salt file uploads
        saltUpload.addSucceededListener(event -> {
            // Store the uploaded salt data
            try {
                fileDataContainer.setSalt(saltBuffer.getInputStream().readAllBytes());
                isSaltFileUploaded = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Decrypt button
        Button decryptButton = new Button("Decrypt");
        decryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%")
                .set("cursor", "pointer");

        // Button action
        decryptButton.addClickListener(e -> {
            // Check if all required files are uploaded
            if (!isEncryptedFileUploaded || !isIvFileUploaded || !isSaltFileUploaded || password.getValue().isEmpty()) {
                notify("Please upload all required files and enter your password", 3000, "warning");
                return;
            }

            try {
                String encryptionAlgorithm = "AES/" + decryptionMode.getValue() + "/PKCS5Padding";
                SecretKey key = AESFileEncDec.getKeyFromPassword(password.getValue(), fileDataContainer.getSalt(), keySize.getValue());
                IvParameterSpec iv = new IvParameterSpec(fileDataContainer.getIv());

                decryptedData = AESFileEncDec.decryptFile(encryptionAlgorithm, key, iv, fileDataContainer.getEncryptedData());

                // Remove the old download link if it exists
                if (downloadLink != null) {
                    mainContainer.remove(downloadLink);
                }

                // Save the decrypted data to a file
                String decryptedFileName = fileDataContainer.getFileName().substring(0, fileDataContainer.getFileName().length() - 4);
                File decryptedFile = new File(decryptedFileName);
                FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile);
                fileOutputStream.write(decryptedData);
                fileOutputStream.close();

                // Create and add the new download link
                downloadLink = downloadLink(decryptedFileName);
                mainContainer.add(downloadLink);

                // Clear the password field
                password.clear();

                // Show a notification
                notify("File decrypted successfully", 3000, "success");

            } catch (NoSuchAlgorithmException |
                     InvalidKeySpecException | InvalidAlgorithmParameterException
                     | NoSuchPaddingException
                     | IllegalBlockSizeException | BadPaddingException
                     | InvalidKeyException | IOException ex) {
                // Show a notification for decryption errors
                notify("Error decrypting the file. Please check your password and try again.", 3000, "error");
                throw new RuntimeException(ex);
            } finally {
                // Reset the flags for the next decryption attempt
                isEncryptedFileUploaded = false;
                isIvFileUploaded = false;
                isSaltFileUploaded = false;
            }
        });

        // Add components to the mainContainer
        mainContainer.add(titlesContainer, encryptedFileUpload, ivUpload, saltUpload, password, keyModeContainer, decryptButton);

        // Add the mainContainer to the screen
        add(mainContainer);
    }

    private void setUploadStyle(Upload upload) {
        upload.setWidthFull();
        upload.getElement().getThemeList().add("primary");
    }

    private Anchor downloadLink(String decryptedFileName) {
        Anchor link = new Anchor(new StreamResource(decryptedFileName, () -> new ByteArrayInputStream(decryptedData)), "Download Decrypted File");
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

    /**
     * Shows a notification message.
     *
     * @param msg      The message to show.
     * @param duration The duration of the notification.
     * @param type     The type of the notification.
     */
    public void notify(String msg, int duration, String type) {
        Notification notification = new Notification(msg, duration, Notification.Position.TOP_CENTER);

        // Check for valid theme variants
        if ("success".equalsIgnoreCase(type)) {
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else if ("error".equalsIgnoreCase(type)) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else if ("warning".equalsIgnoreCase(type)) {
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        } else if ("primary".equalsIgnoreCase(type)) {
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        } else {
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }

        notification.open();
    }

    private Upload generateUploadComponent(MemoryBuffer buffer, Span label) {
        Upload upload = new Upload(buffer);
        upload.setDropLabel(label);
        upload.setAcceptedFileTypes(".enc");
        setUploadStyle(upload);
        return upload;
    }

}
