package com.flowable.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowable.usercenter.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("""
        <script>
        SELECT u.*, d.name AS dept_name
        FROM sys_user u LEFT JOIN sys_department d ON u.department_id = d.id
        WHERE 1=1
        <if test='keyword != null'>AND (u.name LIKE CONCAT('%',#{keyword},'%') OR u.username LIKE CONCAT('%',#{keyword},'%'))</if>
        <if test='roleCode != null'>AND EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_code = #{roleCode})</if>
        ORDER BY u.id
        </script>
    """)
    @Results({@Result(column = "dept_name", property = "deptName")})
    List<SysUser> selectWithDept(@Param("keyword") String keyword, @Param("roleCode") String roleCode);

    @Select("SELECT role_code FROM sys_user_role WHERE user_id = #{userId}")
    List<String> selectRoleCodes(@Param("userId") Long userId);
}
