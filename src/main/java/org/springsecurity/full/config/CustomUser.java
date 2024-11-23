package org.springsecurity.full.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springsecurity.full.entity.User;

import java.util.Arrays;
import java.util.Collection;

public class CustomUser implements UserDetails {

    private User user;

    public CustomUser(User user) { // конструктор
        this.user = user;
    }

    // Переопределенные методы из интерфейса
    @Override
    public boolean isAccountNonExpired() {  // Указывает, истек ли срок действия учетной записи пользователя.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {  // Указывает, заблокирован или разблокирован пользователь.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {  // Указывает, истек ли срок действия учетных данных пользователя (пароля).
        return true;
    }

    @Override
    public boolean isEnabled() {  // Указывает, включен или отключен пользователь.
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {  // Возвращает полномочия, предоставленные пользователю.
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole());
        return Arrays.asList(simpleGrantedAuthority);
    }

    @Override
    public String getPassword() {  // Возвращает пароль, используемый для аутентификации пользователя.
        return user.getPassword();
    }

    @Override
    public String getUsername() {  // Возвращает имя пользователя, используемое для аутентификации пользователя.
        return user.getEmail();
    }
}
