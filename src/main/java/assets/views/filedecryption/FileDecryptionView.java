package assets.views.filedecryption;

import assets.AES.AESFilesEncDec;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
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
        mainContainer.getStyle().set("gap", "2rem");

        // Title Container
        Div titlesContainer = new Div();
        H1 title = new H1("File Decryption");
        H3 subtitle = new H3("Upload a file to decrypt it");
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
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
            fileDataContainer.setFileName(event.getFileName());
        });

        // Decrypt button
        Button decryptButton = new Button("Decrypt");
        decryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%")
                .set("cursor", "pointer");

        // Button action
        decryptButton.addClickListener(e -> {
           byte[] uploadedFileData = fileDataContainer.getData();
           String uploadedFileName = fileDataContainer.getFileName();

           if(uploadedFileName != null && uploadedFileData != null) {
               try {
                   String encryptionAlgorithm = "AES/" + decryptionMode.getValue() + "/PKCS5Padding";
                   SecretKey key = AESFilesEncDec.getKeyFromPassword(password.getValue(), new byte[16], keySize.getValue());
                     IvParameterSpec iv = AESFilesEncDec.generateIv();

                        decryptedData = AESFilesEncDec.decryptFile(encryptionAlgorithm, key, iv, uploadedFileData);

                        //remove the old download link if it exists
                        if(downloadLink != null) {
                            mainContainer.remove(downloadLink);
                        }

                        //save the decrypted data to a file
                        String decryptedFileName = uploadedFileName.substring(0, uploadedFileName.length() - 4);
                        File decryptedFile = new File(decryptedFileName);
                        FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile);
                        fileOutputStream.write(decryptedData);
                        fileOutputStream.close();

                        // Create a download link
                        downloadLink = downloadLink(decryptedFileName);
                        mainContainer.add(downloadLink);

                        //clear the password field
                        password.clear();

                        //show a notification
                        notify("File decrypted successfully", 3000, "SUCCESS");

               } catch (NoSuchAlgorithmException |
                        InvalidKeySpecException | InvalidAlgorithmParameterException
                        | NoSuchPaddingException
                        | IllegalBlockSizeException | BadPaddingException
                        | InvalidKeyException | IOException
                       ex) {
                   throw new RuntimeException(ex);
               }

           }else {
                notify("Please upload a file to decrypt", 3000, "ERROR");
           }


        });

        // Add components to the mainContainer
        mainContainer.add(titlesContainer, singleFileUpload, password, keyModeContainer, decryptButton);

        // Add the mainContainer to the screen
        add(mainContainer);
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
        notification.addThemeVariants(NotificationVariant.valueOf(type));
        notification.open();
    }
}
