package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.dtos.UserDto;
import org.xenon.echo.dtos.UserUpdateRequest;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.Role;
import org.xenon.echo.exceptions.UserNotFoundException;
import org.xenon.echo.mappers.UserMapper;
import org.xenon.echo.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(Role role, UUID excludeUserId) {
        List<User> users = (role != null)
                ? userRepository.findByRoleAndIdNot(role, excludeUserId)
                : userRepository.findAllByIdNot(excludeUserId);

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return userMapper.toDto(user);
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        userRepository.delete(user);
    }

    public UserDto updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getEmail() != null) user.setEmail(request.getEmail());

        return userMapper.toDto(user);
    }

    public void enableUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        user.setEnabled(true);
    }

    public void disableUser(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        user.setEnabled(false);
    }

    public void changeUserRole(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found"));
        if(user.getRole() == Role.USER){
            user.setRole(Role.ADMIN);
        }
    }

    public void resetUserPassword(UUID userId, String newPassword){
        User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found"));
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public void forceVerify(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found"));
        user.setVerified(true);
    }

    public User createUserFromOauth(OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(UUID.randomUUID().toString());
        user.setVerified(true);

        userRepository.save(user);

        return user;
    }
}