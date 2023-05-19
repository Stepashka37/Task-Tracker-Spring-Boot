import com.example.TaskTrackerApplication;
import com.example.dao.TaskDao;
import com.example.exceptions.TaskNotFoundException;
import com.example.model.Task;
import com.example.model.TaskStatus;
import com.example.model.TaskType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest(classes = TaskTrackerApplication.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
//@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskDaoTest {

    private final TaskDao taskDao;

    @Autowired
    public TaskDaoTest(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Test
    public void contextLoads(){
        assertNotNull(taskDao);
    }

    @Test
    public void getAllTest(){
        assertEquals(3, taskDao.getAll().size());

    }

    @Test
    public void getByIdTest(){
        assertNotNull(taskDao.getById(1));
        assertEquals("TASK1 Test", taskDao.getById(1).getName());
        assertEquals(1, taskDao.getById(1).getId());
    }

    @Test
    public void getByIdNotExistTest(){
        assertThrows(TaskNotFoundException.class,
                ()->{
                    taskDao.getById(10) ;
                });
    }

    @Test
    public void createTest(){
        Task task = Task.builder()
                .name("TaskCreated")
                .description("TaskCreated description")
                .status(TaskStatus.NEW)
                .type(TaskType.TASK)
                .startTime(LocalDateTime.of(2023, 05, 14, 12, 00, 00))
                .endTime(LocalDateTime.of(2023, 05, 14, 13, 00, 00))
                .duration(60)
                .build();
        Task taskCreated = taskDao.create(task);
        assertEquals(4, taskCreated.getId());
        assertEquals("TaskCreated", taskCreated.getName());
        assertEquals("TaskCreated description", taskCreated.getDescription());
    }

    @Test
    public void updateTest(){
        Task task = Task.builder()
                .id(1)
                .name("TaskUPD")
                .description("TaskUPD description")
                .status(TaskStatus.NEW)
                .type(TaskType.TASK)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .duration(60)
                .build();
        Task taskUpd = taskDao.update(task);
        assertEquals(taskUpd, task);
    }

    @Test
    public void updateNotExistTest(){
        Task task = Task.builder()
                .id(4)
                .name("TaskUPD")
                .description("TaskUPD description")
                .status(TaskStatus.NEW)
                .type(TaskType.TASK)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .duration(60)
                .build();
        assertThrows(TaskNotFoundException.class,
                ()->{
                    taskDao.update(task) ;
                });
    }

    @Test
    public void deleteByIdTest(){
        taskDao.deleteById(1);
        assertThrows(TaskNotFoundException.class,
                ()->{
                    taskDao.getById(1) ;
                });
        assertEquals(2, taskDao.getAll().size());
    }

    @Test
    public void deleteAllTest(){
        taskDao.deleteAll();
        assertEquals(0, taskDao.getAll().size());
    }

    @Test
    public void getPrioritizedTest(){
        List<Task> prioritizedTasks = taskDao.getPrioritized("January", 3);
        assertEquals(6, prioritizedTasks.size());
        /*assertEquals(1, prioritizedTasks.get(0).getId());
        assertEquals(2, prioritizedTasks.get(1).getId());
        assertEquals(3, prioritizedTasks.get(2).getId());

        Task task = Task.builder()
                .name("TaskCreated")
                .description("TaskCreated description")
                .status(TaskStatus.NEW)
                .type(TaskType.TASK)
                .startTime(LocalDateTime.of(2023, 05, 01, 11, 00, 00))
                .endTime(LocalDateTime.of(2023, 05, 01, 12, 00, 00))
                .duration(60)
                .build();
        taskDao.create(task);
        prioritizedTasks = taskDao.getPrioritized("January", 4);
        assertEquals(4, prioritizedTasks.get(0).getId());
        assertEquals(1, prioritizedTasks.get(1).getId());
        assertEquals(2, prioritizedTasks.get(2).getId());
        assertEquals(3, prioritizedTasks.get(3).getId());
*/


    }

}
