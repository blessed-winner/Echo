package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.dtos.UserDto;
import org.xenon.echo.dtos.UserUpdateRequest;
import org.xenon.echo.enums.Role;
import org.xenon.echo.entities.User;
import org.xenon.echo.exceptions.UserNotFoundException;
import org.xenon.echo.mappers.UserMapper;
import org.xenon.echo.repositories.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  private List<User> users;

  @Transactional(readOnly = true)
  public List<UserDto> getAllUsers(Role role, UUID excludeUserId){
      if(role != null){
          users = userRepository.findByRoleAndIdNot(role,excludeUserId);
      } else {
          users = userRepository.findAllByIdNot(excludeUserId);
      }

      return users.stream()
              .map(userMapper::toDto)
              .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public UserDto getUserById(UUID id){
      var user = userRepository.findById(id).orElse(null);
      if(user == null){
          return null;
      }

      return userMapper.toDto(user);
  }

  public void deleteUser(UUID id){
      var user = userRepository.findById(id).orElse(null);
      if(user == null){
          throw new UserNotFoundException("User Not Found");
      }

      userRepository.delete(user);
  }

  public UserDto updateUser(UUID id, UserUpdateRequest request){
      var user = userRepository.findById(id).orElse(null);
      if(user == null){
          throw new UserNotFoundException("User Not Found");
      }

      if(request.getName() != null) user.setName(request.getName());
      if(request.getRole() != null) user.setRole(request.getRole());
      if(request.getEmail() != null) user.setEmail(request.getEmail());

      var updatedUser = userRepository.save(user);
      return userMapper.toDto(updatedUser);
  }
}
