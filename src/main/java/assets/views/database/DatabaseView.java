package assets.views.database;

import assets.DB.DBConnector;
import assets.DB.Person;
import assets.views.MainLayout;
import assets.views.sharedComponents.Notify;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
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
import java.time.LocalDate;
import java.util.List;

import static assets.DB.DBConnector.handleRegistration;

/**
 * This class represents the view for the database page. It contains a form for
 * registering new users
 * and a grid for displaying existing users in the database.
 */
@PageTitle("Data-base")
@Route(value = "db", layout = MainLayout.class)
@Uses(Icon.class)
public class DatabaseView extends Composite<VerticalLayout> {

    /**
     * Constructs a new instance of the DatabaseView class.
     */
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
        getContent().add(content);

        // Set the default tab
        content.removeAll();
        content.add(createRegistrationFormLayout());

        // Form Layout
        VerticalLayout form = createRegistrationFormLayout();



        // on click on tab from the tabs
        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == formTab) {
                content.removeAll();
                content.add(form);
            } else if (event.getSelectedTab() == recordsTab) {
                content.removeAll();
                try {
                    content.add(DBRecords());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Creates the layout for the registration form.
     *
     * @return The layout for the registration form.
     */
    private VerticalLayout createRegistrationFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H3 h3 = new H3("Register Form");

        // Form
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
        form.setWidth("100%");

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

        // Layout setup
        layout.setWidth("100%");
        layout.setMaxWidth("800px");
        h3.setWidth("100%");
        layout.add(h3, form, layoutRow);

        // Add click event handling for the Submit button
        handleSubmit(buttonPrimary, form);

        // Add click event handling for the Clear button
        buttonSecondary.addClickListener(e -> {
            fname.clear();
            lname.clear();
            email.clear();
            pass.clear();
            date.clear();
            phone.clear();
            job.clear();
        });

        return layout;
    }


    /**
     * Retrieves the records from the database and displays them in a grid.
     *
     * @return The grid containing the records from the database.
     * @throws SQLException If an error occurs while retrieving the records from the
     *                      database.
     */
    private Grid<Person> DBRecords() throws SQLException {
        Grid<Person> mainGrid = new Grid<>(Person.class, false);

        // Set the Height of the grid to be dynamic
        mainGrid.setAllRowsVisible(true);

        // Set the data provider
        List<Person> persons = DBConnector.getPersons();

        // Add columns
        mainGrid.addColumn(Person::getFname).setHeader("First Name");
        mainGrid.addColumn(Person::getLname).setHeader("Last Name");
        mainGrid.addColumn(Person::getEmail).setHeader("Email");
        mainGrid.addColumn(Person::getPassword).setHeader("Password");
        mainGrid.addColumn(Person::getBirthdate).setHeader("Birthdate");
        mainGrid.addColumn(Person::getPhoneNO).setHeader("Phone Number");
        mainGrid.addColumn(Person::getJob).setHeader("Job");

        // Set the data
        mainGrid.setItems(persons);

        return mainGrid;
    }

    /**
     * Handles the submission of the registration form.
     *
     * @param submitButton The button that was clicked to submit the form.
     * @param form         The form that was submitted.
     */
    private void handleSubmit(Button submitButton, FormLayout form) {

        // Handle the click event
        submitButton.addClickListener(e -> {

            // Get the values from the form
            String fname = ((TextField) form.getChildren().toArray()[0]).getValue();
            String lname = ((TextField) form.getChildren().toArray()[1]).getValue();
            String email = ((EmailField) form.getChildren().toArray()[2]).getValue();
            String pass = ((PasswordField) form.getChildren().toArray()[3]).getValue();
            LocalDate date = ((DatePicker) form.getChildren().toArray()[4]).getValue();
            String phone = ((TextField) form.getChildren().toArray()[5]).getValue();
            String job = ((TextField) form.getChildren().toArray()[6]).getValue();


            if(fname.isEmpty() || lname.isEmpty() || email.isEmpty() || pass.isEmpty() || date.toString().isEmpty() || phone.isEmpty() || job.isEmpty()){
                Notify.notify("Please fill all the fields",3000, "warning");
            }else {
                // Handle the registration
                try {
                    if (handleRegistration(fname, lname, email, pass, date.toString(), phone, job)) {
                        Notify.notify("Registration successful", 3000, "success");
                    } else {
                        Notify.notify("Registration failed", 3000, "error");
                    }
                } catch (SQLException ex) {
                    Notify.notify("Registration failed", 3000, "");
                    throw new RuntimeException(ex);
                }
            }


        });

    }


}
