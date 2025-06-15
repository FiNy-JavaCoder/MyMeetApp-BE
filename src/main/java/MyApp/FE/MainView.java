package MyApp.FE;

import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.enums.GenderType;
import MyApp.BE.service.User.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("x")
public class MainView extends VerticalLayout {

    private Grid<UserProfileDTO> userGrid;

    private final UserService userService;

    public MainView(@Autowired UserService userService) {
        this.userService = userService;

        add(new H1("Vítejte v mé Vaadin aplikaci!"));

        userGrid = new Grid<>(UserProfileDTO.class, false);
        userGrid.addColumn(UserProfileDTO::getUserId).setHeader("ID").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        userGrid.addColumn(UserProfileDTO::getNickName).setHeader("Přezdívka").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        userGrid.addColumn(UserProfileDTO::getAge).setHeader("Věk").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        userGrid.addColumn(UserProfileDTO::getRegions).setHeader("Oblast").setAutoWidth(true).setTextAlign(ColumnTextAlign.CENTER);
        userGrid.setAllRowsVisible(true);
        userGrid.setWidth("auto");
        userGrid.addClassName("my-custom-grid-border");

        userGrid.asSingleSelect().addValueChangeListener(event -> {
            UserProfileDTO selectedUser = event.getValue();
            if (selectedUser != null) {
                getUI().ifPresent(ui -> ui.navigate(UserProfileView.class, selectedUser.getUserId()));
            }
        });
        loadUsers(GenderType.female);

        add(userGrid);

        Button womenButton = new Button("ženy");
        womenButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        womenButton.addClickListener(event -> {
            loadUsers(GenderType.female);
            System.out.println("Na tlačítko womenButton bylo kliknuto!");
        });
        Button menButton = new Button("muži");
        menButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        menButton.addClickListener(event -> {
            loadUsers(GenderType.male);
            System.out.println("Na tlačítko menButton bylo kliknuto!");
        });

        add(womenButton);

        add(menButton);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setAlignItems(Alignment.CENTER);
        setPadding(true);
    }

    private void loadUsers(GenderType genderType) {
        List<UserProfileDTO> users = userService.findByGender(genderType);
        userGrid.setItems(users);
    }
}