package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 媒资信息 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaFilesMapper extends BaseMapper<MediaFiles> {

    /**
     * 根据分片参数获取待处理任务
     * @param shardTotal 总数
     * @param shardIndex 分片索引
     * @param count 任务数
     * @return 待处理任务集合
     */
    @Select("SELECT t.* FROM media_process t WHERE t.id % #{shardTotal} = #{shardindex} " +
            "and t.status='1' limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal,
                                              @Param("shardindex") int shardIndex,
                                              @Param("count") int count);

}
