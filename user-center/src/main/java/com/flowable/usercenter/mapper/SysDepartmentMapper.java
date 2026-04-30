package com.flowable.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flowable.usercenter.entity.SysDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {

    @Select("SELECT d.*, (SELECT COUNT(*) FROM sys_user u WHERE u.department_id = d.id) AS user_count FROM sys_department d ORDER BY d.sort_order")
    @Results({@Result(column = "user_count", property = "userCount")})
    List<SysDepartment> selectWithUserCount();
}
