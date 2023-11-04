package othmanalibrahim.views.textencryption;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

        // vertical layout
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidthFull();
        setHeightFull();

        Div mainContainer = new Div();
        mainContainer.addClassNames("display-flex", "flex-direction-column", "align-items-center", "justify-content-center");
        mainContainer.setHeight("20rem");


        // title
        H1 title = new H1("Text Encryption");

        //Div
        Div container = new Div();
        container.addClassNames("display-flex", "flex-direction-column", "align-items-center", "justify-content-center");

        //add text field
        TextField textField = new TextField();
        textField.setPlaceholder("Enter text to encrypt");
        textField.setClearButtonVisible(true);
        textField.focus();

        //add button
        Button button = new Button("Encrypt");
        button.addClickShortcut(Key.ENTER);
        button.addClickListener(click -> {
            Notification.show("Text encrypted");
        });

        container.add(textField, button);

        mainContainer.add(title, container);

        //add components to layout
        add(mainContainer);



    }

}
