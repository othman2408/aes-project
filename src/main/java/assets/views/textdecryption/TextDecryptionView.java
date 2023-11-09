package assets.views.textdecryption;

import assets.AES.AESTextDecryption;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import assets.views.MainLayout;

/**
 * This class represents the UI view for text decryption & functionality.
 */
@PageTitle("Text Decryption")
@Route(value = "text-decryption", layout = MainLayout.class)
public class TextDecryptionView extends HorizontalLayout {
    private TextArea encryptedTextArea;
    private PasswordField passwordField;
    private Select<Integer> keySize;
    private Select<String> encryptionMode;
    private TextArea result;

    /**
     * Constructor for the TextDecryptionView class.
     */
    public TextDecryptionView() {
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
        H1 title = new H1("Text Decryption");
        H3 subtitle = new H3("Decrypt an encrypted text");
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

        encryptedTextArea = createEncryptedTextArea();
        Div optionsContainer = createOptionsContainer();

        Button decryptButton = decryptButton();

        actionContainer.add(encryptedTextArea, optionsContainer, decryptButton);

        return actionContainer;
    }

    /**
     * Creates the text area for the encrypted text input.
     *
     * @return The text area for the encrypted text input.
     */
    private TextArea createEncryptedTextArea() {
        TextArea encryptedTextArea = new TextArea();
        encryptedTextArea.getStyle().set("width", "100%");
        encryptedTextArea.setPlaceholder("Enter the encrypted text");
        encryptedTextArea.setLabel("Encrypted Text");
        encryptedTextArea.setClearButtonVisible(true);
        encryptedTextArea.setRequired(true);
        return encryptedTextArea;
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

        passwordField = createPasswordField();
        Div modeKeyContainer = createModeKeyContainer();

        optionsContainer.add(passwordField, modeKeyContainer);

        return optionsContainer;
    }

    /**
     * Creates the password field for the secret key.
     *
     * @return The password field for the secret key.
     */
    private PasswordField createPasswordField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setLabel("Secret Key");
        passwordField.setHelperText("Enter your secret key");
        passwordField.setClearButtonVisible(true);
        passwordField.setRequired(true);
        return passwordField;
    }

    /**
     * Creates the container for the key size and encryption mode options.
     *
     * @return The container for the key size and encryption mode options.
     */
    private Div createModeKeyContainer() {
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
        return modeKeyContainer;
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
     * Creates the button for the decryption action.
     *
     * @return The button for the decryption action.
     */
    private Button decryptButton() {
        Button decryptButton = new Button("Decrypt");
        decryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%")
                .set("cursor", "pointer");

        decryptButton.addClickListener(e -> {
            if (isValidInput(encryptedTextArea)) {
                decryptText(encryptedTextArea);

            } else {
                notify("Please fill all the fields", 4000, "LUMO_WARNING");
            }
        });

        return decryptButton;
    }

    /**
     * Checks if the input is valid.
     *
     * @param encryptedTextArea The text area for the encrypted text input.
     * @return True if the input is valid, false otherwise.
     */
    private boolean isValidInput(TextArea encryptedTextArea) {
        return !encryptedTextArea.isEmpty();
    }

    /**
     * Decrypts the encrypted text input.
     *
     * @param encryptedTextArea The text area for the encrypted text input.
     */
    private void decryptText(TextArea encryptedTextArea) {
        try {
            String decryptedText = AESTextDecryption.decrypt(
                    encryptedTextArea.getValue(),
                    passwordField.getValue(),
                    keySize.getValue(),
                    encryptionMode.getValue());
            result.setValue(decryptedText);
            notify("Text decrypted successfully", 4000, "LUMO_SUCCESS");
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
        notification.addThemeVariants(NotificationVariant.valueOf(type));
        notification.open();
    }

    /**
     * Creates the container for the result of the decryption.
     *
     * @return The container for the result of the decryption.
     */
    private Div createResultContainer() {
        Div resultContainer = new Div();
        resultContainer.getStyle()
                .set("flex", "1")
                .set("width", "80%")
                .set("display", "flex")
                .set("flex-direction", "column");

        result = new TextArea();
        result.setLabel("Decrypted Text");
        result.getStyle().set("min-height", "8rem");
        result.getStyle().set("flex", "1");

        resultContainer.add(result);
        return resultContainer;
    }
}
