package com.github.onozaty.sample.service;

import com.github.onozaty.sample.domain.User;
import com.github.onozaty.sample.domain.UserInput;
import com.github.onozaty.sample.mapper.UserMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

  private final UserMapper userMapper;

  public UserService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userMapper.findAll();
  }

  @Transactional(readOnly = true)
  public User findById(Long id) {
    return userMapper.findById(id).orElseThrow(() -> new UserNotFoundException(id));
  }

  public User create(UserInput input) {
    return userMapper.insert(input);
  }

  public User update(Long id, UserInput input) {
    User updated = userMapper.update(id, input);
    if (updated == null) {
      throw new UserNotFoundException(id);
    }
    return updated;
  }

  public void delete(Long id) {
    int deleted = userMapper.delete(id);
    if (deleted == 0) {
      throw new UserNotFoundException(id);
    }
  }
}
