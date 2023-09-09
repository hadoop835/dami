package demo;

import org.noear.dami.DamiBus;
import org.noear.dami.TopicListener;
import org.noear.dami.impl.Payload;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author kongweiguang
 * @since 1.0
 */
public class ObjDemo {
    static String demo_topic = "demo.user.info";

    public static void main(String[] args) {
        TopicListener<Payload<User, User>> listener = createListener();

        //监听
        DamiBus.<User, User>obj().listen(demo_topic, listener);

        //发送测试
        sendTest();

        //取消监听
        DamiBus.<User, User>obj().unlisten(demo_topic, listener);
    }

    //创建监听器
    private static TopicListener<Payload<User, User>> createListener() {
        return payload -> {
            //接收处理
            System.out.println(payload);

            if (payload.isRequest()) {
                final User content = payload.getContent().setSing("鸡你太美");
                //如果是请求载体，再响应一下
                DamiBus.<User, User>obj().response(payload, content);
            }
        };
    }

    //发送测试
    private static void sendTest() {
        final User user = new User().setName("kk").setAge(2.5).setHobby(new String[]{"唱", "跳", "rap", "打篮球"});
        //普通发送
        DamiBus.<User, Void>obj().send(demo_topic, user);

        //普通发送,自定义构建参数
        DamiBus.<User, Void>obj().send(new Payload<>("123", demo_topic, user));

        //请求并等响应
        User rst1 = DamiBus.<User, User>obj().requestAndResponse(demo_topic, user);
        System.out.println("响应返回: " + rst1);

        user.setSing("ai kun");
        //请求并等回调
        DamiBus.<User, User>obj().requestAndCallback(demo_topic, user, rst2 -> {
            System.out.println("响应回调: " + rst2);
        });
    }

    static class User {
        private String name;
        private Double age;
        private String[] hobby;
        private String sing;

        public String sign() {
            return sing;
        }

        public User setSing(final String sing) {
            this.sing = sing;
            return this;
        }

        public String name() {
            return name;
        }

        public User setName(final String name) {
            this.name = name;
            return this;
        }

        public Double age() {
            return age;
        }

        public User setAge(final Double age) {
            this.age = age;
            return this;
        }

        public String[] hobby() {
            return hobby;
        }

        public User setHobby(final String[] hobby) {
            this.hobby = hobby;
            return this;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final User user = (User) o;
            return Objects.equals(name, user.name) && Objects.equals(age, user.age) && Arrays.equals(hobby, user.hobby) && Objects.equals(sing, user.sing);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(name, age, sing);
            result = 31 * result + Arrays.hashCode(hobby);
            return result;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                    .add("name='" + name + "'")
                    .add("age=" + age)
                    .add("hobby=" + Arrays.toString(hobby))
                    .add("sign='" + sing + "'")
                    .toString();
        }
    }
}