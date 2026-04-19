package com.github.onozaty.sample.mapper;

import com.github.onozaty.sample.domain.User;
import com.github.onozaty.sample.domain.UserInput;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

  @Select(
      """
      SELECT *
      FROM users
      ORDER BY id
      """)
  List<User> findAll();

  @Select(
      """
      SELECT *
      FROM users
      WHERE id = #{id}
      """)
  Optional<User> findById(Long id);

  @Select(
      """
      INSERT INTO users (name, email, created_at, updated_at)
      VALUES (#{input.name}, #{input.email}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
      RETURNING *
      """)
  User insert(@Param("input") UserInput input);

  @Select(
      """
      UPDATE users
      SET name = #{input.name},
          email = #{input.email},
          updated_at = CURRENT_TIMESTAMP
      WHERE id = #{id}
      RETURNING *
      """)
  User update(@Param("id") Long id, @Param("input") UserInput input);

  @Delete(
      """
      DELETE FROM users
      WHERE id = #{id}
      """)
  int delete(Long id);
}
