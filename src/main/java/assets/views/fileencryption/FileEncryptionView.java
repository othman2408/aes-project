package assets.views.fileencryption;

import assets.AES.AESFileEncDec;
import assets.views.sharedComponents.Notify;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
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

    // Download links for the encrypted file, IV, and salt
    private Div downloadLinksContainer;
    private Anchor downloadLink;
    private Anchor downloadIvLink;
    private Anchor downloadSaltLink;

    // Byte arrays to store the encrypted data, salt, and IV
    private byte[] encryptedData;
    private byte[] salt;
    private byte[] iv;

    /**
     * Constructor for the FileEncryptionView class.
     * Sets up the UI components for the file encryption view.
     */
    public FileEncryptionView() {
        // Set the alignment and size of the main container
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();

        // Create the main container
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setAlignItems(Alignment.CENTER);
        mainContainer.setWidth("50%");
        mainContainer.getStyle().set("gap", "1rem");

        // Create the title container
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

        // Create the upload container
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

        // Create the password field
        PasswordField password = new PasswordField();
        password.setLabel("Password");
        password.setPlaceholder("Enter your password");
        password.setHelperText("Enter your password");
        password.setRequired(true);
        password.setRequiredIndicatorVisible(true);
        password.getStyle().set("width", "100%");

        // Create the key and mode options container
        Div keyModeContainer = new Div();
        keyModeContainer.getStyle()
                .set("display", "flex")
                .set("width", "100%")
                .set("flex-direction", "row")
                .set("gap", ".5rem");

        // Create the key size options
        Select<Integer> keySize = new Select<>();
        keySize.setItems(128, 192, 256);
        // add bit to the end of the item
        keySize.setItemLabelGenerator(item -> item + " bit");
        keySize.setValue(128);
        keySize.getStyle().set("width", "100%");
        keySize.setLabel("Key Size");
        keySize.setHelperText("Select the key size");

        // Create the encryption mode options
        Select<String> encryptionMode = new Select<>();
        encryptionMode.setItems("CBC", "ECB");
        encryptionMode.setValue("CBC");
        encryptionMode.getStyle().set("width", "100%");
        encryptionMode.setLabel("Encryption Mode");
        encryptionMode.setHelperText("Select the encryption mode");

        // Add the key size and encryption mode options to the key and mode options
        // container
        keyModeContainer.add(keySize, encryptionMode);

        /**
         * The FileDataContainer class is a Java class that holds the data and file name
         * of a file.
         */
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

        // Create the file upload component
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

        // Create the encrypt button
        Button encryptButton = new Button("Encrypt");
        encryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%")
                .set("cursor", "pointer");

        // Add a click listener to the encrypt button
        encryptButton.addClickListener(e -> {
            byte[] uploadedFileData = fileDataContainer.getData();
            String uploadedFileName = fileDataContainer.getFileName();

            if (uploadedFileName != null && uploadedFileData != null) {
                try {
                    String selectedMode = encryptionMode.getValue();
                    int selectedKeySize = keySize.getValue();
                    String encryptionAlgorithm = "AES/" + selectedMode + "/PKCS5Padding";

                    // Remove existing download links and containers if they exist
                    if (downloadLinksContainer != null && mainContainer.getChildren().anyMatch(component -> component.equals(downloadLinksContainer))) {
                        mainContainer.remove(downloadLinksContainer);
                        downloadLinksContainer.getElement().removeFromParent();
                        downloadLinksContainer = null;
                    }

                    salt = AESFileEncDec.generateSalt();
                    SecretKey key = AESFileEncDec.getKeyFromPassword(password.getValue(), salt, selectedKeySize);

                    IvParameterSpec ivSpec = null;
                    if (selectedMode.equals("CBC")) {
                        ivSpec = AESFileEncDec.generateIv();
                        iv = ivSpec.getIV();

                        String ivFileName = uploadedFileName + "_iv.enc";
                        File ivFile = new File(ivFileName);
                        try (FileOutputStream ivOutput = new FileOutputStream(ivFile)) {
                            ivOutput.write(iv);
                        }

                        downloadIvLink = downloadLink(ivFileName);
                    }

                    String encryptedFileName = uploadedFileName + ".enc";
                    File encryptedFile = new File(encryptedFileName);
                    encryptedData = AESFileEncDec.encryptFile(encryptionAlgorithm, key, ivSpec, uploadedFileData);
                    try (FileOutputStream output = new FileOutputStream(encryptedFile)) {
                        output.write(encryptedData);
                    }

                    String saltFileName = uploadedFileName + "_salt.enc";
                    File saltFile = new File(saltFileName);
                    try (FileOutputStream saltOutput = new FileOutputStream(saltFile)) {
                        saltOutput.write(salt);
                    }

                    downloadLink = downloadLink(encryptedFileName);
                    downloadSaltLink = downloadLink(saltFileName);

                    // Create and add the new download links container
                    downloadLinksContainer = DownloadLinksContainer(downloadLink, downloadSaltLink);
                    if (selectedMode.equals("CBC")) {
                        downloadLinksContainer.add(downloadIvLink); // Add the IV link to the container
                    }
                    mainContainer.add(downloadLinksContainer);

                    Notify.notify("File encrypted successfully", 3000, "success");

                } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException |
                         InvalidKeyException | InvalidAlgorithmParameterException |
                         BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException exception) {
                    exception.printStackTrace();
                }
            } else {
                Notify.notify("Please upload a file to encrypt", 3000, "error");
            }
        });




        // Add the components to the main container
        mainContainer.add(titlesContainer, singleFileUpload, password, keyModeContainer, encryptButton);

        // Add the main container to the screen
        add(mainContainer);
    }

    /**
     * Creates a download link for a file with the given file name.
     *
     * @param fileName The name of the file to create a download link for.
     * @return An Anchor component that can be clicked to download the file.
     */
    private Anchor downloadLink(String fileName) {
        Anchor link = new Anchor(new StreamResource(fileName, () -> {
            try {
                return new ByteArrayInputStream(Files.readAllBytes(Paths.get(fileName)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), "- " + fileName);

        link.getElement().setAttribute("download", true);
        return link;
    }

    private Div DownloadLinksContainer(Anchor downloadLink, Anchor downloadSaltLink) {
        Div downloadLinksContainer = new Div();

        H5 downloadLinksHeader = new H5("Download Encrypted Files: ");
        downloadLinksHeader.getStyle().set("margin-bottom", ".5rem").set("text-decoration", "underline");

        downloadLinksContainer.getStyle().set("flex-direction", "column")
                .set("display", "flex")
                .set("gap", ".5rem")
                .set("width", "-webkit-fill-available")
                .set("margin-top", "1rem")
                .set("margin-bottom", "1rem")
                .set("background", "#e8ebef")
                .set("padding", "1rem")
                .set("border-radius", "5px")
                .set("user-select", "none");

        downloadLinksContainer.add(downloadLinksHeader, downloadLink, downloadSaltLink);

        return downloadLinksContainer;

    }

    private void removeGeneratedFiles(String uploadedFileName, Div downloadLinksContainer, VerticalLayout mainContainer) {
        if (downloadLink != null && mainContainer.getChildren().anyMatch(component -> component.equals(downloadLink))) {
            mainContainer.remove(downloadLink);
            downloadLink.getElement().removeFromParent();
            downloadLink = null;
        }
        if (downloadIvLink != null && mainContainer.getChildren().anyMatch(component -> component.equals(downloadIvLink))) {
            mainContainer.remove(downloadIvLink);
            downloadIvLink.getElement().removeFromParent();
            downloadIvLink = null;
        }
        if (downloadSaltLink != null && mainContainer.getChildren().anyMatch(component -> component.equals(downloadSaltLink))) {
            mainContainer.remove(downloadSaltLink);
            downloadSaltLink.getElement().removeFromParent();
            downloadSaltLink = null;
        }

        // Remove previously generated files
        removeFileIfExists(uploadedFileName + ".enc");
        removeFileIfExists(uploadedFileName + "_iv.enc");
        removeFileIfExists(uploadedFileName + "_salt.enc");
    }

    private void removeFileIfExists(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete(); // Delete the file from the file system
        }
    }



}