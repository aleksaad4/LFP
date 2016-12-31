package ad4si2.lfp;

import ad4si2.lfp.utils.test.H2Helper;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LfpApplication.class})
@ActiveProfiles(value = {"itest"})
public class BaseTest {

    @Inject
    private H2Helper h2Helper;

    @After
    public void tearDown() throws Exception {
        h2Helper.clearH2DB();
    }
}
