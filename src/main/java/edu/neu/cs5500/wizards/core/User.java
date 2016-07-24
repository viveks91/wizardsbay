package edu.neu.cs5500.wizards.core;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.security.Principal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"id", "name"})
@JsonPropertyOrder({"firstName", "lastName", "username", "password", "email", "address"})
public class User implements Principal {

    @JsonProperty
    private Integer id;

    @JsonProperty
    @NotEmpty
    @Size(min = 3)
    private String username;

    @JsonProperty
    @NotEmpty
    @Size(min = 3)
    private String password;

    @JsonProperty
    @NotEmpty
    private String firstName;

    @JsonProperty
    @NotEmpty
    private String lastName;

    @JsonProperty
    private String address;

    @JsonProperty
    @NotEmpty
    private String email;

    public User() {
    }

    public User(String username, String password, String firstName, String lastName, String address, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ApiModelProperty(value = "Email-id of the user", required = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ApiModelProperty(value = "Username of the user", required = true, allowableValues = "Length more than 3")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @ApiModelProperty(value = "Password for the user", required = true, allowableValues = "Length more than 3")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ApiModelProperty(value = "First name of the user", required = true)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ApiModelProperty(value = "Second name of the user", required = true)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ApiModelProperty(value = "Address of the user")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (!username.equals(user.username)) return false;
        if (!password.equals(user.password)) return false;
        if (!firstName.equals(user.firstName)) return false;
        if (!lastName.equals(user.lastName)) return false;
        if (address != null ? !address.equals(user.address) : user.address != null) return false;
        return email.equals(user.email);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + email.hashCode();
        return result;
    }
}
