package com.flowable.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowable.usercenter.entity.SysMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT DISTINCT m.* FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id WHERE rm.role_code IN (SELECT role_code FROM sys_user_role WHERE user_id = #{userId}) ORDER BY m.sort_order")
    List<SysMenu> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT m.permission_code FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id WHERE rm.role_code IN (SELECT role_code FROM sys_user_role WHERE user_id = #{userId}) AND m.permission_code IS NOT NULL")
    List<String> selectPermissions(@Param("userId") Long userId);
}
