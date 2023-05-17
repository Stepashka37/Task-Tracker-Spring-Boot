import com.example.TaskTrackerApplication;
import com.example.dao.EpicDao;
import com.example.dao.SubtaskDao;
import com.example.exceptions.EpicNotFoundException;
import com.example.exceptions.SubtaskNotFoundException;
import com.example.model.Subtask;
import com.example.model.TaskStatus;
import com.example.model.TaskType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource("/application-test.properties")
@SpringBootTest(classes = TaskTrackerApplication.class)
@Sql({"classpath:schema.sql", "classpath:data.sql"})
//@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubtaskDaoTest {

    private final SubtaskDao subtaskDao;
    private final EpicDao epicDao;

    @Autowired
    public SubtaskDaoTest(SubtaskDao subtaskDao, EpicDao epicDao) {
        this.subtaskDao = subtaskDao;
        this.epicDao = epicDao;
    }


    @Test
    public void contextLoads(){
        assertNotNull(subtaskDao);
    }

    @Test
    public void getAllTest(){
        assertEquals(2, subtaskDao.getAll().size());

    }

    @Test
    public void getByIdTest(){
        assertNotNull(subtaskDao.getById(1));
        assertEquals("SUBTASK1 Test", subtaskDao.getById(1).getName());
        assertEquals(1, subtaskDao.getById(1).getId());
    }

    @Test
    public void getByIdNotExistTest(){
        assertThrows(SubtaskNotFoundException.class,
                ()->{
                    subtaskDao.getById(10) ;
                });
    }

    @Test
    public void createTest(){
        Subtask subtask = Subtask.builder()
                .name("SubtaskCreated")
                .description("SubtaskCreated description")
                .status(TaskStatus.NEW)
                .type(TaskType.EPIC)
                .startTime(LocalDateTime.of(2023, 05, 14, 12, 00, 00))
                .endTime(LocalDateTime.of(2023, 05, 14, 13, 00, 00))
                .duration(60)
                .epicId(1)
                .build();
        Subtask subtaskCreated = subtaskDao.create(subtask);
        assertEquals(3, subtaskCreated.getId());
        assertEquals("SubtaskCreated", subtaskCreated.getName());
        assertEquals("SubtaskCreated description", subtask.getDescription());
    }

    @Test
    public void updateTest(){
        Subtask subtask = Subtask.builder()
                .id(1)
                .name("SubtaskUPD")
                .description("SubtaskUPD description")
                .status(TaskStatus.NEW)
                .type(TaskType.SUBTASK)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .duration(60)
                .epicId(1)
                .build();
        Subtask subtaskUpd = subtaskDao.update(subtask);
        assertEquals(subtaskUpd, subtask);
    }

    @Test
    public void updateNotExistTest(){
        Subtask subtask = Subtask.builder()
                .id(3)
                .name("SubtaskUPD")
                .description("SubtaskUPD description")
                .status(TaskStatus.NEW)
                .type(TaskType.SUBTASK)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .duration(60)
                .build();
        assertThrows(SubtaskNotFoundException.class,
                ()->{
                    subtaskDao.update(subtask) ;
                });
    }

    @Test
    public void deleteByIdTest(){
        subtaskDao.deleteById(1);
        assertThrows(SubtaskNotFoundException.class,
                ()->{
                    subtaskDao.getById(1) ;
                });
        assertEquals(1, subtaskDao.getAll().size());
    }

    @Test
    public void deleteAllTest(){
        subtaskDao.deleteAll();
        assertEquals(0, subtaskDao.getAll().size());
    }

    @Test
    public void getEpicSubtasksTest(){
        assertEquals(2, subtaskDao.getEpicSubtasks(1).size());
    }

    @Test
    public void deleteAllSubtasksAndCheckEpicDeleted(){
        subtaskDao.deleteById(1);
        subtaskDao.deleteById(2);
        assertThrows(EpicNotFoundException.class,
                () -> {
                        epicDao.getById(1);
        });

    }

}
