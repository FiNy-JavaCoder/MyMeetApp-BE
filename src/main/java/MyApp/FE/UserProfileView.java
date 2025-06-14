package MyApp.FE;

import MyApp.BE.dto.ErrorDTO;
import MyApp.BE.dto.UserDTO;
import MyApp.BE.dto.mapper.UserMapper;
import MyApp.BE.entity.UserEntity;
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
    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private Long userId;

    private H2 userNameHeader;
    private Paragraph userNickName;
    private Paragraph userEmail;
    private Paragraph userGender;
    private Paragraph userAge;
    private Paragraph userRegions;

    public UserProfileView(@Autowired
                           UserService userService,
                           IUserRepository userRepository,
                           UserMapper userMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
        ResponseEntity<?> responseEntity = userService.getPerson(userId);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Object body = responseEntity.getBody();
            if (body instanceof UserDTO) {
                UserEntity userentity = (UserEntity) body;
                updateUserProfileUI(userMapper.toDTO(userentity));
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

    private void updateUserProfileUI(UserDTO user) {
        if (user != null) {
            userNameHeader.setText("Profil uživatele: " + user.getNickName());
            userNickName.setText("Přezdívka: " + user.getNickName());
            userEmail.setText("Email: " + user.getEmail());
            userGender.setText("Pohlaví: " + user.getGender());
            userAge.setText("Věk: " + user.getAge());
            userRegions.setText("Regiony: " + (user.getRegions() != null ? user.getRegions().toString() : "Žádné"));
        } else {
            displayErrorMessage("Uživatel nenalezen.");
            throw new NotFoundException();
        }
    }

    // Pomocná metoda pro zobrazení chybové zprávy v UI
    private void displayErrorMessage(String message) {
        userNameHeader.setText("Chyba");
        userNickName.setText(message);
        userEmail.setText("");
        userGender.setText("");
        userAge.setText("");
        userRegions.setText("");
    }
}