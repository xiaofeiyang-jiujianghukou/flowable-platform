package com.flowable.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowable.usercenter.entity.SysRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT r.*, (SELECT COUNT(*) FROM sys_user_role ur WHERE ur.role_code = r.code) AS user_count FROM sys_role r ORDER BY r.id")
    @Results({@Result(column = "user_count", property = "userCount")})
    List<SysRole> selectWithUserCount();

    @Insert("INSERT IGNORE INTO sys_user_role (user_id, role_code) VALUES (#{userId}, #{roleCode})")
    void assignUser(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId} AND role_code = #{roleCode}")
    void removeUser(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    @Select("SELECT menu_id FROM sys_role_menu WHERE role_code = #{roleCode}")
    List<Long> selectMenuIds(@Param("roleCode") String roleCode);

    @Delete("DELETE FROM sys_role_menu WHERE role_code = #{roleCode}")
    void deleteRoleMenus(@Param("roleCode") String roleCode);

    @Insert("INSERT IGNORE INTO sys_role_menu (role_code, menu_id) VALUES (#{roleCode}, #{menuId})")
    void insertRoleMenu(@Param("roleCode") String roleCode, @Param("menuId") Long menuId);
}
