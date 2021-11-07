package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    private Boolean expirable = false;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    public Token(User user, TokenType type) {
        this(user, null, type, false);
    }

    public Token(User user, int minutesValidFor, TokenType type) {
        this(user, LocalDateTime.now().plusMinutes(minutesValidFor), type, true);
    }

    public Token(User user, LocalDateTime expiresAt, TokenType type, Boolean expirable) {
        this.user = user;
        this.value = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.tokenType = type;
        this.expirable = expirable;
    }
}
