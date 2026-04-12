package com.github.onozaty.sample.service;

import com.github.onozaty.sample.domain.User;
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

  public User create(User user) {
    userMapper.insert(user);
    return userMapper.findById(user.getId()).orElseThrow();
  }

  public User update(Long id, User user) {
    findById(id); // 存在確認
    user.setId(id);
    userMapper.update(user);
    return userMapper.findById(id).orElseThrow();
  }

  public void delete(Long id) {
    findById(id); // 存在確認
    userMapper.delete(id);
  }
}
