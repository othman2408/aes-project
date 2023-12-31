package assets.views.filedecryption;

import assets.AES.AESFileEncDec;
import assets.views.sharedComponents.Notify;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
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

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * This class represents the view for file decryption functionality.
 * It allows the user to upload an encrypted file, an initialization vector (IV)
 * file, and a salt file,
 * and then decrypts the encrypted file using the uploaded files and a password.
 * The decrypted file can then be downloaded.
 */
@PageTitle("File Decryption")
@Route(value = "file-decryption", layout = MainLayout.class)
@Uses(Icon.class)
public class FileDecryptionView extends HorizontalLayout {

    private Anchor downloadLink;
    private byte[] decryptedData;
    private boolean isEncryptedFileUploaded = false;
    private boolean isIvFileUploaded = false;
    private boolean isSaltFileUploaded = false;
    private boolean isIvRequired = true;

    /**
     * Constructs a new instance of the FileDecryptionView class.
     * Initializes the UI components and sets up event listeners.
     */
    public FileDecryptionView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();

        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setAlignItems(Alignment.CENTER);
        mainContainer.setWidth("50%");
        mainContainer.getStyle().set("gap", "1rem");

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

        MemoryBuffer encryptedFileBuffer = new MemoryBuffer();
        Upload encryptedFileUpload = generateUploadComponent(encryptedFileBuffer, new Span("Upload Encrypted File"));

        MemoryBuffer ivBuffer = new MemoryBuffer();
        Upload ivUpload = generateUploadComponent(ivBuffer, new Span("Upload Initialization Vector (IV) File"));

        MemoryBuffer saltBuffer = new MemoryBuffer();
        Upload saltUpload = generateUploadComponent(saltBuffer, new Span("Upload Salt File"));

        PasswordField password = new PasswordField();
        password.setLabel("Password");
        password.setPlaceholder("Enter your password");
        password.setHelperText("Enter your password");
        password.setRequired(true);
        password.setRequiredIndicatorVisible(true);
        password.getStyle().set("width", "100%");

        Div keyModeContainer = new Div();
        keyModeContainer.getStyle()
                .set("display", "flex")
                .set("width", "100%")
                .set("flex-direction", "row")
                .set("gap", ".5rem");

        Select<Integer> keySize = new Select<>();
        keySize.setItems(128, 192, 256);
        keySize.setItemLabelGenerator(item -> item + " bit");
        keySize.setValue(128);
        keySize.getStyle().set("width", "100%");
        keySize.setLabel("Key Size");
        keySize.setHelperText("Select the key size");

        Select<String> decryptionMode = new Select<>();
        decryptionMode.setItems("CBC", "ECB");
        decryptionMode.setValue("CBC");
        decryptionMode.getStyle().set("width", "100%");
        decryptionMode.setLabel("Decryption Mode");
        decryptionMode.setHelperText("Select the decryption mode");

        keyModeContainer.add(keySize, decryptionMode);

        decryptionMode.addValueChangeListener(e -> {
            isIvRequired = e.getValue().equals("CBC");
            ivUpload.setVisible(isIvRequired);
        });

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

        FileDataContainer fileDataContainer = new FileDataContainer();

        encryptedFileUpload.addSucceededListener(event -> {
            try {
                fileDataContainer.setEncryptedData(encryptedFileBuffer.getInputStream().readAllBytes());
                isEncryptedFileUploaded = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fileDataContainer.setFileName(event.getFileName());
        });

        ivUpload.addSucceededListener(event -> {
            try {
                fileDataContainer.setIv(ivBuffer.getInputStream().readAllBytes());
                isIvFileUploaded = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        saltUpload.addSucceededListener(event -> {
            try {
                fileDataContainer.setSalt(saltBuffer.getInputStream().readAllBytes());
                isSaltFileUploaded = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button decryptButton = new Button("Decrypt");
        decryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%")
                .set("cursor", "pointer");

        decryptButton.addClickListener(e -> {
            if (!isEncryptedFileUploaded || (!isIvFileUploaded && isIvRequired) || !isSaltFileUploaded || password.getValue().isEmpty()) {
                Notify.notify("Please upload all required files and enter your password", 3000, "error");
                return;
            }

            try {
                String decryptionAlgorithm = "AES/" + decryptionMode.getValue() + "/PKCS5Padding";

                SecretKey key = AESFileEncDec.getKeyFromPassword(password.getValue(), fileDataContainer.getSalt(),
                        keySize.getValue());

                IvParameterSpec iv = null;
                if (isIvRequired) {
                    iv = new IvParameterSpec(fileDataContainer.getIv());
                }

                decryptedData = AESFileEncDec.decryptFile(decryptionAlgorithm, key, iv,
                        fileDataContainer.getEncryptedData());

                if (downloadLink != null) {
                    mainContainer.remove(downloadLink);
                }

                String decryptedFileName = fileDataContainer.getFileName().substring(0,
                        fileDataContainer.getFileName().length() - 4);
                File decryptedFile = new File(decryptedFileName);
                FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile);
                fileOutputStream.write(decryptedData);
                fileOutputStream.close();

                downloadLink = downloadLink(decryptedFileName);
                mainContainer.add(downloadLink);

                password.clear();

                Notify.notify("File decrypted successfully", 3000, "success");

            } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException
                     | NoSuchPaddingException
                     | IllegalBlockSizeException | BadPaddingException
                     | InvalidKeyException | IOException ex) {
                Notify.notify("Error while decrypting the file", 3000, "error");
                throw new RuntimeException(ex);
            } finally {
                isEncryptedFileUploaded = false;
                isIvFileUploaded = false;
                isSaltFileUploaded = false;
            }
        });

        mainContainer.add(titlesContainer, encryptedFileUpload, ivUpload, saltUpload, password, keyModeContainer,
                decryptButton);

        add(mainContainer);
    }

    /**
     * Sets the style for the upload component.
     *
     * @param upload The upload component to set the style for.
     */
    private void setUploadStyle(Upload upload) {
        upload.setWidthFull();
        upload.getElement().getThemeList().add("primary");
    }

    /**
     * Generates an anchor element for downloading the decrypted file.
     *
     * @param decryptedFileName The name of the decrypted file.
     * @return The anchor element for downloading the decrypted file.
     */
    private Anchor downloadLink(String decryptedFileName) {
        Anchor link = new Anchor(new StreamResource(decryptedFileName, () -> new ByteArrayInputStream(decryptedData)),
                "Download Decrypted File");
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
     * Generates an upload component with the specified buffer and label.
     *
     * @param buffer The buffer to use for the upload component.
     * @param label  The label to use for the upload component.
     * @return The generated upload component.
     */
    private Upload generateUploadComponent(MemoryBuffer buffer, Span label) {
        Upload upload = new Upload(buffer);
        upload.setDropLabel(label);
        upload.setAcceptedFileTypes(".enc");
        setUploadStyle(upload);
        return upload;
    }
}
