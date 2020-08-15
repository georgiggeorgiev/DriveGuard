package com.joro.driveguard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "phone_number", unique = true)
    @Pattern(regexp = "08[789]\\d{7}", message = "*Моля въведете коректен телефонен номер")
    @NotEmpty(message = "*Полето за телефонен номер не може да бъде празно")
    private String phoneNumber;

    @Column(name = "password")
    @Length(min = 5, message = "*Паролата трябва да бъде поне 5 символа")
    @NotEmpty(message = "*Полето за парола не може да бъде празно")
    private String password;

    @Column(name = "first_name")
    @NotEmpty(message = "*Полето за име не може да бъде празно")
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty(message = "*Полето за фамилия не може да бъде празно")
    private String lastName;

    @Column(name = "active")
    private Boolean active;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Column(name = "api_key")
    private String APIKey;
}
