package com.github.onozaty.sample.mapper;

import com.github.onozaty.sample.domain.User;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

  @Select("""
      SELECT id, name, email, created_at, updated_at
      FROM users
      ORDER BY id
      """)
  List<User> findAll();

  @Select("""
      SELECT id, name, email, created_at, updated_at
      FROM users
      WHERE id = #{id}
      """)
  Optional<User> findById(Long id);

  @Select("""
      INSERT INTO users (name, email, created_at, updated_at)
      VALUES (#{name}, #{email}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
      RETURNING *
      """)
  User insert(User user);

  @Select("""
      UPDATE users
      SET name = #{name},
          email = #{email},
          updated_at = CURRENT_TIMESTAMP
      WHERE id = #{id}
      RETURNING *
      """)
  User update(User user);

  @Delete("""
      DELETE FROM users
      WHERE id = #{id}
      """)
  void delete(Long id);
}
