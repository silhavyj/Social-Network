package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/***
 * This class represents a token that issued whenever a user is required to
 * confirm their registration, or when they want to change their password.
 * The value of the token is sent to them via e-mail, and they're required to
 * use it as a form of verification when doing these actions.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    /*** primary key of the tokens table within the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*** token value (UUID.randomUUID()) */
    private String value;

    /***
     * Flag if the token can expire. The registration token
     * cannot expire but when they want to change their password
     * they have only 5 minutes to do so
     * */
    private Boolean expirable = false;

    /*** time stamp when the token was created */
    private LocalDateTime createdAt;

    /*** time stamp when the token expires */
    private LocalDateTime expiresAt;

    /*** user who has been assigned the token */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    /*** type of the token (reset password, registration) */
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    /***
     * Creates an instance of Token
     * @param user user associated with the token
     * @param type type of the token
     */
    public Token(User user, TokenType type) {
        this(user, null, type, false);
    }

    /***
     * Creates an instance of Token
     * @param user user associated with the token
     * @param minutesValidFor number of minutes the token is valid for
     * @param type type of the token
     */
    public Token(User user, int minutesValidFor, TokenType type) {
        this(user, LocalDateTime.now().plusMinutes(minutesValidFor), type, true);
    }

    /***
     * Creates an instance of Token
     * @param user user associated with the token
     * @param expiresAt datetime when the token expires
     * @param type type of the token
     * @param expirable flag if the token can actually expire
     */
    public Token(User user, LocalDateTime expiresAt, TokenType type, Boolean expirable) {
        this.user = user;
        this.value = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.tokenType = type;
        this.expirable = expirable;
    }
}
