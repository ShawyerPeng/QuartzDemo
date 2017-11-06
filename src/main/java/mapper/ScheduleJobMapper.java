package mapper;

import model.ScheduleJob;
import model.ScheduleJobExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScheduleJobMapper {
    int countByExample(ScheduleJobExample example);

    int deleteByExample(ScheduleJobExample example);

    int deleteByPrimaryKey(Integer jobId);

    int insert(ScheduleJob record);

    int insertSelective(ScheduleJob record);

    List<ScheduleJob> selectByExample(ScheduleJobExample example);

    ScheduleJob selectByPrimaryKey(Integer jobId);

    int updateByExampleSelective(@Param("record") ScheduleJob record, @Param("example") ScheduleJobExample example);

    int updateByExample(@Param("record") ScheduleJob record, @Param("example") ScheduleJobExample example);

    int updateByPrimaryKeySelective(ScheduleJob record);

    int updateByPrimaryKey(ScheduleJob record);

    List<ScheduleJob> selectAll();
}