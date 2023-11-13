package assets.views.database;

import assets.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import java.sql.SQLException;

import static assets.DB.DBConnector.handleRegistration;

@PageTitle("data-base")
@Route(value = "db", layout = MainLayout.class)
@Uses(Icon.class)
public class DatabaseView extends Composite<VerticalLayout> {

    public DatabaseView() {
        // Center the content
        getContent().setAlignItems(Alignment.CENTER);
        getContent().getStyle().set("min-height", "100vh");


        // tabs
        Tab formTab = new Tab("Register Form");
        Tab recordsTab = new Tab("Database Records");
        Tabs tabs = new Tabs(formTab, recordsTab);
        getContent().add(tabs);

        // Content Container
        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setJustifyContentMode(JustifyContentMode.CENTER);
        content.getStyle().set("min-height", "80vh").set("background-color", "var(--lumo-shade-10pct)").set("border-radius", "var(--lumo-border-radius-m)");
        getContent().add(content);

        // Set the default tab
        content.removeAll();
        content.add(createFormLayout());





        // on click on tab from the tabs
        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == formTab) {
                content.removeAll();
                content.add(createFormLayout());
            } else if (event.getSelectedTab() == recordsTab) {
                content.removeAll();
                content.add(new Span("Records"));
            }
        });

    }

    private VerticalLayout createFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H3 h3 = new H3("Register Form");

        // Form
        FormLayout form = createForm();
        form.setWidth("100%");

        // Buttons
        HorizontalLayout layoutRow = createButtonLayout();

        // Layout setup
        layout.setWidth("100%");
        layout.setMaxWidth("800px");
        h3.setWidth("100%");
        layout.add(h3, form, layoutRow);

        return layout;
    }

    private FormLayout createForm() {
        FormLayout form = new FormLayout();
        TextField fname = new TextField("First Name");
        TextField lname = new TextField("Last Name");
        EmailField email = new EmailField("Email");
        PasswordField pass = new PasswordField("Password field");
        pass.setWidth("min-content");
        DatePicker date = new DatePicker("Birthday");
        TextField phone = new TextField("Phone Number");
        TextField job = new TextField("Occupation");

        // Append all fields to the form
        form.add(fname, lname, email, pass, date, phone, job);

        return form;
    }

    private HorizontalLayout createButtonLayout() {
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

        return layoutRow;
    }

    private boolean handleSubmit(Button submitButton, FormLayout form) {
        submitButton.addClickListener(e -> {
            // Get values from the form
            String fname = ((TextField) form.getChildren().toArray()[0]).getValue();
            String lname = ((TextField) form.getChildren().toArray()[1]).getValue();
            String email = ((EmailField) form.getChildren().toArray()[2]).getValue();
            String pass = ((PasswordField) form.getChildren().toArray()[3]).getValue();
            String date = ((DatePicker) form.getChildren().toArray()[4]).getValue().toString().toString();
            String phone = ((TextField) form.getChildren().toArray()[5]).getValue();
            String job = ((TextField) form.getChildren().toArray()[6]).getValue();

            Notification.show(fname);
            Notification.show(lname);
            Notification.show(email);
            Notification.show(pass);
            Notification.show(date);
            Notification.show(phone);
            Notification.show(job);

            try {

                boolean register = handleRegistration(fname, lname, email, pass, date, phone, job);
                if (register) {
                    Notification.show("Registration Successful!", 3000, Notification.Position.TOP_CENTER);
                } else {
                    Notification.show("Registration Failed!", 3000, Notification.Position.TOP_CENTER);
                }
            } catch (SQLException throwable) {
                throwable.getCause();
            }

        });

        return false;
    }

}
