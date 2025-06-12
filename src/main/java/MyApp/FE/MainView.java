package MyApp.FE;

import MyApp.BE.dto.UserDTO;
import MyApp.BE.enums.GenderType;
import MyApp.BE.service.User.UserService;
import com.vaadin.flow.component.grid.Grid; // Nový import pro Grid!
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("x")
public class MainView extends VerticalLayout {

    // Nyní budeme používat Grid místo VerticalLayout pro seznam osob
    private Grid<UserDTO> userGrid;

    private final UserService userService;

    public MainView(@Autowired UserService userService) {
        this.userService = userService;

        add(new H1("Vítejte v mé Vaadin aplikaci!"));

        userGrid = new Grid<>(UserDTO.class, true);


        userGrid.setSizeFull();

        userGrid.asSingleSelect().addValueChangeListener(event -> {
            UserDTO selectedUser = event.getValue();
            if (selectedUser != null) {
                getUI().ifPresent(ui -> ui.navigate(UserProfileView.class, selectedUser.getUserId()));
            }
        });

        loadUsers();

        add(userGrid);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setAlignItems(Alignment.CENTER);
        setPadding(true);
    }

    private void loadUsers() {
        List<UserDTO> users = userService.findByGender(GenderType.female);
        userGrid.setItems(users);
}}