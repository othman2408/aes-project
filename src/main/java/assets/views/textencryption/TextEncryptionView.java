package assets.views.textencryption;

import assets.AES.AESTextEncryption;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
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

/**
 * This class represents the UI view for text encryption & functionality.
 */
@PageTitle("Text Encryption")
@Route(value = "text-encryption", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class TextEncryptionView extends HorizontalLayout {
    private PasswordField passwordField;
    private Select<Integer> keySize;
    private Select<String> encryptionMode;
    private TextArea result;

    /**
     * Constructor for the TextEncryptionView class.
     */
    public TextEncryptionView() {
        initializeLayout();
        add(createMainContainer());
    }

    /**
     * Initializes the layout of the view.
     */
    private void initializeLayout() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();
    }

    /**
     * Creates the main container for the view.
     * 
     * @return The main container for the view.
     */
    private VerticalLayout createMainContainer() {
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setAlignItems(Alignment.CENTER);
        mainContainer.setWidth("50%");
        mainContainer.getStyle().set("padding", "0");

        mainContainer.add(createTitlesContainer(), createActionContainer(), createResultContainer());

        return mainContainer;
    }

    /**
     * Creates the container for the titles of the view.
     * 
     * @return The container for the titles of the view.
     */
    private Div createTitlesContainer() {
        Div titlesContainer = new Div();
        H1 title = new H1("Text Encryption");
        H3 subtitle = new H3("Enter a text to encrypt");
        subtitle.getStyle()
                .set("font-weight", "normal")
                .set("font-size", "1.3rem")
                .set("margin-top", ".5rem")
                .set("text-align", "center");

        titlesContainer.add(title, subtitle);
        return titlesContainer;
    }

    /**
     * Creates the container for the actions of the view.
     * 
     * @return The container for the actions of the view.
     */
    private Div createActionContainer() {
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

        TextArea plainTextArea = createPlainTextTextArea();
        Div optionsContainer = createOptionsContainer();

        Button encryptButton = encryptButton(plainTextArea);

        actionContainer.add(plainTextArea, optionsContainer, encryptButton);

        return actionContainer;
    }

    /**
     * Creates the text area for the plain text input.
     * 
     * @return The text area for the plain text input.
     */
    private TextArea createPlainTextTextArea() {
        TextArea plainTextArea = new TextArea();
        plainTextArea.getStyle().set("width", "100%");
        plainTextArea.setPlaceholder("Enter a text to encrypt");
        plainTextArea.setLabel("Plain Text");
        plainTextArea.setClearButtonVisible(true);
        plainTextArea.setRequired(true);
        return plainTextArea;
    }

    /**
     * Creates the container for the options of the view.
     * 
     * @return The container for the options of the view.
     */
    private Div createOptionsContainer() {
        Div optionsContainer = new Div();
        optionsContainer.getStyle()
                .set("width", "100%")
                .set("display", "flex")
                .set("flex-direction", "column");

        passwordField = new PasswordField();
        passwordField.setLabel("Secret Key");
        passwordField.setHelperText("Enter your secret key");
        passwordField.setRequired(true);

        Div modeKeyContainer = new Div();
        modeKeyContainer.getStyle()
                .set("display", "flex")
                .set("width", "100%")
                .set("flex-direction", "row")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", ".5rem");

        keySize = keySizeSelect();
        encryptionMode = encryptionModeSelect();

        modeKeyContainer.add(keySize, encryptionMode);
        optionsContainer.add(passwordField, modeKeyContainer);

        return optionsContainer;
    }

    /**
     * Creates the select for the key size option.
     * 
     * @return The select for the key size option.
     */
    private Select<Integer> keySizeSelect() {
        Select<Integer> keySize = new Select<>();
        keySize.setItems(128, 192, 256);
        keySize.setValue(128);
        keySize.getStyle().set("width", "100%");
        keySize.setLabel("Key Size");
        keySize.setHelperText("Select the key size");
        return keySize;
    }

    /**
     * Creates the select for the encryption mode option.
     * 
     * @return The select for the encryption mode option.
     */
    private Select<String> encryptionModeSelect() {
        Select<String> encryptionMode = new Select<>();
        encryptionMode.setItems("CBC", "ECB");
        encryptionMode.setValue("CBC");
        encryptionMode.getStyle().set("width", "100%");
        encryptionMode.setLabel("Encryption Mode");
        encryptionMode.setHelperText("Select the encryption mode");
        return encryptionMode;
    }

    /**
     * Creates the button for the encryption action.
     * 
     * @param plainTextArea The text area for the plain text input.
     * @return The button for the encryption action.
     */
    private Button encryptButton(TextArea plainTextArea) {
        Button encryptButton = new Button("Encrypt");
        encryptButton.getStyle()
                .set("background-color", "#1E90FF")
                .set("color", "white")
                .set("width", "100%")
                .set("cursor", "pointer");
        encryptButton.addClickShortcut(Key.ENTER);

        encryptButton.addClickListener(e -> {
            if (isValidInput(plainTextArea)) {
                encryptText(plainTextArea);
            } else {
                notify("Please enter a text to encrypt", 4000, "LUMO_WARNING");
            }
        });

        return encryptButton;
    }

    /**
     * Checks if the input is valid.
     * 
     * @param plainTextArea The text area for the plain text input.
     * @return True if the input is valid, false otherwise.
     */
    private boolean isValidInput(TextArea plainTextArea) {
        return !plainTextArea.isEmpty();
    }

    /**
     * Encrypts the plain text input.
     * 
     * @param plainTextArea The text area for the plain text input.
     */
    private void encryptText(TextArea plainTextArea) {
        try {
            String encryptedText = AESTextEncryption.encrypt(
                    plainTextArea.getValue(),
                    passwordField.getValue(),
                    keySize.getValue(),
                    encryptionMode.getValue());
            result.setValue(encryptedText);
            notify("Text encrypted successfully", 4000, "LUMO_SUCCESS");
        } catch (Exception exception) {
            notify(exception.getMessage(), 4000, "LUMO_ERROR");
            exception.getCause();
        }
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

    /**
     * Creates the container for the result of the encryption.
     * 
     * @return The container for the result of the encryption.
     */
    private Div createResultContainer() {
        Div resultContainer = new Div();
        resultContainer.getStyle()
                .set("flex", "1")
                .set("width", "80%")
                .set("display", "flex")
                .set("flex-direction", "column");

        result = new TextArea();
        result.addClassName("text-encryption-view-text-area-1");
        result.setLabel("Encrypted Text");
        result.getStyle().set("min-height", "8rem");
        result.getStyle().set("flex", "1");

        resultContainer.add(result);
        return resultContainer;
    }
}
