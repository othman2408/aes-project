package assets.views.filedecryption;

import assets.views.FileComp.FileProcessingComponent;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import assets.views.MainLayout;

@PageTitle("File Decryption")
@Route(value = "file-decryption", layout = MainLayout.class)
@Uses(Icon.class)
public class FileDecryptionView extends HorizontalLayout {

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

        // File Upload


        //Key Size & Decryption mode options
        Div optionsContainer = new Div();
        optionsContainer.getStyle()
                .set("display", "flex")
                .set("width", "100%")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-direction", "column")
                .set("gap", ".5rem");

        //Password Field
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

        //Decryption mode options
        Select<String> DecryptionMode = new Select<>();
        DecryptionMode.setItems("CBC", "ECB");
        DecryptionMode.setValue("CBC");
        DecryptionMode.getStyle().set("width", "100%");
        DecryptionMode.setLabel("Decryption Mode");
        DecryptionMode.setHelperText("Select the Decryption mode");

        // Add components to the keyModeContainer
        keyModeContainer.add(keySize, DecryptionMode);

        // Add components to the optionsContainer
        optionsContainer.add(password, keyModeContainer);


        // Decrypt button
        Button decryptButton = new Button("Decrypt");
        decryptButton.getStyle().set("background-color", "#1E90FF").set("color", "white").set("width", "100%").set("cursor", "pointer");

        // Button action
        decryptButton.addClickListener(e -> {
            Notification.show("Text decrypted");
        });



        // Add components to the mainContainer
        mainContainer.add(titlesContainer, uploadContainer, optionsContainer, decryptButton);

        // Add the mainContainer to the screen
        add(mainContainer);

    }
}
