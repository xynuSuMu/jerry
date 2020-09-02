package webapp.app1.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: chenlong
 * @Date: 2020/8/6 21:37
 * @Description:
 */
public interface UserMapper {
    List<User> selectUser();

    int crateUUID(@Param("code") String code);

    String getUUID();

}
