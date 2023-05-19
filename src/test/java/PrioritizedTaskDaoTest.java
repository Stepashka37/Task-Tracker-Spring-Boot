import com.example.TaskTrackerApplication;
import com.example.dao.PrioritizedTaskDao;
import com.example.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;


import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource("/application-test.properties")
@SpringBootTest(classes = TaskTrackerApplication.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
//@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PrioritizedTaskDaoTest {
    private final PrioritizedTaskDao prioritizedTaskDao;

    @Autowired
    public PrioritizedTaskDaoTest(PrioritizedTaskDao prioritizedTaskDao) {
        this.prioritizedTaskDao = prioritizedTaskDao;
    }

    @Test
    public void contextLoads(){
        assertNotNull(prioritizedTaskDao);
    }

    @Test
    public void getPrioritizedTaskTest(){
        List<? extends Task> prioritizedTasks = prioritizedTaskDao.getPrioritizedTasks("Week", 6);
        assertEquals(6, prioritizedTasks.size());
        assertEquals(1, prioritizedTasks.get(0).getId());
        assertEquals(2, prioritizedTasks.get(1).getId());
        assertEquals(3, prioritizedTasks.get(2).getId());
        System.out.println(prioritizedTasks);


    }


}
