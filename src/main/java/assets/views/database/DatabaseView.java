package assets.views.database;

import assets.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

@PageTitle("data-base")
@Route(value = "db", layout = MainLayout.class)
@Uses(Icon.class)
public class DatabaseView extends Composite<VerticalLayout> {

    public DatabaseView() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.getStyle().set("min-height", "100vh");

        H3 h3 = new H3("Register Form | Database View");

        // Form
        FormLayout formLayout2Col = new FormLayout();
        TextField fname = new TextField("First Name");
        TextField lname = new TextField("Last Name");
        EmailField email = new EmailField("Email");
        PasswordField pass = new PasswordField("Password field");
        pass.setWidth("min-content");
        DatePicker date = new DatePicker("Birthday");
        TextField phone = new TextField("Phone Number");
        TextField job = new TextField("Occupation");
        formLayout2Col.add(fname, lname, email, pass, date, phone, job);
        formLayout2Col.setWidth("100%");

        // Buttons
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonPrimary = new Button("Submit");
        Button buttonSecondary = new Button("Clear");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary.setWidth("min-content");
        buttonSecondary.setWidth("min-content");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow.add(buttonPrimary, buttonSecondary);

        // Layout
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layout.setWidth("100%");
        layout.setMaxWidth("800px");
        layout.setHeight("min-content");
        h3.setWidth("100%");
        layout.add(h3, formLayout2Col, layoutRow);
        getContent().add(layout);

        handleSubmit(buttonPrimary);
    }

    private boolean handleSubmit(Button submitButton) {
        submitButton.addClickListener(e -> {
            Notification.show("Submitted!", 3000, Notification.Position.TOP_CENTER);
        });

        return true;
    }


}
