package MyApp.FE;

import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.UserProfileDTO;
import MyApp.BE.dto.mapper.UserMapper;
import MyApp.BE.entity.repository.IUserRepository;
import MyApp.BE.service.User.UserService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.BeforeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Route("user-profile/{}")
public class UserProfileView extends VerticalLayout implements HasUrlParameter<Long> {

    private final UserService userService;
    private Long userId;

    private H2 userNameHeader;
    private Paragraph userNickName;
    private Paragraph userGender;
    private Paragraph userAge;
    private Paragraph userRegions;

    public UserProfileView(@Autowired
                           UserService userService) {
        this.userService = userService;
        setAlignItems(Alignment.START);
        setJustifyContentMode(JustifyContentMode.START);
        setPadding(true);

        userNameHeader = new H2("Načítám profil...");
        userNickName = new Paragraph("Přezdívka: ");
        userGender = new Paragraph("Pohlaví: ");
        userAge = new Paragraph("Věk: ");
        userRegions = new Paragraph("Regiony: ");

        add(userNameHeader, userNickName, userGender, userAge, userRegions);
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
        ResponseEntity<?> responseEntity = userService.getPerson(userId);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Object body = responseEntity.getBody();
            if (body instanceof UserProfileDTO) {
                UserProfileDTO userProfileDTO = (UserProfileDTO) body; // Správné přetypování
                updateUserProfileUI(userProfileDTO);
            } else {
                displayErrorMessage("Neznámý formát odpovědi pro uživatele s ID " + userId + ".");
                throw new NotFoundException();
            }
        } else {
            Object body = responseEntity.getBody();
            if (body instanceof ErrorDTO) {
                ErrorDTO error = (ErrorDTO) body;
                displayErrorMessage("Chyba při načítání profilu: " + error.getMessage() + " (Kód: " + responseEntity.getStatusCode() + ")");
            } else {
                displayErrorMessage("Chyba při načítání profilu uživatele s ID " + userId + ". Neočekávaná odpověď serveru. (Kód: " + responseEntity.getStatusCode() + ")");
            }
            throw new NotFoundException();
        }
    }

    private void updateUserProfileUI(UserProfileDTO userProfileDTO) {
        if (userProfileDTO != null) {
            userNameHeader.setText("Profil uživatele: " + userProfileDTO.getNickName());
            userNickName.setText("Přezdívka: " + userProfileDTO.getNickName());
            userGender.setText("Pohlaví: " + userProfileDTO.getGender());
            userAge.setText("Věk: " + userProfileDTO.getAge());
            userRegions.setText("Regiony: " + (userProfileDTO.getRegions() != null ? userProfileDTO.getRegions().toString() : "Žádné"));
        } else {
            displayErrorMessage("Uživatel nenalezen.");
            throw new NotFoundException();
        }
    }

    private void displayErrorMessage(String message) {
        userNameHeader.setText("Chyba");
        userNickName.setText(message);
        userGender.setText("");
        userAge.setText("");
        userRegions.setText("");
    }
}