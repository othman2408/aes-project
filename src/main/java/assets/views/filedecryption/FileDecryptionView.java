package assets.views.filedecryption;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import assets.views.MainLayout;

@PageTitle("File Decryption")
@Route(value = "file-decryption", layout = MainLayout.class)
@Uses(Icon.class)
public class FileDecryptionView extends Composite<VerticalLayout> {

    public FileDecryptionView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
    }
}
