package othmanalibrahim.views.textencryption;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import othmanalibrahim.views.MainLayout;

@PageTitle("Text Encryption")
@Route(value = "text-encryption", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class TextEncryptionView extends HorizontalLayout {

    public TextEncryptionView() {


        // Create UI

        //Place the mainContainer in the center of the screen
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();



        //Main Container
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setAlignItems(Alignment.CENTER); // Align items to the center
        mainContainer.setWidth("50%"); // Set width to 50%
        mainContainer.setHeight("70%"); // Set height to 50%
//        mainContainer.getStyle().set("background-color", "gray"); // Set blue background color
        mainContainer.getStyle().set("gap", "2rem"); // Add padding for gap


        // Title Container
        Div titlesContainer = new Div();
        H1 title = new H1("Text Encryption");
        H3 subtitle = new H3("Enter a text to encrypt");
        subtitle.getStyle().set("text-align", "center");
        titlesContainer.add(title, subtitle);

        // Action Container
        Div actionContainer = new Div();
        actionContainer.getStyle()
                .set("width", "50%").set("height", "30%")
                .set("display", "flex").set("flex-direction", "column")
                .set("align-items", "center").set("justify-content", "center")
                .set("gap", ".5rem");

        TextField textField = new TextField();
        textField.getStyle().set("width", "100%");

        textField.setPlaceholder("Enter a text to encrypt");
        textField.setClearButtonVisible(true);
        textField.focus();

        Button encryptButton = new Button("Encrypt");
        encryptButton.getStyle().set("width", "100%").set("cursor", "pointer");

        // Button action
        encryptButton.addClickListener(e -> {
            Notification.show("Text encrypted");
        });

        // Enter key action
        textField.addKeyPressListener(Key.ENTER, e -> {
            Notification.show("Text encrypted");
        });


        // Add components to the action container
        actionContainer.add(textField, encryptButton);

        // Result container
        Div resultContainer = new Div();
        resultContainer.getStyle()
                .set("flex", "1") // Let it grow and take available height
                .set("width", "80%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", ".5rem");

        TextArea result = new TextArea();
        result.setReadOnly(true);
        result.setLabel("Encrypted text");


        // Make the TextArea grow and take full available height
        result.getStyle().set("flex", "1");

        // Add components to the result container
        resultContainer.add(result);

        // Add all containers to the main container
        mainContainer.add(titlesContainer, actionContainer, resultContainer);

        // Add components to the layout
        add(mainContainer);


    }

}
