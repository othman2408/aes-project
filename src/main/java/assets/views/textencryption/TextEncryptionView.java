package assets.views.textencryption;

import assets.AES.AESTextEncDec;
import assets.views.MainLayout;
import assets.views.sharedComponents.Notify;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

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
        setMinHeight("100%");
        getStyle().set("margin-top", "3rem");
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
        mainContainer.getStyle().set("padding", "1rem 0").set("max-height", "100vh");


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
                .set("padding-top", ".8rem")
                .set("text-align", "center")
                .set("margin-bottom", "2rem");

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
                Notify.notify("Please enter a valid input", 4000, "LUMO_ERROR");
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

            // Validation
            if (passwordField.isEmpty()) {
                Notify.notify("Please enter a secret key", 4000, "error");
                return;
            }

            // Encrypt the plain text input
            // Ignore IV if ECB selected

            String encryptedText = AESTextEncDec.encrypt(plainTextArea.getValue(), passwordField.getValue(), keySize.getValue(), encryptionMode.getValue());
            result.setValue(encryptedText);

            // Store IV and Salt
            String ivString  = AESTextEncDec.getIVString();
            String saltString = AESTextEncDec.getSaltString();

            // Save them in local storage
            WebStorage.setItem("ivString", ivString);
            WebStorage.setItem("saltString", saltString);


            // Notify the user that the encryption was successful
            Notify.notify("Text encrypted successfully", 4000, "success");

        } catch (Exception exception) {
            Notify.notify(exception.getMessage(),  4000, "error");

        }
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
        result.getStyle().set("flex", "1").set("padding-bottom", "2rem");

        resultContainer.add(result);
        return resultContainer;
    }
}
