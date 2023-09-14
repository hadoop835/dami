package features.demo82_solon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;

@ExtendWith(SolonJUnit5Extension.class)
public class Demo82 {
    @Inject
    EventUserNotices eventUserNotices;

    @Test
    public void main() {
        eventUserNotices.onCreated(82, "noear");
    }
}
