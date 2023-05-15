import com.example.TaskTrackerApplication;
import com.example.dao.TaskDao;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest(classes = TaskTrackerApplication.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskTest {

    private final TaskDao taskDao;

    @Autowired
    public TaskTest(TaskDao taskDao) {
        this.taskDao = taskDao;
    }


    @Test
    public void getTest(){
        assertEquals(3, taskDao.getAllTasks().size());
        assertEquals(1, taskDao.getAllEpics().size());
        assertEquals(2, taskDao.getAllSubtasks().size());

    }

    @Test
    public void getSecTest(){
        assertEquals(3, taskDao.getAllTasks().size());
        System.out.println(taskDao.getAllTasks());
        System.out.println(taskDao.getAllEpics());
        System.out.println(taskDao.getAllSubtasks());
        assertEquals(1, taskDao.getAllEpics().size());
        assertEquals(2, taskDao.getAllSubtasks().size());

    }



}
