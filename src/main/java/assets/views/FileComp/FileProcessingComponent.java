package assets.views.FileComp;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import java.io.*;
import java.util.*;

public class FileProcessingComponent extends VerticalLayout {

    private final Upload uploadField;
    private final Span errorField;
    public final File uploadFolder;
    public final List<File> downloadedFiles;
    private Set<String> sessionDownloadedFiles;

    public FileProcessingComponent() {
        uploadFolder = getUploadFolder();
        sessionDownloadedFiles = getSessionDownloadedFiles();
        downloadedFiles = new ArrayList<>();
        uploadField = new Upload(createFileReceiver(uploadFolder));
        uploadField.setMaxFiles(100);
        uploadField.setMaxFileSize(1 * 1024 * 1024);

        errorField = new Span();
        errorField.setVisible(false);
        errorField.getStyle().set("color", "red");

        uploadField.addFailedListener(e -> showErrorMessage(e.getReason().getMessage()));
        uploadField.addFileRejectedListener(e -> showErrorMessage(e.getErrorMessage()));
        uploadField.setWidthFull();

        setPadding(false);
        add(uploadField, errorField, createDownloadLinksArea());
    }

    private Receiver createFileReceiver(File uploadFolder) {
        return (MultiFileReceiver) (filename, mimeType) -> {
            File file = new File(uploadFolder, filename);
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return null;
            }
        };
    }

    private void showErrorMessage(String message) {
        errorField.setVisible(true);
        errorField.setText(message);
    }

    private File getUploadFolder() {
        File folder = new File("uploaded-files");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    private Div createDownloadLinksArea() {
        Div downloadLinksArea = new Div();
        downloadLinksArea.getStyle()
                .set("background-color", "rgb(231 235 238 / 70%)")
                .set("width", "-webkit-fill-available")
                .setPadding("1rem")
                .set("border-radius", "5px")
                .set("display", "flex")
                .set("flex-direction", "column");

        H5 header = new H5("Encrypted/Decrypted Files:");
        downloadLinksArea.add(header);

        Upload uploadField = getUploadField();
        uploadField.addSucceededListener(e -> {
            hideErrorField();
            refreshFileLinks(downloadLinksArea);
        });
        downloadLinksArea.add(uploadField);

        refreshFileLinks(downloadLinksArea);

        return downloadLinksArea;
    }

    private Upload getUploadField() {
        return new Upload(createFileReceiver(uploadFolder));
    }

    private void hideErrorField() {
        errorField.setVisible(false);
    }

    private void refreshFileLinks(Div downloadLinksArea) {
        downloadLinksArea.removeAll();

        H5 header = new H5("Encrypted/Decrypted Files:");
        downloadLinksArea.add(header);

        for (File file : Objects.requireNonNull(uploadFolder.listFiles())) {
            if (!sessionDownloadedFiles.contains(file.getName())) {
                addLinkToFile(downloadLinksArea, file);
            }
        }
    }

    private void addLinkToFile(Div downloadLinksArea, File file) {
        StreamResource streamResource = new StreamResource(file.getName(), () -> getStream(file));
        Anchor link = new Anchor(streamResource, String.format("%s (%d KB)", file.getName(),
                (int) file.length() / 1024));
        link.getElement().setAttribute("download", true);
        link.getElement().setAttribute("onclick", "markAsDownloaded('" + file.getName() + "')");
        downloadLinksArea.add(link);
    }

    private InputStream getStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Set<String> getSessionDownloadedFiles() {
        Set<String> downloadedFiles = (Set<String>) VaadinSession.getCurrent().getAttribute("downloadedFiles");
        return downloadedFiles != null ? downloadedFiles : new HashSet<>();
    }

    private void setSessionDownloadedFiles(Set<String> downloadedFiles) {
        VaadinSession.getCurrent().setAttribute("downloadedFiles", downloadedFiles);
    }

    private void markAsDownloaded(String fileName) {
        sessionDownloadedFiles.add(fileName);
        setSessionDownloadedFiles(sessionDownloadedFiles);
    }

//    private void encryptFile(File inputFile, File outputFile) {
//        try {
//            AESFilesEncDec.encryptFile("AES/CBC/PKCS5Padding", encryptionPassword, inputFile, outputFile);
//        } catch (IOException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
//                 NoSuchPaddingException | InvalidKeyException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void decryptFile(File inputFile, File outputFile) {
//        try {
//            AESFilesEncDec.decryptFile("AES/CBC/PKCS5Padding", encryptionPassword, inputFile, outputFile);
//        } catch (IOException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
//                 NoSuchPaddingException | InvalidKeyException e) {
//            e.printStackTrace();
//        }
//    }
}
