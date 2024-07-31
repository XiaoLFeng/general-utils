import com.xlf.utility.util.ConvertUtil;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * MapToBeanTest
 * <hr/>
 * MapToBean测试类, 用于测试MapToBean方法
 */
public class MapToBeanTest {
    @Test
    public void mapToBean() {
        UserDO userDO;
        HashMap<Object, Object> map = new HashMap<>();
        map.put("uuid", "uuid");
        map.put("username", "username");
        map.put("email", "email");
        map.put("phone", "phone");
        map.put("avatar", "avatar");
        map.put("password", "password");
        map.put("realAuthUuid", "realAuthUuid");
        map.put("enable", true);
        map.put("ban", null);
        map.put("banReason", "banReason");
        map.put("role", "role");
        map.put("member", "member");
        map.put("createdAt", "2024-07-31 15:40:44.847001");
        map.put("updatedAt", "2024-07-31 15:40:44.847001");
        map.put("bannedAt", "");
        map.put("userQq", "userQq");
        map.put("userWechat", "userWechat");
        userDO = ConvertUtil.convertMapToObject(map, UserDO.class);
        System.out.println(userDO);
    }

    public static class UserDO {
        public String uuid;
        public String username;
        public String email;
        public String phone;
        public String avatar;
        public String password;
        public String realAuthUuid;
        public Boolean enable;
        public Boolean ban;
        public String banReason;
        public String role;
        public String member;
        public Timestamp createdAt;
        public Date updatedAt;
        public Timestamp bannedAt;
        public String userQq;
        public String userWechat;

        @Override
        public String toString() {
            return "UserDO(uuid=" + this.uuid + ", username=" + this.username + ", email=" + this.email + ", phone=" + this.phone + ", avatar=" + this.avatar + ", password=" + this.password + ", realAuthUuid=" + this.realAuthUuid + ", enable=" + this.enable + ", ban=" + this.ban + ", banReason=" + this.banReason + ", role=" + this.role + ", member=" + this.member + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", bannedAt=" + this.bannedAt + ", userQq=" + this.userQq + ", userWechat=" + this.userWechat + ")";
        }
    }
}
