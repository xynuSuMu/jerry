package webapp.app1.mapper;

import java.util.List;

/**
 * @Auther: chenlong
 * @Date: 2020/8/6 21:37
 * @Description:
 */
public interface UserMapper {
    List<User> selectUser();

    int updateUser();

    int updateUserV2();
}
