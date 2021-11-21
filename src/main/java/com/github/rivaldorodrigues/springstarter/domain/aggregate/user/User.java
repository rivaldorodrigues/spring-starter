package com.github.rivaldorodrigues.springstarter.domain.aggregate.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.rivaldorodrigues.springstarter.domain.aggregate.profile.Profile;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;


@Data
@Entity
@Builder
@EnableJpaAuditing
@NoArgsConstructor
@AllArgsConstructor
@Table(name="\"User\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private String name;

    @Email()
    @Size(min = 6, max = 254)
    @Column(length = 254, unique = true)
    private String email;

    @Column(length = 20, unique = true)
    private String login;

    @NotNull
    @Size(min = 6, max = 60)
    @Column(length = 60, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotEmpty
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "profile_user",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id", referencedColumnName = "id")
    )
    private Set<Profile> profiles;

    @JsonIgnore
    private String passwordResetCode;

    @JsonIgnore
    private LocalDateTime passwordResetExpiration;
}
