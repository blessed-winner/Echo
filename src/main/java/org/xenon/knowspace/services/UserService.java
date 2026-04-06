package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.dtos.UserDto;
import org.xenon.knowspace.entities.Role;
import org.xenon.knowspace.entities.User;
import org.xenon.knowspace.exceptions.UserNotFoundException;
import org.xenon.knowspace.mappers.UserMapper;
import org.xenon.knowspace.repositories.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private List<User> users;
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

  public UserDto updateUser(UUID id, UserDto userDto){
      var user = userRepository.findById(id).orElse(null);
      if(user == null){
          throw new UserNotFoundException("User Not Found");
      }

      user.setName(userDto.getName());
      user.setRole(user.getRole());
      user.setEmail(userDto.getEmail());

      var updatedUser = userRepository.save(user);
      return userMapper.toDto(updatedUser);
  }
}
