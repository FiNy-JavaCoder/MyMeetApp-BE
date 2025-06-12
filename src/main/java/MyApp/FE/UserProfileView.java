package MyApp.FE;

import MyApp.BE.dto.UserDTO;
import MyApp.BE.service.User.UserService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent; // Zůstává pro informaci, ale není přímo použit v setParameter
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.BeforeEvent; // !!! NOVÝ IMPORT - PŘESNĚ TENTO JE POTŘEBA !!!
import org.springframework.beans.factory.annotation.Autowired;

// @Route("user-profile")
@Route("user-profile")
public class UserProfileView extends VerticalLayout implements HasUrlParameter<Long> {

    private final UserService userService;
    private Long userId;

    private H2 userNameHeader;
    private Paragraph userNickName;
    private Paragraph userEmail;
    private Paragraph userGender;
    private Paragraph userAge;
    private Paragraph userRegions;

    public UserProfileView(@Autowired UserService userService) {
        this.userService = userService;
        setAlignItems(Alignment.START);
        setJustifyContentMode(JustifyContentMode.START);
        setPadding(true);

        userNameHeader = new H2("Načítám profil...");
        userNickName = new Paragraph("Přezdívka: ");
        userEmail = new Paragraph("Email: ");
        userGender = new Paragraph("Pohlaví: ");
        userAge = new Paragraph("Věk: ");
        userRegions = new Paragraph("Regiony: ");

        add(userNameHeader, userNickName, userEmail, userGender, userAge, userRegions);
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        if (parameter != null) {
            this.userId = parameter;
            loadUserProfile(this.userId);
        } else {
            userNameHeader.setText("Chyba: Uživatel nenalezen.");
            userNickName.setText("Nelze načíst profil, protože nebylo zadáno ID uživatele.");
            throw new NotFoundException();
        }
    }

    private void loadUserProfile(Long userId) {
        UserDTO user = userService.getPerson(userId);

        if (user != null) {
            userNameHeader.setText("Profil uživatele: " + user.getNickName());
            userNickName.setText("Přezdívka: " + user.getNickName());
            userEmail.setText("Email: " + user.getEmail());
            userGender.setText("Pohlaví: " + user.getGender());
            userAge.setText("Věk: " + user.getAge());
            userRegions.setText("Regiony: " + (user.getRegions() != null ? user.getRegions().toString() : "Žádné"));
        } else {
            userNameHeader.setText("Uživatel nenalezen.");
            userNickName.setText("Uživatel s ID " + userId + " nebyl nalezen v databázi.");
            userEmail.setText("");
            userGender.setText("");
            userAge.setText("");
            userRegions.setText("");
            throw new NotFoundException();
        }
    }
}